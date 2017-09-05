package com.wly.stock.tradeplat.simulate;

import com.wly.stock.common.IStockOrderManager;
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

    public TradeSimulate(IStockOrderManager stockOrderManager)
    {
        SetStockOrderManager(stockOrderManager);
    }

    @Override
    public void SetContext(Object context)
    {

    }

    @Override
    public void SetStockOrderManager(IStockOrderManager stockOrderManager)
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
