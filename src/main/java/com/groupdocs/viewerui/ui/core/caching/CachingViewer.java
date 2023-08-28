package com.groupdocs.viewerui.ui.core.caching;

import com.groupdocs.viewerui.ui.api.cache.IFileCache;
import com.groupdocs.viewerui.ui.core.IViewer;
import com.groupdocs.viewerui.ui.core.entities.DocumentInfo;
import com.groupdocs.viewerui.ui.core.entities.FileCredentials;
import com.groupdocs.viewerui.ui.core.entities.Page;
import com.groupdocs.viewerui.ui.core.entities.PageResource;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CachingViewer implements IViewer {
    private static final Map<String, Object> _fileLocks = new WeakHashMap<>();
    private final IViewer _viewer;
    private final IFileCache _fileCache;

    public CachingViewer(IViewer viewer, IFileCache fileCache) {
        _viewer = viewer;
        _fileCache = fileCache;
    }

    private static void synchronizedBlock(String fileName, Runnable synchronizedBlock) {
        synchronized (getFileLock(fileName)) {
            synchronizedBlock.run();
        }
    }

    private static synchronized Object getFileLock(String filename) {
        // Get or create a lock object for the filename
        return _fileLocks.computeIfAbsent(filename, k -> new Object());
    }

    public String getPageExtension() {
        return _viewer.getPageExtension();
    }

    public Page createPage(int pageNumber, byte[] data) {
        return _viewer.createPage(pageNumber, data);
    }

    public Page getPage(FileCredentials fileCredentials, int pageNumber) {
        String cacheKey = CacheKeys.getPageCacheKey(pageNumber, getPageExtension());
        final byte[][] bytes = {_fileCache.get(cacheKey, fileCredentials.getFilePath())};
        if (bytes[0] == null) {
            synchronizedBlock(cacheKey, () -> {
                bytes[0] = _fileCache.get(cacheKey, fileCredentials.getFilePath());
                // If still does not exist
                if (bytes[0] == null) {
                    Page page = _viewer.getPage(fileCredentials, pageNumber);

                    saveResources(fileCredentials.getFilePath(), page.getPageNumber(), page.getResources().stream());

                    bytes[0] = page.getData();
                }
            });
        }

        Page page = createPage(pageNumber, bytes[0]);
        return page;
    }

    public DocumentInfo getDocumentInfo(FileCredentials fileCredentials) {
        String cacheKey = CacheKeys.FILE_INFO_CACHE_KEY;
        DocumentInfo[] documentInfo = {_fileCache.get(cacheKey, fileCredentials.getFilePath())};
        if (documentInfo[0] == null) {
            synchronizedBlock(fileCredentials.getFilePath(), () ->
            {
                documentInfo[0] = _fileCache.get(cacheKey, fileCredentials.getFilePath());
                if (documentInfo[0] == null) {
                    documentInfo[0] = _viewer.getDocumentInfo(fileCredentials);
                }

            });
        }
        return documentInfo[0];
    }

    public byte[] getPdf(FileCredentials fileCredentials) {
        String cacheKey = CacheKeys.PDF_FILE_CACHE_KEY;
        final byte[][] bytes = {_fileCache.get(cacheKey, fileCredentials.getFilePath())};
        if (bytes[0] == null) {
            synchronizedBlock(fileCredentials.getFilePath(), () -> {
                bytes[0] = _fileCache.get(cacheKey, fileCredentials.getFilePath());
                if (bytes[0] == null) {
                    bytes[0] = _viewer.getPdf(fileCredentials);
                }
            });
        }
        return bytes[0];
    }

    public byte[] getPageResource(FileCredentials fileCredentials, int pageNumber, String resourceName) {
        String cacheKey = CacheKeys.getHtmlPageResourceCacheKey(pageNumber, resourceName);
        final byte[][] bytes = {_fileCache.get(cacheKey, fileCredentials.getFilePath())};

        if (bytes[0] == null) {
            synchronizedBlock(fileCredentials.getFilePath(), () -> {
                bytes[0] = _fileCache.get(cacheKey, fileCredentials.getFilePath());
                if (bytes[0] == null) {
                    bytes[0] = _viewer.getPageResource(fileCredentials, pageNumber, resourceName);
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
        synchronizedBlock(fileCredentials.getFilePath(), () -> {
            List<CachedPage> pagesOrNulls = getPagesOrNullsFromCache(fileCredentials.getFilePath(), pageNumbers);
            int[] missingPageNumbers = getMissingPageNumbers(pagesOrNulls.stream());

            if (missingPageNumbers.length == 0) {
                pages[0] = toPages(pagesOrNulls.stream());
                return;
            }

            List<Page> createdPages = _viewer.getPages(fileCredentials, missingPageNumbers);

            saveToCache(fileCredentials.getFilePath(), createdPages.stream());

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
                    return new CachedPage(pageNumber, _fileCache.get(pageCacheKey, filePath));
                }).collect(Collectors.toList());
    }

    @Override
    public void close() {
//        _viewer.close();
    }
}
