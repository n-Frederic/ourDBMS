package Storage.Page;

import Storage.BPlusTree.BpTree;
import Storage.Value.Value;
import Table.Table;

import java.io.*;
import java.util.*;

public class PageManager {
    private static final int MAX_PAGES = 1000;  // 最大页数
    private static ArrayList<Page> pages;       // 页的集合

    private final PageIO pageIO;          // 磁盘文件模拟存储
    private Set<Page> modifiedPages;            // 记录需要写回磁盘的页面

    Meta meta;


    public PageManager(String diskFileName) throws IOException {
        // 先初始化列表长度，全用null填满，防止set越界
        pages = new ArrayList<>(Collections.nCopies(MAX_PAGES,null));
        pageIO = new PageIO(diskFileName);
        modifiedPages = new HashSet<>();
        meta = Meta.readMetaFromDisk(pageIO.getFile());
    }

    public static ArrayList<Page> getPages() {
        return pages;
    }



    public BpTree buildTreeFromFile() throws IOException {
        BpTree tree = new BpTree();
        // PageIO pageIO = tree.pageIO;

        Stack<Page> temp = new Stack<>();

        Page rootPage= pageIO.readPage(pageIO.getRootPageId());
        temp.push(rootPage);
        tree.setRoot(rootPage);

        ArrayList<Page> leafPages = new ArrayList<>();

        while(!temp.isEmpty()) {
            Page current = temp.pop();
            int pageId = current.getPageId();
            // 一开始放进去的时候，叶子少前后，非叶子少孩子，最小值序列
            // 不少父亲，因为除了根节点，其他在栈中的节点都已经在下面被初始化好父亲了

            if(!current.isLeaf) {
                int num = current.getChildren().size();
                for(int i = 0; i < num; i++) {
                    int childId = current.children.get(i).getPageId();
                    Page child = pageIO.readPage(childId);
                    child.setParent(current);
                    current.children.set(i,child);
                    // TODO: 只处理了孩子数组，最小值序列还没处理

                    temp.push(child);
                }
            } else {
                leafPages.add(current);
                // TODO:为了构建链表，得对page的最小值进行排序
            }

            pages.set(pageId,current);
        }






//        // 1. 读取第0页获取元数据
//        byte[] zeroPageData = pageIO.readPage(0);
        // DataInputStream zeroIn = new DataInputStream(new ByteArrayInputStream(zeroPageData));
//
//        int rootPageId = zeroIn.readInt(); // 根页ID
//        int maxPageId = zeroIn.readInt();  // 最大页ID
//        int maxKeys = zeroIn.readInt();    // 最大键数


//        // 3. 递归构建树结构
//        buildTreeRecursively(tree, tree.root);
//
//        // 4. 设置叶子节点链表
//        buildLeafLinkedList(tree);

        return tree;
    }


    /**
     * 先试图从内存中获取页，如果没有，则从磁盘中加载对应页
     * 不包括第0页
     */
    // 获取页
    public Page getPage(int pageId) throws IOException {
        Page page = pages.get(pageId);
        if(page!=null){
            return page;
        }

        page = pageIO.readPage(pageId);

        pages.set(pageId, page);
        return page;
    }

    /**
     * 从内存中获取一个为空的页码创建page
     */
    public static Page createPage() {
        for (int i = 1; i < MAX_PAGES; i++) {
            if (pages.get(i) == null) {
                Page newPage = new Page(i);
                pages.set(i, newPage);
                return newPage;
            }
        }
        return null;
    }

    /**
     * 将页数据保存到磁盘
     */
    public void savePageToDisk(Page page) throws IOException {
        pageIO.writePage(page);
        modifiedPages.remove(page);  // 保存到磁盘后，移除改动记录
    }


    /**
     * 刷新所有已修改的页面到磁盘
     */
    public void flushModifiedPages() throws IOException {
        for (Page page : modifiedPages) {
            savePageToDisk(page);
        }
        modifiedPages.clear();
    }

    /**
     * 标记一个页面已被修改，需要写回磁盘
     */
    public void markPageModified(Page page) {
        modifiedPages.add(page);
    }

    /**
     * 关闭磁盘文件
     */
    public void close() throws IOException {
        flushModifiedPages();
        pageIO.getFile().close();
    }

    /**
     * 根据主键找到页号
     */
    public int findPageNum(Value value) throws IOException {
        int pageNum = -1; // 初始假设未找到页号

        // 从文件中获取第0页，作为根节点
        Page rootPage = pageIO.readPage(meta.getRootPageId());

        // 递归查找页号
        pageNum = findPageNumHelper(rootPage, value);

        return pageNum;
    }

    /**
     * 递归方法，根据给定的值遍历树结构，找到对应的页号
     */
    private int findPageNumHelper(Page currentPage, Value keyValue) throws IOException {
        // 如果当前页是叶子页，直接返回
        if (currentPage.isLeaf()) {
            List<Tuple> tuples = currentPage.getTuples();
            for (Tuple tuple : tuples) {
                if (tuple.getPrimaryV().compare(keyValue) == 0) {
                    return currentPage.getPageId();
                }
            }
        } else {
            ArrayList<Value> entries = currentPage.getEntries();
            for (int i = 0; i < entries.size(); i++) {              // 要求叶子节点的最小值序列是递增的
                Value minKey = entries.get(i);
                if (keyValue.compare(minKey) > 0) {
                    int childPageId = currentPage.getChildren().get(i).getPageId();
                    Page childPage = pageIO.readPage(childPageId);
                    return findPageNumHelper(childPage, keyValue);
                }
            }
        }
        return -1; // 如果没找到
    }



}

