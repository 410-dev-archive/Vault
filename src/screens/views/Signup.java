package screens.views;

import java.sql.ResultSet;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;

import database.SQLParameter;
import database.SQLStatementBuilder;
import database.SQLite3;
import encoders.UserEncode;
import screens.ColorScheme;
import screens.Frame;
import screens.UpdatableColor;

public class Signup extends JPanel implements UpdatableColor {

    JLabel labelName = new JLabel("Name");
    JTextField fieldName = new JTextField();
    
    JLabel labelPass = new JLabel("Password");
    JPasswordField fieldPass = new JPasswordField();
    
    JButton signUp = new JButton("Sign Up");

    public Signup() {
        super();
        this.setBackground(ColorScheme.background);
        this.setBounds(0, 0, Frame.frame.getWidth(), Frame.frame.getHeight());
        Frame.frame.setResizable(false);
        this.setLayout(null);

        // Set bounds for components
        labelName.setBounds(Frame.frame.getWidth() / 2 - 100, Frame.frame.getHeight() / 2 - 100, 100, 20);
        fieldName.setBounds(Frame.frame.getWidth() / 2 - 100, Frame.frame.getHeight() / 2 - 50, 200, 20);

        labelPass.setBounds(Frame.frame.getWidth() / 2 - 100, Frame.frame.getHeight() / 2 - 20, 100, 20);
        fieldPass.setBounds(Frame.frame.getWidth() / 2 - 100, Frame.frame.getHeight() / 2, 200, 20);

        signUp.setBounds(Frame.frame.getWidth() / 2 - 100, Frame.frame.getHeight() / 2 + 50, 200, 20);

        // fieldPass listens for enter key
        fieldPass.addActionListener(e -> {
            signUp();
        });

        // Add mouse listener to the button
        signUp.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                signUp();
            }
        });

        // Add components to the panel
        this.add(labelName);
        this.add(fieldName);
        this.add(labelPass);
        this.add(fieldPass);
        this.add(signUp);
    }

    public void signUp() {
        signUp.setEnabled(false);
        signUp.setText("Signing Up...");

        // Check if password satisfies requirements
        if (String.valueOf(fieldPass.getPassword()).length() < 8) {
            JOptionPane.showMessageDialog(null, "Password must be at least 8 characters long");
            signUp.setEnabled(true);
            signUp.setText("Sign Up");
            return;
        }

        // Check if the user name is already taken
        SQLParameter param = new SQLParameter();
        param.column = "user";
        param.value = UserEncode.generateLoginToken(fieldName.getText(), String.valueOf(fieldPass.getPassword()));
        param.operator = SQLParameter.EQUAL;

        try {
            SQLStatementBuilder builder = new SQLStatementBuilder("users", SQLStatementBuilder.SELECT);
            builder.addParameter(param);
            
            ResultSet rs = SQLite3.executeQuery(builder.build());
            
            if (rs.next()) {
                JOptionPane.showMessageDialog(null, "Username is already taken.");
                signUp.setEnabled(true);
                signUp.setText("Sign Up");
                return;
            }

            // Add user to database
            SQLStatementBuilder builder2 = new SQLStatementBuilder("users", SQLStatementBuilder.INSERT);
            builder2.addParameter(param);
            SQLite3.executeQuery(builder2.build());
        }catch(Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }

        JOptionPane.showMessageDialog(null, "Signup Successful.", "", JOptionPane.INFORMATION_MESSAGE);
        Frame.setContent(new Login());
    }

    @Override
    public void updateColor() {
        this.setBackground(ColorScheme.background);
        this.repaint();
    }
    
}
