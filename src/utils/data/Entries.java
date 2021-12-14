package utils.data;

import java.util.ArrayList;

public class Entries {
    private static ArrayList<Entry> decryptedData = new ArrayList<>();
    private static int currentIdentityNumber = 0;

    public static int getIdentity() {
        return currentIdentityNumber;
    }

    private static void hasUpdated() {
        currentIdentityNumber = (int) (Math.random() * 1000000);
    }

    public static void add(Entry entry) {
        decryptedData.add(entry);
        hasUpdated();
    }

    public static void remove(int index) {
        decryptedData.remove(index);
        hasUpdated();
    }

    public static void remove(Entry entry) {

        // Search decryptedDatat that has same ID
        for (int i = 0; i < decryptedData.size(); i++) {
            if (decryptedData.get(i).getId() == entry.getId()) {
                remove(i);
                break;
            }
        }
    }

    public static ArrayList<Entry> getEntries() {
        return decryptedData;
    }

}
