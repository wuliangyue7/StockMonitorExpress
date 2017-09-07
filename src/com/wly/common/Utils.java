package com.wly.common;

import com.google.gson.JsonObject;
import com.wly.database.DBPool;
import com.wly.database.DBQuery;
import com.wly.user.UserInfo;
import org.apache.http.HeaderIterator;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by Administrator on 2016/11/22.
 */
public class Utils
{
    static  public  void Log(Object obj)
    {
       // System.out.println(GetTimestampNow().toString()+" "+ obj);
    }
    static  public  void LogException(Exception ex)
    {
        System.out.println(ex.getMessage());
        ex.printStackTrace();
        //System.out.println(ex.getStackTrace());
    }

    static public Timestamp GetTimestampNow()
    {
        java.util.Date date=new java.util.Date();
        return  new Timestamp(date.getTime());
    }

    public static String GetResponseFull(HttpResponse httpResponse)
            throws ParseException, IOException
    {
        StringBuilder sb = new StringBuilder();
        // 获取响应消息实体
        HttpEntity entity = httpResponse.getEntity();
        // 响应状态
        sb.append("status:" + httpResponse.getStatusLine()+"\n");
        sb.append("headers:\n");

        HeaderIterator iterator = httpResponse.headerIterator();
        while (iterator.hasNext()) {
            sb.append("\t" + iterator.next()+"\n");
        }
        // 判断响应实体是否为空
        if (entity != null) {
            String responseString = EntityUtils.toString(entity);
            sb.append("response length:" + responseString.length()+"\n");
            sb.append("response content:"
                    + responseString.replace("\r\n", ""));
        }

        return sb.toString();
    }

    public static String GetResponseContent(HttpResponse httpResponse)
    {
        try
        {
            HttpEntity entity = httpResponse.getEntity();
            return EntityUtils.toString(entity);
        }
        catch (Exception ex)
        {
            return ex.getMessage();
        }
    }

    public static byte[] GetResponseBytes(HttpResponse httpResponse)
    {
        try
        {
            HttpEntity entity = httpResponse.getEntity();
            return EntityUtils.toByteArray(entity);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        return null;
    }

    public static volatile int IdIndex = 0;
    static public int GetId()
    {
        return ++IdIndex;
    }

    static public String GetDate()
    {
        return GetDate("yyyyMMdd");// new Date()为获取当前系统时间
    }

    static public String GetDataTime()
    {
        return GetDate("yyyyMMdd-HHmmss");// new Date()为获取当前系统时间
    }

    static public String GetDate(String format)
    {
        SimpleDateFormat df = new SimpleDateFormat(format);//设置日期格式
        return df.format(new Date());// new Date()为获取当前系统时间
    }

    static public boolean IsNullOrEmpty(String str)
    {
        if(str == null || str.length() == 0)
        {
            return true;
        }

        return false;
    }

    static public String GetDateTime()
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");//设置日期格式
        return df.format(new Date());// new Date()为获取当前系统时间
    }

    static public String GetVerifyCode()
    {
        return "";
    }

    static public String GetInput(String msg)
    {
        Scanner in=new Scanner(System.in);
        System.out.println(msg);
        PrintStream psBak = System.out;
        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) {
                // do nothing
            }
        }));
        Logger logger = LogUtils.GetLogger(LogUtils.LOG_REALTIME);
        Enumeration appenders =  logger.getAllAppenders();
        logger.removeAllAppenders();

        System.out.println(msg);
        String input = in.nextLine();

        while(appenders.hasMoreElements())
        {
            logger.addAppender((Appender)appenders.nextElement());
        }

        System.setOut(psBak);
        return input;
    }

    static public void WriteFile(String filePath, byte[] bytes)
    {
        try
        {
            OutputStream os = new FileOutputStream(filePath);
            os.write(bytes, 0, bytes.length);
            os.close();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    static public String FormatResult(int code, String msg)
    {
        return String.format("{\"code\":%d,\"msg\":\"%s\"}", code, msg);
    }

    static public JsonObject ParserCookie2Json(Cookie cookie)
    {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("name", cookie.getName());
        jsonObject.addProperty("value", cookie.getValue());
        jsonObject.addProperty("domain", cookie.getDomain());
        jsonObject.addProperty("path", cookie.getPath());
        if(cookie.getExpiryDate() != null)
        {
            jsonObject.addProperty("expiryDate", cookie.getExpiryDate().toString());
        }

        return jsonObject;
    }

    static public Cookie ParserJson2Cookie(JsonObject jsonObject)
    {
        BasicClientCookie cookie = null;
//        if(!jsonObject.has("name") || jsonObject.has("value"))
//        {
//            return cookie;
//        }

        cookie = new BasicClientCookie(jsonObject.get("name").getAsString(), jsonObject.get("value").getAsString());
        if(jsonObject.has("domain"))
        {
            cookie.setDomain(jsonObject.get("domain").getAsString());
        }

        if(jsonObject.has("path"))
        {
            cookie.setPath(jsonObject.get("path").getAsString());
        }

        if(jsonObject.has("expiryDate"))
        {
            cookie.setExpiryDate(new Date(jsonObject.get("expiryDate").getAsString()));
        }

        return cookie;
    }
}
