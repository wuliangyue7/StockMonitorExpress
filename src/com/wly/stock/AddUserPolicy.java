package com.wly.stock;

import com.wly.common.LogUtils;
import com.wly.database.DBPool;
import com.wly.stock.common.StockConst;
import com.wly.stock.strategy.StragegyStep;

/**
 * Created by wuly on 2017/9/7.
 */
public class AddUserPolicy
{
    static public void main(String[] args)
    {
        LogUtils.Init("config/log4j.properties");
        DBPool dbPool = DBPool.GetInstance();
        dbPool.Init("jdbc:mysql://127.0.0.1/stockmonitorexpress?useSSL=true", "root", "123456");
        StragegyStep stragegyStep = new StragegyStep(null);
        stragegyStep.id = 1;
        stragegyStep.userId = 1;
        stragegyStep.platId = StockConst.PlatEastmoney;
        stragegyStep.stragegyStat = StragegyStep.StragegyStepStatNormal;
        stragegyStep.priceLast = 23;
        stragegyStep.code = "600056";
        stragegyStep.priceInit = 24.10f;
        stragegyStep.countInit = 3000;
        stragegyStep.priceStepUint = 1.2f;
        stragegyStep.countStepUnit = 500;
        stragegyStep.sellOffset = 0.08f;
        stragegyStep.buyOffset = -0.03f;
        stragegyStep.priceMin = 20.0f;
        stragegyStep.priceMax = 30f;
        StragegyStep.SaveToDB(stragegyStep);
    }
}
