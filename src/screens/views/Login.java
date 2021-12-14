package screens.views;

import java.sql.ResultSet;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.event.MouseInputAdapter;

import database.SQLParameter;
import database.SQLStatementBuilder;
import database.SQLite3;
import encoders.UserEncode;
import screens.ColorScheme;
import screens.Frame;
import screens.UpdatableColor;
import utils.data.UserInfo;

public class Login extends JPanel implements UpdatableColor {

    private int trials = 0;

    public Login() {

        // Configure the panel
        super();
        this.setBounds(0, 0, Frame.frame.getWidth(), Frame.frame.getHeight());
        this.setLayout(null);
        this.setBackground(ColorScheme.background);

        // Create the components
        JLabel labelName = new JLabel("Username");
        JLabel labelPass = new JLabel("Password");

        JTextField fieldName = new JTextField();
        JPasswordField fieldPass = new JPasswordField();

        JButton buttonSignUp = new JButton("Sign Up");
        JButton buttonLogin = new JButton("Login");


        // Color Setup here
        labelName.setForeground(ColorScheme.text);
        labelPass.setForeground(ColorScheme.text);

        fieldName.setForeground(ColorScheme.text);
        fieldPass.setForeground(ColorScheme.text);

        buttonSignUp.setForeground(ColorScheme.text);
        buttonLogin.setForeground(ColorScheme.text);

        buttonSignUp.setBackground(ColorScheme.button);
        buttonLogin.setBackground(ColorScheme.button);


        // Place components
        // TODO: Set the components to be relative and neat
        labelName.setBounds(Frame.frame.getWidth() / 2 - 100, Frame.frame.getHeight() / 2 - 100, 100, 20);
        fieldName.setBounds(Frame.frame.getWidth() / 2 - 100, Frame.frame.getHeight() / 2 - 50, 200, 20);

        labelPass.setBounds(Frame.frame.getWidth() / 2 - 100, Frame.frame.getHeight() / 2 - 25, 100, 20);
        fieldPass.setBounds(Frame.frame.getWidth() / 2 - 100, Frame.frame.getHeight() / 2, 200, 20);

        buttonSignUp.setBounds(Frame.frame.getWidth() / 2 - 100, Frame.frame.getHeight() / 2 + 50, 200, 20);
        buttonLogin.setBounds(Frame.frame.getWidth() / 2 - 100, Frame.frame.getHeight() / 2 + 80, 200, 20);

        // Password field listens for enter key
        fieldPass.addActionListener(e -> {
            if (fieldName.getText().equals("") || String.valueOf(fieldPass.getPassword()).equals("")) {
                JOptionPane.showMessageDialog(null, "Please enter a username and password");
            } else {
                login(fieldName.getText(), String.valueOf(fieldPass.getPassword()));
            }
        });

        // Add button click events
        buttonSignUp.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                Frame.setContent(new Signup());
            }
        });

        buttonLogin.addMouseListener(new MouseInputAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                login(fieldName.getText(), String.valueOf(fieldPass.getPassword()));
            }
        });

        // Add components here
        this.add(labelName);
        this.add(fieldName);
        this.add(labelPass);
        this.add(fieldPass);
        this.add(buttonSignUp);
        this.add(buttonLogin);
    }

    private void login(String username, String password) {
        String token = UserEncode.generateLoginToken(username, password);

        SQLParameter user = new SQLParameter();
        user.column = "user";
        user.value = token;
        user.operator = SQLParameter.EQUAL;

        SQLStatementBuilder builder = new SQLStatementBuilder("users", SQLStatementBuilder.SELECT);
        builder.addParameter(user);

        ResultSet rs = null;
        try{
            rs = SQLite3.executeQuery(builder);
            if (rs.next()) {
                Frame.setContent(new Home(new UserInfo(token)));
                trials = 0;
            } else {
                trials++;
                JOptionPane.showMessageDialog(null, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }catch(Exception e){
            trials++;
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Invalid username or password", "Error", JOptionPane.ERROR_MESSAGE);
        }

        if (trials >= 5) {
            JOptionPane.showMessageDialog(null, "Too many failed login attempts", "Error", JOptionPane.ERROR_MESSAGE);
            SQLite3.close();
            System.exit(0);
        }
    }

    @Override
    public void updateColor() {
        this.setBackground(ColorScheme.background);
        this.repaint();
    }
}
