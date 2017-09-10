package com.wly.stock.common;

import com.wly.common.LogUtils;
import com.wly.database.DBPool;
import com.wly.database.DBQuery;

import java.sql.ResultSet;

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
    static public final int OrderStat_Cancel_Ready = 8; //待撤单
    static public final int OrderStat_Cancel_Waiting = 9; //撤单请求中
    static public final int OrderStat_Cancel_Succ = 10; //已撤销
    static public final int OrderStat_Cancel_Failed = 11; //撤单失败
    static public final int OrderStat_WaitForCancel = 12; //已报待撤销

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
    public int userId;
    public String dateTime;
    public String code;
    public String name;
    public  int tradeFlag;
    public int orderCount;
    public float orderPrice;    //订单价格
    public float dealPrice;     //成交价格
    public int dealCount;      //成交数量
    public int platId;
    public String platOrderId = "";       //交易平台订单id

    private int orderStat = OrderStat_None;   //订单状态
    public String statMessage;  //状态消息

    public Object context;

    public void SetOrderStat(int newStat)
    {
        this.orderStat = newStat;
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
        final String strFormat = "id=%d code=%s name=%s tradeFlag=%s orderCount=%d orderPrice=%.2f dealPrice=%.2f orderStat=%d\n";
        return String.format(strFormat, 0, code, name, tradeFlag, orderCount, orderPrice, dealPrice, orderStat);
    }

    static public OrderInfo GetOrderInfoFromDb(int orderId)
    {
        OrderInfo orderInfo = null;
        final String strFormat = "select * from order_book where id = %d";
        String sqlStr = String.format(strFormat, orderId);
        DBQuery dbQuery = DBPool.GetInstance().ExecuteQuerySync(sqlStr);
        try {
            ResultSet rs = dbQuery.resultSet;
            orderInfo.id = rs.getInt("id");
            orderInfo.platId = rs.getInt("plat_id");
            orderInfo.tradeFlag = rs.getInt("trade_flag");
            orderInfo.SetOrderStat(rs.getInt("order_stat"));
            orderInfo.orderPrice = rs.getFloat("order_price");
            orderInfo.orderCount = rs.getInt("order_count");
            orderInfo.platOrderId = rs.getString("plat_order_id");
            orderInfo.dealPrice = rs.getFloat("deal_price");
            orderInfo.dealCount = rs.getInt("deal_count");
            orderInfo.dateTime = rs.getString("datetime");
            dbQuery.Close();
        } catch (Exception ex) {
            LogUtils.GetLogger(LogUtils.LOG_REALTIME).error(ex.getMessage());
            return orderInfo;
        } finally {
            dbQuery.Close();
            return orderInfo;
        }
    }

    static public int SaveDb(OrderInfo orderInfo)
    {
        final String sqlFormat = "insert into order_book (user_id, plat_id, code, trade_flag, order_stat, order_price, " +
                "order_count, plat_order_id, deal_price, deal_count, datetime) " +
                "values(%d, %d, '%s', %d, %d, %.2f, %d, '%s', %.2f, %d, '%s')";

        String sqlStr = String.format(sqlFormat, orderInfo.userId, orderInfo.platId, orderInfo.code, orderInfo.tradeFlag, orderInfo.GetOrderStat(), orderInfo.orderPrice,
                orderInfo.orderCount, orderInfo.platOrderId, orderInfo.dealPrice, orderInfo.dealCount, orderInfo.dateTime);
        orderInfo.id = DBPool.GetInstance().ExecuteNoQuerySqlSync(sqlStr, true);
        return orderInfo.id;
    }

    static public void UpdateOrderStat(int id, int stat)
    {
        final String sqlFormat = "update order_book set order_stat = %d where id = %d";
        String sqlStr = String.format(sqlFormat, stat, id);
        DBPool.GetInstance().ExecuteNoQuerySqlSync(sqlStr);
    }

    static public void UpdateOrderPlatOrderId(int id, String platOrderId)
    {
        final String sqlFormat = "update order_book set plat_order_id = '%s',order_stat = %d  where id = %d";
        String sqlStr = String.format(sqlFormat, platOrderId, OrderInfo.OrderStat_Order_Succ, id);
        DBPool.GetInstance().ExecuteNoQuerySqlSync(sqlStr);
    }

    static public int GetOrderStat(int orderId)
    {
        final String sqlFormat = "select order_stat from order_book where id = %d";
        String sqlStr = String.format(sqlFormat, orderId);
        DBQuery dbQuery = DBPool.GetInstance().ExecuteQuerySync(sqlStr);
        int orderStat = OrderInfo.OrderStat_None;
        try {
            ResultSet rs = dbQuery.resultSet;
            if(rs.next())
            {
                orderStat = rs.getInt("order_stat");
            }
            dbQuery.Close();
        } catch (Exception ex) {
            LogUtils.GetLogger(LogUtils.LOG_REALTIME).error(ex.getMessage());
            return orderStat;
        } finally {
            dbQuery.Close();
            return orderStat;
        }
    }
}
