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

import database.SQLStatementBuilder;

import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Base64;

import screens.ColorScheme;
import screens.UpdatableColor;
import screens.ViewDimension;
import utils.DataController;
import utils.data.Entry;
import utils.data.UserInfo;

public class ViewEntry extends JPanel implements UpdatableColor {

    private static String data = "";

    private static boolean isSaving = false;

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

    private boolean hasClicked = false;

    private MouseInputAdapter onEditButtonClicked = new MouseInputAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            try {
                if(hasClicked) {
                    return;
                }else{
                    hasClicked = true;
                }
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

        delete = new JButton("Delete");
        export = new JButton("Export");
        viewTimestamps = new JButton("View Timestamps");
        modifiedTimestamp = new JButton("Modified Timestamp");
        editNormalText = new JButton("Edit");
        this.title = new JTextField();
        tags = new JTextField();
        content = new JTextField();

        Thread asyncThread = new Thread() {
            @Override
            public void run() {
                try {
                    switch(entry.decryptObjectOnly(userInfo)) {
                        case Entry.DECRYPT_FAILURE:
                            JOptionPane.showMessageDialog(null, "Decryption failed");
                            break;
                        default:
                            data = entry.getObject();
                            break;

                    }
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
                    switch(DataController.deleteRow(entry)) {
                        case DataController.EXIT_SQL_FAILURE:
                            JOptionPane.showMessageDialog(null, "Deletion failed");
                            break;
                        default:
                            JOptionPane.showMessageDialog(null, "Deletion successful");
                            frame.dispose();
                            break;
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

                    // TODO: BETA2.0 - Change to blobs in case of file
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
                System.out.println("SAVE EVENT");
                entry.setObject(content);
                int exitCode = DataController.addOrEditData(entry, isFile, userInfo, SQLStatementBuilder.UPDATE);
                switch(exitCode) {
                    case DataController.EXIT_SQL_FAILURE:
                        JOptionPane.showMessageDialog(null, "Failed to update data. Please try again. (SQL)");
                        break;
                    case DataController.EXIT_SUCCESS:
                        JOptionPane.showMessageDialog(null, "Data updated successfully!");
                        editNormalText.setText("Success!");
                        break;
                    case DataController.EXIT_ENCRYPT_FAILURE:
                        JOptionPane.showMessageDialog(null, "Failed to encrypt data. Please try again.");
                        break;
                    case DataController.EXIT_FAILURE:
                        JOptionPane.showMessageDialog(null, "Failed to update data. Please try again. (UNKNOWN)");
                        break;
                    case DataController.EXIT_FILE_IO_FAILURE:
                        JOptionPane.showMessageDialog(null, "Failed to read file. Please try again.");
                        break;
                    case DataController.EXIT_FILE_NOT_FOUND:
                        JOptionPane.showMessageDialog(null, "File not found.");
                        break;
                    default:
                        JOptionPane.showMessageDialog(null, "There's problem in the code - Incompatible Communication. Please check for the update, or contact the developer.");
                        break;
                }
                // editNormalText.addMouseListener(onEditButtonClicked);
                // editNormalText.setEnabled(true);
                frame.dispose();
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
            if (!isSaving) {
                editNormalText.setText("Save");   
                editNormalText.addMouseListener(onEditButtonClicked);
                editNormalText.setEnabled(true);
            }
        }else{
            if (!isSaving) {
                editNormalText.setText("Change File");
                editNormalText.addMouseListener(onEditButtonClicked);
                editNormalText.setEnabled(true);
            }
        }

        this.add(title);
        this.add(content);
        // TODO: Add tags
        tags.setText("");
        // this.add(tags);
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
