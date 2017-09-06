package com.wly.stock.common;

import com.google.gson.JsonObject;

/**
 * Created by wuly on 2017/8/26.
 */
public interface ITradePlatform
{
    void SetContext(JsonObject context);
    boolean IsInit();
    void SetStockOrderManager(ITradeManager stockOrderManager);
    void DoGetStockAsset();
    void DoGetRmbAsset();
    void DoOrderRequest(OrderInfo orderInfo);
    void DoQueryOrderStat();
    void DoCancelOrder(OrderInfo orderInfo);
}
