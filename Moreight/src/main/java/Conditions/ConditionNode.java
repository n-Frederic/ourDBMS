package Conditions;
import Storage.Page.Tuple;

import java.util.ArrayList;

public abstract class ConditionNode {
    @Override
    public abstract String toString();
    public abstract ArrayList<Tuple> evaluate();
//    public abstract  ArrayList<Tuple> evaluate( ArrayList<Tuple> data);
}
