package Main;

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


//
//        PageIO pageIO = new PageIO("../TestData/DatabaseManager/TEST/student/student.idb");
//
//        Page page = pageIO.readPage(4);
//
//        page.showInfo();;

        DatabaseManager.createDataBase("TEST");
        TableManager tm = new TableManager();
        ArrayList<Field> fields = new ArrayList<>();

        fields.add(new Field("id", "INT"));
        fields.add(new Field("name", "STRING"));
        fields.add(new Field("age", "INT"));

        TableManager.CreateTable("student", fields);



        Table table = new Table("student");

//        Page page = table.getPageManager().getPage(3);
//
//        page.showInfo();

        table.getPageManager().getMeta().showInfo();
        System.out.println();
//
////
////        // TODO:头部叶子节点没更新  HAS BEEN DONE!
////
//        for (int i = 0; i < 13; i++) {
//            Value[] values1 = {new IntValue(i), new StringValue("jjj" + i), new IntValue(20)};
//            Tuple t1 = new Tuple(values1);
//            t1.setPrimaryV(new IntValue(i)); // 主键不变
//            table.insert(t1);
//        }

        for (int i = 14; i < 20; i++) {
            Value[] values1 = {new IntValue(i), new StringValue("jjj" + i), new IntValue(20)};
            Tuple t1 = new Tuple(values1);
            t1.setPrimaryV(new IntValue(i)); // 主键不变
            table.insert(t1);
        }

//        int i = 13;
//        Value[] values1 = {new IntValue(i), new StringValue("jjj" + i), new IntValue(20)};
//        Tuple t1 = new Tuple(values1);
//        t1.setPrimaryV(new IntValue(i)); // 主键不变
//        table.insert(t1);


        System.out.println();
        table.getPageManager().getMeta().showInfo();
        System.out.println();
//
//
////        RandomAccessFile raf = table.getPageManager().getPageIO().getFile();
////        raf.seek(3*8*1024+4);
////        System.out.println(raf.readBoolean());
//
//
//
        System.out.println("根页：" + table.getTree().getRoot().getPageId());

//        if(table.getPageManager().getPages().get(1) == null) {
//            System.out.println("第一页已经成功清空");
//            System.out.println();
//        }


        Page page = table.getPageManager().getPage(3);

        page.showInfo();
    }
}
