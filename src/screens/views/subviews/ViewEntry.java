package screens.views.subviews;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.MouseInputAdapter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.DefaultStyledDocument.ElementSpec;

import database.SQLParameter;
import database.SQLStatementBuilder;
import database.SQLite3;

import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Base64;

import screens.ColorScheme;
import screens.UpdatableColor;
import screens.ViewDimension;
import utils.CoreCryptography;
import utils.DateManager;
import utils.data.Entries;
import utils.data.Entry;
import utils.data.UserInfo;

public class ViewEntry extends JPanel implements UpdatableColor {

    private static String data = "";

    private static JFrame frame;
    private static Entry entry;
    private static UserInfo userInfo;
    private JButton delete = new JButton("Delete");
    private JButton export = new JButton("Export");
    private JButton viewTimestamps = new JButton("View Timestamps");
    private JButton modifiedTimestamp = new JButton("Modified Timestamp");
    private JButton editNormalText = new JButton("Edit");
    private JTextField title = new JTextField();
    private JTextField tags = new JTextField();
    private JTextField content = new JTextField();

    private MouseInputAdapter onEditButtonClicked = new MouseInputAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            try {
                String contentString = "";
                boolean isFile = false;
                if (entry.getType().equals("Normal Text")) {
                    // Update as normal text
                    int returned = JOptionPane.showConfirmDialog(null, "Do you want to overwrite the data?", "File Selected", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
                    if (returned == JOptionPane.OK_OPTION) {
                        isFile = false;
                        contentString = content.getText();
                    }else{
                        return;
                    }
                }else{
                    // Update as file
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                    int result = fileChooser.showOpenDialog(null);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = fileChooser.getSelectedFile();
                        int returned = JOptionPane.showConfirmDialog(null, "You have selected a file to store in the database: " + selectedFile.getAbsolutePath(), "File Selected", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
                        if (returned == JOptionPane.OK_OPTION) {
                            isFile = true;
                            contentString = selectedFile.getAbsolutePath();
                            JOptionPane.showMessageDialog(null, "File selected: " + selectedFile.getAbsolutePath());
                        }else{
                            JOptionPane.showMessageDialog(null, "File not selected");
                            contentString = "";
                            isFile = false;
                            return;
                        }
                    }
                }

                save(isFile, contentString);
            }catch(Exception ex) {
                ex.printStackTrace();
            }
        }
    };

    public ViewEntry(JFrame frame, Entry entry, UserInfo userInfo) {
        super();
        this.setBackground(ColorScheme.background);
        this.setLayout(null);
        this.setBounds(0, 0, frame.getWidth(), frame.getHeight());

        ViewEntry.frame = frame;
        ViewEntry.entry = entry;
        ViewEntry.userInfo = userInfo;
        ViewEntry.frame.setMinimumSize(new java.awt.Dimension(400, 500));

        JLabel title = new JLabel("Decrypting contents...");
        title.setBounds(frame.getWidth() / 2 - title.getPreferredSize().width / 2, frame.getHeight() / 2 - title.getPreferredSize().height / 2, title.getPreferredSize().width, title.getPreferredSize().height);
        title.setForeground(ColorScheme.text);
        this.add(title);
        

        Thread asyncThread = new Thread() {
            @Override
            public void run() {
                try {
                    data = entry.getObject();
                    data = CoreCryptography.decrypt(data, ViewEntry.userInfo.getDecryptString(ViewEntry.userInfo.getLoginToken()));
                    remove(title);
                    buildView();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        asyncThread.start();
    }

    public void buildView() {
        
        title.setText(entry.getName());

        if (entry.getType().equals("Normal Text")) {
            content.setText(data);
            editNormalText = new JButton("Edit");
        }else{
            content.setText("File with extension: " + entry.getType());
            content.setEditable(false);
        }

        String tagsStr = "";
        for(String tag : entry.getTags()) {
            tagsStr += "#" + tag + "  ";
        }
        tags.setText(tagsStr);

        delete.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this entry?", "Confirm", JOptionPane.YES_NO_OPTION);
                if(confirm == JOptionPane.YES_OPTION) {
                    try {
                        SQLParameter parameter = new SQLParameter();
                        parameter.column = "id";
                        parameter.value = entry.getId() + "";
                        parameter.operator = SQLParameter.EQUAL;

                        SQLStatementBuilder builder = new SQLStatementBuilder("data", SQLStatementBuilder.DELETE);
                        builder.addParameter(parameter);

                        SQLite3.executeQuery(builder);
                        Entries.remove(entry);
                        
                        JOptionPane.showMessageDialog(null, "Entry deleted successfully!");
                        frame.dispose();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        export.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    // Select file
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    fileChooser.setDialogTitle("Select file to export to");
                    FileNameExtensionFilter filter = new FileNameExtensionFilter("Document exported from Vault", entry.getType());
                    fileChooser.setFileFilter(filter);
                    int result = fileChooser.showSaveDialog(null);

                    if (result == JFileChooser.APPROVE_OPTION) {
                        String filePath = fileChooser.getSelectedFile().getAbsolutePath();
                        if (!filePath.endsWith("." + entry.getType())) filePath += "." + entry.getType();
                        File file = new File(filePath);
                        FileOutputStream fos = new FileOutputStream(file);
                        fos.write(Base64.getDecoder().decode(data));
                        fos.close();
                        JOptionPane.showMessageDialog(null, "File exported successfully!");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        viewTimestamps.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    JFrame frame = new JFrame("View Timestamps");
                    frame.setSize(500, 500);
                    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    frame.setLayout(null);
                    frame.setVisible(true);

                    ArrayList<String> timestamps = entry.getReadDate();
                    new Timestamps(timestamps, frame);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        modifiedTimestamp.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    JFrame frame = new JFrame("Modified Timestamps");
                    frame.setSize(500, 500);
                    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    frame.setLayout(null);
                    frame.setVisible(true);

                    ArrayList<String> timestamps = entry.getModifiedDate();
                    new Timestamps(timestamps, frame);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        editNormalText.addMouseListener(onEditButtonClicked);

        asyncUIUpdate();

        ViewEntry.frame.setContentPane(this);
        ViewEntry.frame.setVisible(true);
    }

    private void save(boolean isFile, String content) {
        editNormalText.removeMouseListener(onEditButtonClicked);
        editNormalText.setText("Updating...");
        editNormalText.setEnabled(false);
        Thread action = new Thread() {
            public void run() {
                String savableContent = content;
                String extension = "";
                if (isFile) {
                    // Read file to base64
                    try {
                        byte[] bytes = Base64.getEncoder().encode(Files.readAllBytes(new File(content).toPath()));
                        savableContent = new String(bytes, "UTF-8");
                        extension = content.substring(content.lastIndexOf(".") + 1);
                    }catch (NoSuchFileException e) {
                        JOptionPane.showMessageDialog(null, "File not found");
                        return;
                    }catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error reading file");
                        return;
                    }
                }else {
                    extension = "Normal Text";
                }

                // Encrypt
                Entry newEntry = new Entry();
                newEntry.setName(title.getText());
                newEntry.setModifiedDate(entry.getModifiedDate());
                newEntry.addModifiedDate();
                newEntry.setAddedDate(entry.getAddedDate());
                newEntry.setTags(tags.getText());
                newEntry.setType(extension);

                String addedDate = entry.getAddedDate();
                String name = "";
                String object = "";
                String readDate = "";
                String modifiedDate = addedDate + ">>>";
                String ntags = "";
                String type = "";

                try{
                    
                    name = CoreCryptography.encrypt(title.getText(), userInfo.getDecryptString(userInfo.getLoginToken()));
                    object = CoreCryptography.encrypt(savableContent, userInfo.getDecryptString(userInfo.getLoginToken()));
                    readDate = CoreCryptography.encrypt(addedDate, userInfo.getDecryptString(userInfo.getLoginToken()));
                    modifiedDate = CoreCryptography.encrypt(addedDate, userInfo.getDecryptString(userInfo.getLoginToken()));
                    if (tags.getText().replaceAll(" ", "").replaceAll("#", "").equals("")) {
                        ntags = CoreCryptography.encrypt(tags.getText(), userInfo.getDecryptString(userInfo.getLoginToken()));
                    }else {
                        ntags = CoreCryptography.encrypt("", userInfo.getDecryptString(userInfo.getLoginToken()));
                    }
                    addedDate = CoreCryptography.encrypt(addedDate, userInfo.getDecryptString(userInfo.getLoginToken()));
                    type = CoreCryptography.encrypt(extension, userInfo.getDecryptString(userInfo.getLoginToken()));

                    newEntry.setObject(object);
                }catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error encrypting content");
                    return;
                }

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
                a.value = object;
                a.nextOperand = ",";

                b.column = "owner";
                b.value = userInfo.getLoginToken();
                b.nextOperand = ",";

                c.column = "type";
                c.value = type;
                c.nextOperand = ",";

                d.column = "addedDate";
                d.value = addedDate;
                d.nextOperand = ",";

                e.column = "readDate";
                e.value = readDate;
                e.nextOperand = ",";

                f.column = "tags";
                f.value = ntags;
                f.nextOperand = ",";

                g.column = "modifiedDate";
                g.value = modifiedDate;
                g.nextOperand = ",";

                h.column = "title";
                h.value = name;
                h.nextOperand = ",";

                SQLStatementBuilder sql = new SQLStatementBuilder("data", SQLStatementBuilder.UPDATE);
                sql.addParameter(a);
                sql.addParameter(b);
                sql.addParameter(c);
                sql.addParameter(d);
                sql.addParameter(e);
                sql.addParameter(f);
                sql.addParameter(g);
                sql.addParameter(h);
                
                SQLParameter searchFor = new SQLParameter();
                searchFor.column = "id";
                searchFor.value = entry.getId() + "";
                searchFor.operator = SQLParameter.EQUAL;

                sql.addParameter2(searchFor);

                try {
                    SQLite3.executeQuery(sql);
                    SQLite3.close();

                    Entries.remove(entry);
                    Entries.add(newEntry);
                    JOptionPane.showMessageDialog(null, "Successfully saved to database");
                    editNormalText.setText("Updated!");
                }catch(Exception e2) {
                    e2.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error saving to database");
                    editNormalText.setText("Error while updating");
                }

                editNormalText.addMouseListener(onEditButtonClicked);
                editNormalText.setEnabled(true);
            }
        };

        action.start();
    }

    private void updateCurrentUI() {

        this.removeAll();

        ViewDimension frameDimension = new ViewDimension();
        frameDimension.width = frame.getWidth();
        frameDimension.height = frame.getHeight();

        ViewDimension titleDimension = new ViewDimension();
        ViewDimension contentDimension = new ViewDimension();
        ViewDimension tagsDimension = new ViewDimension();
        ViewDimension deleteButtonDimension = new ViewDimension();
        ViewDimension exportButtonDimension = new ViewDimension();
        ViewDimension viewTimestampsButtonDimension = new ViewDimension();
        ViewDimension modifiedTimestampButtonDimension = new ViewDimension();
        ViewDimension editNormalTextButtonDimension = new ViewDimension();

        titleDimension.width = frameDimension.width - 10;
        titleDimension.height = title.getPreferredSize().height;

        tagsDimension.width = frameDimension.width - 10;
        tagsDimension.height = tags.getPreferredSize().height;

        deleteButtonDimension.width = frameDimension.width - 10;
        deleteButtonDimension.height = delete.getPreferredSize().height;

        exportButtonDimension.width = frameDimension.width - 10;
        exportButtonDimension.height = export.getPreferredSize().height;

        viewTimestampsButtonDimension.width = frameDimension.width - 10;
        viewTimestampsButtonDimension.height = viewTimestamps.getPreferredSize().height;

        modifiedTimestampButtonDimension.width = frameDimension.width - 10;
        modifiedTimestampButtonDimension.height = modifiedTimestamp.getPreferredSize().height;

        editNormalTextButtonDimension.width = frameDimension.width - 10;
        editNormalTextButtonDimension.height = editNormalText.getPreferredSize().height;

        contentDimension.width = frameDimension.width - 10;
        contentDimension.height = frameDimension.height - titleDimension.height - tagsDimension.height - deleteButtonDimension.height - exportButtonDimension.height - viewTimestampsButtonDimension.height - modifiedTimestampButtonDimension.height - editNormalTextButtonDimension.height - 90;

        titleDimension.alignCenter(frameDimension);
        tagsDimension.alignCenter(frameDimension);
        deleteButtonDimension.alignCenter(frameDimension);
        exportButtonDimension.alignCenter(frameDimension);
        viewTimestampsButtonDimension.alignCenter(frameDimension);
        modifiedTimestampButtonDimension.alignCenter(frameDimension);
        editNormalTextButtonDimension.alignCenter(frameDimension);
        contentDimension.alignCenter(frameDimension);

        titleDimension.toTop(frameDimension);
        tagsDimension.y = titleDimension.y + titleDimension.height + 5;
        contentDimension.y = tagsDimension.y + tagsDimension.height + 5;
        deleteButtonDimension.y = contentDimension.y + contentDimension.height + 5;
        exportButtonDimension.y = deleteButtonDimension.y + deleteButtonDimension.height + 5;
        viewTimestampsButtonDimension.y = exportButtonDimension.y + exportButtonDimension.height + 5;
        modifiedTimestampButtonDimension.y = viewTimestampsButtonDimension.y + viewTimestampsButtonDimension.height + 5;
        editNormalTextButtonDimension.y = modifiedTimestampButtonDimension.y + modifiedTimestampButtonDimension.height + 5;

        title.setBounds(titleDimension.x, titleDimension.y, titleDimension.width, titleDimension.height);
        tags.setBounds(tagsDimension.x, tagsDimension.y, tagsDimension.width, tagsDimension.height);
        content.setBounds(contentDimension.x, contentDimension.y, contentDimension.width, contentDimension.height);
        delete.setBounds(deleteButtonDimension.x, deleteButtonDimension.y, deleteButtonDimension.width, deleteButtonDimension.height);
        export.setBounds(exportButtonDimension.x, exportButtonDimension.y, exportButtonDimension.width, exportButtonDimension.height);
        viewTimestamps.setBounds(viewTimestampsButtonDimension.x, viewTimestampsButtonDimension.y, viewTimestampsButtonDimension.width, viewTimestampsButtonDimension.height);
        modifiedTimestamp.setBounds(modifiedTimestampButtonDimension.x, modifiedTimestampButtonDimension.y, modifiedTimestampButtonDimension.width, modifiedTimestampButtonDimension.height);
        editNormalText.setBounds(editNormalTextButtonDimension.x, editNormalTextButtonDimension.y, editNormalTextButtonDimension.width, editNormalTextButtonDimension.height);

        title.setForeground(ColorScheme.text);
        tags.setForeground(ColorScheme.text);
        content.setForeground(ColorScheme.text);
        delete.setForeground(ColorScheme.text);
        export.setForeground(ColorScheme.text);
        viewTimestamps.setForeground(ColorScheme.text);
        modifiedTimestamp.setForeground(ColorScheme.text);
        editNormalText.setForeground(ColorScheme.text); 
        
        if (entry.getType().equals("Normal Text")) {
            editNormalText.setText("Save");   
        }else{
            editNormalText.setText("Change File");
        }

        this.add(title);
        this.add(content);
        this.add(tags);
        this.add(delete);
        this.add(export);
        this.add(viewTimestamps);
        this.add(modifiedTimestamp);
        this.add(editNormalText);
        repaint();
    }

    private void asyncUIUpdate() {
        Thread t = new Thread() {
            public void run() {
                int width = 0;
                int height = 0;
                long hasLoaded = 0;
                while(true) {
                    if (width != frame.getWidth() || height != frame.getHeight() || hasLoaded < 2) {
                        width = frame.getWidth();
                        height = frame.getHeight();
                        hasLoaded++;
                        updateCurrentUI();
                    }

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        t.start();
    }

    @Override
    public void updateColor() {
        this.setBackground(ColorScheme.background);
        repaint();
    }
    
}
