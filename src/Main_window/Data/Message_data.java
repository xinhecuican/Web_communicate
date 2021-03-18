package Main_window.Data;

import java.util.*;

/**
 * @author: 李子麟
 * @date: 2021/3/15 20:47
 **/
public class Message_data
{
    public String name;
    private List<message_rightdata> dataList;

    public Message_data()
    {
        name = "";
        dataList = new ArrayList<message_rightdata>();
    }
    public Message_data(String name)
    {
        this.name = name;
        dataList = new ArrayList<message_rightdata>();
    }

    public Message_data(String name, message_rightdata data)
    {
        dataList = new ArrayList<message_rightdata>();
        this.name = name;
        dataList.add(data);
    }

    public void add(message_rightdata data)
    {
        dataList.add(data);
    }

    public boolean is_empty()
    {
        return dataList.isEmpty();
    }

    public message_rightdata recently_message()
    {
        return dataList.get(dataList.size() - 1);
    }

    public boolean remove(int index)
    {
        if (dataList.isEmpty())
        {
            return false;
        }
        dataList.remove(index);
        return true;
    }

    public  boolean remove(message_rightdata data)
    {
        if (dataList.isEmpty())
        {
            return false;
        }
        dataList.remove(data);
        return true;
    }

    public List<message_rightdata> get_datalist()
    {
        return dataList;
    }




}