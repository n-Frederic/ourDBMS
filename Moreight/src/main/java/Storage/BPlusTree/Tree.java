package Storage.BPlusTree;

import Storage.Page.Tuple;

/**
 * Tree
 * @author zhangtianlong
 */
public interface Tree {

    Tuple find(Tuple key);

    boolean remove(Tuple key);

    void insert(Tuple key);
}
