package Main_window;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: 李子麟
 * @date: 2021/3/20 20:42
 **/
public class Tools
{
    public static boolean isNumeric(String str) {
        // 该正则表达式可以匹配所有的数字 包括负数
        Pattern pattern = Pattern.compile("-?[0-9]+(\\.[0-9]+)?");
        String bigStr;
        try
        {
            bigStr = new BigDecimal(str).toString();
        }
        catch (Exception e)
        {
            return false;//异常 说明包含非数字。
        }

        Matcher isNum = pattern.matcher(bigStr); // matcher是全匹配
        if (!isNum.matches())
        {
            return false;
        }
        return true;
    }
}
