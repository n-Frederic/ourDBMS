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
    private ArrayList<Page> modifiedPages;            // 记录需要写回磁盘的页面



    public static ArrayList<Page> sortPagesByMinValue(ArrayList<Page> leafPages) {
        // 创建列表副本以避免修改原列表
        ArrayList<Page> sorted = new ArrayList<>(leafPages);

        // 使用自定义比较器按 minValue 排序
        Collections.sort(sorted, new Comparator<Page>() {
            @Override
            public int compare(Page p1, Page p2) {
               if(p1 == null) return -1;
               else if(p2 == null) return 1;
               else return p1.minValue.compare(p2.minValue);
            }
        });

        return sorted;
    }


    public PageManager(String diskFileName) throws IOException {
        // 先初始化列表长度，全用null填满，防止set越界
        pages = new ArrayList<>(Collections.nCopies(MAX_PAGES, null));
        pageIO = new PageIO(diskFileName);
        modifiedPages = new ArrayList<>();
    }

    public static ArrayList<Page> getPages() {
        return pages;
    }

    public Meta getMeta() {
        return pageIO.meta;
    }

    public PageIO getPageIO() {
        return pageIO;
    }

    public BpTree buildTreeFromFile() throws IOException {
        BpTree tree = new BpTree();
        // PageIO pageIO = tree.pageIO;

        Stack<Page> temp = new Stack<>();

        Page rootPage = pageIO.readPage(pageIO.getRootPageId());
//        System.out.println("ididid"+pageIO.getRootPageId());
        temp.push(rootPage);
        tree.setRoot(rootPage);

        ArrayList<Page> leafPages = new ArrayList<>();

        while (!temp.isEmpty()) {
            Page current = temp.pop();
            int pageId = current.getPageId();
            // 一开始放进去的时候，叶子少前后，非叶子少孩子，最小值序列
            // 不少父亲，因为除了根节点，其他在栈中的节点都已经在下面被初始化好父亲了


            if (!current.isLeaf) {
                int num = current.getChildren().size();
                for (int i = 0; i < num; i++) {
                    int childId = current.children.get(i).getPageId();
                    Page child = pageIO.readPage(childId);
                    child.setParent(current);
                    current.children.set(i, child);

                    Value id = current.children.get(i).getMinValue();
                    current.entries.add(id);
                    temp.push(child);
                    current.entries = Value.sortValues(current.entries);

                }
            } else {
                leafPages.add(current);
            }


            pages.set(pageId, current);
        }

        ArrayList<Page> sortedPage = PageManager.sortPagesByMinValue(leafPages);
        pages = PageManager.sortPagesByMinValue(pages);



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
        if (page != null) {
            return page;
        }

        page = pageIO.readPage(pageId);

        pages.set(pageId, page);
        return page;
    }

    /**
     * 从内存中获取一个为空的页码创建page
     */
    public Page createPage(boolean isLeaf) {
        for (int i = 1; i < MAX_PAGES; i++) {
            if (pages.get(i) == null) {
                Page newPage = new Page(i, isLeaf);

                updatePageToManager(newPage);
                if (i > pageIO.meta.getHighestPageId()) {
                    pageIO.meta.setHighestPageId(i);
                }
                return newPage;
            }
        }
        return null;
    }

    public Page createPage(boolean isLeaf, boolean isRoot) {
        for (int i = 1; i < MAX_PAGES; i++) {
            if (pages.get(i) == null) {
                Page newPage = new Page(i, isLeaf,isRoot);

                updatePageToManager(newPage);
                if (i > pageIO.meta.getHighestPageId()) {
                    pageIO.meta.setHighestPageId(i);
                }
                return newPage;
            }
        }
        return null;
    }


    // 更新指定页面的 minValue
    public void updateMinValue(Page page) {
        if (page.isLeaf && !page.tuples.isEmpty()) {
            page.minValue = page.tuples.get(0).getPrimaryV();
        }
    }

    public void updatePageToManager(Page page) {
        if((page.isLeaf && !page.tuples.isEmpty()) || (!page.isLeaf && !page.entries.isEmpty())) {
            pages.set(page.getPageId(),page);
        } else pages.set(page.getPageId(),null);

        modifiedPages.add(page);
    }

    /**
     * 将页数据保存到磁盘
     */
    public void savePageToDisk(Page page) throws IOException {
        pageIO.writePage(page);
        System.out.println("writePage调用了");
    }


    /**
     * 刷新所有已修改的页面到磁盘
     */
    public void flushModifiedPages() throws IOException {
        Iterator<Page> iterator = modifiedPages.iterator();

        while(iterator.hasNext()) {
            savePageToDisk(iterator.next());
            iterator.remove();
        }

        modifiedPages.clear();
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
        Page rootPage = pageIO.readPage(pageIO.meta.getRootPageId());

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

    /**
     * 在树中插入一个节点
     * @param key 待插入的行
     * @param tree B+树
     */

    public void insert(Page page, Tuple key, BpTree tree) throws IOException {
        if (page.isLeaf) {
            if (!isLeafToSplit(page)) {
                System.out.println("直接插入叶节点");
                insertInLeaf(page, key);
                updatePageToManager(page);
            } else {
                //需要分裂为左右两个节点
                Page left = this.createPage(true);
                Page right = this.createPage(true);
                // 初次更新left,right到manager
                if (page.previous != null) {
                    left.previous = page.previous;
                    page.previous.next = left;
                } else {
                    tree.setHead(left);
                }
                if (page.next != null) {
                    right.next = page.next;
                    page.next.previous = right;
                }
                left.next = right;
                right.previous = left;
                // for GC
                page.previous = null;
                page.next = null;

                // 插入后再分裂
                insertInLeaf(page,key);


                int leftSize = getUpper(page.tuples.size(), 2);
                int rightSize = page.tuples.size() - leftSize;
//                System.out.printf("leaf key left:%d  right:%d\n", leftSize, rightSize);
                // 左右节点拷贝
                for (int i = 0; i < leftSize; i++) {
                    left.tuples.add(page.tuples.get(i));
                }
                for (int i = 0; i < rightSize; i++) {
                    right.tuples.add(page.tuples.get(leftSize + i));
                }
                // 不是根节点
                if (!page.isRoot) {
                    // 调整父子节点关系
                    // 寻找当前节点在父节点的位置
                    System.out.println("parent children is null:" + (page.parent.children == null));

                    int index = page.parent.children.indexOf(page);
//                    System.out.println("parent children size:" + parent.children.size());
//                    System.out.println("index:" + index);

                    // 删除当前指针
                    page.parent.children.remove(page);
                    left.setParent(page.parent);
                    right.setParent(page.parent);
                    // 将分裂后节点的指针添加到父节点
                    page.parent.children.add(index, left);
                    page.parent.children.add(index + 1, right);
                    // for GC
                    page.tuples = null;
                    page.children = null;

                    // 父节点[非叶子节点]中插入关键字，是右边的第一位
                    insertInParent(page.parent,right.tuples.getFirst().getPrimaryV());

                    updatePageToManager(left);
                    updatePageToManager(right);
                    updatePageToManager(left.previous);
                    updatePageToManager(right.next);
                    updatePageToManager(page);

                    System.out.println("父节点插入key");
                    updateNode(page.parent,tree);
                    // for GC
                    page.parent = null;
                } else {
                    // 是根节点
                    System.out.println("生成新的根节点");
                    page.isRoot = false;
                    Page rootPage = this.createPage(false, true);


                    tree.setRoot(rootPage);
                    left.parent = rootPage;
                    right.parent = rootPage;

                    updatePageToManager(left);
                    updatePageToManager(right);

                    rootPage.children.add(left);
                    rootPage.children.add(right);
                    // for GC
                    page.tuples = null;
                    page.children = null;
                    // 根节点插入关键字
                    insertInParent(rootPage,left.tuples.getFirst().getPrimaryV());
                    insertInParent(rootPage,right.tuples.getFirst().getPrimaryV());

                    updatePageToManager(rootPage);
                }
            }
        } else {
            Value keyValue = key.getPrimaryV();
            int left = 0, right = page.entries.size() - 1;

            while (left <= right) {
                int mid = left + (right - left) / 2;
                if (keyValue.compare(page.entries.get(mid)) < 0) {
                    right = mid - 1;
                } else {
                    left = mid + 1;
                }
            }
            insert(page.children.get(left),key, tree);
        }

        flushModifiedPages();
        System.out.println("插入完成，并刷新了内存");
    }

    public boolean remove(Page page, Tuple key, BpTree tree) {
        boolean isFound = false;
        if (page.isLeaf) {
            // 如果是叶子节点
            if (!contains(page,key)) {
                // 不包含关键字
                return false;
            }
            // 是叶子节点且是根节点,直接删除
            if (page.isRoot) {
                if (removeInLeaf(page,key)) {
                    isFound = true;
                    updateMinValue(page);
                    updatePageToManager(page);
                }
            } else {
                if (canRemoveDirectInLeaf(page)) {
                    // 可以在叶节点中直接删除
                    if (removeInLeaf(page,key)) {
                        isFound = true;
                        updateMinValue(page);
                        updatePageToManager(page);
                    }
                } else {
                    // 如果当前关键字不够,并且前节点有足够的关键字,从前节点借
                    if (leafCanBorrow(page,page.previous)) {
                        if (removeInLeaf(page,key)) {
                            borrowLeafPrevious(page);
                            isFound = true;
                            updateMinValue(page);
                            updatePageToManager(page);
                            updatePageToManager(page.previous);
                        }
                    } else if (leafCanBorrow(page,page.next)) {
                        if (removeInLeaf(page,key)) {
                            borrowLeafNext(page);
                            isFound = true;
                            updateMinValue(page);
                            updatePageToManager(page);
                            updatePageToManager(page.next);
                        }
                        // 从后兄弟节点借
                    } else {
                        // 合并叶子节点, 先合并后删除
                        Page tmpParent = page.parent;
                        // 和前叶子节点合并
                        if (leafCanMerge(page,page.previous)) {
                            mergeToPreLeaf(page.previous, page);
                            if (removeInLeaf(page.previous,key)) {
                                isFound = true;
                            }
                            // 删除在父节点中的key
                            int parentKeyIdx = getMiddleKeyIdxInParent(page,page);
                            page.parent.entries.remove(parentKeyIdx);
                            // 删除在父节点中的指针
                            page.parent.children.remove(page);
                            // for GC
                            page.parent = null;
                            page.entries = null;
                            // 更新 叶节点链表
                            if (page.next != null) {
                                Page tmp = page;
                                tmp.previous.next = tmp.next;
                                tmp.next.previous = tmp.previous;
                                tmp.previous = null;
                                tmp.next = null;
                            } else {
                                page.previous.next = null;
                                page.previous = null;
                            }
                            // 更新前叶子节点的minValue
                            updateMinValue(page);
                            // 更新前叶子节点和当前节点
                            updatePageToManager(page);

                            // 和后叶子节点合并
                        } else if (leafCanMerge(page,page.next)) {
                            mergeToPreLeaf(page, page.next);
                            if (removeInLeaf(page,key)) {
                                isFound = true;
                            }
                            // 删除在父节点中的key
                            int parentKeyIdx = getMiddleKeyIdxInParent(page,page.next);
                            page.parent.entries.remove(parentKeyIdx);
                            // 删除在父节点中的指针
                            page.parent.children.remove(page.next);
                            // for GC
                            page.next.parent = null;
                            page.next.entries = null;
                            // 更新 叶节点链表
                            if (page.next.next != null) {
                                Page tmp = page.next;
                                page.next = tmp.next;
                                tmp.next.previous = page;
                                tmp.previous = null;
                                tmp.next = null;
                            } else {
                                page.next.previous = null;
                                page.next = null;
                            }
                            // 更新当前叶子结点的minValue
                            updateMinValue(page);
                            // 更新当前节点
                            updatePageToManager(page);
                        }
                        updateRemove(tmpParent,tree);
                    }
                }
            }
        } else {
            // 非叶子节点,继续向下搜索
            if (key.getPrimaryV().compare(page.entries.getFirst()) < 0) {
                if (remove(page.children.getFirst(), key, tree)) {
                    isFound = true;
                }
            } else if (key.getPrimaryV().compare(page.entries.getLast()) >= 0) {
                if (remove(page.children.getLast(), key, tree)) {
                    isFound = true;
                }
            } else {
                for (int i = 0; i < (page.entries.size() - 1); i++) {
                    if (key.getPrimaryV().compare(page.entries.get(i)) >= 0 && key.getPrimaryV().compare(page.entries.get(i + 1)) < 0) {
                        if (remove(page.children.get(i + 1), key, tree)) {
                            isFound = true;
                            break;
                        }
                    }
                }
            }
        }
        return isFound;
    }

    /**
     * 中间节点删除后的更新操作
     */
    private void updateRemove(Page page, BpTree tree) {
        int half = getUpper(page.maxLength, 2);
        if (page.children.size() < half || page.children.size() < 2) {
            if (page.isRoot) {
                if (page.children.size() >= 2) {
                    return;
                } else {
                    // 如果根节点只有一个指针,则删除 根节点,让其孩子节点作为根节点
                    Page rootPage = page.children.getFirst();
                    tree.setRoot(rootPage);
                    rootPage.isRoot = true;
                    rootPage.parent = null;
                    // for GC
                    page.entries = null;
                    page.children = null;
                }
            } else {
                // 计算前后兄弟节点
                int curIdx = page.parent.children.indexOf(page);
                int preIdx = curIdx - 1;
                int nextIdx = curIdx + 1;
                Page preNode = null;
                Page nextNode = null;
                if (preIdx >= 0) {
                    preNode = page.parent.children.get(preIdx);
                }
                if (nextIdx < page.parent.children.size()) {
                    nextNode = page.parent.children.get(nextIdx);
                }
                if (middleNodeCanBorrow(page,preNode)) {
                    // 从前节点借
                    borrowMiddleNodePrevious(page,preNode);
                } else if (middleNodeCanBorrow(page,nextNode)) {
                    // 从后继节点借
                    borrowMiddleNodeNext(page,nextNode);
                } else {
                    // 和兄弟节点合并
                    Page tmpParent = page.parent;
                    if (middleNodeCanMerge(page,preNode)) {
                        // 与前节点合并
                        mergeToPreMiddleNode(preNode, page);
                        int parentKeyIdx = getMiddleKeyIdxInParent(page,nextNode);
                        page.parent.entries.remove(parentKeyIdx);
                        page.parent.children.remove(parentKeyIdx + 1);
                        // for GC
                        page.parent = null;
                        page.entries = null;
                        page.children = null;
                    } else if (middleNodeCanMerge(page,nextNode)) {
                        mergeToPreMiddleNode(page, nextNode);
                        int parentKeyIdx = getMiddleKeyIdxInParent(page,nextNode);
                        page.parent.entries.remove(parentKeyIdx);
                        page.parent.children.remove(parentKeyIdx + 1);
                        // for GC
                        nextNode.parent = null;
                        nextNode.entries = null;
                        nextNode.children = null;
                    }
                    updateRemove(tmpParent,tree);
                }
            }
        }
    }

    /**
     * 从前兄弟节点中借
     */
    private void borrowMiddleNodePrevious(Page page, Page preNode) {
        /**
         *        20
         * 3  7        30
         * ---------------------
         *        7
         *    3        20   30
         */
        int parentKeyIdx = getMiddleKeyIdxInParent(preNode,page);
        // 父节点中下沉的 key
        Value downKey = page.parent.entries.get(parentKeyIdx);
        page.entries.add(0, downKey);
        // 从父节点中删除 key
        page.parent.entries.remove(parentKeyIdx);

        int preSize = preNode.entries.size();
        // 前节点中提升到父节点的key
        Value upKey = preNode.entries.get(preSize - 1);
        page.parent.entries.add(parentKeyIdx, upKey);
        // 删除提升节点
        preNode.entries.remove(preSize - 1);
        // 前节点的最后一个指针后移到当前节点
        int preChildSize = preNode.children.size();
        Page borrowPoint = preNode.children.get(preChildSize - 1);
        page.children.add(0 , borrowPoint);
        preNode.children.remove(preChildSize - 1);
        borrowPoint.parent = page;
    }

    /**
     * 从后继兄弟节点中借
     */
    private void borrowMiddleNodeNext(Page page, Page nextPage) {
        /**
         *        20
         *   7        30   40
         * ---------------------
         *            30
         *   7   20        40
         */
        int parentKeyIdx = getMiddleKeyIdxInParent(page,nextPage);
        Value downKey = page.parent.entries.get(parentKeyIdx);
        page.entries.add(downKey);
        page.parent.entries.remove(parentKeyIdx);

        Value upKey = nextPage.entries.getFirst();
        page.parent.entries.add(parentKeyIdx, upKey);
        nextPage.entries.removeFirst();
        // 后继节点的第一个指针移到当前节点最后面
        Page borrowPoint = nextPage.children.getFirst();
        page.children.add(borrowPoint);
        nextPage.children.removeFirst();
    }

    /**
     * 将后一个节点中的关键字合并到 前节点中
     */
    private void mergeToPreLeaf(Page first, Page sec) {
        first.tuples.addAll(sec.tuples);
    }

    /**
     * 将后一个中间节点的关键字和指针复制到 前一个中间节点中
     */
    private void mergeToPreMiddleNode(Page first, Page sec) {
        int parentKeyIdx = getMiddleKeyIdxInParent(first,sec);
        // 将父节点关键字下沉
        first.entries.add(first.parent.entries.get(parentKeyIdx));

        for (int i = 0; i < sec.entries.size(); i++) {
            first.entries.add(sec.entries.get(i));
        }
        // sec的指针复制
        for (int i = 0; i < sec.children.size(); i++) {
            // 变更父亲节点
            sec.children.get(i).parent = first;
            first.children.add(sec.children.get(i));
        }
    }

    /**
     * checked
     * 从前兄弟叶子节点 借
     */
    private void borrowLeafPrevious(Page page) {
        int size = page.previous.tuples.size();
        Tuple borrowedTuple = page.previous.tuples.get(size - 1);
        page.previous.tuples.remove(size - 1);
        page.tuples.add(0, borrowedTuple);
        // 更新父节点中间关键字（next 的最小值上移）
        int parentEntryIdx = getMiddleKeyIdxInParent(page,page);
        Value newSeparator = page.tuples.get(0).getPrimaryV();
        page.parent.entries.set(parentEntryIdx, newSeparator);
    }

    /**
     * checked
     * 从后兄弟叶子节点 借
     */
    private void borrowLeafNext(Page page) {
        Tuple borrowedTuple = page.next.tuples.get(0);
        page.next.tuples.remove(0);
        page.tuples.add(borrowedTuple);

        // 更新父节点中间关键字（next 的最小值上移）
        int parentEntryIdx = getMiddleKeyIdxInParent(page, page.next);
        Value newSeparator = page.next.tuples.get(0).getPrimaryV();
        page.parent.entries.set(parentEntryIdx, newSeparator);
    }

    /**
     * 获取 当前节点指针 和 前节点指针 之间的 关键字 在父节点中的位置
     */
    private int getMiddleKeyIdxInParent(Page page, Page node) {
        int index = page.parent.children.indexOf(node);
        return index - 1;
    }

    /**
     * checked
     * 兄弟叶节点是否能够借出
     */
    private boolean leafCanBorrow(Page page, Page node) {
        if (node != null && node.parent == page.parent) {
            int min = getUpper(page.maxTuples, 2);
            return node.entries.size() > min;
        }
        return false;
    }

    /**
     * 兄弟中间节点是否可以借
     */
    private boolean middleNodeCanBorrow(Page page, Page node) {
        if (node != null) {
            int min = getUpper(page.maxLength, 2);
            return node.children.size() > min && node.parent == page.parent;
        }
        return false;
    }

    /**
     * checked
     * 叶子节点是否可以合并
     */
    private boolean leafCanMerge(Page page, Page node) {
        if (node != null && page.parent == node.parent) {
            // 当前节点和目标节点合并后总的行数不超过最大容量（12）
            return (page.tuples.size() + node.tuples.size()) <= page.maxTuples;
        }
        return false;
    }

    /**
     * 中间节点是否可以合并
     */
    private boolean middleNodeCanMerge(Page page, Page node) {
        if (node != null) {
            return (page.entries.size() + node.entries.size() + 1) <= (page.maxLength - 1)
                    && page.parent == node.parent;
        }
        return false;
    }


    /**
     * 上取整
     */
    private int getUpper(int x, int y) {
        if (y == 2) {
            int remainder = x & 1;
            if (remainder == 0) {
                return x >> 1;
            } else {
                return (x >> 1) + 1;
            }
        } else {
            int remainder = x % y;
            if (remainder == 0) {
                return x / y;
            } else {
                return x / y + 1;
            }
        }
    }

    /**
     *checked maybe
     * 关键字是否可以直接在叶节点中删除
     */
    private boolean canRemoveDirectInLeaf(Page page) {
        if (page.isLeaf) {
            int maxKey = page.maxTuples;
            int remainder = maxKey % 2;
            int half;
            if (remainder == 0) {
                half = maxKey / 2;
            } else {
                half = maxKey / 2 + 1;
            }
            if ((page.tuples.size() - 1) < half) {
                return false;
            } else {
                return true;
            }
        } else {
            throw new UnsupportedOperationException("it isn't leaf node.");
        }
    }

    /**
     * checked
     * 直接在叶子节点中删除,不改变树结构
     */
    private boolean removeInLeaf(Page page, Tuple key) {
        int index = -1;
        for (int i = 0; i < page.tuples.size(); i++) {
            if (key.compare(page.tuples.get(i)) == 0) {
                index  = i;
                break;
            }
        }
        if (index != -1) {
            page.tuples.remove(index);
        }
        return index != -1;
    }

    /**
     * checked
     * 判断当前叶子节点是否包含关键字
     */
    private boolean contains(Page page, Tuple key) {
        for (Tuple tuple : page.tuples) {
            if (tuple.compare(key) == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * 非叶节点插入关键字后,检查是否需要分裂
     * CHECK MAYBE
     */
    private void updateNode(Page page, BpTree tree) {
        // 需要分裂
        if (isNodeToSplit(page)) {
            System.out.println("非叶节点插入关键字后,需要分裂");
            Page left = this.createPage(false);
            Page right = this.createPage(false);

            int pLeftSize = getUpper(page.entries.size(), 2);
            int pRightSize = page.entries.size() - pLeftSize;   //fix bug

            // 提升到父节点的关键字
            Value keyToParent = page.entries.get(pLeftSize);

            System.out.printf("middle node p left:%d  right:%d\n", pLeftSize, pRightSize);
            // 复制左边的关键字，指针
            for (int i = 0; i < pLeftSize; i++) {
                left.entries.add(page.entries.get(i));
                left.children.add(page.children.get(i));
                left.children.get(i).setParent(left);
            }

            // 复制右边的关键字+指针
            for (int i = 0; i < pRightSize; i++) {
                right.entries.add(page.entries.get(pLeftSize + i));
                right.children.add(page.children.get(pLeftSize + i));
                right.children.get(i).setParent(right);     // fix index bug
            }

            if (!page.isRoot) {
                System.out.println("current is root:" + false);
                System.out.println("非叶节点的父节点插入key");
                int index = page.parent.children.indexOf(page);
                page.parent.children.remove(index);
                left.parent = page.parent;
                right.parent = page.parent;
                page.parent.children.add(index, left);
                page.parent.children.add(index + 1, right);
                // 插入关键字
//                parent.insertInParent(keyToParent);
                page.parent.entries.add(index, keyToParent);

                updatePageToManager(left);
                updatePageToManager(right);

                updateNode(page.parent, tree);

                page.entries.clear();
                page.children.clear();
                page.entries = null;
                page.children = null;
                page.parent = null;

                updatePageToManager(page);
            } else {
                // 是根节点
                System.out.println("current is root:" + true);
                System.out.println("parent null:" + (page.parent == null));
                page.isRoot = false;
                Page rootPage = this.createPage(false, true);
                tree.setRoot(rootPage);
                left.parent = rootPage;
                right.parent = rootPage;
                rootPage.children.add(left);
                rootPage.children.add(right);
                page.children.clear();
                page.entries.clear();
                page.children = null;
                page.entries = null;
                // 插入关键字
//                rootPage.insertInParent(keyToParent);
                rootPage.entries.add(left.entries.getFirst());
                rootPage.entries.add(keyToParent);

                updatePageToManager(left);
                updatePageToManager(right);
                updatePageToManager(page);
                updatePageToManager(rootPage);
            }


        }
    }

    /**
     * 叶子节点是否需要分裂,
     * 用于插入前进行判断
     * CHECK
     */
    private boolean isLeafToSplit(Page page) {
        if (page.isLeaf) {
            return page.tuples.size() > (page.maxTuples - 1);
        } else {
            throw new UnsupportedOperationException("the node is not leaf.");
        }
    }

    /**
     * 中间节点是否需要分裂,
     * 已经插入指针和关键字
     * CHECK
     */
    private boolean isNodeToSplit(Page page) {
        // 由于是先插入关键字,所以不需要[=]
        if (page.isLeaf) {
            throw new UnsupportedOperationException("error access to leaf");
        }
        return page.entries.size() > page.maxLength;
    }


    /**
     * 插入到当前叶子节点中,不分裂
     * 保持有序插入
     * CHECK
     */
    private void insertInLeaf(Page page, Tuple tuple) {
        if (!page.isLeaf) {
            throw new UnsupportedOperationException("can't insert into middle node.");
        }

        // 插入tuples的适当位置，保持有序
        for (int i = 0; i < page.tuples.size(); i++) {
            if (page.tuples.get(i).compare(tuple) > 0) {
                page.tuples.add(i, tuple);
                return;
            }
        }

        page.tuples.add(tuple); // 插入到末尾
    }

    /**
     * 插入到非叶子节点中,不分裂
     * CHECK
     */
    private void insertInParent(Page page, Value key){
        if (page.isLeaf) {
            throw new UnsupportedOperationException("can't insert into leaf node.");
        }

        for (int i = 0; i < page.entries.size(); i++) {
            if (page.entries.get(i).compare(key) > 0) {
                page.entries.add(i, key);
                return;
            }
        }
        page.entries.add(key); // 插入到末尾

    }


    /**
     * 验证节点是否满足 point数 = key数 + 1
     */
    private boolean checkKeyPointRelation(Page page) {
        if (!page.isLeaf) {
            if ((page.entries.size() + 1) == page.children.size()) {
                return true;
            } else {
                System.out.println("不满足 point数 = key数 + 1");
                return false;
            }
        }
        return true;
    }

    /**
     * 检查关键字是否有序
     */
    private boolean keyIsOrder(Page page) {
        for (int i = 0; i < (page.entries.size() - 1); i++) {
            if (page.entries.get(i).compare(page.entries.get(i + 1)) > 0) {
                System.out.println("节点关键字 不 有序");
                return false;
            }
        }
        return true;
    }

//    /**
//     * 验证节点是否符合B+树 定义
//     */
//    boolean validate(Page page) {
//        if (checkKeyPointRelation(page)) {
//            // 检查关键字是否有序
//            if (keyIsOrder(page)) {
//                if (page.isLeaf) {
//                    if (page.isRoot) {
//                        // 是页节点 且是 根节点
//                        return true;
//                    } else {
//                        // 是叶子节点 不是 根节点
//                        if (page.entries.size() < getUpper(page.maxLength - 1, 2) || page.entries.size() > (page.maxLength - 1)) {
//                            System.out.println("叶节点key数 不合法");
//                            return false;
//                        }
//                        if (page.parent == null) {
//                            System.out.println("叶子节点的父节点的指针为空");
//                            return false;
//                        }
//                    }
//                    return true;
//                } else {
//                    // 非叶子节点
//                    // 先检查指针数是否符合
//                    if (page.isRoot) {
//                        if (page.children.size() < 2) {
//                            System.out.printf("根节点指针数 不合法, children:%d\n", page.children.size());
//                            return false;
//                        }
//                    } else {
//                        if (page.children.size() < getUpper(page.maxLength, 2) || page.children.size() > page.maxLength) {
//                            System.out.printf("非叶节点指针数 不合法, children:%d\n", page.children.size());
//                            System.out.printf("entry:%d\n", page.entries.size());
//                            return false;
//                        }
//                        for (Page node : page.children) {
//                            if (node.parent == null) {
//                                System.out.println("中间节点的父指针为空");
//                                return false;
//                            }
//                        }
//                    }
//                    for (Page node : page.children) {
//                        if (node.validate()) {
//                            // 子节点符合B+树定义
//                            int pIdx = page.children.indexOf(node);
//                            Value minChildKey = node.entries.getFirst();
//                            Value maxChildKey = node.entries.getLast();
//                            if (pIdx == 0) {
//                                // 第一个指针
//                                boolean isValid = maxChildKey.compare(page.entries.getFirst()) < 0;
//                                if (!isValid) {
//                                    System.out.println("子节点与父节点不满足大小关系");
//                                    return false;
//                                }
//                            } else if (pIdx == (page.children.size() - 1)) {
//                                // 最后一个指针
//                                boolean isValid = minChildKey.compare(page.entries.getLast()) >= 0;
//                                if (!isValid) {
//                                    System.out.println("子节点与父节点不满足大小关系");
//                                    return false;
//                                }
//                            } else {
//                                Value preKey = page.entries.get(pIdx - 1);
//                                Value nextKey = page.entries.get(pIdx);
//                                boolean isValid = minChildKey.compare(preKey) >= 0
//                                        && maxChildKey.compare(nextKey) < 0;
//                                if (!isValid) {
//                                    System.out.println("子节点与父节点不满足大小关系");
//                                    return false;
//                                }
//                            }
//                        } else {
//                            // 子节点违反B+树定义
//                            System.out.println("子节点违反B+树定义");
//                            return false;
//                        }
//                    }
//                    return true;
//                }
//            } else {
//                // 关键字不有序
//                return false;
//            }
//        } else {
//            return false;
//        }
//    }

}

