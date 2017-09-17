package com.wly.user;

import com.google.gson.JsonObject;
import com.wly.common.IAsyncCallBack;
import com.wly.common.ITickable;
import com.wly.stock.common.StockConst;
import com.wly.stock.common.*;
import com.wly.stock.strategy.StragegyOrder;
import com.wly.stock.strategy.StragegyBase;
import com.wly.stock.strategy.StragegyStep;
import com.wly.stock.tradeplat.eastmoney.TradeEastmoney;
import com.wly.stock.tradeplat.simulate.TradeSimulate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    private ArrayList<StragegyBase> stragegyList = new ArrayList<>();

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
        stragegyList.clear();
        stragegyList.addAll(StragegyOrder.GetStagegyOrderList(this));

        stragegySteps.clear();
        stragegySteps.addAll(StragegyStep.GetStagegyStepList(this));
        return true;
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

        for(i=0; i<stragegyList.size(); ++i)
        {
            stragegyList.get(i).OnTick();
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
