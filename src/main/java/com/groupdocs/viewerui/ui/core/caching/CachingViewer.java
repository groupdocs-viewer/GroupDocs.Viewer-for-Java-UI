package com.groupdocs.viewerui.ui.core.caching;

import com.groupdocs.viewerui.ui.api.cache.FileCache;
import com.groupdocs.viewerui.ui.core.IViewer;
import com.groupdocs.viewerui.ui.core.entities.*;
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

    public String getThumbExtension() {
        return _viewer.getThumbExtension();
    }

    public Page createPage(int pageNumber, byte[] data) {
        return _viewer.createPage(pageNumber, data);
    }

    public Thumb createThumb(int pageNumber, byte[] data) {
        return _viewer.createThumb(pageNumber, data);
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

                    bytes[0] = page.getPageData();
                    _fileCache.set(cacheKey, fileCredentials.getFilePath(), bytes[0]);
                }
            });
        }

        Page page = createPage(pageNumber, bytes[0]);
        return page;
    }

    public Thumb getThumb(FileCredentials fileCredentials, int pageNumber) {
        String cacheKey = CacheKeys.getThumbCacheKey(pageNumber, getThumbExtension());
        final byte[][] bytes = {_fileCache.get(cacheKey, fileCredentials.getFilePath(), byte[].class)};
        if (bytes[0] == null) {
            synchronizedBlock(cacheKey, () -> {
                bytes[0] = _fileCache.get(cacheKey, fileCredentials.getFilePath(), byte[].class);
                // If still does not exist
                if (bytes[0] == null) {
                    Thumb thumb = _viewer.getThumb(fileCredentials, pageNumber);

                    bytes[0] = thumb.getThumbData();
                    _fileCache.set(cacheKey, fileCredentials.getFilePath(), bytes[0]);
                }
            });
        }
        Thumb thumb = createThumb(pageNumber, bytes[0]);
        return thumb;
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
        int[] missingPageNumbers = getMissingPageNumbersPage(pagesOrNulls.stream());

        if (missingPageNumbers.length == 0) {
            return toPages(pagesOrNulls.stream());
        }

        List<Page> createdPages = createPages(fileCredentials, missingPageNumbers);

        List<Page> pages = combinePage(pagesOrNulls.stream(), createdPages);

        return pages;
    }

    private List<Page> createPages(FileCredentials fileCredentials, int[] pageNumbers) {
        List<Page>[] pages = new List[1];
        final String filePath = fileCredentials.getFilePath();
        synchronizedBlock(filePath, () -> {
            List<CachedPage> pagesOrNulls = getPagesOrNullsFromCache(filePath, pageNumbers);
            int[] missingPageNumbers = getMissingPageNumbersPage(pagesOrNulls.stream());

            if (missingPageNumbers.length == 0) {
                pages[0] = toPages(pagesOrNulls.stream());
                return;
            }

            List<Page> createdPages = _viewer.getPages(fileCredentials, missingPageNumbers);

            saveToCachePage(filePath, createdPages.stream());

            pages[0] = combinePage(pagesOrNulls.stream(), createdPages);
        });
        return pages[0];
    }

    private List<Page> combinePage(Stream<CachedPage> dst, List<Page> missing) {
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

    private List<Thumb> combineThumb(Stream<CachedThumb> dst, List<Thumb> missing) {
        return dst
                .map(pageOrNull -> {
                            Thumb result = null;
                            if (pageOrNull.getData() == null) {
                                final Optional<Thumb> optionalPage = missing.stream().filter(page -> page.getPageNumber() == pageOrNull.getPageNumber())
                                        .findFirst();
                                result = optionalPage.orElse(null);
                            } else {
                                result = createThumb(pageOrNull.getPageNumber(), pageOrNull.getData());
                            }
                            return result;
                        }
                ).collect(Collectors.toList());
    }

    private void saveToCachePage(String filePath, Stream<Page> createdPages) {
        createdPages
                .forEach(page ->
                {
                    String cacheKey = CacheKeys.getPageCacheKey(page.getPageNumber(), _viewer.getPageExtension());

                    _fileCache.set(cacheKey, filePath, page.getPageData());
                    /*List<String> saveResourcesTask = */
                    saveResources(filePath, page.getPageNumber(), page.getResources().stream());

                });
    }

    private void saveToCacheThumb(String filePath, Stream<Thumb> createdThumbs) {
        createdThumbs
                .forEach(page ->
                {
                    String cacheKey = CacheKeys.getThumbCacheKey(page.getPageNumber(), _viewer.getThumbExtension());
                    _fileCache.set(cacheKey, filePath, page.getThumbData());
                });
    }

    @Override
    public List<Thumb> getThumbs(FileCredentials fileCredentials, int[] pageNumbers) {
        List<CachedThumb> pagesOrNulls = getThumbsOrNullsFromCache(fileCredentials.getFilePath(), pageNumbers);
        int[] missingPageNumbers = getMissingPageNumbersThumb(pagesOrNulls.stream());

        if (missingPageNumbers.length == 0) {
            return toThumbs(pagesOrNulls);
        }

        List<Thumb> createdPages = createThumbs(fileCredentials, missingPageNumbers);

        List<Thumb> thumbs = combineThumb(pagesOrNulls.stream(), createdPages);

        return thumbs;
    }

    private List<Page> toPages(Stream<CachedPage> pagesOrNulls) {
        return pagesOrNulls
                .map(p -> createPage(p.getPageNumber(), p.getData()))
                .collect(Collectors.toList());
    }

    private List<Thumb> toThumbs(List<CachedThumb> thumbsOrNulls) {
        List<Thumb> thumbs = thumbsOrNulls.stream()
                .map(t -> createThumb(t.getPageNumber(), t.getData()))
                .collect(Collectors.toList());

        return thumbs;
    }

    private List<Thumb> createThumbs(FileCredentials fileCredentials, int[] pageNumbers) {
        List<Thumb>[] thumbs = new List[1];
        final String filePath = fileCredentials.getFilePath();
        synchronizedBlock(filePath, () -> {
            List<CachedThumb> pagesOrNulls = getThumbsOrNullsFromCache(filePath, pageNumbers);
            int[] missingPageNumbers = getMissingPageNumbersThumb(pagesOrNulls.stream());

            if (missingPageNumbers.length == 0) {
                thumbs[0] = toThumbs(pagesOrNulls);
                return;
            }

            List<Thumb> createdPages = _viewer.getThumbs(fileCredentials, missingPageNumbers);

            saveToCacheThumb(filePath, createdPages.stream());

            thumbs[0] = combineThumb(pagesOrNulls.stream(), createdPages);
        });
        return thumbs[0];
    }

    private int[] getMissingPageNumbersPage(Stream<CachedPage> pagesOrNulls) {
        return pagesOrNulls
                .filter(p -> p.getData() == null)
                .mapToInt(CachedPage::getPageNumber)
                .toArray();
    }

    private int[] getMissingPageNumbersThumb(Stream<CachedThumb> thumbsOrNulls) {
        return thumbsOrNulls
                .filter(p -> p.getData() == null)
                .mapToInt(CachedThumb::getPageNumber)
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
        final Object lock = new Object();
        final Object actualLock = _asyncLock.computeIfAbsent(filename, k -> new WeakReference<>(lock)).get(); // Retrieve the actual lock object
        return actualLock == null ? lock : actualLock;
    }

    private List<CachedThumb> getThumbsOrNullsFromCache(String filePath, int[] pageNumbers) {
        return Arrays.stream(pageNumbers)
                .mapToObj(pageNumber -> {
                    final String cacheKey = CacheKeys.getThumbCacheKey(pageNumber, getThumbExtension());
                    final byte[] data = _fileCache.get(cacheKey, filePath, byte[].class);
                    return new CachedThumb(pageNumber, data);
                }).collect(Collectors.toList());
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
