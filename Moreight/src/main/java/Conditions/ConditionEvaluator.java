
package Conditions;
import java.util.*;

import Storage.Page.Tuple;

public class ConditionEvaluator {
    public static  ArrayList<Tuple> evaluateConditions(ConditionNode root) {
        if (root == null) {
            return null;
        }
        return root.evaluate();
    }
}

