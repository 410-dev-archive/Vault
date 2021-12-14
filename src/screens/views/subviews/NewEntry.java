package screens.views.subviews;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.MouseInputAdapter;

import database.SQLParameter;
import database.SQLStatementBuilder;
import database.SQLite3;
import screens.ColorScheme;
import screens.UpdatableColor;
import screens.ViewDimension;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.sql.ResultSet;
import java.util.Base64;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import utils.data.Entries;
import utils.data.Entry;
import utils.data.UserInfo;
import utils.CoreCryptography;
import utils.DateManager;

public class NewEntry extends JPanel implements UpdatableColor {

    private UserInfo userInfo;
    private JFrame frame;

    private String filePath = "";

    private static boolean isUpdated = false;

    private static JTextField fieldExtension = new JTextField();
    private static JTextField fieldTitle = new JTextField();
    private static JTextArea textAreaContent = new JTextArea();
    private static JTextField fieldTags = new JTextField();
    private static JButton saveButton = new JButton("Save");
    private static JLabel labelTitle = new JLabel("Title");
    private static JLabel labelContent = new JLabel("Content");
    private static JButton useFileButton = new JButton("Select File");
    private static JLabel labelTags = new JLabel("Tags (Split by comma)");
    private static JLabel labelExtension = new JLabel("Extension");
    private static JScrollPane fieldContent;

    private MouseInputAdapter onClickSaveAdapter = new MouseInputAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            save(fieldTitle.getText(), textAreaContent.getText(), fieldTags.getText());
        }
    };

    private void updateAdaptiveUI() {
        try {
            if (isUpdated) {
                remove(fieldExtension);
                remove(fieldTitle);
                remove(textAreaContent);
                remove(fieldTags);
                remove(saveButton);
                remove(labelTitle);
                remove(labelContent);
                remove(useFileButton);
                remove(labelTags);
                remove(labelExtension);
                remove(fieldContent);
            }else{
                isUpdated = true;
            }

            ViewDimension frameDimension = new ViewDimension();
            frameDimension.width = frame.getWidth();
            frameDimension.height = frame.getHeight();

            ViewDimension labelTitleDimension = new ViewDimension();
            ViewDimension fieldTitleDimension = new ViewDimension();
            ViewDimension labelContentDimension = new ViewDimension();
            ViewDimension textAreaContentDimension = new ViewDimension();
            ViewDimension useFileButtonDimension = new ViewDimension();
            ViewDimension labelTagsDimension = new ViewDimension();
            ViewDimension fieldTagsDimension = new ViewDimension();
            ViewDimension labelExtensionDimension = new ViewDimension();
            ViewDimension fieldExtensionDimension = new ViewDimension();
            ViewDimension saveButtonDimension = new ViewDimension();

            labelTitleDimension.width = frameDimension.width - 10;
            labelTitleDimension.height = 25;

            fieldTitleDimension.width = frameDimension.width - 10;
            fieldTitleDimension.height = 25;

            labelContentDimension.width = frameDimension.width - 10;
            labelContentDimension.height = 25;

            labelTagsDimension.width = frameDimension.width - 10;
            labelTagsDimension.height = 25;

            fieldTagsDimension.width = frameDimension.width - 10;
            fieldTagsDimension.height = 25;

            labelExtensionDimension.width = frameDimension.width - 10;
            labelExtensionDimension.height = 25;

            fieldExtensionDimension.width = frameDimension.width - 10;
            fieldExtensionDimension.height = 25;

            saveButtonDimension.width = frameDimension.width - 10;
            saveButtonDimension.height = 25;

            useFileButtonDimension.width = frameDimension.width - 10;
            useFileButtonDimension.height = 25;

            textAreaContentDimension.width = frameDimension.width - 10;
            textAreaContentDimension.height = frameDimension.height - (labelTitleDimension.height + fieldTitleDimension.height + labelContentDimension.height + labelTagsDimension.height + fieldTagsDimension.height + labelExtensionDimension.height + fieldTagsDimension.height + saveButtonDimension.height + useFileButtonDimension.height + 100);

            labelTitleDimension.alignCenter(frameDimension);
            fieldTitleDimension.alignCenter(frameDimension);
            labelContentDimension.alignCenter(frameDimension);
            textAreaContentDimension.alignCenter(frameDimension);
            labelTagsDimension.alignCenter(frameDimension);
            fieldTagsDimension.alignCenter(frameDimension);
            labelExtensionDimension.alignCenter(frameDimension);
            fieldExtensionDimension.alignCenter(frameDimension);
            saveButtonDimension.alignCenter(frameDimension);
            useFileButtonDimension.alignCenter(frameDimension);

            labelTitleDimension.toTop(frameDimension);

            fieldTitleDimension.y = labelTitleDimension.y + labelTitleDimension.height + 5;
            labelContentDimension.y = fieldTitleDimension.y + fieldTitleDimension.height + 5;
            textAreaContentDimension.y = labelContentDimension.y + labelContentDimension.height + 5;
            labelTagsDimension.y = textAreaContentDimension.y + textAreaContentDimension.height + 5;
            fieldTagsDimension.y = labelTagsDimension.y + labelTagsDimension.height + 5;
            labelExtensionDimension.y = fieldTagsDimension.y + fieldTagsDimension.height + 5;
            fieldExtensionDimension.y = labelExtensionDimension.y + labelExtensionDimension.height + 5;
            useFileButtonDimension.y = fieldExtensionDimension.y + fieldExtensionDimension.height + 5;
            saveButtonDimension.y = useFileButtonDimension.y + useFileButtonDimension.height + 5;

            labelTitle.setBounds(labelTitleDimension.x, labelTitleDimension.y, labelTitleDimension.width, labelTitleDimension.height);
            fieldTitle.setBounds(fieldTitleDimension.x, fieldTitleDimension.y, fieldTitleDimension.width, fieldTitleDimension.height);

            labelContent.setBounds(labelContentDimension.x, labelContentDimension.y, labelContentDimension.width, labelContentDimension.height);
            textAreaContent.setBounds(textAreaContentDimension.x, textAreaContentDimension.y, textAreaContentDimension.width, textAreaContentDimension.height);
            fieldContent = new JScrollPane(textAreaContent);

            labelTags.setBounds(labelTagsDimension.x, labelTagsDimension.y, labelTagsDimension.width, labelTagsDimension.height);
            fieldTags.setBounds(fieldTagsDimension.x, fieldTagsDimension.y, fieldTagsDimension.width, fieldTagsDimension.height);

            useFileButton.setBounds(useFileButtonDimension.x, useFileButtonDimension.y, useFileButtonDimension.width, useFileButtonDimension.height);

            labelExtension.setBounds(labelExtensionDimension.x, labelExtensionDimension.y, labelExtensionDimension.width, labelExtensionDimension.height);
            fieldExtension.setBounds(fieldExtensionDimension.x, fieldExtensionDimension.y, fieldExtensionDimension.width, fieldExtensionDimension.height);

            saveButton.setBounds(saveButtonDimension.x, saveButtonDimension.y, saveButtonDimension.width, saveButtonDimension.height);

            fieldContent.setBounds(textAreaContentDimension.x, textAreaContentDimension.y, textAreaContentDimension.width, textAreaContentDimension.height);
            fieldContent.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            fieldContent.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

            add(labelTitle);
            add(fieldTitle);
            add(labelContent);
            add(fieldContent);
            add(useFileButton);
            add(labelTags);
            add(fieldTags);
            add(labelExtension);
            add(fieldExtension);
            add(saveButton);
        }catch(Exception ee) {
            System.out.println(ee.toString());
        }
    }

    private void asyncAdaptiveGUI() {
        Thread t = new Thread() {
            public void run() {

                int alreadyRan = 0;

                int width = 0;
                int height = 0;

                while(true) {
                    if (frame.getWidth() != width || frame.getHeight() != height || alreadyRan < 2) {
                        width = frame.getWidth();
                        height = frame.getHeight();
                        alreadyRan++;
                        updateAdaptiveUI();
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

    public NewEntry(JFrame parentFrame, UserInfo userInfo) {
        super();
        this.frame = parentFrame;
        this.userInfo = userInfo;

        this.setLayout(null);
        this.frame.setSize(500, 500);
        this.setSize(this.frame.getWidth(), this.frame.getHeight());

        this.setBackground(ColorScheme.background);

        useFileButton.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
                int result = fileChooser.showOpenDialog(null);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    int returned = JOptionPane.showConfirmDialog(null, "You have selected a file to store in the database: " + selectedFile.getAbsolutePath(), "File Selected", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
                    if (returned == JOptionPane.OK_OPTION) {
                        filePath = selectedFile.getAbsolutePath();
                        JOptionPane.showMessageDialog(null, "File selected: " + selectedFile.getAbsolutePath());
                        fieldContent.setVisible(false);
                    }else{
                        JOptionPane.showMessageDialog(null, "File not selected");
                        fieldContent.setVisible(true);
                        filePath = "";
                    }
                }
            }
        });

        saveButton.addMouseListener(onClickSaveAdapter);

        // Set bounds
        asyncAdaptiveGUI();

        this.setVisible(true);

        frame.setContentPane(this);
        frame.setVisible(true);
    }


    private void save(String title, String content, String tags) {
        saveButton.removeMouseListener(onClickSaveAdapter);
        saveButton.setText("Saving...");
        saveButton.setEnabled(false);
        Thread action = new Thread() {
            public void run() {
                String savableContent = content;
                fieldExtension.setText("Normal Text");
                if (!filePath.equals("")) {
                    // Read file to base64
                    try {
                        byte[] bytes = Base64.getEncoder().encode(Files.readAllBytes(new File(filePath).toPath()));
                        savableContent = new String(bytes, "UTF-8");
                        fieldExtension.setText(filePath.substring(filePath.lastIndexOf(".") + 1));
                    }catch (NoSuchFileException e) {
                        JOptionPane.showMessageDialog(null, "File not found");
                        return;
                    }catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error reading file");
                        return;
                    }
                }

                // Encrypt
                String addedDate = DateManager.getTimestamp();
                String name = "";
                String object = "";
                String readDate = addedDate + ">>>";
                String modifiedDate = addedDate + ">>>";
                String ntags = "";
                String type = "";

                Entry entry = new Entry();
                entry.setName(title);
                entry.setModifiedDate(addedDate);
                entry.setAddedDate(addedDate);
                entry.setTags(tags);
                entry.setType(fieldExtension.getText());
                try{
                    
                    name = CoreCryptography.encrypt(title, userInfo.getDecryptString(userInfo.getLoginToken()));
                    object = CoreCryptography.encrypt(savableContent, userInfo.getDecryptString(userInfo.getLoginToken()));
                    readDate = CoreCryptography.encrypt(addedDate, userInfo.getDecryptString(userInfo.getLoginToken()));
                    modifiedDate = CoreCryptography.encrypt(addedDate, userInfo.getDecryptString(userInfo.getLoginToken()));
                    ntags = CoreCryptography.encrypt(tags, userInfo.getDecryptString(userInfo.getLoginToken()));
                    addedDate = CoreCryptography.encrypt(addedDate, userInfo.getDecryptString(userInfo.getLoginToken()));
                    type = CoreCryptography.encrypt(fieldExtension.getText(), userInfo.getDecryptString(userInfo.getLoginToken()));

                    entry.setObject(object);
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

                b.column = "owner";
                b.value = userInfo.getLoginToken();

                c.column = "type";
                c.value = type;

                d.column = "addedDate";
                d.value = addedDate;

                e.column = "readDate";
                e.value = readDate;

                f.column = "tags";
                f.value = ntags;

                g.column = "modifiedDate";
                g.value = modifiedDate;

                h.column = "title";
                h.value = name;

                SQLStatementBuilder sql = new SQLStatementBuilder("data", SQLStatementBuilder.INSERT);
                sql.addParameter(a);
                sql.addParameter(b);
                sql.addParameter(c);
                sql.addParameter(d);
                sql.addParameter(e);
                sql.addParameter(f);
                sql.addParameter(g);
                sql.addParameter(h);
                
                SQLParameter searchFor = new SQLParameter();
                searchFor.column = "addedDate";
                searchFor.value = addedDate;
                searchFor.operator = SQLParameter.EQUAL;
                searchFor.nextOperand = SQLParameter.AND;

                SQLParameter searchFor2 = new SQLParameter();
                searchFor2.column = "title";
                searchFor2.value = name;
                searchFor2.operator = SQLParameter.EQUAL;

                SQLStatementBuilder sql2 = new SQLStatementBuilder("data", SQLStatementBuilder.SELECT);
                sql2.addParameter(searchFor);
                sql2.addParameter(searchFor2);

                try {
                    SQLite3.executeQuery(sql);
                    SQLite3.close();

                    ResultSet rs = SQLite3.executeQuery(sql2);
                    rs.next();
                    long id = rs.getInt("id");

                    entry.setId(id);
                    Entries.add(entry);
                    JOptionPane.showMessageDialog(null, "Successfully saved to database");

                    // Clear fields
                    fieldTitle.setText("");
                    textAreaContent.setText("");
                    fieldTags.setText("");
                    fieldExtension.setText("Normal Text");
                    filePath = "";
                    fieldContent.setVisible(true);

                    frame.dispose();
                }catch(Exception e2) {
                    e2.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error saving to database");
                    saveButton.addMouseListener(onClickSaveAdapter);
                    saveButton.setText("Save");
                    saveButton.setEnabled(true);
                }
            }
        };

        action.start();
    }


    @Override
    public void updateColor() {
        this.setBackground(ColorScheme.background);
        repaint();
    }
}
