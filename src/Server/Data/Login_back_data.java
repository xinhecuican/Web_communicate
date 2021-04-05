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
    public String name;
    public List<Send_data> storage_data;
    public Login_type type;
    public Login_back_data(Login_type type, List<Send_data> storage_data)
    {
        this.type = type;
        this.storage_data = storage_data;
        id = 0;
    }

    public enum Login_type
    {
        None,
        Friend_info,
        Error,
        Login,
        Register,
        Login_And_Heart,

    }
}
