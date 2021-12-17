package screens.views.subviews;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.MouseInputAdapter;

import database.SQLStatementBuilder;
import screens.ColorScheme;
import screens.UpdatableColor;
import screens.ViewDimension;

import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import utils.data.Entry;
import utils.data.UserInfo;
import utils.DataController;
import utils.DateManager;

public class NewEntry extends JPanel implements UpdatableColor {

    private UserInfo userInfo;
    private JFrame frame;

    private String filePath = "";

    private static boolean isUpdated = false;
    // private static boolean isSaving = false;

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
            add(useFileButton);
            // TODO: add tags
            // add(labelTags);
            // add(fieldTags);
            add(labelExtension);
            add(fieldExtension);
            add(saveButton);
            add(fieldContent);
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
        frame.setSize(500, 500);
        frame.setResizable(true);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
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

    private void enableSaveButton() {
        saveButton.addMouseListener(onClickSaveAdapter);
        saveButton.setText("Save");
        saveButton.setEnabled(true);
    }


    private void save(String title, String content, String tags) {
        // isSaving = true;
        saveButton.removeMouseListener(onClickSaveAdapter);
        saveButton.setText("Saving...");
        saveButton.setEnabled(false);
        Thread action = new Thread() {
            public void run() {
                String savableContent = content;
                fieldExtension.setText("Normal Text");
                if (!filePath.equals("")) {
                    String[] exit = DataController.readFile(filePath);
                    if (exit.length > DataController.EXPECTED_LENGTH) {
                        int error = Integer.parseInt(exit[DataController.INDEX_ERROR]);
                        switch(error) {
                            case DataController.EXIT_FILE_NOT_FOUND:
                                JOptionPane.showMessageDialog(null, "File not found");
                                break;
                            case DataController.EXIT_FILE_IO_FAILURE:
                                JOptionPane.showMessageDialog(null, "Error reading file");
                                break;
                            default:
                                JOptionPane.showMessageDialog(null, "Unknown error");
                                break;
                        }
                        enableSaveButton();
                        return;
                    }else{
                        savableContent = exit[DataController.INDEX_CONTENT];
                        fieldExtension.setText(exit[DataController.INDEX_EXTENSION]);
                    }
                }

                // Encrypt
                Entry unencryptedEntry = new Entry(false);
                unencryptedEntry.setName(title);
                unencryptedEntry.addModifiedDate();
                unencryptedEntry.setAddedDate(DateManager.getTimestamp());
                unencryptedEntry.setTags(tags);
                unencryptedEntry.setType(fieldExtension.getText());
                unencryptedEntry.setObject(savableContent);

                int exit = DataController.addOrEditData(unencryptedEntry, !fieldExtension.getText().equals("Normal Text"), userInfo, SQLStatementBuilder.INSERT);
                
                switch(exit) {
                    case DataController.EXIT_SUCCESS:
                        break;
                    case DataController.EXIT_FAILURE:
                        JOptionPane.showMessageDialog(null, "Error saving entry");
                        enableSaveButton();
                        return;
                }

                JOptionPane.showMessageDialog(null, "Successfully saved to database");

                // Clear fields
                fieldTitle.setText("");
                textAreaContent.setText("");
                fieldTags.setText("");
                fieldExtension.setText("Normal Text");
                filePath = "";
                fieldContent.setVisible(true);

                enableSaveButton();

                // isSaving = false;

                frame.dispose();
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
