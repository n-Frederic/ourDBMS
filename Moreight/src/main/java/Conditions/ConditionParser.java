package Conditions;
import Storage.Value.Value;
import Table.Table;
import Storage.BPlusTree.Value.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConditionParser {
    Table table;
    public void setFilepath(Table table){
        this.table=table;
    }

    public ConditionParser(Table table){
        this.table=table;
    }


    public  void validateBrackets(List<String> tokens) {
        int balance = 0;
        for (String token : tokens) {
            if (token.equals("(")) balance++;
            else if (token.equals(")")) balance--;
            if (balance < 0) throw new IllegalArgumentException("括号过多 ')'");
        }
        if (balance != 0) throw new IllegalArgumentException("括号未闭合");
    }

    public  List<String> tokenizeWhere(String input) {
        List<String> tokens = new ArrayList<>();
        Matcher m = Pattern.compile(
                "\\(|\\)|\\bAND\\b|\\bOR\\b|\\w+\\s*(=|!=|<=|>=|<|>)\\s*('[^']*'|\\d+|\\w+)"
        ).matcher(input);
        while (m.find()) {
            tokens.add(m.group().trim());
        }
        return tokens;
    }



    public  ConditionNode parseConditionTree(List<String> tokens) {
        validateBrackets(tokens); // ✅ 提前校验括号配对
        return parseOr(tokens);
    }

    private ConditionNode parseOr(List<String> tokens) {
        ConditionNode left = parseAnd(tokens);
        while (!tokens.isEmpty() && tokens.get(0).equalsIgnoreCase("OR")) {
            tokens.remove(0);
            ConditionNode right = parseAnd(tokens);
            left = new ConditionOperator("OR", left, right);
        }
        return left;
    }

    private  ConditionNode parseAnd(List<String> tokens) {
        ConditionNode left = parsePrimary(tokens,table);
        while (!tokens.isEmpty() && tokens.get(0).equalsIgnoreCase("AND")) {
            tokens.remove(0);
            ConditionNode right = parsePrimary(tokens,table);
            left = new ConditionOperator("AND", left, right);
        }
        return left;
    }
    //where条件解析
    private  ConditionNode parsePrimary(List<String> tokens,Table table) {
        if (tokens.get(0).equals("(")) {
            tokens.remove(0);
            ConditionNode node = parseOr(tokens);
            if (tokens.isEmpty() || !tokens.get(0).equals(")")) {
                throw new IllegalArgumentException("括号未闭合");
            }
            tokens.remove(0);
            return node;
        } else {
            String raw = tokens.remove(0);
            Matcher m = Pattern.compile("(\\w+)\\s*(=|!=|<=|>=|<|>)\\s*(.+)").matcher(raw);

            String type=table.getSchema().getField(m.group(1)).getType();;
            value= Value.getType(,table);
            Value.parse(value,m.group(3));

            if (m.matches()) {

                return new Condition(m.group(1),value, m.group(2),table);
            } else {
                throw new IllegalArgumentException("非法条件: " + raw);
            }
        }
    }
}
