package screens.views;

import java.sql.ResultSet;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.MouseInputAdapter;

import java.awt.event.MouseEvent;

import database.SQLParameter;
import database.SQLStatementBuilder;
import database.SQLite3;

import screens.ColorScheme;
import screens.Frame;
import screens.UpdatableColor;
import screens.ViewDimension;
import screens.views.subviews.About;
import screens.views.subviews.NewEntry;
import screens.views.subviews.ViewEntry;

import utils.CoreCryptography;
import utils.data.Entries;
import utils.data.Entry;
import utils.data.UserInfo;


public class Home extends JPanel implements UpdatableColor {

    private UserInfo userInfo;

    private static boolean decryptionComplete = false;
    private static boolean fileListReady = false;
    private static ViewDimension frameDimension = new ViewDimension();

    private JList fileLists;
    private JButton addButton = new JButton("Add");
    private JButton aboutButton = new JButton("About");
    private JButton searchButton = new JButton("Search...");

    public Home(UserInfo user) {
        super();
        this.userInfo = user;
        Frame.frame.setResizable(true);
        this.setBackground(ColorScheme.background);
        this.setBounds(0, 0, Frame.frame.getWidth(), Frame.frame.getHeight());
        this.setLayout(null);

        JLabel label = new JLabel("Please wait, currently decrypting storage.");
        label.setBounds(Frame.frame.getWidth() / 2 - 150, Frame.frame.getHeight() / 2 - 50, 300, 100);
        this.add(label);
        repaint();

        decryptData();

        Thread decryptionThread = new Thread() {
            public void run() {
                while (!decryptionComplete) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                remove(label);
                repaint();
                buildUI();
                repaint();
            }
        };
        decryptionThread.start();

    }


    private void asyncAdaptiveGUI() {
        Thread t = new Thread() {
            public void run() {
                while(true) {
                    frameDimension.height = Frame.frame.getHeight();
                    frameDimension.width = Frame.frame.getWidth();

                    ViewDimension addButtonDimension = new ViewDimension();
                    addButtonDimension.width = 100;
                    addButtonDimension.height = 30;

                    ViewDimension aboutButtonDimension = new ViewDimension();
                    aboutButtonDimension.width = 100;
                    aboutButtonDimension.height = 30;

                    ViewDimension searchButtonDimension = new ViewDimension();
                    searchButtonDimension.width = 100;
                    searchButtonDimension.height = 30;

                    addButtonDimension.alignCenter(frameDimension);
                    aboutButtonDimension.alignCenter(frameDimension);
                    searchButtonDimension.alignCenter(frameDimension);

                    addButtonDimension.toBottom(frameDimension);
                    aboutButtonDimension.toBottom(frameDimension);
                    searchButtonDimension.toBottom(frameDimension);

                    addButtonDimension.toRight(frameDimension);
                    aboutButtonDimension.toLeft(frameDimension);

                    addButtonDimension.y -= addButtonDimension.height + 10;
                    aboutButtonDimension.y -= aboutButtonDimension.height + 10;
                    searchButtonDimension.y -= searchButtonDimension.height + 10;
                    
                    addButton.setBounds(addButtonDimension.x, addButtonDimension.y, addButtonDimension.width, addButtonDimension.height);
                    aboutButton.setBounds(aboutButtonDimension.x, aboutButtonDimension.y, aboutButtonDimension.width, aboutButtonDimension.height);
                    searchButton.setBounds(searchButtonDimension.x, searchButtonDimension.y, searchButtonDimension.width, searchButtonDimension.height);

                    add(addButton);
                    add(aboutButton);
                    add(searchButton);
                    repaint();

                    try{
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        t.start();
    }

    private void asyncFileListUpdate() {
        Thread t = new Thread() {
            public void run() {
                int hasEntriesListChanged = -1;
                while (true) {
                    if (hasEntriesListChanged != Entries.getIdentity()) {
                        hasEntriesListChanged = Entries.getIdentity();
                        if (fileLists != null) {
                            remove(fileLists);
                        }
                        ArrayList<String> entryNames = new ArrayList<>();
                        for (Entry entry : Entries.getEntries()) {
                            String tags = "";
                            for (String tag : entry.getTags()) {
                                tags += "#" + tag + " ";
                            }
                            int prefixLength = entry.getType().length() + 2;
                            String typePrefix = "[" + entry.getType() + "]";
                            for(int i = prefixLength; i < 17; i++) {
                                typePrefix += " ";
                            }
                            entryNames.add(typePrefix + entry.getName() + " " + tags + " (" + entry.getAddedDate() + ")");
                        }
                        fileLists = new JList(entryNames.toArray());
                        fileLists.addMouseListener(new MouseInputAdapter() {
                            public void mouseClicked(MouseEvent evt) {
                                JList list = (JList)evt.getSource();
                                if (evt.getClickCount() == 2) {
                                    int index = list.locationToIndex(evt.getPoint());
                                    if (index >= 0) {
                                        JFrame frame = new JFrame(Entries.getEntries().get(index).getName());
                                        frame.setResizable(true);
                                        frame.setSize(500, 600);
                                        frame.setLocationRelativeTo(null);
                                        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                
                                        new ViewEntry(frame, Entries.getEntries().get(index), userInfo);
                                    }
                                }
                            }
                        });
                        fileListReady = true;
                        add(fileLists);
                        repaint();
                    }
                    fileLists.setBounds(0, 0, frameDimension.width, frameDimension.height - 100);
                    
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        t.start();
    }

    private void buildUI() {
        // Create components
        

        // TODO: Add more buttons and functionalities
        asyncFileListUpdate();

        while(true) {
            if(fileListReady) {
                break;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        asyncAdaptiveGUI();

        // Place components
        fileLists.setBounds(0, 0, Frame.frame.getWidth(), Frame.frame.getHeight() - 100);

        // On addButton click
        addButton.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JFrame frame = new JFrame("Add Entry");
                frame.setResizable(true);
                frame.setSize(300, 300); // TODO: Change the dimension
                frame.setLocationRelativeTo(null);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                new NewEntry(frame, userInfo);
            }
        });

        // On aboutButton click
        aboutButton.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (About.isOpen) return;
                About.isOpen = true;
                JFrame frame = new JFrame("About");
                frame.setResizable(false);
                frame.setSize(600, 200);
                frame.setLocationRelativeTo(null);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

                new About(frame);
            }
        });

        // Add components
        this.add(fileLists);
    }


    @Override
    public void updateColor() {
        this.setBackground(ColorScheme.background);
        this.repaint();
    }



    private void decryptData() {
        Thread decryptionThread = new Thread() {
            @Override
            public void run() {
                try {
                    SQLParameter param = new SQLParameter();
                    param.column = "owner";
                    param.value = userInfo.getLoginToken();
                    param.operator = SQLParameter.EQUAL;

                    SQLStatementBuilder builder = new SQLStatementBuilder("data", SQLStatementBuilder.SELECT);
                    ResultSet rs = SQLite3.executeQuery(builder);

                    while(rs.next()) {
                        String id = rs.getInt("id") + "";
                        String data = rs.getString("data");
                        String type = rs.getString("type");
                        String addedDate = rs.getString("addedDate");
                        String modified = rs.getString("modifiedDate");
                        String readDate = rs.getString("readDate");
                        String tags = rs.getString("tags");
                        String title = rs.getString("title");

                        if (addedDate == null || addedDate.equals("") || addedDate.equals("null")) {
                            break;
                        }

                        addedDate = CoreCryptography.decrypt(addedDate, userInfo.getDecryptString(userInfo.getLoginToken()));
                        type = CoreCryptography.decrypt(type, userInfo.getDecryptString(userInfo.getLoginToken()));
                        modified = CoreCryptography.decrypt(modified, userInfo.getDecryptString(userInfo.getLoginToken()));
                        readDate = CoreCryptography.decrypt(readDate, userInfo.getDecryptString(userInfo.getLoginToken()));
                        tags = CoreCryptography.decrypt(tags, userInfo.getDecryptString(userInfo.getLoginToken()));
                        title = CoreCryptography.decrypt(title, userInfo.getDecryptString(userInfo.getLoginToken()));

                        ArrayList<String> tags_list = new ArrayList<>();
                        if(!tags.equals("")) {
                            String[] tags_array = tags.split(",");
                            for(String tag : tags_array) {
                                tags_list.add(tag);
                            }
                        }

                        ArrayList<String> readDate_list = new ArrayList<>();
                        if(!readDate.equals("")) {
                            String[] readDate_array = readDate.split(",");
                            for(String date : readDate_array) {
                                readDate_list.add(date);
                            }
                        }
                        
                        ArrayList<String> modified_list = new ArrayList<>();
                        if(!modified.equals("")) {
                            String[] modified_array = modified.split(",");
                            for(String date : modified_array) {
                                modified_list.add(date);
                            }
                        }

                        Entry entry = new Entry(title, Long.parseLong(id), data, type, addedDate, readDate_list, modified_list, tags_list);
                        Entries.add(entry);
                    }

                    decryptionComplete = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error while decrypting data");
                    System.exit(1);
                    SQLite3.close();
                }
            }
        };
        decryptionThread.start();
    }
}
