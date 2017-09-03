package com.wly.stock.common;

import com.wly.user.UserInfo;

/**
 * Created by wuly on 2017/8/26.
 */
public interface ITradePlatform
{
    void SetContext(Object context);
    void SetStockOrderManager(IStockOrderManager stockOrderManager);
    void DoGetStockAsset();
    void DoGetRmbAsset();
    void DoOrderRequest(OrderInfo orderInfo);
    void DoQueryOrderStat();
    void DoCancelOrder(OrderInfo orderInfo);
}
