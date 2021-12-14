import java.io.File;

import database.SQLite3;
import screens.Frame;
import screens.views.Login;
import screens.views.Signup;

public class Main {
    public static void main(String[] args) throws Exception {

        String databasePath = "/Users/hoyounsong/Code/Vault/Vault.sqlite3";

        if (args.length > 0) {
            // Check if there is -d flag
            for(int i = 0; i < args.length; i++) {
                if (args[i].equals("-d")) {
                    if (args.length > i + 1) {
                        databasePath = args[i + 1];
                    }
                }
            }
        }

        SQLite3.dbFile = databasePath;
        Frame.createFrame();

        // Check if file exist
        File db = new File(databasePath);

        // If exists, then login. Otherwise create new database and signup
        if (db.isFile()) Frame.setContent(new Login());
        else {
            // TODO: Create new database
            Frame.setContent(new Signup());
        }

    }
}
