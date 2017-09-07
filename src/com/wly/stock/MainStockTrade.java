package com.wly.stock;

import com.wly.common.LogUtils;
import com.wly.database.DBPool;
import com.wly.stock.infoplat.sina.StockInfoProviderSina;
import com.wly.stock.service.ServiceHttp;
import com.wly.stock.service.ServiceStockRuntimeInfo;
import com.wly.user.UserManager;

import java.util.concurrent.Executors;

/**
 * Created by wuly on 2017/6/26.
 */
public class MainStockTrade
{
    public static void main(String[] args)
    {
        LogUtils.Init("config/log4j.properties");
        DBPool dbPool = DBPool.GetInstance();
        dbPool.Init("jdbc:mysql://127.0.0.1/stockmonitorexpress?useSSL=true", "root", "123456");

        StockContext stockContext = StockContext.GetInstance();
        stockContext.SetExecutorService(Executors.newCachedThreadPool());
        stockContext.SetServiceStockRuntimeInfo(new ServiceStockRuntimeInfo(new StockInfoProviderSina()));
        stockContext.SetUserManger(new UserManager());
        stockContext.GetServiceStockRuntimeInfo().Start();

        stockContext.Start();
        ServiceHttp serviceHttp = new ServiceHttp();
        serviceHttp.Start();
    }
}
