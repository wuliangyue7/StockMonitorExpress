package com.wly.stock.strategy;

import com.wly.common.Utils;
import com.wly.stock.StockContext;
import com.wly.stock.common.*;
import com.wly.stock.tradeplat.eastmoney.EastmoneyUtils;
import com.wly.user.UserInfo;

/**
 * Created by wuly on 2017/8/26.
 */
public class StragegyStep implements IOrderStatMonitor,IStockRuntimeInfoMonitor
{
    static public final int StragegyStepStatClose = 0;
    static public final int StragegyStepStatWaitInit = 1;
    static public final int StragegyStepStatNormal = 2;

    public int id;
    public String code;
    public int stragegyStat;
    public float priceInit;
    public int countInit;
    public int countStep;
    public float priceStepUint;
    public float buyOffset;
    public float sellOffset;
    public float priceMin;
    public float priceMax;
    public float priceLast; //上一次交易的参考价格

    public Object orderContext;

    public OrderInfo orderBuy;
    public OrderInfo orderSell;

    public UserInfo userInfo;
    public ITradePlatform iTradePlatform;

    @Override
    public void OnNewStockStat(OrderInfo orderInfo)
    {
        switch (orderInfo.GetOrderStat())
        {
            case OrderInfo.OrderStat_Order_Succ:
                userInfo.AddOrder(orderInfo);
                break;
            case OrderInfo.OrderStat_Deal:
                OnOrderDeal(orderInfo);
                break;
            case OrderInfo.OrderStat_Cancel_Succ:
                if (orderInfo.tradeFlag == StockConst.TradeBuy)
                {
                    orderBuy = null;
                }
                else if(orderInfo.tradeFlag == StockConst.TradeSell)
                {
                    orderSell = null;
                }
                break;
        }
    }

    private void OnOrderDeal(OrderInfo orderInfo)
    {
        if(orderInfo.tradeFlag == StockConst.TradeBuy)
        {
            if(stragegyStat == StragegyStepStatWaitInit)
            {
                UpdatePriceLast(priceInit);
                UpdateStragegyStat(StragegyStepStatNormal);
            }
            else if(stragegyStat == StragegyStepStatNormal)
            {
                UpdatePriceLast(priceLast- priceStepUint);
            }
            orderBuy = null;
            if(orderSell != null)
            {
                iTradePlatform.DoCancelOrder(orderSell);
            }
        }
        else if(orderInfo.tradeFlag == StockConst.TradeSell)
        {
            UpdatePriceLast(priceLast + priceStepUint);
            orderSell = null;
            if(orderBuy != null)
            {
                iTradePlatform.DoCancelOrder(orderBuy);
            }
        }
    }

    @Override
    public void OnTick()
    {
        if(stragegyStat == StragegyStepStatClose)
        {
            return;
        }

        StockRuntimeInfo stockRuntimeInfo = StockContext.GetInstance().GetServiceStockRuntimeInfo().GetStockRuntimeInfoByCode(code);
        switch (stragegyStat)
        {
            case StragegyStepStatWaitInit:
                ProcessInit(stockRuntimeInfo);
                break;
            case StragegyStepStatNormal:
                ProcessNormal(stockRuntimeInfo);
                break;
            default:
                System.out.print("unknow stragegy stat: "+stragegyStat);
                break;
        }
    }

    private void ProcessInit(StockRuntimeInfo stockRuntimeInfo)
    {
        if (orderBuy != null)
        {
            return;
        }
        if (StockUtils.TestTrade(stockRuntimeInfo, StockConst.TradeBuy, priceInit, countInit))
        {
            TryDoBuyOrder(priceInit, countInit);
        }
    }

    private void ProcessNormal(StockRuntimeInfo stockRuntimeInfo)
    {
        float priceBuy, priceSell;
        priceBuy = priceLast- priceStepUint + buyOffset;
        priceSell = priceLast+ priceStepUint + sellOffset;
        if(priceBuy >= priceMin)
        {
            ProcessBuy(stockRuntimeInfo, priceBuy);
        }

        ProcessSell(stockRuntimeInfo, priceSell);
    }

    private void ProcessBuy(StockRuntimeInfo stockRuntimeInfo, float priceBuy)
    {
        if(orderBuy != null)
        {
            return;
        }

        if (StockUtils.TestTrade(stockRuntimeInfo, StockConst.TradeBuy, priceBuy, countStep))
        {
            TryDoBuyOrder(priceBuy, countStep);
        }
    }

    private void ProcessSell(StockRuntimeInfo stockRuntimeInfo, float priceSell)
    {
        if(orderSell != null)
        {
            return;
        }

        TryDoSellOrder(priceSell, countStep);
    }

    private void TryDoBuyOrder(float price, int count)
    {
        float fee = EastmoneyUtils.CaculateTradeFee(code, StockConst.TradeBuy, price, count);
        float cost = price * count + fee;
        if(cost <= userInfo.rmbAsset.activeAmount)
        {
            orderBuy = CreateOrder(StockConst.TradeBuy, price, count);
            iTradePlatform.DoOrderRequest(orderBuy);
        }
    }

    private void TryDoSellOrder(float price, int count)
    {
        StockAsset stockAsset = userInfo.GetStockAsset(code);

        if(price >= priceMax)
        {
            count = stockAsset.amountActive;
        }
        else
        {
            if (stockAsset.amountActive != stockAsset.amountTotal && stockAsset.amountActive < count)
            {
                return;
            }

            if (stockAsset.amountActive == stockAsset.amountTotal)
            {
                if ((stockAsset.amountTotal <= count * 1.7f) || (stockAsset.amountTotal < count))
                {
                    count = stockAsset.amountTotal;
                }
            }
        }

        orderSell = CreateOrder(StockConst.TradeSell, price, count);
        iTradePlatform.DoOrderRequest(orderSell);
    }

    private OrderInfo CreateOrder(int tradeFlag, float price, int count)
    {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.tradeFlag = tradeFlag;
        orderInfo.code = code;
        orderInfo.date = Utils.GetDate();
        orderInfo.orderPrice = price;
        orderInfo.count = count;
        orderInfo.iOrderStatMonitor = this;
        orderInfo.context = orderContext;
        return orderInfo;
    }

    private void UpdateStragegyStat(int stragegyStat)
    {
      this.stragegyStat = stragegyStat;
    }

    private void UpdatePriceLast(float price)
    {
        priceLast = price;
    }
}
