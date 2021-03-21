package Main_window.Separate_panel;

import Main_window.Main;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * @author: 李子麟
 * @date: 2021/3/18 18:49
 **/
public class Search_panel extends JPanel
{
    private GridBagLayout layout;
    public Search_panel()
    {
        setOpaque(false);
        layout = new GridBagLayout();
        setLayout(layout);


        addComponentListener(new ComponentAdapter()
        {
            @Override
            public void componentResized(ComponentEvent e)
            {

            }
        });
    }

    public void set_text_and_button(JTextField text_field, JButton button)
    {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridheight = 20;
        constraints.weightx = 1;
        //constraints.weighty = 1;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        layout.setConstraints(text_field, constraints);
        //constraints.gridwidth = 5;
        constraints.weightx = 0;
        layout.setConstraints(button, constraints);

        add(text_field);
        add(button);
        //setPreferredSize(new Dimension(Main.LEFT_PANEL_WIDTH, 20));
    }
}
