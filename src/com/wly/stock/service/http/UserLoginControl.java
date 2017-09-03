package com.wly.stock.service.http;

import org.restexpress.Request;
import org.restexpress.Response;

/**
 * Created by wuly on 2017/8/12.
 */
public class UserLoginControl
{
    public String Create(Request req, Response res)
    {
        System.out.println("UserLoginControl: "+req.getBaseUrl());
        res.setResponseCreated();
        return "UserLoginControl";
    }
}
