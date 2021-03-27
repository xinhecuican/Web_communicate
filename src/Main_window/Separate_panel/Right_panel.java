package Main_window.Separate_panel;

import Main_window.Data.Send_data;
import Main_window.Data.message_rightdata;
import Main_window.Main;
import Main_window.Window;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.regex.Pattern;

public class Right_panel extends JPanel
{
    JTextPane message_inner_panel;
    JTextArea message_area;
    private JScrollPane message_panel ;
    private JToolBar message_toolbar ;
    private JPanel enter_panel;
    private JTextArea enter_area;
    SimpleAttributeSet other_set_name;
    SimpleAttributeSet other_set;
    SimpleAttributeSet user_set;
    SimpleAttributeSet user_set_name;
    private static Pattern regex = Pattern.compile("\\s*");//匹配若干个空格

    public Right_panel()
    {
        message_inner_panel = new JTextPane();
        message_inner_panel.setEditable(false);
        message_panel = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        message_toolbar = new JToolBar();
        enter_panel = new JPanel();
        message_area = new JTextArea(20, 20);
        message_area.setLineWrap(true);

        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        add(message_panel);
        add(message_toolbar);
        add(enter_panel);

        constraints.gridwidth = 0;
        constraints.weightx = 1;
        constraints.weighty = 1;
        constraints.gridheight= 10;
        constraints.fill = GridBagConstraints.BOTH;
        layout.setConstraints(message_panel, constraints);
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.weighty = 0;
        constraints.gridheight = 1;
        layout.setConstraints(message_toolbar, constraints);
        constraints.gridheight = 4;
        layout.setConstraints(enter_panel, constraints);
        setLayout(layout);

        enter_panel.setPreferredSize(new Dimension(600, 200));
        //message_panel.setPreferredSize(new Dimension(600, 400));
        //setPreferredSize(new Dimension(600, 800));



        set_message_panel();
        set_message_toolbar();
        set_enter_panel();
        setEnabled(true);
        setVisible(true);
    }

    public void clear()
    {
        message_inner_panel.setText("");
    }

    public void add_piece_message(message_rightdata data)
    {

        Document doc = message_inner_panel.getStyledDocument();
        try
        {
            doc.insertString(doc.getLength(), data.message_sender_name + "   " + data.time+"\n", user_set_name);
            doc.insertString(doc.getLength(), data.message+'\n', user_set);
            message_inner_panel.updateUI();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        //message_area.append(data.time + "\n" + data.message);
    }

    private void set_enter_panel()
    {
        BorderLayout layout = new BorderLayout();
        //GridBagLayout layout = new GridBagLayout();
        enter_panel.setLayout(layout);
        enter_area = new JTextArea("", 10, 20);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        constraints.weighty = 1;
        JScrollPane scrollPane = new JScrollPane(enter_area);
        scrollPane.setMinimumSize(new Dimension(600, 200));
        enter_panel.add(scrollPane, BorderLayout.CENTER);
    }

    private void set_message_panel()
    {
        message_panel.setViewportView(message_inner_panel);
        other_set = new SimpleAttributeSet();
        user_set = new SimpleAttributeSet();
        other_set_name = new SimpleAttributeSet();
        user_set_name = new SimpleAttributeSet();
        StyleConstants.setAlignment(other_set, StyleConstants.ALIGN_LEFT);
        StyleConstants.setLineSpacing(other_set, 1);
        StyleConstants.setFontSize(other_set, 15);
        StyleConstants.setFontFamily(other_set, "Arial Black");
        StyleConstants.setAlignment(user_set, StyleConstants.ALIGN_RIGHT);
        StyleConstants.setLineSpacing(user_set, 1);
        StyleConstants.setFontSize(user_set, 15);
        StyleConstants.setFontFamily(user_set, "Arial Black");
        StyleConstants.setLeftIndent(user_set, (float)message_panel.getWidth() / 2);
        StyleConstants.setBold(other_set_name, true);
        StyleConstants.setFontSize(other_set_name, 20);
        StyleConstants.setLineSpacing(other_set_name, (float) 1.5);
        StyleConstants.setAlignment(other_set_name, StyleConstants.ALIGN_LEFT);
        StyleConstants.setBold(user_set_name, true);
        StyleConstants.setFontSize(user_set_name, 20);
        StyleConstants.setLineSpacing(user_set_name, (float) 1.5);
        StyleConstants.setAlignment(user_set_name, StyleConstants.ALIGN_LEFT);

    }

    private void set_message_toolbar()
    {
        BorderLayout layout = new BorderLayout();
        message_toolbar.setLayout(layout);
        JButton button_file_choose = new JButton("打开");
        button_file_choose.setToolTipText("发送文件");

        message_toolbar.add(button_file_choose, BorderLayout.WEST);
        message_toolbar.addSeparator();
        message_toolbar.setFloatable(false);
        button_file_choose.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                JFileChooser file_chooser = new JFileChooser();
                file_chooser.setDialogTitle("打开");
                int mode;
                if((mode = file_chooser.showSaveDialog(button_file_choose)) == JFileChooser.APPROVE_OPTION)
                {
                    File files = file_chooser.getSelectedFile();
                }
                else if(mode == JFileChooser.ERROR_OPTION)
                {
                    JOptionPane.showMessageDialog(file_chooser, "打开文件失败",
                            "错误", JOptionPane.ERROR_MESSAGE, null);
                }

            }
        });
        JButton button_send_message = new JButton("发送");
        message_toolbar.add(button_send_message, BorderLayout.EAST);
        button_send_message.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                String text = enter_area.getText();
                if(!(regex.matcher(text).matches() || text.equals(""))
                        && Scroll_panel.select_button != null)//不是空格并且选中一个选项卡
                {
                    add_piece_message(new message_rightdata(Window.get_time(), text, Main.main_user.name));
                    boolean is_user = Main.main_user.add_message(Scroll_panel.select_button.id,
                            new message_rightdata(Window.get_time(), text, Main.main_user.name));
                    Send_data send_data = new Send_data(Scroll_panel.select_button.id,
                            new message_rightdata(Window.get_time(), text, Main.main_user.name));
                    send_data.data_type = is_user ?
                            Send_data.Data_type.One_piece_message : Send_data.Data_type.Piece_group_message;
                    Main.main_user.send_message(send_data);
                    //给对方的，所以is_user = false
                }
                enter_area.setText("");
            }
        });
    }
}
