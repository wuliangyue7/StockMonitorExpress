package com.wly.stock.tradeplat.simulate;

import com.google.gson.JsonObject;
import com.wly.stock.common.ITradeManager;
import com.wly.stock.common.ITradePlatform;
import com.wly.stock.common.OrderInfo;

/**
 * Created by Administrator on 2017/9/4.
 */
public class TradeSimulate implements ITradePlatform
{
    public TradeSimulate()
    {
    }

    public TradeSimulate(ITradeManager stockOrderManager)
    {
        SetStockOrderManager(stockOrderManager);
    }

    @Override
    public void SetContext(JsonObject context)
    {

    }

    @Override
    public boolean IsInit()
    {
        return false;
    }

    @Override
    public void SetStockOrderManager(ITradeManager stockOrderManager)
    {

    }

    @Override
    public void DoGetStockAsset()
    {

    }

    @Override
    public void DoGetRmbAsset()
    {

    }

    @Override
    public void DoOrderRequest(OrderInfo orderInfo)
    {

    }

    @Override
    public void DoQueryOrderStat()
    {

    }

    @Override
    public void DoCancelOrder(OrderInfo orderInfo)
    {

    }
}
