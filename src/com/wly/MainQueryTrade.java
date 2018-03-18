package com.wly;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.wly.common.LogUtils;
import com.wly.common.Utils;
import com.wly.network.http.CookieItem;
import com.wly.stock.tradeplat.eastmoney.EastMoneyTradeUtils;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;

/**
 * Created by wuly on 2018/3/17.
 */
public class MainQueryTrade
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

        String loginInfo = Utils.ReadFile(String.format("loginInfo/%s.txt", acct));
        JsonObject jsonObject = new JsonParser().parse(loginInfo).getAsJsonObject();
        CookieStore cookieStore = new BasicCookieStore();
        CookieItem.FillCookieStore(cookieStore, jsonObject.getAsJsonArray("cookies"));
        String validatekey = jsonObject.get("validatekey").getAsString();
        EastMoneyTradeUtils.QueryMoneyInfo(cookieStore, validatekey);
        EastMoneyTradeUtils.QueryTradeInfo(cookieStore, validatekey);

    }
}
