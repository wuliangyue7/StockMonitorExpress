package com.wly.stock;

import com.wly.common.LogUtils;
import com.wly.common.Utils;
import com.wly.stock.common.StockConst;
import com.wly.stock.common.StockRuntimeInfo;
import com.wly.stock.common.StockUtils;
import com.wly.stock.infoplat.sina.StockInfoProviderSina;
import com.wly.stock.tradeplat.eastmoney.EastMoneyTradeUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by wuly on 2018/5/2.
 */
public class TradeMaxPriceMonitor
{
    static public void main(String[] args)
    {
        LogUtils.Init("config/log4j.properties");
        MaxPriceMonitorInfo maxPriceMonitorInfo = new MaxPriceMonitorInfo();
        if (args.length >= 1)
        {
            maxPriceMonitorInfo.acct = args[0];
        }
        else
        {
            maxPriceMonitorInfo.acct = Utils.GetInput("please input account:");
        }

        if(args.length >= 2)
        {
            maxPriceMonitorInfo.code = args[1];
        }
        else
        {
            maxPriceMonitorInfo.code = Utils.GetInput("please input stock code:");
        }

        String strCount = null;
        if(args.length >= 3)
        {
            strCount = args[2];
        }
        else
        {
            strCount = Utils.GetInput("please input trade count:");
        }
        maxPriceMonitorInfo.countTrade = Integer.parseInt(strCount);

        String strPrice = "";
        if(args.length >= 4)
        {
            strPrice = args[3];
        }
        else
        {
            strPrice = Utils.GetInput("please input trade price:");
        }
        maxPriceMonitorInfo.price = Float.parseFloat(strPrice);

        String strMonitorCount = null;
        if(args.length >= 5)
        {
            strMonitorCount = args[4];
        }
        else
        {
            strMonitorCount = Utils.GetInput("please input Monitor Count:");
        }
        maxPriceMonitorInfo.countLeft = Integer.parseInt(strMonitorCount)*100;

        Timer timer = new Timer();
        StockMaxPriceMonitor task = new StockMaxPriceMonitor();
        task.maxPriceMonitorInfo = maxPriceMonitorInfo;
        timer.schedule(task, 0, 1000);
    }
}

class MaxPriceMonitorInfo
{
    public String acct;
    public String code;
    public int countTrade;
    public float price;
    public int countLeft;
    public int tradeFlag = StockConst.TradeSell;
}

class StockMaxPriceMonitor extends TimerTask
{
    public MaxPriceMonitorInfo maxPriceMonitorInfo;
    private StockInfoProviderSina provider = new StockInfoProviderSina();
    @Override
    public void run() {

        try {
            StockRuntimeInfo stockRuntimeInfo = provider.GetStockInfoByCode(maxPriceMonitorInfo.code);
//            System.out.println(stockRuntimeInfo.toDesc());
            StockRuntimeInfo.PriceInfo priceInfo = stockRuntimeInfo.buyInfo.get(0);
            String buyDesc = String.format("Code:%s Name:%s Price: %.2f change;%.2f ratio:%.2f%% buyCount:%d targetCount:%d",
                    stockRuntimeInfo.code, stockRuntimeInfo.name, stockRuntimeInfo.priceNew, stockRuntimeInfo.GetChange(),
                    stockRuntimeInfo.GetRatio(), priceInfo.amount/100, maxPriceMonitorInfo.countLeft/100);
            System.out.println(buyDesc);

            float maxPrice = StockUtils.GetMaxPrice(stockRuntimeInfo.priceLast);
            if(maxPrice == priceInfo.price && priceInfo.amount <= maxPriceMonitorInfo.countLeft)
            {
                EastMoneyTradeUtils.DoTrade(maxPriceMonitorInfo.acct, maxPriceMonitorInfo.code, stockRuntimeInfo.name,
                        StockConst.TradeSell, maxPriceMonitorInfo.countTrade, maxPriceMonitorInfo.price);
                LogUtils.LogRealtime("TradeMaxPriceMonitor Done!");
                System.gc();
                cancel();
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }
}
