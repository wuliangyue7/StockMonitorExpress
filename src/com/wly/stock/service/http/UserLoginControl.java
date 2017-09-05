package com.wly.stock.service.http;

import com.wly.database.DBOperator;
import com.wly.database.DBPool;
import io.netty.handler.codec.http.HttpMethod;
import org.restexpress.Request;
import org.restexpress.Response;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by wuly on 2017/8/12.
 */
public class UserLoginControl
{
    //for post
    public String create(Request req, Response res)
    {
        Map<String, List<String>> params = req.getBodyFromUrlFormEncoded();
      //  String userId = params.get("userId").get(0);

        return "UserLoginControl create";
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
