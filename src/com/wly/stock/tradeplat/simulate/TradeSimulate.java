package com.wly.stock.tradeplat.simulate;

import com.google.gson.JsonObject;
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
    public void DoGetStockAsset()
    {

    }

    @Override
    public void DoGetRmbAsset()
    {

    }

    @Override
    public void DoOrderRequest(int orderId, String code, int tradeFlag, float orderPrice, int orderCount) {

    }

    @Override
    public void DoQueryOrderStat()
    {

    }

    public void DoCancelOrder(OrderInfo orderInfo)
    {

    }

    @Override
    public void OnTick() {

    }
}
