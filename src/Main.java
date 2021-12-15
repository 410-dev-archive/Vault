import java.io.File;

import database.SQLite3;
import screens.Frame;
import screens.views.Login;
import screens.views.Signup;
import utils.data.Static;

public class Main {
    public static void main(String[] args) throws Exception {


        if (args.length > 0) {
            // Check if there is -d flag
            for(int i = 0; i < args.length; i++) {
                if (args[i].equals("-d")) {
                    if (args.length > i + 1) {
                        Static.DATABASE_NAME = args[i + 1];
                    }
                }
            }
        }

        SQLite3.dbFile = Static.DATABASE_NAME;
        Frame.createFrame();

        // Check if file exist
        File db = new File(Static.DATABASE_NAME);

        // If exists, then login. Otherwise create new database and signup
        if (db.isFile()) Frame.setContent(new Login());
        else {
            SQLite3.createDatabase();
            Frame.setContent(new Signup());
        }

    }
}
