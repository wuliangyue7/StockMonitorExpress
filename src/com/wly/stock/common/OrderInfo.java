package com.wly.stock.common;

/**
 * Created by Administrator on 2017/2/9.
 */
public class OrderInfo
{
    static public final int OrderStat_None = 0; //未知状态
    static public final int OrderStat_Ready = 1; //待下单状态
    static public final int OrderStat_Order_Waiting = 2; //下单请求中
    static public final int OrderStat_Order_Succ = 3; //已下单
    static public final int OrderStat_Order_Failed = 4; //下单失败
    static public final int OrderStat_Query_Waiting = 5; //订单状态查询中
    static public final int OrderStat_Deal = 6;  //已成交
    static public final int OrderStat_Half = 7; //部分成交
    static public final int OrderStat_Cancel_Waiting = 8; //撤单请求中
    static public final int OrderStat_Cancel_Succ = 9; //已撤销
    static public final int OrderStat_Cancel_Failed = 10; //已撤销
    static public final int OrderStat_WaitForCancel = 11; //已报待撤销

    public static String GetSOrderInfoStatDesc(int stat)
    {
        String statDesc = "None";
        switch (stat)
        {
            case OrderStat_Ready:
                statDesc = "ready";
                break;
            case OrderStat_Order_Waiting:
                statDesc = "OrderWaiting";
                break;
            case OrderStat_Order_Succ:
                statDesc = "orderSucc";
                break;
            case OrderStat_Order_Failed:
                statDesc = "OrderFailed";
                break;
            case OrderStat_Query_Waiting:
                statDesc = "statQuery";
                break;
            case OrderStat_Deal:
                statDesc = "deal";
                break;
            case OrderStat_Half:
                statDesc = "half";
                break;
            case OrderStat_Cancel_Waiting:
                statDesc = "CancelWaiting";
                break;
            case OrderStat_Cancel_Succ:
                statDesc = "cancelSucc";
                break;
            case OrderStat_Cancel_Failed:
                statDesc = "cancelFailed";
                break;
            case OrderStat_WaitForCancel:
                statDesc = "waitForCancel";
                break;
            default:
                statDesc = "none";
                break;
        }
        return statDesc;
    }

    public int id;
    public String date;
    public String code;
    public String name;
    public  int tradeFlag;
    public int count;
    public float orderPrice;    //订单价格
    public float dealPrice;     //成交价格
    public int platId;
    public String platOrderId;       //交易平台订单id

    private int orderStat = OrderStat_None;   //订单状态
    public String statMessage;  //状态消息

    public IOrderStatMonitor iOrderStatMonitor;

    public Object context;

    public void SetOrderStat(int newStat)
    {
        if (orderStat != newStat)
        {
            orderStat = newStat;
            if(iOrderStatMonitor != null)
            {
                iOrderStatMonitor.OnNewStockStat(this);
            }
        }
    }

    public int GetOrderStat()
    {
        return orderStat;
    }

    public boolean IsWaiting()
    {
        return orderStat == OrderStat_Order_Waiting || orderStat == OrderStat_Cancel_Waiting
                || orderStat == OrderStat_Query_Waiting;
    }

    @Override
    public String toString()
    {
        final String strFormat = "id=%d code=%s name=%s tradeFlag=%s count=%d orderPrice=%.2f dealPrice=%.2f orderStat=%d\n";
        return String.format(strFormat, 0, code, name, tradeFlag, count, orderPrice, dealPrice, orderStat);
    }
}
