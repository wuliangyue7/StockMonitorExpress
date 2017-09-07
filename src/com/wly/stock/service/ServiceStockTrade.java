//package com.wly.stock.service;
//
//import com.wly.stock.common.IOrderStatMonitor;
//import com.wly.stock.common.OrderInfo;
//import com.wly.stock.tradeplat.eastmoney.TradeEastmoney;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
///**
// * Created by wuly on 2017/6/26.
// */
//public class ServiceStockTrade implements IOrderStatMonitor
//{
//    private static ServiceStockTrade sInstance = null;
//
//    public static ServiceStockTrade GetInstance()
//    {
//        if (sInstance == null)
//        {
//            sInstance = new ServiceStockTrade();
//        }
//
//        return sInstance;
//    }
//
//    private TradeEastmoney tradeEastmoney;
//    private ArrayList<OrderInfo> orderInfoList = new ArrayList<>();
//    private HashMap<String, OrderInfo> orderInfoHashMap =  new HashMap<>();
//
//    private ServiceStockTrade()
//    {
//        tradeEastmoney = new TradeEastmoney();
//    }
//
//    public void DoOrderRequest(OrderInfo orderInfo)
//    {
//        tradeEastmoney.DoOrderRequest(orderInfo);
//    }
//
//    @Override
//    public void OnNewStockStat(OrderInfo orderInfo)
//    {
//        switch (orderInfo.GetOrderStat())
//        {
//            case OrderInfo.OrderStat_Order_Succ:
//                orderInfoHashMap.put(orderInfo.platOrderId, orderInfo);
//                break;
//        }
//    }
//
//    public void OnStockStat(String orderPlatId, int orderStat)
//    {
//
//    }
//
//
//}
