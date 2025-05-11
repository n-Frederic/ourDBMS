package Main;

import Controller.Operating;
import Storage.Page.*;
import Storage.Value.IntValue;
import Storage.Value.StringValue;
import Storage.Value.Value;
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

        TableManager tm = new TableManager();
        ArrayList<Field> fields = new ArrayList<>();

        fields.add(new Field("id", "INT"));
        fields.add(new Field("name", "STRING"));
        fields.add(new Field("age", "INT"));

        TableManager.CreateTable("student", fields);

        Table table = new Table("student");

        table.getPageManager().getMeta().showInfo();
        System.out.println();


        // TODO:头部叶子节点没更新

        Value[] values1 = {new IntValue(5),new StringValue("jjj"),new IntValue(20)};
        Tuple t1 = new Tuple(values1);
        t1.setPrimaryV(new IntValue(5));
        table.insert(t1);


        table.getPageManager().getMeta().showInfo();
        System.out.println();


        System.out.println("根页：" + table.getTree().getRoot().getPageId());
        Page page = table.getPageManager().getPage(1);

        System.out.println("叶子的页id" + page.getPageId());
        System.out.println("页是叶子吗" + page.isLeaf());
        System.out.println("页是根吗" + page.isRoot());
        System.out.println("前一个页的id" + page.getPrevious());
        System.out.println("后一个页的id" + page.getNext());
        System.out.println("页的行数"+page.getTuples().size());

        for(Tuple tuple : page.getTuples()) {
            System.out.println(tuple.getPrimaryV());
            for(Value value : tuple.getValues()) {
                System.out.print(value.toString() + " ");
            }
            System.out.println();
        }





    }
}
