package com.wly.user;

import com.google.gson.JsonObject;
import com.wly.common.IAsyncCallBack;
import com.wly.common.ITickable;
import com.wly.common.LogUtils;
import com.wly.common.Utils;
import com.wly.database.DBPool;
import com.wly.database.DBQuery;
import com.wly.stock.StockMarketInfoManager;
import com.wly.stock.common.StockConst;
import com.wly.stock.common.*;
import com.wly.stock.strategy.StragegyStep;
import com.wly.stock.tradeplat.ITradeInterface;
import com.wly.stock.tradeplat.eastmoney.TradeEastmoney;
import com.wly.stock.tradeplat.simulate.TradeSimulate;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Administrator on 2017/2/8.
 */
public class UserInfo implements IAsyncCallBack, ITickable
{
    private int id;
    public String name;

    private HashMap<Integer, ITradePlatform> tradePlatformHashMap = new HashMap<>();
    public ITradePlatform tradeInterface;
    private ArrayList<StragegyStep> stragegySteps = new ArrayList<>();

    public UserInfo(int id)
    {
        this.id = id;
        tradePlatformHashMap.put(StockConst.PlatSimulate, new TradeSimulate());
        tradePlatformHashMap.put(StockConst.PlatEastmoney, new TradeEastmoney(this));

        InitPolicySteps();
    }

    public void InitTradeContext(int platId, JsonObject context)
    {
        ITradePlatform tradePlatform = tradePlatformHashMap.get(platId);
        if(tradePlatform != null)
        {
            tradePlatform.SetContext(context);
            tradePlatform.DoRefreshAsset();
        }
    }

    public int GetUserId()
    {
        return id;
    }

    private boolean InitPolicySteps()
    {
        try {
            DBPool dbPool = DBPool.GetInstance();
            DBQuery dbQuery = dbPool.ExecuteQuerySync(String.format("select * from policy_step where user_id=%d", id));
            ResultSet rs = dbQuery.resultSet;
            StragegyStep stragegyStep;
            while (rs.next())
            {
                stragegyStep = new StragegyStep(this);
                stragegyStep.id = rs.getInt("id");
                stragegyStep.userId = rs.getInt("user_id");
                stragegyStep.platId = rs.getInt("plat_id");
                stragegyStep.code = rs.getString("code");
                stragegyStep.priceInit = rs.getFloat("price_init");
                stragegyStep.countInit = rs.getInt("count_init");
                stragegyStep.priceStepUint = rs.getFloat("price_unit");
                stragegyStep.countStepUnit = rs.getInt("step_unit");
                stragegyStep.buyOffset = rs.getFloat("buy_offset");
                stragegyStep.sellOffset = rs.getFloat("sell_offset");
                stragegyStep.priceMin = rs.getFloat("min_price");
                stragegyStep.priceMax = rs.getFloat("max_price");
                stragegyStep.stragegyStat = rs.getInt("policy_stat");
                stragegyStep.priceLast = rs.getFloat("price_last");
                stragegyStep.orderIdBuy = rs.getInt("buyorder_id");
                stragegyStep.orderIdSell = rs.getInt("sellorder_id");
                stragegySteps.add(stragegyStep);
                StockMarketInfoManager.GetInstance().AddMonitorCode(stragegyStep.code);
            }
            dbQuery.Close();
            return true;
        }
        catch (Exception ex)
        {
            LogUtils.GetLogger(LogUtils.LOG_REALTIME).error(ex.getMessage());
            return  false;
        }
    }

    @Override
    public void OnTick()
    {
        for(Map.Entry<Integer, ITradePlatform> entry : tradePlatformHashMap.entrySet())
        {
            entry.getValue().OnTick();
        }

        int i;
        for(i=0; i<stragegySteps.size(); ++i)
        {
            stragegySteps.get(i).OnTick();
        }
    }

    public StockAsset GetStockAsset(int platId, String code)
    {
        ITradePlatform tradeInterface = tradePlatformHashMap.get(platId);
        return tradeInterface.GetStockAsset(code);
    }

    public RmbAsset GetRmbAsset(int platId)
    {
        ITradePlatform tradeInterface = tradePlatformHashMap.get(platId);
        return tradeInterface.GetRmbAsset();
    }

    @Override
    public void OnCallback(int id, Object obj)
    {

    }
}
