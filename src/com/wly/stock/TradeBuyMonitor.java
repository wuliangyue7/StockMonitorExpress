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
 * Created by wuly on 2018/5/14.
 */
public class TradeBuyMonitor
{
    static  public void main(String[] args)
    {
        LogUtils.Init("config/log4j.properties");
        BuyPriceMonitorInfo buyPriceMonitorInfo = new BuyPriceMonitorInfo();
        if (args.length >= 1)
        {
            buyPriceMonitorInfo.acct = args[0];
        }
        else
        {
            buyPriceMonitorInfo.acct = Utils.GetInput("please input account:");
        }

        if(args.length >= 2)
        {
            buyPriceMonitorInfo.code = args[1];
        }
        else
        {
            buyPriceMonitorInfo.code = Utils.GetInput("please input stock code:");
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
        buyPriceMonitorInfo.countTrade = Integer.parseInt(strCount);

        String strPrice = "";
        if(args.length >= 4)
        {
            strPrice = args[3];
        }
        else
        {
            strPrice = Utils.GetInput("please input trade price:");
        }
        buyPriceMonitorInfo.price = Float.parseFloat(strPrice);

        Timer timer = new Timer();
        StockBuyPriceMonitor task = new StockBuyPriceMonitor();
        task.buyPriceMonitorInfo = buyPriceMonitorInfo;
        timer.schedule(task, 0, 1000);
    }
}

class BuyPriceMonitorInfo
{
    public String acct;
    public String code;
    public int countTrade;
    public float price;
    public int tradeFlag = StockConst.TradeBuy;
}

class StockBuyPriceMonitor extends TimerTask
{
    public BuyPriceMonitorInfo buyPriceMonitorInfo;
    private StockInfoProviderSina provider = new StockInfoProviderSina();
    @Override
    public void run() {

        try {
            StockRuntimeInfo stockRuntimeInfo = provider.GetStockInfoByCode(buyPriceMonitorInfo.code);
            //            System.out.println(stockRuntimeInfo.toDesc());
            StockRuntimeInfo.PriceInfo priceInfo = stockRuntimeInfo.buyInfo.get(0);
            String buyDesc = String.format("Code:%s Name:%s Price: %.2f change;%.2f ratio:%.2f%% buyCount:%d targetPrice:%.2f",
                    stockRuntimeInfo.code, stockRuntimeInfo.name, stockRuntimeInfo.priceNew, stockRuntimeInfo.GetChange(),
                    stockRuntimeInfo.GetRatio(), priceInfo.amount/100, buyPriceMonitorInfo.price);
            System.out.println(buyDesc);

            if(stockRuntimeInfo.TestDeal(buyPriceMonitorInfo.tradeFlag, buyPriceMonitorInfo.price,
                    buyPriceMonitorInfo.countTrade))
            {
                EastMoneyTradeUtils.DoTrade(buyPriceMonitorInfo.acct, buyPriceMonitorInfo.code, stockRuntimeInfo.name,
                        buyPriceMonitorInfo.tradeFlag, buyPriceMonitorInfo.countTrade, buyPriceMonitorInfo.price);
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
