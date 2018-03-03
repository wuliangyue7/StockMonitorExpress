package com.wly.network.http;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.wly.common.LogUtils;
import com.wly.common.Utils;
import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.cookie.BasicClientCookie;
import sun.misc.BASE64Encoder;

import java.net.URLEncoder;
import java.util.Date;

/**
 * Created by wuly on 2017/8/11.
 */
public class CookieItem
{
    public String path;
    public String domain;
    public String key;
    public String value;
    public Date expireDate;

    public BasicClientCookie GetCookie()
    {
         BasicClientCookie cookie = new BasicClientCookie(key, value);
        cookie.setPath(path);
        cookie.setDomain(domain);
        cookie.setExpiryDate(expireDate);
        return cookie;
    }

    static  public CookieStore FillCookieStore(CookieStore cookieStore, JsonArray jsonArray)
    {
        Cookie cookie;
        int i;
        for(i=0; i<jsonArray.size(); ++i)
        {
            cookie = CookieItem.ParserJson2Cookie(jsonArray.get(i).getAsJsonObject());
            if(cookie != null)
            {
                cookieStore.addCookie(cookie);
            }
        }
        return cookieStore;
    }

    static public Cookie ParserJson2Cookie(JsonObject jsonObject)
    {
        BasicClientCookie cookie = null;
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
}
