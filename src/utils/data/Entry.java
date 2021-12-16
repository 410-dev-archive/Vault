package utils.data;

import java.util.ArrayList;

import utils.CoreCryptography;
import utils.DateManager;

public class Entry {
    private String name = "";
    private long id = -1;
    private String object = "";
    private String type = "";
    private String addedDate = "";
    private ArrayList<String> readDate = new ArrayList<String>();
    private String readDateString = "";
    private ArrayList<String> modifiedDate = new ArrayList<String>();
    private String modifiedDateString = "";
    private ArrayList<String> tags = new ArrayList<String>();
    private String tagsString = "";
    private boolean isEncrypted = false;
    private boolean isObjectEncrypted = false;

    public static final String ARRAY_SPLITTER = ">>>";

    public static final int ENCRYPT_SUCCESS = 0;
    public static final int ENCRYPT_FAILURE = 1;
    public static final int ENCRYPTED_ALREADY = 2;
    public static final int DECRYPT_SUCCESS = 0;
    public static final int DECRYPT_FAILURE = 1;
    public static final int DECRYPTED_ALREADY = 2;

    public Entry(boolean isEncrypted) {
        this.isEncrypted = isEncrypted;
        this.isObjectEncrypted = isEncrypted;
    }

    public Entry(
        String name, 
        long id, 
        String object, 
        String type, 
        String addedDate,
        ArrayList<String> readDate, 
        ArrayList<String> modifiedDate, 
        ArrayList<String> tags, 
        boolean isEncrypted) {
        this.name = name;
        this.id = id;
        this.object = object;
        this.type = type;
        this.addedDate = addedDate;
        this.readDate = readDate;
        this.modifiedDate = modifiedDate;
        this.tags = tags;
        this.isEncrypted = isEncrypted;
        this.isObjectEncrypted = isEncrypted;
    }

    public Entry(
        String name, 
        String id, 
        String object, 
        String type, 
        String addedDate,
        String readDate, 
        String modifiedDate, 
        String tags, 
        boolean isEncrypted) {
        this.name = name;
        this.id = Long.parseLong(id);
        this.object = object;
        this.type = type;
        this.addedDate = addedDate;
        this.isEncrypted = isEncrypted;
        this.isObjectEncrypted = isEncrypted;

        String[] readDateArray = readDate.split(ARRAY_SPLITTER);
        String[] modifiedDateArray = modifiedDate.split(ARRAY_SPLITTER);
        String[] tagsArray = tags.split(ARRAY_SPLITTER);

        for (String readDateString : readDateArray) {
            this.readDate.add(readDateString);
        }
        for (String modifiedDateString : modifiedDateArray) {
            this.modifiedDate.add(modifiedDateString);
        }
        for (String tagsString : tagsArray) {
            this.tags.add(tagsString);
        }
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public String getObject() {
        return object;
    }

    public String getType() {
        return type;
    }

    public String getAddedDate() {
        return addedDate;
    }

    public ArrayList<String> getReadDate() {
        return readDate;
    }

    public ArrayList<String> getModifiedDate() {
        return modifiedDate;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public String getReadDateString() {
        if (isEncrypted) return readDateString;
        String readDateString = "";
        for (String date : readDate) {
            readDateString += date + ARRAY_SPLITTER;
        }
        this.readDateString = readDateString;
        return readDateString;
    }

    public String getModifiedDateString() {
        if (isEncrypted) return modifiedDateString;
        String modifiedDateString = "";
        for (String date : modifiedDate) {
            modifiedDateString += date + ARRAY_SPLITTER;
        }
        this.modifiedDateString = modifiedDateString;
        return modifiedDateString;
    }

    public String getTagsString() {
        if (isEncrypted) return tagsString;
        String tagsString = "";
        for (String tag : tags) {
            tagsString += tag + " ";
        }
        this.tagsString = tagsString;
        return tagsString;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }

    public void setReadDate(ArrayList<String> readDate) {
        this.readDate = readDate;
    }

    public void setReadDate(String readDate) {
        ArrayList<String> modifiedDateList = new ArrayList<String>();
        String[] modifiedDateArray = readDate.split(ARRAY_SPLITTER);
        for (String date : modifiedDateArray) {
            modifiedDateList.add(date);
        }
        this.readDate = modifiedDateList;
    }

    public void addReadDate() {
        this.readDate.add(DateManager.getTimestamp());
    }

    public void setModifiedDate(ArrayList<String> modifiedDate) {
        this.modifiedDate = modifiedDate;
    }

    public void setModifiedDate(String modifiedDate) {
        ArrayList<String> modifiedDateList = new ArrayList<String>();
        String[] modifiedDateArray = modifiedDate.split(ARRAY_SPLITTER);
        for (String date : modifiedDateArray) {
            modifiedDateList.add(date);
        }
        this.modifiedDate = modifiedDateList;
    }

    public void addModifiedDate() {
        this.modifiedDate.add(DateManager.getTimestamp());
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public void setTags(String tags) {
        ArrayList<String> tagsList = new ArrayList<String>();
        String[] tagsArray = tags.split(" ");
        for (String tag : tagsArray) {
            tagsList.add(tag);
        }
        this.tags = tagsList;
    }

    public int encryptObjectOnly(UserInfo userInfo) {
        if (!isObjectEncrypted) {
            try {
                object = CoreCryptography.encrypt(object, userInfo.getDecryptString(userInfo.getLoginToken()));
                isObjectEncrypted = true;
                return ENCRYPT_SUCCESS;
            } catch (Exception e) {
                return ENCRYPT_FAILURE;
            }
        }
        return ENCRYPTED_ALREADY;
    }

    public int encrypt(UserInfo userInfo) {
        return encrypt(userInfo, true);
    }

    public int encrypt(UserInfo userInfo, boolean doObjects) {
        try {
            if (isEncrypted) {
                if (doObjects && isObjectEncrypted) {
                    return ENCRYPTED_ALREADY;
                }else if (doObjects && !isObjectEncrypted) {
                    object = CoreCryptography.encrypt(object, userInfo.getDecryptString(userInfo.getLoginToken()));
                    isObjectEncrypted = true;
                    return ENCRYPT_SUCCESS;
                }
            }
            name = CoreCryptography.encrypt(name, userInfo.getDecryptString(userInfo.getLoginToken()));
            if (doObjects) object = CoreCryptography.encrypt(object, userInfo.getDecryptString(userInfo.getLoginToken()));
            type = CoreCryptography.encrypt(type, userInfo.getDecryptString(userInfo.getLoginToken()));
            addedDate = CoreCryptography.encrypt(addedDate, userInfo.getDecryptString(userInfo.getLoginToken()));
            readDateString = CoreCryptography.encrypt(getReadDateString(), userInfo.getDecryptString(userInfo.getLoginToken()));
            modifiedDateString = CoreCryptography.encrypt(getModifiedDateString(), userInfo.getDecryptString(userInfo.getLoginToken()));
            tagsString = CoreCryptography.encrypt(getTagsString(), userInfo.getDecryptString(userInfo.getLoginToken()));
            isEncrypted = true;
            return ENCRYPT_SUCCESS;
        }catch(Exception e) {
            e.printStackTrace();
            return ENCRYPT_FAILURE;
        }
    }

    public int decryptObjectOnly(UserInfo userInfo) {
        if (isObjectEncrypted) {
            try {
                object = CoreCryptography.decrypt(object, userInfo.getDecryptString(userInfo.getLoginToken()));
                isObjectEncrypted = false;
                return DECRYPT_SUCCESS;
            }catch(Exception e) {
                e.printStackTrace();
                return DECRYPT_FAILURE;
            }
        }
        return DECRYPTED_ALREADY;
    }

    public int decrypt(UserInfo userInfo) {
        return decrypt(userInfo, true);
    }

    public int decrypt(UserInfo userInfo, boolean doObjects) {
        try {
            if (!isEncrypted) {
                if (doObjects && !isObjectEncrypted) {
                    return DECRYPTED_ALREADY;
                }else if (doObjects && isObjectEncrypted) {
                    object = CoreCryptography.decrypt(object, userInfo.getDecryptString(userInfo.getLoginToken()));
                    isObjectEncrypted = false;
                    return DECRYPT_SUCCESS;
                }
            }
            name = CoreCryptography.decrypt(name, userInfo.getDecryptString(userInfo.getLoginToken()));
            if (doObjects) object = CoreCryptography.decrypt(object, userInfo.getDecryptString(userInfo.getLoginToken()));
            type = CoreCryptography.decrypt(type, userInfo.getDecryptString(userInfo.getLoginToken()));
            addedDate = CoreCryptography.decrypt(addedDate, userInfo.getDecryptString(userInfo.getLoginToken()));
            readDateString = CoreCryptography.decrypt(readDateString, userInfo.getDecryptString(userInfo.getLoginToken()));
            modifiedDateString = CoreCryptography.decrypt(modifiedDateString, userInfo.getDecryptString(userInfo.getLoginToken()));
            tagsString = CoreCryptography.decrypt(tagsString, userInfo.getDecryptString(userInfo.getLoginToken()));
            isEncrypted = false;
            return DECRYPT_SUCCESS;
        }catch(Exception e) {
            e.printStackTrace();
            return DECRYPT_FAILURE;
        }
    }
}
