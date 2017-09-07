package com.wly.stock.common;

import com.google.gson.JsonObject;
import com.wly.common.ITickable;

/**
 * Created by wuly on 2017/8/26.
 */
public interface ITradePlatform extends ITickable
{
    void SetContext(JsonObject context);
    boolean IsInit();
    void DoGetStockAsset();
    void DoGetRmbAsset();
    void DoOrderRequest(int orderId, String code, int tradeFlag, float orderPrice, int orderCount);
    void DoQueryOrderStat();
}
