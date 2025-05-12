package Storage.BPlusTree;

import Storage.Page.Page;
import Storage.Page.PageIO;
import Storage.Page.PageManager;
import Storage.Page.Tuple;
import Table.Schema;

import java.io.IOException;

/**
 * B+树
 * @author zhangtianlong
 */
public class BpTree {

    Page head;
    Page root;

    private PageIO pageIO;

    private Schema schema;

    public BpTree() {

    }

    public BpTree(String filePath) throws IOException {

    }

    public Page getHead() {
        return head;
    }

    public void setHead(Page head) {
        this.head = head;
    }

    public Page getRoot() {
        return root;
    }

    public void setRoot(Page root) {
        this.root = root;
    }

    public void remove(PageManager manager, Tuple tuple) throws IOException {
        manager.remove(head, tuple, this);
    }
}
