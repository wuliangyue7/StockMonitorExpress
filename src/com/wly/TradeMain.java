package com.wly;

import com.wly.common.LogUtils;
import com.wly.common.Utils;
import com.wly.stock.tradeplat.eastmoney.TradeEastmoneyImpl;

/**
 * Created by wuly on 2016/11/26.
 */
public class TradeMain
{
    static public void main(String[] args)
    {
        LogUtils.Init("config/log4j.properties");
        TradeEastmoneyImpl tradeEastmoney = new TradeEastmoneyImpl();
        String psw = Utils.GetInput("please input password:");
        tradeEastmoney.Login("540600166072", psw);
    }
}
