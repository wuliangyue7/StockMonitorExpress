package com.wly.stock;

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
        StockContext stockContext = StockContext.GetInstance();
        stockContext.SetExecutorService(Executors.newCachedThreadPool());
        stockContext.SetServiceStockRuntimeInfo(new ServiceStockRuntimeInfo(new StockInfoProviderSina()));
//        stockContext.SetServiceStockTrade(ServiceStockTrade.GetInstance());
        stockContext.SetUserManger(new UserManager());
        stockContext.GetServiceStockRuntimeInfo().Start();

        stockContext.Start();
        ServiceHttp serviceHttp = new ServiceHttp();
        serviceHttp.Start();
    }
}
