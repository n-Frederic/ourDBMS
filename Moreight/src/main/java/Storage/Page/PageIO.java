package Storage.Page;
import Storage.Value.*;
import java.util.List;
import java.util.ArrayList;
import Storage.BPlusTree.BpTree;

import java.io.IOException;
import java.io.RandomAccessFile;


public class PageIO {
    private final RandomAccessFile file;
    private static final String DIRECTORY = "../TestData/DatabaseManager";
    public static final int PAGE_SIZE = 8 * 1024; // 8KB
    Meta meta;


    public PageIO(String filePath) throws IOException {
        this.file = new RandomAccessFile(filePath, "rw");
    }

    public RandomAccessFile getFile() {
        return file;
    }



    /**
     * 读序号为pageId的字节数组并反序列化为page （不包括第0页）
     */
    public  Page readPage(int pageId) throws IOException {
        file.seek((long) pageId * Page.PAGE_SIZE);
        byte[] data = new byte[Page.PAGE_SIZE];
        file.readFully(data);

        boolean isLeaf = readIsLeafFlag(data);
        if(isLeaf) {
            return Page.leafFromBytes(data);
        } else {
            return Page.InnerFromBytes(data);
        }
    }

    /**
     * 把已经构建好的page序列化进id所在的地方 （不包括第0页）
     * 分为新页，旧页
     * 由于开创新页的时候本没有必要初始化id，所以用allocate自动分配id
     * 旧页直接按照id写回
     */
    public void writePage(Page page) throws IOException {
        if(page.PageId > meta.getHighestPageId()) {
            int id = allocateNewPage();
            page.setPageId(id);
        }

        byte[] data;
        if(page.isLeaf) {
            data = page.leafToBytes();
        } else data = page.InnerToBytes();

        file.seek((long) page.PageId * Page.PAGE_SIZE);
        file.write(data);
    }


    /**
     * 获取 第0页的根页页码
     */
    public int getRootPageId() throws IOException {
        file.seek(0); // file header
        return file.readInt();
    }

    /**
     * 确定是否为叶子节点
     */
    public static boolean readIsLeafFlag(byte[] pageData) {
        // 第五个字节的索引是4（因为从0开始计数）
        byte isLeafByte = pageData[4];

        // 布尔值在Java中存储为1(true)或0(false)
        return isLeafByte != 0;
    }


    public  BpTree buildTreeFromFile() throws IOException {
        BpTree tree = new BpTree();
       // PageIO pageIO = tree.pageIO;

        Page rootPage=this.readPage(getRootPageId());
        tree.setRoot(rootPage);




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


    public void setRootPageId(int rootId) throws IOException {
        file.seek(0);
        file.writeInt(rootId);
    }

    /**
     * 分配新页
     * 更新最高页码
     */

    public int allocateNewPage() throws IOException {
        meta.setHighestPageId(meta.getHighestPageId()+1);  // 增加最高页码
        meta.updateHighestPageId(file);  // 将新的 highestPageId 写入文件
        long length = file.length();
        int newPageId = meta.getHighestPageId();
        file.setLength(length + Page.PAGE_SIZE);
        return newPageId;
    }

    /**
     * 根据主键找到页号
     */
    public int findPageNum(Tuple tuple) throws IOException {
        int pageNum = -1; // 初始假设未找到页号
        Value value = tuple.getPrimaryV(); // 获取主键值

        // 从文件中获取第0页，作为根节点
        Page rootPage = readPage(meta.getRootPageId());

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
                    // 找到符合条件的子节点，递归查找
                    int childPageId = getChildPageId(currentPage, i);
                    Page childPage = readPage(childPageId);
                    return findPageNumHelper(childPage, keyValue);
                }
            }
        }
        return -1; // 如果没找到
    }

    // 从当前页获取子节点的页号
    private int getChildPageId(Page currentPage, int index) {
        return currentPage.getChildren().get(index).getPageId(); // 假设我们有子节点

    }


    public void insert(byte[] record) throws IOException {
        // 读第0页的元信息
        file.seek(0);
        int rootPageId = file.readInt();
        int highestPageId = file.readInt();
        int maxkeys = file.readInt();

        // 空树的情况
        if (rootPageId == 0) {
            int newId = highestPageId + 1;
            Page newRootPage = PageManager.createPage();  // 创建一个新的页
            newRootPage.setPageId(newId);

            Tuple newTuple = new Tuple();
        }
    }
}
