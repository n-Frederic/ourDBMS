package Storage.BPlusTree;

import Storage.Page.PageIO;
import Storage.Page.Tuple;
import Table.Schema;

import java.io.IOException;

/**
 * B+树
 * @author zhangtianlong
 */
public class BpTree {

    BpNode head;
    BpNode root;
    private final String filePath;
    private final PageIO pageIO;
    private int rootPageId;
    private Schema schema;

    public BpTree(String filePath) throws IOException {
        this.filePath = filePath;
        this.pageIO = new PageIO(filePath);
        this.rootPageId = pageIO.getRootPageId();
    }

    public BpNode getHead() {
        return head;
    }
}
