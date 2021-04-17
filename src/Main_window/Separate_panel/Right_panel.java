package Main_window.Separate_panel;

import Interface.IComponent;
import Main_window.UI.Background_message_panel;
import Main_window.Component.File_send_panel;
import Main_window.Data.Send_data;
import Main_window.Data.message_rightdata;
import Main_window.Main;
import Main_window.Pop_window.Voice_Window;
import Main_window.Window;
import Server.Data.File_info;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Right_panel extends JPanel implements IComponent
{
    JTextPane message_inner_panel;
    JTextArea message_area;
    private Background_message_panel message_panel ;
    private JToolBar message_toolbar ;
    private JScrollPane enter_panel;
    private JTextPane enter_inner_panel;
    SimpleAttributeSet other_set_name;
    SimpleAttributeSet other_set;
    SimpleAttributeSet user_set;
    SimpleAttributeSet user_set_name;
    SimpleAttributeSet time_set;
    SimpleAttributeSet icon_set;
    SimpleAttributeSet file_set;
    private static Pattern regex = Pattern.compile("\\s*");//匹配若干个空格
    private ArrayList<File_info> ready_to_send_file;
    private boolean is_sending_files;

    public Right_panel()
    {
        Window.current.register_after_initialize(this);
        message_inner_panel = new JTextPane();
        message_inner_panel.setEditable(false);
        message_inner_panel.setContentType("text/html");
        message_panel = new Background_message_panel(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        message_toolbar = new JToolBar();
        enter_panel = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        enter_inner_panel = new JTextPane();
        ready_to_send_file = new ArrayList<>();
        message_area = new JTextArea(20, 20);
        message_area.setLineWrap(true);
        is_sending_files = false;

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
        constraints.ipady = 200;
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

    /**
     * 添加文字消息到消息面板中
     * @param data
     */
    public synchronized void add_piece_message(message_rightdata data)
    {
        StyledDocument doc = message_inner_panel.getStyledDocument();
        try
        {
            doc.insertString(doc.getLength(), data.time + "\n", time_set);
            if(data.message_sender_name.equals(Main.main_user.name))
            {
                doc.insertString(doc.getLength(), data.message_sender_name + "\n", user_set_name);
                doc.insertString(doc.getLength(), data.message + '\n', user_set);
            }
            else
            {
                doc.insertString(doc.getLength(), data.message_sender_name  + "\n", other_set_name);
                doc.insertString(doc.getLength(), data.message + '\n', other_set);
            }
            //message_inner_panel.updateUI();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        //message_area.append(data.time + "\n" + data.message);
    }

    public synchronized void add_file_message(File_send_panel send_panel)
    {
        StyledDocument doc = message_inner_panel.getStyledDocument();
        try
        {
            boolean is_user = send_panel.get_my_id() == Main.main_user.getId();
            if(is_user)
            {
                doc.insertString(doc.getLength(),   Main.main_user.name+ "\n", user_set_name);
            }
            else
            {
                if(Main.main_user.find_friend(send_panel.get_my_id()) != null)
                {
                    doc.insertString(doc.getLength(),
                            Main.main_user.find_friend(send_panel.get_my_id()).getName() + "\n", other_set_name
                    );
                }
            }
            StyleConstants.setComponent(file_set, send_panel);

            if(is_user)
            {
                doc.insertString(doc.getLength(), " ", other_set);
            }
            doc.insertString(doc.getLength(), send_panel.get_file_name() +
                    (is_user ? "\n" : "") , file_set);
            if(!is_user)
            {
                doc.insertString(doc.getLength(), "\n", other_set);
            }
            file_set = new SimpleAttributeSet();
            updateUI();
        }
        catch (BadLocationException e)
        {
            e.printStackTrace();
        }
    }

    private void set_enter_panel()
    {
        enter_panel.setViewportView(enter_inner_panel);
        /*BorderLayout layout = new BorderLayout();
        //GridBagLayout layout = new GridBagLayout();
        enter_panel.setLayout(layout);
        enter_area = new JTextArea("", 10, 20);
        enter_area.setLineWrap(true);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1;
        constraints.weighty = 1;
        JScrollPane scrollPane = new JScrollPane(enter_area);
        scrollPane.setMinimumSize(new Dimension(600, 200));
        enter_panel.add(scrollPane, BorderLayout.CENTER);*/
    }

    private void set_message_panel()
    {
        message_panel.setViewportView(message_inner_panel);
        message_inner_panel.setOpaque(false);
        message_panel.setOpaque(false);
        message_panel.getViewport().setOpaque(false);
        other_set = new SimpleAttributeSet();
        user_set = new SimpleAttributeSet();
        other_set_name = new SimpleAttributeSet();
        user_set_name = new SimpleAttributeSet();
        time_set = new SimpleAttributeSet();
        icon_set = new SimpleAttributeSet();
        file_set = new SimpleAttributeSet();
        StyleConstants.setFontSize(time_set, 10);
        StyleConstants.setForeground(time_set, Color.GRAY);
        StyleConstants.setAlignment(time_set, StyleConstants.ALIGN_CENTER);
        StyleConstants.setSpaceBelow(time_set,1.5f);
        StyleConstants.setSpaceAbove(time_set, 2f);

        StyleConstants.setAlignment(other_set, StyleConstants.ALIGN_LEFT);
        StyleConstants.setLineSpacing(other_set, 1);
        StyleConstants.setFontSize(other_set, 15);
        StyleConstants.setFontFamily(other_set, "Arial Black");
        StyleConstants.setRightIndent(other_set, 136.5f);

        StyleConstants.setAlignment(user_set, StyleConstants.ALIGN_RIGHT);
        StyleConstants.setLineSpacing(user_set, 1);
        StyleConstants.setFontSize(user_set, 15);
        StyleConstants.setFontFamily(user_set, "Arial Black");
        StyleConstants.ParagraphConstants.setBackground(user_set,new Color(18, 183, 245));
        StyleConstants.setLeftIndent(user_set, 136.5f);

        //StyleConstants.setLeftIndent(user_set, (float)message_inner_panel.getWidth() / 2);

        StyleConstants.setBold(other_set_name, true);
        StyleConstants.setFontSize(other_set_name, 10);
        StyleConstants.setLineSpacing(other_set_name, (float) 1.5);
        StyleConstants.setAlignment(other_set_name, StyleConstants.ALIGN_LEFT);
        StyleConstants.setForeground(other_set_name, new Color(231, 121, 250));

        StyleConstants.setBold(user_set_name, true);
        StyleConstants.setFontSize(user_set_name, 10);
        StyleConstants.setLineSpacing(user_set_name, (float) 1.5);
        StyleConstants.setAlignment(user_set_name, StyleConstants.ALIGN_RIGHT);
        //StyleConstants.setForeground(user_set_name, new Color(129, 203, 255));


    }

    private void set_message_toolbar()
    {
        message_toolbar.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        JButton button_file_choose = new JButton("打开");
        button_file_choose.setToolTipText("发送文件");

        message_toolbar.add(button_file_choose, constraints);
        message_toolbar.setFloatable(false);
        button_file_choose.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                FileDialog dialog = new FileDialog(Window.current, "打开", FileDialog.LOAD);
                dialog.setMultipleMode(true);
                dialog.setVisible(true);
                if(Scroll_panel.select_button == null)
                {
                    return;
                }
                File[] files = dialog.getFiles();
                is_sending_files = true;
                for(File file : files)
                {
                    File_info info = new File_info(file, Main.main_user.getId());
                    info.send_to_id = Scroll_panel.select_button.id;
                    ready_to_send_file.add(info);
                    Icon ico = FileSystemView.getFileSystemView().getSystemIcon(file);
                    StyleConstants.setIcon(icon_set, ico);
                    StyledDocument doc = enter_inner_panel.getStyledDocument();
                    try
                    {
                        doc.insertString(doc.getLength(), file.getName(), icon_set);
                    }
                    catch (BadLocationException e)
                    {
                        e.printStackTrace();
                    }
                }

                /*JFileChooser file_chooser = new JFileChooser();
                file_chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                file_chooser.setDialogTitle("打开");
                file_chooser.setMultiSelectionEnabled(true);
                int mode;
                if((mode = file_chooser.showOpenDialog(button_file_choose)) == JFileChooser.APPROVE_OPTION)
                {
                    if(Scroll_panel.select_button == null)
                    {
                        return;
                    }
                    File[] files = file_chooser.getSelectedFiles();
                    is_sending_files = true;
                    for(File file : files)
                    {
                        File_info info = new File_info(file, Main.main_user.getId());
                        info.send_to_id = Scroll_panel.select_button.id;
                        ready_to_send_file.add(info);
                        Icon ico = FileSystemView.getFileSystemView().getSystemIcon(file);
                        StyleConstants.setIcon(icon_set, ico);
                        StyledDocument doc = enter_inner_panel.getStyledDocument();
                        try
                        {
                            doc.insertString(doc.getLength(), file.getName(), icon_set);
                        }
                        catch (BadLocationException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
                else if(mode == JFileChooser.ERROR_OPTION)
                {
                    JOptionPane.showMessageDialog(file_chooser, "打开文件失败",
                            "错误", JOptionPane.ERROR_MESSAGE, null);
                }*/

            }
        });
        JButton button_voice_chat = new JButton("语音");
        message_toolbar.add(button_voice_chat, constraints);
        button_voice_chat.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                if(Scroll_panel.select_button == null)
                {
                    return;
                }
                if(Voice_Window.current != null && Voice_Window.current.isActive())
                {
                    JOptionPane.showMessageDialog(null, "您正在进行通话");
                    return;
                }
                new Voice_Window(Window.current, true, Main.main_user.getId(), Scroll_panel.select_button.id);
                Send_data data = new Send_data(Scroll_panel.select_button.id, null);
                data.data_type = Send_data.Data_type.Request_voice_call;
                Main.main_user.send_message(data);
            }
        });
        JButton button_send_message = new JButton("发送");
        constraints.gridwidth = 0;
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.anchor = GridBagConstraints.EAST;
        constraints.weightx = 1;
        JPanel temp_panel = new JPanel();
        message_toolbar.add(temp_panel, constraints);
        temp_panel.setLayout(new BorderLayout());
        temp_panel.add(button_send_message, BorderLayout.EAST);

        button_send_message.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent actionEvent)
            {
                if(Scroll_panel.select_button == null)
                {
                    enter_inner_panel.setText("");
                    return;
                }
                if(is_sending_files)
                {
                    boolean is_group = Main.main_user.find_group(Scroll_panel.select_button.id) != null;
                    for(File_info file : ready_to_send_file)
                    {
                        file.is_group = is_group;
                        Main.main_user.send_file(file, is_group);//包含了将信息发送到面板及把信息加入到
                    }

                    ready_to_send_file.clear();
                    enter_inner_panel.setText("");
                    is_sending_files = false;
                    return;
                }
                StyledDocument doc = enter_inner_panel.getStyledDocument();
                String text = null;
                try
                {
                    text = doc.getText(0, doc.getLength());
                }
                catch (BadLocationException e)
                {
                    e.printStackTrace();
                }

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
                enter_inner_panel.setText("");
            }
        });
    }



    @Override
    public void after_initialize()
    {
        StyleConstants.ParagraphConstants.setLeftIndent(user_set, (float)message_inner_panel.getWidth() / 4);
        StyleConstants.setLeftIndent(user_set, (float)message_inner_panel.getWidth() / 4);
        StyleConstants.setRightIndent(other_set, (float)message_inner_panel.getWidth() / 4);
    }
}
