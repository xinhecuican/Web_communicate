package Main_window.Component;

import Main_window.Data.message_rightdata;

import javax.swing.*;
import java.awt.*;
import java.util.zip.GZIPInputStream;

/**
 * @author: 李子麟
 * @date: 2021/3/17 12:45
 **/
public class Right_piece_message extends JPanel
{
    private GridBagConstraints constraints;
    public Right_piece_message(String message)
    {
        setOpaque(false);
        set_style();

        JLabel label = new JLabel(message);
        add(label, constraints);
    }

    public Right_piece_message(message_rightdata data)
    {
        setOpaque(false);
        set_style();
        constraints.gridwidth = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weightx = 1;
        JTextArea textArea = new JTextArea(data.message);
        textArea.setEditable(false);
        textArea.setOpaque(false);
        textArea.setLineWrap(true);
        add(textArea, constraints);
        constraints.fill = GridBagConstraints.NONE;
        constraints.weightx = 0;
        constraints.gridwidth = 0;
        add(new JLabel(data.time), constraints);
    }

    private void set_style()
    {
        GridBagLayout layout = new GridBagLayout();
        setLayout(layout);
        constraints = new GridBagConstraints();
        constraints.weightx = 1;
        constraints.gridwidth = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
    }
}
