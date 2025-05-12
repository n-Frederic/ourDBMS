package Storage.Page;
import Conditions.Condition;
import Storage.Value.*;
import Table.Field;

import java.io.*;
import java.util.*;

public class Page {
    public static final int PAGE_SIZE = 8 * 1024; // 8KB

    private int pageId;    // page 的 id

    boolean isLeaf;

    boolean isRoot;

    Page parent;

    // 以上是内部，叶子公共属性

    final int maxTuples = 12;                                 final int maxLength = 5;
    Page previous;                                            ArrayList<Page> children;

    Page next;                                                ArrayList<Value> entries;

    ArrayList<Tuple> tuples;                                  // 以上是内部节点的属性

    Value minValue;

    // 以上是叶子节点的属性


    public Page(int pageId) {
        this.pageId = pageId;
        this.tuples = new ArrayList<>();
        this.entries = new ArrayList<>();
        this.children = new ArrayList<>();
    }

    public Page() {
        this.tuples = new ArrayList<>();
    }


    public Page(boolean isLeaf) {
        this.isLeaf = isLeaf;
        tuples = new ArrayList<>();
        if (!isLeaf) {
            children = new ArrayList<>();
            entries = new ArrayList<>();
        }
    }

    public Page(boolean isLeaf, boolean isRoot) {
        this(isLeaf);
        this.isRoot = isRoot;
    }

    public Page(int pageId, boolean isLeaf) {
        this(isLeaf);
        this.setPageId(pageId);
    }

    public Page(int pageId, boolean isLeaf, boolean isRoot) {
        this(isLeaf,isRoot);
        this.setPageId(pageId);
    }



    public Page getParent() {
        return parent;
    }

    public void setParent(Page parent) {
        this.parent = parent;
    }

    public Page getNext() {
        return next;
    }

    public void setNext(Page next) {
        this.next = next;
    }

    public Page getPrevious() {
        return previous;
    }

    public void setPrevious(Page previous) {
        this.previous = previous;
    }

    // 判断当前页是否是叶子页
    public boolean isLeaf() {
        return isLeaf;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void showInfo() {
        System.out.println("叶子的页id : " + pageId);
        System.out.println("页是叶子吗 : " + isLeaf);
        System.out.println("页是根吗 : " + isRoot);
        System.out.println("前一个页的id : " + (previous != null ? previous.getPageId() : "null"));
        System.out.println("后一个页的id : " + (next != null ? next.getPageId() : "null"));
        if(!isLeaf) {
            for(Value value : entries) {
                System.out.print(value.toString() + " ");
            }
            System.out.println();
            for(Page child : children) {
                System.out.print(child.getPageId() + " ");
            }
        } else {
            System.out.println("页的行数 : "+ tuples.size());
            System.out.println();

            for(Tuple tuple : tuples) {
                System.out.println(tuple.getPrimaryV());
                for(Value value : tuple.getValues()) {
                    System.out.print(value.toString() + " ");
                }
                System.out.println();
            }
        }
    }



    /**
     * 得到行数组
     * @return 行数组
     */
    public ArrayList<Tuple> getTuples() {
        return tuples;
    }
    public ArrayList<Tuple> getTuples(Condition condition, int index) {
        ArrayList<Tuple> temp=new ArrayList<>();
        for(Tuple t:tuples){
            if(t.getValue(index).equals(condition)){
                temp.add(t);
            }
        }
        return temp;
    }

    // 获取当前页的页号
    public int getPageId() {
        return pageId;
    }

    // 获取当前页的子节点列表
    public ArrayList<Page> getChildren() {
        return children;
    }

    // 设置子节点列表
    public void setChildren(ArrayList<Page> children) {
        this.children = children;
    }

    // 设置当前页的页号
    public void setPageId(int pageId) {
        this.pageId = pageId;
    }

    public ArrayList<Value> getEntries() {
        return entries;
    }

    public Value getMinValue() {
        return minValue;
    }
    public void setMinValue(Value minValue){
        this.minValue = minValue;
    }

    public void setRoot(boolean root) {
        isRoot = root;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public int getMaxTuples() {
        return maxTuples;
    }


    /**
     * 叶子页的结构如下:
     * 页id（int 4b）
     * 是否是叶子节点 （bool, 1B)
     * 是否是根节点 （bool, 1B)
     * 父亲的页id （int, 4B)
     * 前一个叶子页的id （int, 4B)
     * 后一个叶子页的id (int, 4B)
     * 页的行数 （int 4B）
     * 一页中所有行的主键的最小值（长度int,4B + 类型int,4B + 实际值）
     * 每行规定好的长度（512B）（int 4B)
     * 每一行的开始的偏移量 (4b * size)
     * ...（前面共1024B）
     * 实际数据
     * @return 字节数组
     */
    public byte[] leafToBytes() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(PAGE_SIZE);
        DataOutputStream dataOut = new DataOutputStream(out);

        dataOut.writeInt(pageId);    // pageID
        dataOut.writeBoolean(isLeaf);
        dataOut.writeBoolean(isRoot);

        if(parent != null) {
            dataOut.writeInt(parent.getPageId());
        } else dataOut.writeInt(-1);

        if(previous != null) {
            dataOut.writeInt(previous.getPageId());
        } else dataOut.writeInt(-1);

        if(next != null) {
            dataOut.writeInt(next.getPageId());
        } else dataOut.writeInt(-1);

        dataOut.writeInt(tuples.size());     // 行数
        byte[] minValueBytes = minValue.toBytes();

        dataOut.writeInt(minValueBytes.length);
        dataOut.writeInt(minValue.getType());
        dataOut.write(minValueBytes);
        for(int i = minValueBytes.length; i < 30; i++) {
            dataOut.writeByte(0);
        }

        dataOut.writeInt(512);     // 每行最多允许512B的数据

        // 实时维护偏移量
        int currentOffset = 64;
        int dataPointer = 1024;

        ArrayList<byte[]> info = new ArrayList<>();

        // 写入偏移量表
        for(Tuple tuple : tuples) {
            dataOut.writeInt(dataPointer);
            byte[] bytes = tuple.toBytes();
            info.add(bytes);
            dataPointer += 512;
            currentOffset += 4;
        }

        for(int i = currentOffset; i < 1024; i++) {
            dataOut.writeByte(0);
        }

        currentOffset = 1024;

        // 写入实际数据
        for (byte[] b : info) {
            dataOut.write(b);
            for(int i = b.length; i < 512; i++) {
                dataOut.writeByte(0);
            }
            currentOffset += 512;
        }

        for(int i = currentOffset; i < PAGE_SIZE; i++) {
            dataOut.writeByte(0);
        }

        return out.toByteArray();
    }

    /**
     * 叶子页的结构如下:
     * 页id（int 4B）
     * 是否是叶子节点 （bool, 1B)
     * 是否是根节点 （bool, 1B)
     * 父亲的页id （int, 4B)
     * 前一个叶子页的id （int, 4B)
     * 后一个叶子页的id (int, 4B)
     * 页的行数 （int 4b）
     * 一页中所有行的主键的最小值（长度int,4B + 类型int,4B + 实际值）
     * 每行规定好的长度（512B）（int 4B)
     * 每一行的开始的偏移量 (4b)
     * ... (前面共1024B）
     * 实际数据
     * @return 字节数组
     */

    public static Page leafFromBytes(byte[] bytes) throws IOException {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));
        int pageId = in.readInt();
        boolean isLeaf = in.readBoolean();
        boolean isRoot = in.readBoolean();
        int parentId = in.readInt();
        int previousId = in.readInt();
        int nextId = in.readInt();
        int rowCount = in.readInt();

        int minValueLen = in.readInt();
        int minValueType = in.readInt();
        byte[] minValueBytes = new byte[minValueLen];
        in.readFully(minValueBytes);
        Value minValue = Tuple.decodeTypedValue(minValueType,minValueBytes);

        in.skipBytes(30-minValueBytes.length);
        int len = in.readInt();

        ArrayList<Integer> offsets = new ArrayList<>(rowCount);
        for(int i = 0; i < rowCount; i++) {
            offsets.add(in.readInt());
        }

        Page page = new Page(pageId);
        for (int i = 0; i <= rowCount - 1; i++) {
            int rowStart = offsets.get(i);
            int rowEnd = (i + 1 < rowCount) ? offsets.get(i + 1) : 1024+rowCount*512;
            byte[] rowData = Arrays.copyOfRange(bytes, rowStart, rowEnd);
            Tuple tuple = Tuple.fromBytes(rowData);
            page.tuples.add(tuple);
        }

        page.isLeaf = isLeaf;
        page.isRoot = isRoot;
        page.parent = parentId == -1 ? null : new Page(parentId);
        page.previous = previousId == -1 ? null : new Page(previousId);
        page.next = nextId == -1 ? null : new Page(nextId);
        page.setMinValue(minValue);


        return page;
    }


//    /**
//     * 第零页的结构
//     * 根页的页码 ( int 4B )
//     * 目前的最高页码 （ int 4B ）
//     * 中间节点允许的最多key数量 （ int 4B ）
//     * ...（前面共1024B）
//     * 列的当前数量 （int 4B)
//     * 列名 （最多100个字段，每列是 int（列名长度） + n 字节（UTF-8 字节串）)
//     * 列类型 （ int 4B*100 , 最多允许400B)
//     * 每列的检查约束（256B*100，最多允许25600B）
//     */
//
//    public byte[] ZeroToBytes(ArrayList<Field> fields) throws IOException {
//        ByteArrayOutputStream out = new ByteArrayOutputStream(PAGE_SIZE);
//        DataOutputStream dataOut = new DataOutputStream(out);
//
//        dataOut.writeInt(1);
//        dataOut.writeInt(1);
//        dataOut.writeInt(5);
//
//        int size = fields.size();
//        dataOut.writeInt(size);
//
//        // 写入列名（以每个列名长度 + UTF-8 字符串形式写入）
//        int fieldCount = Math.min(size, 100);
//        for (int i = 0; i < fieldCount; i++) {
//            String name = fields.get(i).getName();
//            byte[] nameBytes = name.getBytes(StandardCharsets.UTF_8);
//
//            // 写入列名长度 + 内容
//            dataOut.writeInt(nameBytes.length);
//            dataOut.write(nameBytes);
//        }
//
//        // 如果字段数不足 100 个，补空列名（写入0长度）
//        for (int i = fieldCount; i < 100; i++) {
//            dataOut.writeInt(0);
//        }
//        // 写入字段类型（int，4B * n，最多允许400B)
//        for (int i = 0; i < fieldCount; i++) {
//            int fieldTypeInt = fields.get(i).mapFieldTypeToInt();  // 将字段类型转换为数字
//            dataOut.writeInt(fieldTypeInt);  // 写入字段类型
//        }
//
//        // 填充剩余的空间（如果字段数小于 100，填充 0）
//        for (int i = fieldCount; i < 100; i++) {
//            dataOut.writeInt(0);  // 填充 0
//        }
//
//        return out.toByteArray();
//    }
//

    /**
     * 非叶子页的结构
     * 页码 （ int 4B ）
     * 是否是叶子节点 （boolean 1B)
     * 是否是根节点 （boolean 1B)
     * 父亲的页id （int, 4B)
     * 目前孩子有几个 （int, 4B)
     * 记录的索引的类型 （目前为主键）（int, 4B)
     * 孩子页的行中主键的最小值序列 （ value * 5, 最多允许 512B）
     * 孩子页的页码 （int 4B*5 ）
     */
    public byte[] InnerToBytes() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream(PAGE_SIZE);
        DataOutputStream dataOut = new DataOutputStream(out);

        dataOut.writeInt(pageId);    // pageID
        dataOut.writeBoolean(isLeaf);
        dataOut.writeBoolean(isRoot);

        if(parent != null) {
            dataOut.writeInt(parent.getPageId());
        } else dataOut.writeInt(-1);

        dataOut.writeInt(children.size());
        dataOut.writeInt(entries.getFirst().getType());

        int offset = 0;
        for(Value value : entries) {
            byte[] bytes = value.toBytes();
            dataOut.write(bytes);
            offset += bytes.length;
        }

        for(int i = offset; i <= 512; i++) {
            dataOut.writeByte(0);
        }

        for(Page page : children) {
            dataOut.writeInt(page.getPageId());
        }

        int currentSize = out.size();
        byte[] zero = new byte[PAGE_SIZE-currentSize];
        out.write(zero);

        return out.toByteArray();
    }

    /**
     * 非叶子页的结构
     * 页码 （ int 4B ）
     * 是否是叶子节点 （boolean 1B)
     * 是否是根节点 （boolean 1B)
     * 父亲的页id （int, 4B)
     * 目前孩子有几个 （int, 4B)
     * 记录的索引的类型 （目前为主键）（int, 4B)
     * 孩子页的行中主键的最小值序列 （ Value * 5, 最多允许512B）
     * 孩子页的页码 （int 4B*5 ）
     */
    public static Page InnerFromBytes(byte[] bytes) throws IOException {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));
        int pageId = in.readInt();
        Page page = new Page(pageId);

        boolean isLeaf = in.readBoolean();
        boolean isRoot = in.readBoolean();
        int parentId = in.readInt();
        int size = in.readInt();
        int type = in.readInt();

        int entryBytes = 0;
        switch(type) {
            case 1:
                for(int i = 0; i < size; i++) {
                    StringBuilder sb = new StringBuilder();
                    byte b;
                    while ((b = in.readByte()) != 0) {
                        sb.append((char) b);
                    }
                    page.entries.add(new StringValue(sb.toString()));
                    entryBytes += sb.length();
                }
                break;
            case 2:
                for(int i = 0; i < size; i++) {
                    page.entries.add(new IntValue(in.readInt()));
                }
                entryBytes += 4 * size;
                break;
            case 3:
                for(int i = 0; i < size; i++) {
                    page.entries.add(new LongValue(in.readLong()));
                }
                entryBytes += 8 * size;
                break;
            case 4:
                for(int i = 0; i < size; i++) {
                    page.entries.add(new BooleanValue(in.readBoolean()));
                }
                entryBytes += size;
                break;
        }

        in.skipBytes(Math.max(0, 512 - entryBytes));

        for(int i = 0; i < size; i++) {
            page.children.add(new Page(in.readInt()));
        }

        page.isLeaf = isLeaf;
        page.isRoot = isRoot;
        page.parent = parentId == -1 ? null : new Page(parentId);

        return page;
    }


}
