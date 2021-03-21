package Main_window.Pop_window;

import Main_window.Main;
import Main_window.User_Server.User_server;
import Main_window.User_Server.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author: 李子麟
 * @date: 2021/3/18 20:11
 **/
public class Pop_window extends JDialog
{
    public Pop_window(JFrame parent)
    {
        super(parent);

        pack();
        setResizable(false);
        setVisible(true);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(parent);


    }

    public Pop_window()
    {
        super();
    }
}
