package com.groupdocs.viewerui.ui.core.caching;

import com.groupdocs.viewerui.ui.api.cache.FileCache;
import com.groupdocs.viewerui.ui.core.IViewer;
import com.groupdocs.viewerui.ui.core.entities.DocumentInfo;
import com.groupdocs.viewerui.ui.core.entities.FileCredentials;
import com.groupdocs.viewerui.ui.core.entities.Page;
import com.groupdocs.viewerui.ui.core.entities.PageResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CachingViewer implements IViewer {
    private static final Logger LOGGER = LoggerFactory.getLogger(CachingViewer.class);
    private static final Map<String, WeakReference<Object>> _asyncLock = new ConcurrentHashMap<>();
    private final IViewer _viewer;
    private final FileCache _fileCache;

    public CachingViewer(IViewer viewer, FileCache fileCache) {
        _viewer = viewer;
        _fileCache = fileCache;
    }

    public String getPageExtension() {
        return _viewer.getPageExtension();
    }

    public Page createPage(int pageNumber, byte[] data) {
        return _viewer.createPage(pageNumber, data);
    }

    public Page getPage(FileCredentials fileCredentials, int pageNumber) {
        String cacheKey = CacheKeys.getPageCacheKey(pageNumber, getPageExtension());
        final byte[][] bytes = {_fileCache.get(cacheKey, fileCredentials.getFilePath(), byte[].class)};
        if (bytes[0] == null) {
            synchronizedBlock(cacheKey, () -> {
                bytes[0] = _fileCache.get(cacheKey, fileCredentials.getFilePath(), byte[].class);
                // If still does not exist
                if (bytes[0] == null) {
                    Page page = _viewer.getPage(fileCredentials, pageNumber);

                    saveResources(fileCredentials.getFilePath(), page.getPageNumber(), page.getResources().stream());

                    bytes[0] = page.getData();
                    _fileCache.set(cacheKey, fileCredentials.getFilePath(), bytes[0]);
                }
            });
        }

        Page page = createPage(pageNumber, bytes[0]);
        return page;
    }

    public DocumentInfo getDocumentInfo(FileCredentials fileCredentials) {
        String cacheKey = CacheKeys.FILE_INFO_CACHE_KEY;
        DocumentInfo[] documentInfo = {_fileCache.get(cacheKey, fileCredentials.getFilePath(), DocumentInfo.class)};
        if (documentInfo[0] == null) {
            synchronizedBlock(fileCredentials.getFilePath(), () ->
            {
                documentInfo[0] = _fileCache.get(cacheKey, fileCredentials.getFilePath(), DocumentInfo.class);
                if (documentInfo[0] == null) {
                    documentInfo[0] = _viewer.getDocumentInfo(fileCredentials);
                    _fileCache.set(cacheKey, fileCredentials.getFilePath(), documentInfo[0]);
                }

            });
        }
        return documentInfo[0];
    }

    public byte[] getPdf(FileCredentials fileCredentials) {
        String cacheKey = CacheKeys.PDF_FILE_CACHE_KEY;
        final byte[][] bytes = {_fileCache.get(cacheKey, fileCredentials.getFilePath(), byte[].class)};
        if (bytes[0] == null) {
            synchronizedBlock(fileCredentials.getFilePath(), () -> {
                bytes[0] = _fileCache.get(cacheKey, fileCredentials.getFilePath(), byte[].class);
                if (bytes[0] == null) {
                    bytes[0] = _viewer.getPdf(fileCredentials);
                    _fileCache.set(cacheKey, fileCredentials.getFilePath(), bytes[0]);
                }
            });
        }
        return bytes[0];
    }

    public byte[] getPageResource(FileCredentials fileCredentials, int pageNumber, String resourceName) {
        String cacheKey = CacheKeys.getHtmlPageResourceCacheKey(pageNumber, resourceName);
        final byte[][] bytes = {_fileCache.get(cacheKey, fileCredentials.getFilePath(), byte[].class)};

        if (bytes[0] == null) {
            synchronizedBlock(fileCredentials.getFilePath(), () -> {
                bytes[0] = _fileCache.get(cacheKey, fileCredentials.getFilePath(), byte[].class);
                if (bytes[0] == null) {
                    bytes[0] = _viewer.getPageResource(fileCredentials, pageNumber, resourceName);
                    _fileCache.set(cacheKey, fileCredentials.getFilePath(), bytes[0]);
                }
            });
        }
        return bytes[0];
    }

    private void saveResources(String filePath, int pageNumber, Stream<PageResource> pageResources) {
        pageResources.forEach(resource -> {
            String resourceCacheKey = CacheKeys.getHtmlPageResourceCacheKey(pageNumber, resource.getResourceName());

            _fileCache.set(resourceCacheKey, filePath, resource.getData());
        });
    }

    public List<Page> getPages(FileCredentials fileCredentials, int[] pageNumbers) {
        List<CachedPage> pagesOrNulls = getPagesOrNullsFromCache(fileCredentials.getFilePath(), pageNumbers);
        int[] missingPageNumbers = getMissingPageNumbers(pagesOrNulls.stream());

        if (missingPageNumbers.length == 0) {
            return toPages(pagesOrNulls.stream());
        }

        List<Page> createdPages = createPages(fileCredentials, missingPageNumbers);

        List<Page> pages = combine(pagesOrNulls.stream(), createdPages);

        return pages;
    }

    private List<Page> createPages(FileCredentials fileCredentials, int[] pageNumbers) {
        List<Page>[] pages = new List[1];
        final String filePath = fileCredentials.getFilePath();
        synchronizedBlock(filePath, () -> {
            List<CachedPage> pagesOrNulls = getPagesOrNullsFromCache(filePath, pageNumbers);
            int[] missingPageNumbers = getMissingPageNumbers(pagesOrNulls.stream());

            if (missingPageNumbers.length == 0) {
                pages[0] = toPages(pagesOrNulls.stream());
                return;
            }

            List<Page> createdPages = _viewer.getPages(fileCredentials, missingPageNumbers);

            saveToCache(filePath, createdPages.stream());

            pages[0] = combine(pagesOrNulls.stream(), createdPages);
        });
        return pages[0];
    }

    private List<Page> combine(Stream<CachedPage> dst, List<Page> missing) {
        return dst
                .map(pageOrNull -> {
                            Page result = null;
                            if (pageOrNull.getData() == null) {
                                final Optional<Page> optionalPage = missing.stream().filter(page -> page.getPageNumber() == pageOrNull.getPageNumber())
                                        .findFirst();
                                result = optionalPage.orElse(null);
                            } else {
                                result = createPage(pageOrNull.getPageNumber(), pageOrNull.getData());
                            }
                            return result;
                        }
                ).collect(Collectors.toList());
    }

    private void saveToCache(String filePath, Stream<Page> createdPages) {
        createdPages
                .forEach(page ->
                {
                    String cacheKey = CacheKeys.getPageCacheKey(page.getPageNumber(), _viewer.getPageExtension());

                    _fileCache.set(cacheKey, filePath, page.getData());
                    /*List<String> saveResourcesTask = */
                    saveResources(filePath, page.getPageNumber(), page.getResources().stream());

                });
    }

    private List<Page> toPages(Stream<CachedPage> pagesOrNulls) {
        return pagesOrNulls
                .map(p -> createPage(p.getPageNumber(), p.getData()))
                .collect(Collectors.toList());
    }

    private int[] getMissingPageNumbers(Stream<CachedPage> pagesOrNulls) {
        return pagesOrNulls
                .filter(p -> p.getData() == null)
                .mapToInt(CachedPage::getPageNumber)
                .toArray();
    }

    private List<CachedPage> getPagesOrNullsFromCache(String filePath, int[] pageNumbers) {
        return Arrays.stream(pageNumbers)
                .mapToObj(pageNumber -> {
                    final String pageCacheKey = CacheKeys.getPageCacheKey(pageNumber, getPageExtension());
                    return new CachedPage(pageNumber, _fileCache.get(pageCacheKey, filePath, byte[].class));
                }).collect(Collectors.toList());
    }

    private static void synchronizedBlock(String fileName, Runnable synchronizedBlock) {
        synchronized (getFileLock(fileName)) {
            synchronizedBlock.run();
        }
    }

    private static synchronized Object getFileLock(String filename) {
        return _asyncLock.computeIfAbsent(filename, k -> {
            Object lock = new Object();
            return new WeakReference<>(lock);
        }).get(); // Retrieve the actual lock object
    }

    private static void cleanupUnusedLocks() {
        _asyncLock.entrySet().removeIf(entry -> entry.getValue().get() == null);
    }

    @Override
    public void close() {
//        _viewer.close();
        cleanupUnusedLocks();
    }
}
