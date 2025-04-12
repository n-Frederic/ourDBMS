
package Conditions;
import java.util.*;
import com.google.gson.*;

public class ConditionEvaluator {
    public static JsonArray evaluateConditions(ConditionNode root) {
        if (root == null) {
            return null;
        }
        return root.evaluate();
    }
}

