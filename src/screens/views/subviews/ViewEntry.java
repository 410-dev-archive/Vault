package screens.views.subviews;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;
import javax.swing.filechooser.FileNameExtensionFilter;

import database.SQLParameter;
import database.SQLStatementBuilder;
import database.SQLite3;

import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Base64;

import screens.ColorScheme;
import screens.UpdatableColor;

import utils.CoreCryptography;
import utils.data.Entries;
import utils.data.Entry;
import utils.data.UserInfo;

public class ViewEntry extends JPanel implements UpdatableColor {

    private static String data = "";

    private static JFrame frame;
    private static Entry entry;
    private static UserInfo userInfo;

    public ViewEntry(JFrame frame, Entry entry, UserInfo userInfo) {
        super();
        this.setBackground(ColorScheme.background);
        this.setLayout(null);
        this.setBounds(0, 0, frame.getWidth(), frame.getHeight());

        ViewEntry.frame = frame;
        ViewEntry.entry = entry;
        ViewEntry.userInfo = userInfo;

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
        JButton delete = new JButton("Delete");
        JButton export = new JButton("Export");
        JButton viewTimestamps = new JButton("View Timestamps");
        JButton modifiedTimestamp = new JButton("Modified Timestamp");
        JButton editNormalText = null; // TODO: Implement edit normal text
        JLabel title = new JLabel(entry.getName());
        JLabel tags = new JLabel();
        JLabel content = null;

        if (entry.getType().equals("Normal Text")) {
            content = new JLabel(data);
            editNormalText = new JButton("Edit");
        }else{
            content = new JLabel("File with extension: " + entry.getType());
        }

        String tagsStr = "";
        for(String tag : entry.getTags()) {
            tagsStr += "#" + tag + "  ";
        }
        tags.setText(tagsStr);

        // TODO: Implement button functions

        title.setBounds(frame.getWidth() / 2 - title.getPreferredSize().width / 2, frame.getHeight() / 2 - title.getPreferredSize().height / 2 - content.getPreferredSize().height - 50, title.getPreferredSize().width, title.getPreferredSize().height);
        title.setForeground(ColorScheme.text);
        content.setBounds(frame.getWidth() / 2 - content.getPreferredSize().width / 2, frame.getHeight() / 2 - content.getPreferredSize().height / 2, content.getPreferredSize().width, content.getPreferredSize().height);
        content.setForeground(ColorScheme.text);
        tags.setBounds(frame.getWidth() / 2 - tags.getPreferredSize().width / 2, frame.getHeight() / 2 - tags.getPreferredSize().height / 2 + content.getPreferredSize().height + 50, tags.getPreferredSize().width, tags.getPreferredSize().height);
        tags.setForeground(ColorScheme.text);
        delete.setBounds(frame.getWidth() / 2 - delete.getPreferredSize().width / 2, frame.getHeight() / 2 - delete.getPreferredSize().height / 2 + content.getPreferredSize().height + 50 + tags.getPreferredSize().height + 50, delete.getPreferredSize().width, delete.getPreferredSize().height);
        delete.setForeground(ColorScheme.text);
        export.setBounds(frame.getWidth() / 2 - export.getPreferredSize().width / 2, frame.getHeight() / 2 - export.getPreferredSize().height / 2 + content.getPreferredSize().height + 50 + tags.getPreferredSize().height + 50 + delete.getPreferredSize().height + 50, export.getPreferredSize().width, export.getPreferredSize().height);
        export.setForeground(ColorScheme.text);
        viewTimestamps.setBounds(frame.getWidth() / 2 - viewTimestamps.getPreferredSize().width / 2, frame.getHeight() / 2 - viewTimestamps.getPreferredSize().height / 2 + content.getPreferredSize().height + 50 + tags.getPreferredSize().height + 50 + delete.getPreferredSize().height + 50 + export.getPreferredSize().height + 50, viewTimestamps.getPreferredSize().width, viewTimestamps.getPreferredSize().height);
        viewTimestamps.setForeground(ColorScheme.text);
        modifiedTimestamp.setBounds(frame.getWidth() / 2 - modifiedTimestamp.getPreferredSize().width / 2, frame.getHeight() / 2 - modifiedTimestamp.getPreferredSize().height / 2 + content.getPreferredSize().height + 50 + tags.getPreferredSize().height + 50 + delete.getPreferredSize().height + 50 + export.getPreferredSize().height + 50 + viewTimestamps.getPreferredSize().height + 50, modifiedTimestamp.getPreferredSize().width, modifiedTimestamp.getPreferredSize().height);
        modifiedTimestamp.setForeground(ColorScheme.text);
        if(editNormalText != null) {
            editNormalText.setBounds(frame.getWidth() / 2 - editNormalText.getPreferredSize().width / 2, frame.getHeight() / 2 - editNormalText.getPreferredSize().height / 2 + content.getPreferredSize().height + 50 + tags.getPreferredSize().height + 50 + delete.getPreferredSize().height + 50 + export.getPreferredSize().height + 50 + viewTimestamps.getPreferredSize().height + 50 + modifiedTimestamp.getPreferredSize().height + 50, editNormalText.getPreferredSize().width, editNormalText.getPreferredSize().height);
            editNormalText.setForeground(ColorScheme.text);
        }


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

        // TODO - Edit text


        this.add(title);
        this.add(content);
        this.add(tags);
        this.add(delete);
        this.add(export);
        this.add(viewTimestamps);
        this.add(modifiedTimestamp);
        if(editNormalText != null) {
            this.add(editNormalText);
        }

        ViewEntry.frame.setContentPane(this);
        ViewEntry.frame.setVisible(true);
    }

    @Override
    public void updateColor() {
        this.setBackground(ColorScheme.background);
        repaint();
    }
    
}
