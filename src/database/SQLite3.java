package database;
import java.sql.Connection; 
import java.sql.DriverManager; 
import java.sql.ResultSet; 
import java.sql.Statement;

import javax.swing.JOptionPane;

import screens.ColorScheme;
import utils.data.Constants;

public class SQLite3 {

    private static Connection con = null; 

    public static String dbFile = "";

    public static Connection close() {
        if(con != null) { 
            try {
                con.close();
            }catch(Exception e) {} 
        }

        return con;
    }

    public static ResultSet executeQuery(String queryStatement) throws Exception {
        try { 
            // SQLite JDBC 체크 
            Class.forName("org.sqlite.JDBC"); 
            // SQLite 데이터베이스 파일에 연결
            if (dbFile.equals("")) {
                throw new Exception("Empty DB Path");
            }
            SQLite3.close();
            con = DriverManager.getConnection("jdbc:sqlite:" + dbFile); 
            // SQL 수행 
            Statement stat = con.createStatement(); 
            ResultSet rs = null;
            try{
                rs = stat.executeQuery(queryStatement); 
            }catch(Exception e) {
                if (!e.toString().contains("query does not return ResultSet")) {
                    throw e;
                }
            }
            return rs;
        }catch(Exception e) { 
            throw e;
        }
    }

    public static ResultSet executeQuery(SQLStatementBuilder statement) throws Exception {
        SQLite3.close();
        return executeQuery(statement.build());
    }

    public static void initialSetup() throws Exception {
        String sqlStatementForData = "CREATE TABLE \"data\" (\"id\" integer,\"data\" text,\"owner\" text,\"type\" text,\"addedDate\" text,\"readDate\" text,\"tags\" text,\"modifiedDate\" text,\"title\" text,\"file\" BLOB, PRIMARY KEY (id))";
        String sqlStatementForUser = "CREATE TABLE \"users\" (\"id\" integerserial,\"user\" text NOT NULL, PRIMARY KEY (id))";
        String sqlStatementForCompatibility = "CREATE TABLE \"compatibility\" (\"id\" integerserial,\"key\" text NOT NULL,\"value\" text NOT NULL, PRIMARY KEY (id))";
        String sqlStatementForConfigurations = "CREATE TABLE \"configurations\" (\"id\" integerserial,\"key\" text NOT NULL,\"value\" text NOT NULL, PRIMARY KEY (id))";
        executeQuery(sqlStatementForData);
        executeQuery(sqlStatementForUser);
        executeQuery(sqlStatementForCompatibility);
        executeQuery(sqlStatementForConfigurations);

        executeQuery("INSERT INTO compatibility (key, value) VALUES ('version', '" + Constants.VERSION + "');");
        executeQuery("INSERT INTO compatibility (key, value) VALUES ('databaseType', '" + Constants.DBTYPE + "');");
        executeQuery("INSERT INTO configurations (key, value) VALUES ('color', '" + ColorScheme.ID_WHITE + "');");
    }

    public static void checkCompatibility() {
        try {
            ResultSet rs = executeQuery("SELECT * FROM compatibility where key = 'databaseType'");
            if (rs.next()) {
                String databaseType = rs.getString("value");
                if (!databaseType.equals(Constants.DBTYPE + "")) {
                    throw new Exception("Database type is not compatible");
                }
                ResultSet rs2 = executeQuery("SELECT * FROM compatibility where key = 'version'");
                if (rs2.next()) {
                    String version = rs2.getString("value");
                    if (!version.equals(Constants.VERSION)) {
                        if (Integer.parseInt(version) < Integer.parseInt(Constants.VERSION)) {
                            int exit = JOptionPane.showConfirmDialog(null, "Database is created in older version of Vault. Press OK to update, or close to exit.");
                            if (exit == JOptionPane.OK_OPTION) {
                                SQLite3.close();
                            } else {
                                JOptionPane.showMessageDialog(null, "Unable to load database.");
                                System.exit(0);
                            }
                        }else{
                            JOptionPane.showMessageDialog(null, "Database is created in newer version of Vault. Please update Vault to load this database.");
                            System.exit(0);
                        }
                    }
                }
            } else {
                throw new Exception("Database type is not compatible");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed checking compatibility. Try updating Vault to load this database.");
            System.exit(0);
        }
    }
}
