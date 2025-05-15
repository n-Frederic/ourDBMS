package Main;

import Conditions.Condition;
import Controller.Operating;
import Database.DatabaseManager;
import Storage.Page.*;
import Storage.Value.*;
import Table.Field;
import Table.TableManager;
import Table.Table;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Random;
import java.util.RandomAccess;

public class Main {
    public static void main(String[] args) throws IOException {

        /**
         * 比较麻烦的检查某页的信息，读文件，有根页，有非根页
         */
//        RandomAccessFile raf = new RandomAccessFile("../TestData/DatabaseManager/TEST/student/student.idb","rw");
//        raf.seek(4*8*1024);
//        System.out.println(raf.readInt());
//        System.out.println(raf.readBoolean());
//        System.out.println(raf.readBoolean());
//        System.out.println(raf.readInt());

//        System.out.println(raf.readInt());
//        System.out.println(raf.readInt());
//        System.out.println(raf.readInt());
//        int minValueLen = raf.readInt();
//        System.out.println(minValueLen);
//        int minValueType = raf.readInt();
//        System.out.println(minValueType);
//
//        byte[] minValueBytes = new byte[minValueLen];
//        raf.readFully(minValueBytes);
//        Value minValue = Tuple.decodeTypedValue(minValueType,minValueBytes);
//        System.out.println(minValue.toString());

//        int size = raf.readInt();
//        System.out.println(size);
//        int type = raf.readInt();
//        System.out.println(type);
//
//        int entryBytes = 0;
//        switch(type) {
//            case 1:
//                for(int i = 0; i < size; i++) {
//                    StringBuilder sb = new StringBuilder();
//                    byte b;
//                    while ((b = raf.readByte()) != 0) {
//                        sb.append((char) b);
//                    }
//                    System.out.println(sb);
//                    entryBytes += sb.length();
//                }
//                break;
//            case 2:
//                for(int i = 0; i < size; i++) {
//                    System.out.println(raf.readInt());
//                }
//                entryBytes += 4 * size;
//                break;
//            case 3:
//                for(int i = 0; i < size; i++) {
//                    System.out.println(raf.readLong());
//                }
//                entryBytes += 8 * size;
//                break;
//            case 4:
//                for(int i = 0; i < size; i++) {
//                    raf.readBoolean();
//                }
//                entryBytes += size;
//                break;
//        }
//
//        raf.skipBytes(Math.max(0, 512 - entryBytes));
//
//        for(int i = 0; i < size; i++) {
//            System.out.println(raf.readInt());
//        }


        /**
         * 不基于table构建的检查某页的信息
         * 即定点反序列化
         */

//        PageIO pageIO= new PageIO("../TestData/DatabaseManager/TEST/student/student.idb");
//
//        Page page = pageIO.readPage(5);
//
//        page.showInfo();;

//        /**
//         * 构建数据库
//         */
//        DatabaseManager.createDataBase("TEST");

        /**
         * 使用数据库
         */
        DatabaseManager.useDatabase("TEST");



        /**
         * 测试字段
         */
        ArrayList<Field> fields = new ArrayList<>();
        fields.add(new Field("id", "INT"));
        fields.add(new Field("name", "STRING"));
        fields.add(new Field("age", "INT"));

        /**
         * 创建表
         */
        TableManager.CreateTable("student", fields);

        /**
         * 从文件中反序列化出表对象
         */
        Table table = new Table("student");

//        Page page = table.getPageManager().getPage(3);
//
//        page.showInfo();



        /**
         * 测试插入
         */

//        for (int i = 0; i < 13; i++) {
//            Value[] values1 = {new IntValue(i), new StringValue("jjj" + i), new IntValue(20)};
//            Tuple t1 = new Tuple(values1);
//            t1.setPrimaryV(new IntValue(i)); // 主键不变
//            table.insert(t1);
//        }

//        for (int i = 1; i <= 20; i++) {
//            Value[] values1 = {new IntValue(i), new StringValue("jjj" + i), new IntValue((i*185)%21)};
//            Tuple t1 = new Tuple(values1);
//            t1.setPrimaryV(new IntValue(i)); // 主键不变
//            table.insert(t1);
//        }

//        int i = 20;
//        Value[] values1 = {new IntValue(i), new StringValue("jjj" + i), new IntValue(20)};
//        Tuple t1 = new Tuple(values1);
//        t1.setPrimaryV(new IntValue(i)); // 主键不变
//        table.insert(t1);

        /**
         * 展示表的元信息
         */

        System.out.println();
        Meta meta = table.getPageManager().getMeta();
        meta.showInfo();
        System.out.println();


        /**
         * 这段是检查是否写入的测试代码
         */
////        RandomAccessFile raf = table.getPageManager().getPageIO().getFile();
////        raf.seek(3*8*1024+4);
////        System.out.println(raf.readBoolean());
//

        /**
         * 检查当前树的根页
         */
//        System.out.println("根页：" + table.getTree().getRoot().getPageId());

        /**
         * 这段是检查该删除的页有没有清空
         */
//        if(table.getPageManager().getPages().get(1) == null) {
//            System.out.println("第一页已经成功清空");
//            System.out.println();
//        }


        /**
         * 这段是检查某一页码信息的测试代码
         * 前提：table可以正常构建
         */
//        Page page = table.getPageManager().getPage(1);
//
//        page.showInfo();

        /**
         * 这段是“查”的测试代码
         */
//        ArrayList<Tuple> at = table.selectAll();
//        for(Tuple tuple : at) {
//            for(Value value : tuple.getValues()) {
//                System.out.print(value.toString() + " ");
//            }
//            System.out.println();
//        }
//
//        System.out.println();
//
//        Condition condition = new Condition("id",new IntValue(13),">=");
//        ArrayList<Tuple> st = table.where(table.getPageManager().getPage(4),condition);
//
//        for(Tuple tuple : st) {
//            for(Value value : tuple.getValues()) {
//                System.out.print(value.toString() + " ");
//            }
//            System.out.println();
//        }

//        ArrayList<Tuple> allTuples = table.selectAll();
//        for(Tuple tuple : allTuples) {
//            for(Value value : tuple.getValues()) {
//                System.out.print(value.toString() + " ");
//            }
//            System.out.println();
//        }


        /**
         * 检查desc
         */

//        TableManager.desc(table);

        /**
         * 检查Modify column
         */
        Field updatedField = new Field("name", "int");
        updatedField.setPrimaryKey(false);

        TableManager.modifyColumn(table, "name", updatedField);

        meta.showInfo();



        // 设置主键字段名称
//        table.getSchema().setPrimaryKeyName("id");

        // 插入数据
//        for (int i = 0; i < 3; i++) {
//            Value[] values1 = {new IntValue(i), new StringValue("jjj" + i), new IntValue(20)};
//            Tuple t = new Tuple(values1);
//            t.setPrimaryV(new IntValue(i));
//            table.insert(t);
//        }
//
//        // 输出插入的数据
//        System.out.println(table.getTree().getHead());
//        ArrayList<Tuple> allTuples = table.selectAll();
//        for(Tuple tuple : allTuples) {
//            for(Value value : tuple.getValues()) {
//                System.out.print(value.toString() + " ");
//            }
//            System.out.println();
//        }
//
//        // 示例：更新一个学生的年龄
//        Value newAgeValue = new IntValue(99);
//        ArrayList<Tuple> updateTuples = new ArrayList<>();
//
//        // 假设我们要更新 id 为 1 的学生
//        for (Tuple tuple : allTuples) {
//            if (tuple.getValues()[0].equals(new IntValue(1))) { // 假设主键是 id
//                updateTuples.add(tuple);
//            }
//        }
//
//        // 更新数据
//        table.update(updateTuples, newAgeValue, "age");
//
//        // 查询更新后的数据
//        System.out.println("更新后的学生数据：");
//        allTuples = table.selectAll(); // 获取更新后的所有数据
//
//        // 遍历并输出更新后的数据
//        for (Tuple tuple : allTuples) {
//            for (Value value : tuple.getValues()) {
//                System.out.print(value.toString() + " ");
//            }
//            System.out.println(); // 换行，开始下一行数据
//        }
//
//        // 获取并打印第一页数据
//        System.out.println("第一页内容：");
//        Page firstPage = table.getPageManager().getPage(1);  // 假设页码为 0
//        ArrayList<Tuple> updatedPageTuples = firstPage.getTuples();
//        for (Tuple tuple : updatedPageTuples) {
//            for (Value value : tuple.getValues()) {
//                System.out.print(value.toString() + " ");
//            }
//            System.out.println();
//        }

//        // 1. 准备要更新的数据（假设要把 id=1 的记录改成 id=100）
//        ArrayList<Tuple> tuplesToUpdate = new ArrayList<>();
//        Value newId = new IntValue(100);  // 新的主键值
//
//        // 2. 找出要更新的 tuple（主键为 1 的记录）
//        for (Tuple tuple : table.selectAll()) {
//            if (((IntValue) tuple.getValues()[0]).getInt() == 1) {
//                tuplesToUpdate.add(tuple);
//            }
//        }
//
//        // 3. 调用 update 方法（更新主键列）
//        table.update(tuplesToUpdate, newId, "id");  // "id" 是主键列名
//
//        // 获取并打印第一页数据
//        System.out.println("第一页内容：");
//        Page firstPage = table.getPageManager().getPage(1);
//        firstPage.showInfo();
//        ArrayList<Tuple> updatedPageTuples = firstPage.getTuples();
//        for (Tuple tuple : updatedPageTuples) {
//            for (Value value : tuple.getValues()) {
//                System.out.print(value.toString() + " ");
//            }
//            System.out.println();
//        }

    }
}
