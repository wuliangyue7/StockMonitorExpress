package com.wly.stock.service.http;

import com.wly.stock.StockContext;
import org.restexpress.ContentType;
import org.restexpress.Request;
import org.restexpress.Response;

import java.util.Iterator;
import java.util.Map;

/**
 * Created by wuly on 2017/9/11.
 */
public class TokenInfoEastmoney
{
    //for post
    public String create(Request req, Response res)
    {
        String ret = null;
        String jsonStr = req.getBody().toString(ContentType.CHARSET);
        StockContext.GetInstance().GetUserManager().AddUser(jsonStr);
        return ret;
    }

    //for Get
    public String read(Request req, Response res)
    {
        System.out.println("UserLoginControl Get: "+req.getBaseUrl());
        Map<String, String> queryString = req.getQueryStringMap();
        Iterator iter = queryString.entrySet().iterator();
        while (iter.hasNext())
        {
            Map.Entry entry = (Map.Entry) iter.next();
            System.out.println(entry.getKey()+"="+entry.getValue());
        }
        return "UserLoginControl read";
    }

    public String update(Request req, Response res)
    {
        System.out.println("UserLoginControl update: "+req.getBaseUrl());
        return "UserLoginControl Post update";
    }

    public String readAll(Request request, Response response)
    {
        System.out.println("UserLoginControl readAll: "+request.getBaseUrl());
        return "UserLoginControl readAll";
    }
}
