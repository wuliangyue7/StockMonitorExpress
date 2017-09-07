package com.wly.stock.service.http;

import com.wly.UserInfoManager;
import com.wly.database.DBOperator;
import com.wly.database.DBPool;
import com.wly.stock.StockContext;
import com.wly.stock.common.StockConst;
import com.wly.user.UserManager;
import io.netty.handler.codec.http.HttpMethod;
import org.restexpress.ContentType;
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
