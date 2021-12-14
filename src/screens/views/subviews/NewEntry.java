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

    private JTextField fieldExtension;

    JTextField fieldTitle = new JTextField();
    JTextArea textAreaContent = new JTextArea();
    JTextField fieldTags = new JTextField();
    JButton saveButton = new JButton("Save");

    private MouseInputAdapter onClickSaveAdapter = new MouseInputAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            save(fieldTitle.getText(), textAreaContent.getText(), fieldTags.getText());
        }
    };

    public NewEntry(JFrame parentFrame, UserInfo userInfo) {
        super();
        this.frame = parentFrame;
        this.userInfo = userInfo;

        this.setLayout(null);
        this.setSize(parentFrame.getWidth(), parentFrame.getHeight());

        this.setBackground(ColorScheme.background);

        // TODO: Make the components inside to be adaptive to the frame size

        JLabel labelTitle = new JLabel("Title");
        

        JLabel labelContent = new JLabel("Content");
        
        textAreaContent.setLineWrap(true);
        textAreaContent.setWrapStyleWord(true);
        JScrollPane fieldContent = new JScrollPane(textAreaContent);
        fieldContent.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        fieldContent.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JButton useFileButton = new JButton("Select File");

        JLabel labelTags = new JLabel("Tags (Split by comma)");
        

        JLabel labelExtension = new JLabel("Extension");
        fieldExtension = new JTextField();


        // Set bounds
        labelTitle.setBounds(10, 10, 100, 25);
        fieldTitle.setBounds(120, 10, 200, 25);
        
        labelContent.setBounds(10, 40, 100, 25);
        fieldContent.setBounds(120, 40, 200, 200);

        useFileButton.setBounds(10, 240, 100, 25);

        labelTags.setBounds(10, 270, 100, 25);
        fieldTags.setBounds(120, 270, 200, 25);

        labelExtension.setBounds(10, 300, 100, 25);
        fieldExtension.setBounds(120, 300, 200, 25);

        saveButton.setBounds(10, 330, 100, 25);


        // On click when open pressed
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

        // On click when save pressed
        saveButton.addMouseListener(onClickSaveAdapter);

        // Add components
        this.add(labelTitle);
        this.add(fieldTitle);
        this.add(labelContent);
        this.add(fieldContent);
        this.add(useFileButton);
        this.add(labelTags);
        this.add(fieldTags);
        this.add(saveButton);

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
