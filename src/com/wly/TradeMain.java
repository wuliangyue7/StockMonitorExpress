package com.wly;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wly.common.LogUtils;
import com.wly.common.Utils;
import com.wly.network.http.CookieItem;
import com.wly.stock.common.*;
import com.wly.stock.infoplat.sina.StockInfoProviderSina;
import com.wly.stock.tradeplat.eastmoney.EastMoneyTradeUtils;
import com.wly.stock.tradeplat.eastmoney.TradeEastmoneyImpl;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;

/**
 * Created by wuly on 2016/11/26.
 */
public class TradeMain
{
    static public void main(String[] args) throws Exception
    {
        LogUtils.Init("config/log4j.properties");
        String acct;
        if (args.length >= 1)
        {
            acct = args[0];
        }
        else
        {
            acct = Utils.GetInput("please input account:");
        }

        String code = null;
        if(args.length >= 2)
        {
            code = args[1];
        }
        else
        {
            code = Utils.GetInput("please input stock code:");
        }

        String strPrice = "";
        if(args.length >= 3)
        {
            strPrice = args[2];
        }
        else
        {
            strPrice = Utils.GetInput("please input trade price 0 for runtimePrice:");
        }
        float price = Float.parseFloat(strPrice);

        String strCount = null;
        if(args.length >= 4)
        {
            strCount = args[3];
        }
        else
        {
            strCount = Utils.GetInput("please input trade count:");
        }
        int count = Integer.parseInt(strCount);

        String strTradeFlag = null;
        if(args.length >= 5)
        {
            strTradeFlag = args[4];
        }
        else
        {
            strTradeFlag = Utils.GetInput("please input trade tradeFlag 0-buy 1-sell:");
        }
        int tradeFlag = Integer.parseInt(strTradeFlag);
        if (tradeFlag != StockConst.TradeBuy && tradeFlag != StockConst.TradeSell)
        {
            LogUtils.LogRealtime(String.format("invalid trade flag %d", tradeFlag));
            return;
        }

        StockRuntimeInfo stockRuntimeInfo = StockInfoProviderSina.GetStockRuntimeInfoByCode(code);
        if (stockRuntimeInfo == null)
        {
            LogUtils.LogRealtime("get runtime info failed!");
            return;
        }

        if(price < 0.01)
        {
            if(tradeFlag == StockConst.TradeSell)
            {
                price = stockRuntimeInfo.priceBuy;
            }
            else
            {
                price = stockRuntimeInfo.priceSell;
            }
        }

        float maxPrice = StockUtils.GetMaxPrice(stockRuntimeInfo.priceLast);
        if (maxPrice < price)
        {
            LogUtils.LogRealtime(String.format("price(%f) is bigger then maxprice(%f)", price, maxPrice));
            return;
        }

        float minPrice = StockUtils.GetMinPrice(stockRuntimeInfo.priceLast);
        if (minPrice > price)
        {
            LogUtils.LogRealtime(String.format("price(%f) is lowwer then mminprice(%f)", price, minPrice));
            return;
        }

        TradeInfo  tradeInfo = new TradeInfo();
        tradeInfo.code = code;
        tradeInfo.name = stockRuntimeInfo.name;
        tradeInfo.orderCount = count;
        tradeInfo.tradeFlag = tradeFlag;
        tradeInfo.orderPrice = price;



        String checkInfo = String.format("please confirm trade info (Y/N) trade info:\n %s %s %.2f %d %s", tradeInfo.code, tradeInfo.name, tradeInfo.orderPrice,
                tradeInfo.orderCount, tradeInfo.tradeFlag==StockConst.TradeSell?"Sell":"Buy");
        String confirm = Utils.GetInput(checkInfo);
        if (!confirm.toUpperCase().equals("Y"))
        {
            LogUtils.LogRealtime("Trade Canceled!");
            return;
        }

        String loginInfo = Utils.ReadFile(String.format("loginInfo/%s.txt", acct));
        LogUtils.LogRealtime(loginInfo);
        JsonObject jsonObject = new JsonParser().parse(loginInfo).getAsJsonObject();
        CookieStore  cookieStore = new BasicCookieStore();
        CookieItem.FillCookieStore(cookieStore, jsonObject.getAsJsonArray("cookies"));
        String validatekey = jsonObject.get("validatekey").getAsString();


        EastMoneyTradeUtils.DoTrade(cookieStore, validatekey, tradeInfo);

    }
}
