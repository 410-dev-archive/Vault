package database;

public class SQLParameter {

    public String column = "";
    public String value = "";
    public String operator = "";
    public String nextOperand = "";

    public static final String AND = "AND";
    public static final String OR = "OR";

    public static final String EQUAL = "=";
    public static final String NOT_EQUAL = "!=";
    public static final String GREATER_THAN = ">";
    public static final String LESS_THAN = "<";
    public static final String GREATER_THAN_OR_EQUAL = ">=";
    public static final String LESS_THAN_OR_EQUAL = "<=";
    public static final String LIKE = "LIKE";
    public static final String NOT_LIKE = "NOT LIKE";
}
