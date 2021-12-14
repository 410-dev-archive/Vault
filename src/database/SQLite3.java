package database;
import java.sql.Connection; 
import java.sql.DriverManager; 
import java.sql.ResultSet; 
import java.sql.Statement;

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
}
