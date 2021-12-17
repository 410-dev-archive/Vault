import java.io.File;

import database.SQLite3;
import screens.Frame;
import screens.views.Login;
import screens.views.Signup;
import utils.data.Constants;

public class Main {
    public static void main(String[] args) throws Exception {

        boolean ignoreCompatibility = false;

        if (args.length > 0) {
            // Check if there is -d flag
            for(int i = 0; i < args.length; i++) {
                if (args[i].equals("-d")) {
                    if (args.length > i + 1) {
                        Constants.DATABASE_NAME = args[i + 1];
                    }
                }
            }

            // Check if there is --ignore-compatibility flag
            for(int i = 0; i < args.length; i++) {
                if (args[i].equals("--ignore-compatibility")) {
                    ignoreCompatibility = true;
                }
            }
        }

        SQLite3.dbFile = Constants.DATABASE_NAME;
        Frame.createFrame();

        // Check if file exist
        File db = new File(Constants.DATABASE_NAME);

        // If exists, then check compatibility + login. Otherwise create new database and signup
        if (db.isFile()) {
            if (!ignoreCompatibility) SQLite3.checkCompatibility();
            Frame.setContent(new Login());
        } else {
            SQLite3.initialSetup();
            Frame.setContent(new Signup());
        }

    }
}
