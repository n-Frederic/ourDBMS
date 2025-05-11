package Storage.Page;

import Table.Field;
import Table.Schema;
import java.io.*;
import java.util.ArrayList;


public class Meta {

    private int rootPageId;
    private int highestPageId;
    private int maxKeys;
    private int columnCount;
    private String[] columnNames;
    private int[] columnTypes;
    private String[] columnConstraints;

    public Meta() {}

    public Meta(int rootPageId, int highestPageId, int maxKeys, int columnCount,
                String[] columnNames, int[] columnTypes, String[] columnConstraints) {
        this.rootPageId = rootPageId;
        this.highestPageId = highestPageId;
        this.maxKeys = maxKeys;
        this.columnCount = columnCount;
        this.columnNames = columnNames;
        this.columnTypes = columnTypes;
        this.columnConstraints = columnConstraints;
    }

    public Meta(ArrayList<Field> args) {
        this.rootPageId = 0;
        this.highestPageId = 0;
        this.maxKeys = 5;
        this.columnCount = args.size();

        columnNames = new String[columnCount];
        columnConstraints = new String[columnCount];
        columnTypes = new int[columnCount];

        for(int i = 0; i < args.size(); i++) {
            columnNames[i] = args.get(i).getName();
            columnTypes[i] = args.get(i).mapFieldTypeToInt();
            StringBuilder constraint = new StringBuilder();
            if(args.get(i).isPrimaryKey()) constraint.append("primaryKey ");
            if(args.get(i).isNotNull()) constraint.append("notNull ");
            if(args.get(i).isUnique()) constraint.append("Unique ");
            if(args.get(i).getDefault() != null) constraint.append("Default:").append(args.get(i).getDefault().toString());
            columnConstraints[i] = constraint.toString();
        }
    }


    // Getters and Setters for each field
    public int getRootPageId() {
        return rootPageId;
    }

    public void setRootPageId(int rootPageId) {
        this.rootPageId = rootPageId;
    }

    public int getHighestPageId() {
        return highestPageId;
    }

    public void setHighestPageId(int highestPageId) {
        this.highestPageId = highestPageId;
    }

    /**
     * 更新当前最高页码到文件
     */
    public void updateHighestPageId(RandomAccessFile file) throws IOException {
        file.seek(4);  // 定位到文件中保存最高页码的位置
        file.writeInt(highestPageId);  // 写入新的最高页码
    }

    /**
     *
     * @return
     */
    public void updateRootPageId(RandomAccessFile file) throws IOException {
        file.seek(0);  // 定位到文件中保存最高页码的位置
        file.writeInt(rootPageId);  // 写入新的最高页码
    }

    public int getMaxKeys() {
        return maxKeys;
    }

    public void setMaxKeys(int maxKeys) {
        this.maxKeys = maxKeys;
    }

    public int getColumnCount() {
        return columnCount;
    }

    public void setColumnCount(int columnCount) {
        this.columnCount = columnCount;
    }

    public String[] getColumnNames() {
        return columnNames;
    }

    public void setColumnNames(String[] columnNames) {
        this.columnNames = columnNames;
    }

    public int[] getColumnTypes() {
        return columnTypes;
    }

    public void setColumnTypes(int[] columnTypes) {
        this.columnTypes = columnTypes;
    }

    public String[] getColumnConstraints() {
        return columnConstraints;
    }

    public void setColumnConstraints(String[] columnConstraints) {
        this.columnConstraints = columnConstraints;
    }


    /**
     * 第零页的结构
     * 根页的页码 ( int 4B )
     * 目前的最高页码 （ int 4B ）
     * 中间节点允许的最多key数量 （ int 4B ）
     * 列的当前数量 （int 4B)
     * ...（前面共1024B）
     * 列名 （最多100个字段，每列是 int（列名长度） + n 字节（UTF-8 字节串）最多 56个字节 )  ## 60
     * 列类型 （ int 4B*100 , 最多允许400B)                                                  ## 4
     * 每列的检查约束（int（约束长度） + 最多允许60B））                                     ## 64
     * 故一列总共信息不超过128B
     */
    // 从第0页读取元数据
    public static Meta readMetaFromDisk(RandomAccessFile file) throws IOException {
        file.seek(0);

        int rootPageId = file.readInt();
        int highestPageId = file.readInt();
        int maxKeys = file.readInt();
        int columnCount = file.readInt();

        file.seek(1024);

        String[] columnNames = new String[columnCount];
        int[] columnTypes = new int[columnCount];
        String[] columnConstraints = new String[columnCount];

        for (int i = 0; i < columnCount; i++) {
            int nameLength = file.readInt();
            byte[] nameBytes = new byte[nameLength];
            file.readFully(nameBytes);
            columnNames[i] = new String(nameBytes);

            columnTypes[i] = file.readInt();

            int constraintLength = file.readInt();

            byte[] constraintBytes = new byte[constraintLength];
            file.readFully(constraintBytes);
            columnConstraints[i] = new String(constraintBytes);

            int bytesRead = 4 + nameLength + 4 + 4 + constraintLength;
            int toSkip = 128 - bytesRead;
            if (toSkip > 0) {
                file.skipBytes(toSkip);
            }
        }

        return new Meta(rootPageId, highestPageId, maxKeys, columnCount, columnNames, columnTypes, columnConstraints);
    }

    /**
     * 第零页的结构
     * 根页的页码 ( int 4B )
     * 目前的最高页码 （ int 4B ）
     * 中间节点允许的最多key数量 （ int 4B ）
     * 列的当前数量 （int 4B)
     * ...（前面共1024B）
     * 列名 （最多100个字段，每列是 int（列名长度） + n 字节（UTF-8 字节串）最多 56个字节 )  ## 60
     * 列类型 （ int 4B*100 , 最多允许400B)                                                  ## 4
     * 每列的检查约束（int（约束长度） + 最多允许60B））                                     ## 64
     * 故一列总共信息不超过128B
     */

    // 将元数据写入第0页
    public void writeMetaToDisk(RandomAccessFile file) throws IOException {
        file.seek(0);

        file.writeInt(rootPageId);
        file.writeInt(highestPageId);
        file.writeInt(maxKeys);
        file.writeInt(columnCount);

        file.seek(1024);

        // 列名长度 + 列名 + 列类型 + 约束长度 + 约束
        for (int i = 0; i < columnCount; i++) {
            int offset = 0;
            byte[] nameBytes = columnNames[i].getBytes();
            file.writeInt(nameBytes.length);
            file.write(nameBytes);

            offset += 4+nameBytes.length;

            file.writeInt(columnTypes[i]);
            offset += 4;

            byte[] constraintBytes = columnConstraints[i].getBytes();
            file.writeInt(constraintBytes.length);
            file.write(constraintBytes);
            offset += 4+constraintBytes.length;

            for(int j = offset; j < 128; j++) {
                file.writeByte(0);
            }
        }
    }


    /*
        private int rootPageId;
    private int highestPageId;
    private int maxKeys;
    private int columnCount;
    private String[] columnNames;
    private int[] columnTypes;
    private String[] columnConstraints;
     */
    public void showInfo() {
        System.out.println("rootPageId : " + rootPageId);
        System.out.println("highestPageId : " + highestPageId);
        System.out.println("maxKeys : " + maxKeys);
        System.out.println("columnCount : " + columnCount);
        for(int i = 0; i < columnCount; i++) {
            System.out.println(columnNames[i] + " " + Field.mapIntToFieldType(columnTypes[i]) + " " +columnConstraints[i]);
        }
    }
}

