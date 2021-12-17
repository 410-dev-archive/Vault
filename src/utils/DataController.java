package utils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.sql.ResultSet;
import java.util.Base64;

import database.SQLParameter;
import database.SQLStatementBuilder;
import database.SQLite3;
import utils.data.Entries;
import utils.data.Entry;
import utils.data.UserInfo;

public class DataController {

    public static final int EXIT_SUCCESS = 0;
    public static final int EXIT_ENCRYPT_FAILURE = 1;
    public static final int EXIT_DECRYPT_FAILURE = 2;
    public static final int EXIT_FAILURE = 3;
    public static final int EXIT_FILE_NOT_FOUND = 4;
    public static final int EXIT_FILE_IO_FAILURE = 5;
    public static final int EXIT_SQL_FAILURE = 6;

    public static Entry buildEntry(
        int id, 
        String data, 
        String type, 
        String addedDateCompiled, 
        String readDateCompiled, 
        String tagsCompiled, 
        String modifiedDateCompiled, 
        String title,
        boolean isEncrypted) {
            
        Entry entry = new Entry(isEncrypted);
        entry.setId(id);
        entry.setObject(data);
        entry.setType(type);
        entry.setAddedDate(addedDateCompiled);
        entry.setReadDate(readDateCompiled);
        entry.setTags(tagsCompiled);
        entry.setModifiedDate(modifiedDateCompiled);
        entry.setName(title);
        return entry;
    }

    public static final int INDEX_CONTENT = 0;
    public static final int INDEX_EXTENSION = 1;
    public static final int EXPECTED_LENGTH = 2;
    public static final int INDEX_ERROR = 2;

    public static String[] readFile(String path) {
        try {
            byte[] bytes = Base64.getEncoder().encode(Files.readAllBytes(new File(path).toPath()));
            String extension = path.substring(path.lastIndexOf(".") + 1);
            String savableContent = new String(bytes, "UTF-8");
            return new String[] {savableContent, extension};
        }catch (NoSuchFileException e) {
            e.printStackTrace();
            return new String[]{null, null, EXIT_FILE_NOT_FOUND + ""};
        }catch (Exception e) {
            e.printStackTrace();
            return new String[]{null, null, EXIT_FILE_IO_FAILURE + ""};
        }
    }

    // Parameter of action: SQLStatementBuilder.UPDATE or SQLStatementBuilder.INSERT
    public static int addOrEditData(Entry parentEntry, boolean isFile, UserInfo userInfo, String action) {
        String savableContent = parentEntry.getObject();
        String extension = "";
        if (isFile) {
            String[] fileData = readFile(parentEntry.getObject());
            if (fileData.length > EXPECTED_LENGTH) {
                switch(Integer.parseInt(fileData[INDEX_ERROR])) {
                    case EXIT_FILE_NOT_FOUND:
                        return EXIT_FILE_NOT_FOUND;
                    case EXIT_FILE_IO_FAILURE:
                        return EXIT_FILE_IO_FAILURE;
                }
            }
        }else {
            extension = "Normal Text";
        }

        // Encrypt
        Entry newEntry = new Entry(false);
        newEntry.setName(parentEntry.getName());
        newEntry.setModifiedDate(parentEntry.getModifiedDate());
        newEntry.addModifiedDate();
        newEntry.setAddedDate(parentEntry.getAddedDate());
        newEntry.setTags(parentEntry.getTags());
        newEntry.setType(extension);
        newEntry.setObject(savableContent);

        Entry unencryptedEntry = new Entry(false);
        unencryptedEntry.setName(parentEntry.getName());
        unencryptedEntry.setModifiedDate(parentEntry.getModifiedDate());
        unencryptedEntry.addModifiedDate();
        unencryptedEntry.setAddedDate(parentEntry.getAddedDate());
        unencryptedEntry.setTags(parentEntry.getTags());
        unencryptedEntry.setType(extension);
        unencryptedEntry.setObject(savableContent);

        switch(newEntry.encrypt(userInfo)) {
            case Entry.ENCRYPT_SUCCESS:
                break;
            case Entry.ENCRYPT_FAILURE:
                return EXIT_ENCRYPT_FAILURE;
            case Entry.ENCRYPTED_ALREADY:
                break;
            default:
                return EXIT_FAILURE;
        };

        // Save to database
        SQLParameter a = new SQLParameter();
        SQLParameter b = new SQLParameter();
        SQLParameter c = new SQLParameter();
        SQLParameter d = new SQLParameter();
        SQLParameter e = new SQLParameter();
        SQLParameter f = new SQLParameter();
        SQLParameter g = new SQLParameter();
        SQLParameter h = new SQLParameter();

        a.column = "data";
        a.value = newEntry.getObject();
        a.operator = SQLParameter.EQUAL;
        a.nextOperand = ",";

        b.column = "owner";
        b.value = userInfo.getLoginToken();
        b.operator = SQLParameter.EQUAL;
        b.nextOperand = ",";

        c.column = "type";
        c.value = newEntry.getType();
        c.operator = SQLParameter.EQUAL;
        c.nextOperand = ",";

        d.column = "addedDate";
        d.value = newEntry.getAddedDate();
        d.operator = SQLParameter.EQUAL;
        d.nextOperand = ",";

        e.column = "readDate";
        e.value = newEntry.getReadDateString();
        e.operator = SQLParameter.EQUAL;
        e.nextOperand = ",";

        f.column = "tags";
        f.value = newEntry.getTagsString();
        f.operator = SQLParameter.EQUAL;
        f.nextOperand = ",";

        g.column = "modifiedDate";
        g.value = newEntry.getModifiedDateString();
        g.operator = SQLParameter.EQUAL;
        g.nextOperand = ",";

        h.column = "title";
        h.value = newEntry.getName();
        h.operator = SQLParameter.EQUAL;
        if (action.equals(SQLStatementBuilder.UPDATE)) {
            h.nextOperand = ",";
        }

        SQLStatementBuilder sql = new SQLStatementBuilder("data", action);
        sql.addParameter(a);
        sql.addParameter(b);
        sql.addParameter(c);
        sql.addParameter(d);
        sql.addParameter(e);
        sql.addParameter(f);
        sql.addParameter(g);
        sql.addParameter(h);
        
        if (action.equals(SQLStatementBuilder.UPDATE)) {
            SQLParameter searchFor = new SQLParameter();
            searchFor.column = "id";
            searchFor.value = parentEntry.getId() + "";
            searchFor.operator = SQLParameter.EQUAL;

            sql.addParameter2(searchFor);
        }

        try {
            SQLite3.executeQuery(sql);
            SQLite3.close();

            if (action.equals(SQLStatementBuilder.INSERT)) {
                ResultSet rs = DataController.getList(userInfo);
                long id = 0;
                while (rs.next()){
                    id = rs.getInt("id");
                }

                unencryptedEntry.setId(id); 
                Entries.add(unencryptedEntry);
            }else{
                Entries.remove(parentEntry);
                Entries.add(unencryptedEntry);
            }
            return EXIT_SUCCESS;
        }catch(Exception e2) {
            e2.printStackTrace();
            return EXIT_SQL_FAILURE;
        }

    }

    public static int updateDataOfColum(String id, String column, String data, boolean doEncrypt, UserInfo userInfo) {
        if (doEncrypt) {
            try {
                data = CoreCryptography.encrypt(data, userInfo.getDecryptString(userInfo.getLoginToken()));
            }catch (Exception e) {
                e.printStackTrace();
                return EXIT_ENCRYPT_FAILURE;
            }
        }

        SQLParameter a = new SQLParameter();
        a.column = column;
        a.value = data;

        SQLStatementBuilder sql = new SQLStatementBuilder("data", SQLStatementBuilder.UPDATE);
        sql.addParameter(a);

        SQLParameter searchFor = new SQLParameter();
        searchFor.column = "id";
        searchFor.value = id;
        searchFor.operator = SQLParameter.EQUAL;

        sql.addParameter2(searchFor);

        try {
            SQLite3.executeQuery(sql);
            SQLite3.close();
            return EXIT_SUCCESS;
        }catch(Exception e) {
            e.printStackTrace();
            return EXIT_SQL_FAILURE;
        }
    }
    

    public static int deleteRow(Entry entry) {
        try{
            SQLParameter parameter = new SQLParameter();
            parameter.column = "id";
            parameter.value = entry.getId() + "";
            parameter.operator = SQLParameter.EQUAL;

            SQLStatementBuilder builder = new SQLStatementBuilder("data", SQLStatementBuilder.DELETE);
            builder.addParameter(parameter);

            SQLite3.executeQuery(builder);
            Entries.remove(entry);
            SQLite3.close();
            return EXIT_SUCCESS;
        }catch(Exception e) {
            e.printStackTrace();
            return EXIT_SQL_FAILURE;
        }
    }

    public static ResultSet getList(UserInfo info) {
        SQLStatementBuilder builder = new SQLStatementBuilder("data", SQLStatementBuilder.SELECT);
        SQLParameter parameter = new SQLParameter();
        parameter.column = "owner";
        parameter.value = info.getLoginToken();
        parameter.operator = SQLParameter.EQUAL;
        builder.addParameter(parameter);

        try {
            return SQLite3.executeQuery(builder);
        }catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
