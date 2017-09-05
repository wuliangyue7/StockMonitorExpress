package com.wly.stock.service;

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
//        RestExpress server = new RestExpress()
//                .setName("Echo");
//
//        server.uri("/echo", new Object()
//        {
//            @SuppressWarnings("unused")
//            public String read(Request req, Response res)
//            {
//                String value = req.getHeader("echo");
//                res.setContentType("text/xml");
//
//                if (value == null)
//                {
//                    return "<http_test><error>no value specified</error></http_test>";
//                }
//                else
//                {
//                    return String.format("<http_test><value>%s</value></http_test>", value);
//                }
//            }
//        })
//                .method(HttpMethod.GET)
//                .noSerialization();
//
//        server.bind(8000);
//        server.awaitShutdown();
//
//        ServiceHttp serviceHttp = new ServiceHttp();
//        serviceHttp.Start();
    }

    public void Start()
    {
        RestExpress server = new RestExpress();
        server.setName("stock");
        server.uri("/test", new UserLoginControl()).method(HttpMethod.GET, HttpMethod.POST).noSerialization();
//        server.setPort(8080);
        server.bind(8080);
        server.awaitShutdown();
    }
}
