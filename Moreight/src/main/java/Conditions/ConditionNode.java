package Conditions;
import Storage.BPlusTree.Tuple;
import com.google.gson.JsonArray;

import java.util.ArrayList;
import java.util.Map;

public abstract class ConditionNode {
    @Override
    public abstract String toString();
    public abstract ArrayList<Tuple> evaluate();
//    public abstract  ArrayList<Tuple> evaluate( ArrayList<Tuple> data);
}
