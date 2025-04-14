package Storage.Page;

import Storage.Page.Page;
import Table.Schema;
import Table.SchemaManager;

import java.io.*;
import java.util.*;

public class PageManager {
    private static final int MAX_PAGES = 1000;  // 最大页数
    private Map<Integer, Page> pages;           // 页的集合
    private RandomAccessFile diskFile;          // 磁盘文件模拟存储

    public PageManager(String diskFileName) throws IOException {
        pages = new HashMap<>();
        diskFile = new RandomAccessFile(diskFileName, "rw");
    }

    // 获取页
    public Page getPage(int pageId) throws IOException {
        if (pages.containsKey(pageId)) {
            return pages.get(pageId);
        }

        // 如果页在内存中没有，则从磁盘加载
        byte[] pageData = loadPageFromDisk(pageId);
        Page page = Page.fromBytes(pageData, SchemaManager.getInstance().get("user"));
        pages.put(pageId, page);
        return page;
    }

    // 创建新页
    public Page createPage(Schema schema) throws IOException {
        if (pages.size() >= MAX_PAGES) {
            throw new RuntimeException("页面数已达最大值");
        }

        int newPageId = pages.size() + 1;
        Page newPage = new Page(newPageId, schema);
        pages.put(newPageId, newPage);
        return newPage;
    }

    // 将页数据保存到磁盘
    public void savePageToDisk(Page page) throws IOException {
        byte[] pageData = page.toBytes();
        diskFile.seek(page.getPageId() * Page.PAGE_SIZE);
        diskFile.write(pageData);
    }

    // 从磁盘读取页
    private byte[] loadPageFromDisk(int pageId) throws IOException {
        diskFile.seek(pageId * Page.PAGE_SIZE);
        byte[] pageData = new byte[Page.PAGE_SIZE];
        diskFile.read(pageData);
        return pageData;
    }

    // 关闭磁盘文件
    public void close() throws IOException {
        diskFile.close();
    }
}

