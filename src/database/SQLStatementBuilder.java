package database;

import java.util.ArrayList;

public class SQLStatementBuilder {
    private ArrayList<SQLParameter> filters = new ArrayList<>();

    private String tableName;
    private String command;

    public static final String SELECT = "SELECT";
    public static final String UPDATE = "UPDATE";
    public static final String INSERT = "INSERT";
    public static final String DELETE = "DELETE";

    public SQLStatementBuilder(String tableName, String command) {
        this.tableName = tableName;
        this.command = command;
    }

    public void addParameter(SQLParameter filter) {
        filters.add(filter);
    }

    public String build() {
        StringBuilder builder = new StringBuilder();
        builder.append(command);
        if (command.equals(SELECT)) {
            builder.append(" * FROM ").append(tableName);
            if (filters.size() > 0) {
                builder.append(" WHERE ");
                for (int i = 0; i < filters.size(); i++) {
                    builder.append(filters.get(i).column)
                           .append(" ")
                           .append(filters.get(i).operator)
                           .append(" \"");
                    if (filters.get(i).operator.equals(SQLParameter.LIKE)) {
                        builder.append("%");
                    }
                    builder.append(filters.get(i).value);
                    if (filters.get(i).operator.equals(SQLParameter.LIKE)) {
                        builder.append("%");
                    }
                    builder.append("\"");
                    if (i < filters.size() - 1 && !filters.get(i).nextOperand.equals("")) {
                        builder.append(" " + filters.get(i).nextOperand + " ");
                    }
                }
                builder.append(";");
            }
        } else if (command.equals(DELETE)) {
            builder.append(" FROM ").append(tableName);
            if (filters.size() > 0) {
                builder.append(" WHERE ");
                for (int i = 0; i < filters.size(); i++) {
                    builder.append(filters.get(i).column)
                           .append(" ")
                           .append(filters.get(i).operator)
                           .append(" \"");
                    if (filters.get(i).operator.equals(SQLParameter.LIKE)) {
                        builder.append("%");
                    }
                    builder.append(filters.get(i).value);
                    if (filters.get(i).operator.equals(SQLParameter.LIKE)) {
                        builder.append("%");
                    }
                    builder.append("\"");
                    if (i < filters.size() - 1 && !filters.get(i).nextOperand.equals("")) {
                        builder.append(" " + filters.get(i).nextOperand + " ");
                    }
                }
                builder.append(";");
            }
        } else if (command.equals(UPDATE)) {
            builder.append(" ").append(tableName);
            if (filters.size() > 0) {
                builder.append(" SET ");
                for (int i = 0; i < filters.size(); i++) {
                    // builder.append(filters.get(i).column)
                    //        .append(" = \"")
                    //        .append(filters.get(i).value)
                    //        .append("\"");
                    builder.append(filters.get(i).column)
                           .append(" ")
                           .append(filters.get(i).operator)
                           .append(" \"");
                    if (filters.get(i).operator.equals(SQLParameter.LIKE)) {
                        builder.append("%");
                    }
                    builder.append(filters.get(i).value);
                    if (filters.get(i).operator.equals(SQLParameter.LIKE)) {
                        builder.append("%");
                    }
                    builder.append("\"");
                    if (i < filters.size() - 1 && !filters.get(i).nextOperand.equals("")) {
                        builder.append(" " + filters.get(i).nextOperand + " ");
                    }
                }
                builder.append(";");
            }
        } else if (command.equals(INSERT)) {
            builder.append(" INTO ").append(tableName);
            if (filters.size() > 0) {
                builder.append(" (");
                for (int i = 0; i < filters.size(); i++) {
                    builder.append(filters.get(i).column);
                    if (i < filters.size() - 1) {
                        builder.append(", ");
                    }
                }
                builder.append(") VALUES (");
                for (int i = 0; i < filters.size(); i++) {
                    builder.append("\"")
                           .append(filters.get(i).value)
                           .append("\"");
                    if (i < filters.size() - 1) {
                        builder.append(", ");
                    }
                }
                builder.append(");");
            }
        }

        return builder.toString();
    }
}
