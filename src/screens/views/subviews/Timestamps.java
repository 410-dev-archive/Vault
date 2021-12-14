package screens.views.subviews;

import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import screens.ColorScheme;
import screens.UpdatableColor;

public class Timestamps extends JPanel implements UpdatableColor {

    // TODO: Test needed

    public Timestamps(ArrayList<String> timestamps, JFrame frame) {
        super();
        this.setBackground(ColorScheme.background);
        this.setLayout(null);
        this.setBounds(0, 0, frame.getWidth(), frame.getHeight());

        JTextArea loggable = new JTextArea();
        loggable.setEditable(false);
        loggable.setBackground(ColorScheme.background);
        loggable.setForeground(ColorScheme.text);

        // Make loggable scrollable
        loggable.setLineWrap(true);
        loggable.setWrapStyleWord(true);

        JScrollPane scrollable = new JScrollPane(loggable);
        scrollable.setBounds(0, 0, frame.getWidth(), frame.getHeight());
        scrollable.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollable.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        this.add(scrollable);

        frame.setContentPane(this);
    }

    @Override
    public void updateColor() {

    }
}
