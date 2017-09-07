package com.wly.stock.tradeplat.simulate;

import com.google.gson.JsonObject;
import com.wly.stock.common.ITradePlatform;
import com.wly.stock.common.OrderInfo;
import com.wly.stock.common.StockAsset;
import com.wly.user.RmbAsset;

import java.util.HashMap;

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
    public void DoRefreshAsset() {

    }

    @Override
    public RmbAsset GetRmbAsset() {
        return null;
    }

    @Override
    public StockAsset GetStockAsset(String code) {
        return null;
    }

    @Override
    public HashMap<String, StockAsset> GetStockAssetList() {
        return null;
    }

    public void DoGetStockAsset()
    {

    }

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
