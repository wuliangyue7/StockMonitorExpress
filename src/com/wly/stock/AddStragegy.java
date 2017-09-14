package com.wly.stock;

import com.wly.common.LogUtils;
import com.wly.database.DBPool;
import com.wly.stock.common.StockConst;
import com.wly.stock.strategy.StragegyStep;

/**
 * Created by Administrator on 2017/9/14.
 */
public class AddStragegy
{
    static public void main(String args[])
    {
        StragegyStep stragegyStep = new StragegyStep(null);
        stragegyStep.userId = 1;
        stragegyStep.platId = StockConst.PlatEastmoney;
        stragegyStep.code = "000963";
        stragegyStep.priceInit = 45f;
        stragegyStep.countInit = 600;
        stragegyStep.priceStepUint = 1f;
        stragegyStep.countStepUnit = 200;
        stragegyStep.buyOffset = -0.7f;
        stragegyStep.sellOffset = 0.7f;
        stragegyStep.stragegyStat = StragegyStep.StragegyStepStatWaitInit;
        stragegyStep.priceMax = 999f;
        stragegyStep.priceMin = 40f;

        LogUtils.Init("config/log4j.properties");
        DBPool dbPool = DBPool.GetInstance();
        dbPool.Init("jdbc:mysql://127.0.0.1/stockmonitorexpress?useSSL=true", "root", "123456");
        StragegyStep.SaveToDB(stragegyStep);

    }
}
