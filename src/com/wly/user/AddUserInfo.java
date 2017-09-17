package com.wly.user;

import com.wly.common.LogUtils;
import com.wly.database.DBPool;
import com.wly.stock.common.StockConst;
import com.wly.stock.strategy.StragegyOrder;
//import com.wly.stock.strategy.StrategyStepAll;

/**
 * Created by Administrator on 2017/2/14.
 */
public class AddUserInfo
{
    public static void main(String[] args)
    {
        try
        {
            LogUtils.Init("config/log4j.properties");
            DBPool dbPool = DBPool.GetInstance();
            dbPool.Init("jdbc:mysql://127.0.0.1/stockmonitorexpress?useSSL=true", "root", "123456");
            //InsertUser();
            InsertStragegyOrder();
            System.out.println("insert finish!");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }


    static private void InsertUser()
    {
//        UserInfo userInfo = new UserInfo();
//        userInfo.platId = 0;
//        userInfo.platAcct = "121323";
//        userInfo.platPsw = "121323";

        final String SqlFormat = "insert into userinfo(plat_id, stat, plat_acct, plat_psw) " +
                "values(%d, %d,'%s', '%s')";
//        DBPool.GetInstance().ExecuteNoQuerySqlAsync (String.format(SqlFormat, userInfo.platId, 0, userInfo.platAcct, userInfo.platPsw));
    }

    static private void InsertStragegyOrder()
    {
        StragegyOrder stragegyOrder = new StragegyOrder(null);
        stragegyOrder.userId = 1;
        stragegyOrder.platId = StockConst.PlatEastmoney;
        stragegyOrder.stagegyOrderStat = StragegyOrder.OrderStatNormal;

//        //华东医药
//        stragegyOrder.tradeFlag = StockConst.TradeBuy;
//        stragegyOrder.code = "000963";
//        stragegyOrder.priceTrade = 43.01f;
//        stragegyOrder.countTrade = 1400;

        //荣盛发展 买
//        stragegyOrder.tradeFlag = StockConst.TradeBuy;
//        stragegyOrder.code = "002146";
//        stragegyOrder.priceTrade = 10.34f;
//        stragegyOrder.countTrade = 1200;

        //荣盛发展 卖
//        stragegyOrder.tradeFlag = StockConst.TradeSell;
//        stragegyOrder.code = "002146";
//        stragegyOrder.priceTrade = 11.14f;
//        stragegyOrder.countTrade = 1100;

        //众生药业 买
//        stragegyOrder.tradeFlag = StockConst.TradeBuy;
//        stragegyOrder.code = "002317";
//        stragegyOrder.priceTrade = 11.87f;
//        stragegyOrder.countTrade = 2300;

        StragegyOrder.SaveStragegyOrderDB(stragegyOrder);
    }
}
