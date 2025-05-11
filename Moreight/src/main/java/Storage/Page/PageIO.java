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
    Meta meta;


    public PageIO(String filePath) throws IOException {
        this.file = new RandomAccessFile(filePath, "rw");
        meta = Meta.readMetaFromDisk(file);
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public RandomAccessFile getFile() {
        return file;
    }


    /**
     * 通过id读取page字节数组
     */
    public byte[] loadPageFromDisk(int pageId) throws IOException {
        file.seek((long) pageId * Page.PAGE_SIZE);
        byte[] pageData = new byte[Page.PAGE_SIZE];
        file.readFully(pageData);
        return pageData;
    }

    /**
     * 把pageId对应的页反序列化为page （不包括第0页）
     */
    public  Page readPage(int pageId) throws IOException {
        byte[] data = loadPageFromDisk(pageId);

        boolean isLeaf = readIsLeafFlag(data);
        System.out.println(isLeaf);
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
        if(page.getPageId() > meta.getHighestPageId()) {
            int id = allocateNewPage();
            page.setPageId(id);
        }

        byte[] data;
        if(page.isLeaf && !page.tuples.isEmpty()) {
            data = page.leafToBytes();
        } else if(!page.isLeaf && !page.entries.isEmpty()){
            data = page.InnerToBytes();
        } else {
            data = new byte[Page.PAGE_SIZE];
        }

        file.seek((long) page.getPageId() * Page.PAGE_SIZE);
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
}
