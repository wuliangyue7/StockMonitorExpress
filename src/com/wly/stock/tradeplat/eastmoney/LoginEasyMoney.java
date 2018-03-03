package com.wly.stock.tradeplat.eastmoney;

import com.wly.common.LogUtils;
import com.wly.common.Utils;
import com.wly.stock.tradeplat.eastmoney.TradeEastmoneyImpl;

public class LoginEasyMoney
{
    static public void main(String[] args)
    {
        LogUtils.Init("config/log4j.properties");
        String acct = null, psw=null;
        if (args.length >= 1)
        {
            acct = args[0];
        }

        if (args.length >= 2)
        {
            psw = args[1];
        }

        if(acct == null)
        {
            acct = Utils.GetInput("please input account:");
        }

        if(psw == null)
        {
            psw = Utils.GetInput("please input password:");
        }

        TradeEastmoneyImpl tradeEastmoney = new TradeEastmoneyImpl();
        tradeEastmoney.Login(acct, psw);
    }
}
