package com.wly.stock.service;

import com.wly.stock.service.http.TokenInfoEastmoney;
import com.wly.stock.service.http.UserLoginControl;
import io.netty.handler.codec.http.HttpMethod;
import org.restexpress.Request;
import org.restexpress.Response;
import org.restexpress.RestExpress;

/**
 * Created by wuly on 2017/8/12.
 */
public class ServiceHttp
{
    public static void main(String[] args)
    {
        new  ServiceHttp().Start();
    }

    public void Start()
    {
        RestExpress server = new RestExpress();
        server.setName("stock");
        server.uri("/userlogin", new UserLoginControl()).method(HttpMethod.GET, HttpMethod.POST).noSerialization();
        server.uri("/tokenInfoEastmoney", new TokenInfoEastmoney()).method(HttpMethod.GET, HttpMethod.POST).noSerialization();
//        server.setPort(8080);
        server.bind(8080);
        server.awaitShutdown();
    }
}
