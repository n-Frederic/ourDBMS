
package Conditions;
import java.io.IOException;
import java.util.*;

import Storage.Page.Tuple;

public class ConditionEvaluator {
    public static  ArrayList<Tuple> evaluateConditions(ConditionNode root) throws IOException {
        if (root == null) {
            return null;
        }
        return root.evaluate();
    }
}

