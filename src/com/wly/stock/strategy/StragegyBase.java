package com.wly.stock.strategy;

import com.wly.common.Utils;
import com.wly.stock.common.*;
import com.wly.stock.tradeplat.eastmoney.EastmoneyUtils;
import com.wly.user.RmbAsset;
import com.wly.user.UserInfo;

/**
 * Created by wuly on 2017/9/17.
 */
public abstract class StragegyBase
{
    public int id;
    public int userId;
    public int platId;

    protected UserInfo userInfo;

    public StragegyBase(UserInfo userInfo)
    {
        this.userInfo = userInfo;
    }

    public abstract void OnTick();

    protected OrderInfo CreateOrder(String code, int tradeFlag, float price, int count)
    {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.userId = userId;
        orderInfo.SetOrderStat(OrderInfo.OrderStat_Ready);
        orderInfo.tradeFlag = tradeFlag;
        orderInfo.code = code;
        orderInfo.dateTime = Utils.GetDataTime();
        orderInfo.orderPrice = price;
        orderInfo.orderCount = count;
        orderInfo.id = OrderInfo.SaveDb(orderInfo);
        return orderInfo;
    }

    protected int GetStockAssetCountEffective(String code)
    {
        StockAsset stockAsset = userInfo.GetStockAsset(platId, code);
        if(stockAsset == null)
        {
            return 0;
        }

        return stockAsset.amountActive;
    }

    protected float CacuBuyOrderMoney(String code, float price, int count)
    {
        float fee = EastmoneyUtils.CaculateTradeFee(code, StockConst.TradeBuy, price, count);
        return price * count + fee;
    }

    protected boolean CanBuy(StockRuntimeInfo stockRuntimeInfo, String code, float price, int count)
    {
        boolean ret = false;
        if (!StockUtils.TestTrade(stockRuntimeInfo, StockConst.TradeBuy, price, count))
        {
            return ret;
        }

        float fee = EastmoneyUtils.CaculateTradeFee(code, StockConst.TradeBuy, price, count);
        float cost = price * count + fee;
        RmbAsset rmbAsset = userInfo.GetRmbAsset(platId);
        if(rmbAsset!= null && cost <= rmbAsset.activeAmount)
        {
            ret = true;
        }
        return ret;
    }
}
