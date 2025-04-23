
package Conditions;
import java.util.*;

import Storage.BPlusTree.Tuple;
import com.google.gson.*;

public class ConditionEvaluator {
    public static  ArrayList<Tuple> evaluateConditions(ConditionNode root) {
        if (root == null) {
            return null;
        }
        return root.evaluate();
    }
}

