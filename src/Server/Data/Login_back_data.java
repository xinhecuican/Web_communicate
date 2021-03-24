package Server.Data;
import Main_window.Data.Send_data;
import java.io.Serializable;
import java.util.List;

/**
 * @author: 李子麟
 * @date: 2021/3/20 9:20
 **/
public class Login_back_data implements Serializable
{
    public int id;
    public boolean is_error;
    public String name;
    public List<Send_data> storage_data;
    public Login_back_data(boolean is_error, List<Send_data> storage_data)
    {
        this.is_error = is_error;
        this.storage_data = storage_data;
        id = 0;
    }
}
