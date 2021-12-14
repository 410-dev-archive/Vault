package utils.data;

import java.util.ArrayList;

import utils.DateManager;

public class Entry {
    private String name;
    private long id;
    private String object;
    private String type;
    private String addedDate;
    private ArrayList<String> readDate;
    private ArrayList<String> modifiedDate;
    private ArrayList<String> tags;

    public Entry() {}

    public Entry(String name, long id, String object, String type, String addedDate, ArrayList<String> readDate, ArrayList<String> modifiedDate, ArrayList<String> tags) {
        this.name = name;
        this.id = id;
        this.object = object;
        this.type = type;
        this.addedDate = addedDate;
        this.readDate = readDate;
        this.modifiedDate = modifiedDate;
        this.tags = tags;
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
        String readDateString = "";
        for (String date : readDate) {
            readDateString += date + ">>>";
        }
        return readDateString;
    }

    public String getTagsString() {
        String tagsString = "";
        for (String tag : tags) {
            tagsString += tag + " ";
        }
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
        String[] modifiedDateArray = readDate.split(">>>");
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
        String[] modifiedDateArray = modifiedDate.split(">>>");
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
}
