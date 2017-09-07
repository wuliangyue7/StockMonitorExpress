package com.wly.stock.common;

import com.google.gson.JsonObject;
import com.wly.common.ITickable;
import com.wly.user.RmbAsset;

import java.util.HashMap;

/**
 * Created by wuly on 2017/8/26.
 */
public interface ITradePlatform extends ITickable
{
    void SetContext(JsonObject context);
    boolean IsInit();
    void DoRefreshAsset();
    RmbAsset GetRmbAsset();
    StockAsset GetStockAsset(String code);
    HashMap<String, StockAsset> GetStockAssetList();
    void DoOrderRequest(int orderId, String code, int tradeFlag, float orderPrice, int orderCount);
    void DoQueryOrderStat();
}
