package Operate;
import java.util.*;

public abstract class ExprNode {
    public abstract boolean evaluate(Map<String, String> row);
}
