package Conditions;
import com.google.gson.JsonArray;

import java.util.Map;

public abstract class ConditionNode {
    @Override
    public abstract String toString();
    public abstract JsonArray evaluate();
}
