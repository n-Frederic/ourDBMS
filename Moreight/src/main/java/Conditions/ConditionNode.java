package Conditions;
import Storage.Page.Tuple;

import java.io.IOException;
import java.util.ArrayList;

public abstract class ConditionNode {
    @Override
    public abstract String toString();
    public abstract ArrayList<Tuple> evaluate() throws IOException;
//    public abstract  ArrayList<Tuple> evaluate( ArrayList<Tuple> data);
}
