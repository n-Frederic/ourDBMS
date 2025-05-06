package Storage.Page;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class PageIO {
    private RandomAccessFile file;

    public PageIO(String filePath) throws IOException {
        this.file = new RandomAccessFile(filePath, "rw");
    }

    public Page readPage(int pageId) throws IOException {
        file.seek((long) pageId * Page.PAGE_SIZE);
        byte[] data = new byte[Page.PAGE_SIZE];
        file.readFully(data);
        return Page.fromBytes(data);
    }

    public void writePage(int pageId, Page page) throws IOException {
        byte[] data = page.toBytes();
        file.seek(pageId * Page.PAGE_SIZE);
        file.write(data);
    }

    public int getRootPageId() throws IOException {
        file.seek(0); // file header
        return file.readInt();  // 假设前4字节是 rootPageId
    }

    public void setRootPageId(int rootId) throws IOException {
        file.seek(0);
        file.writeInt(rootId);
    }

    public int allocateNewPage() throws IOException {
        long length = file.length();
        int newPageId = (int)(length / Page.PAGE_SIZE);
        file.setLength(length + Page.PAGE_SIZE);
        return newPageId;
    }
}
