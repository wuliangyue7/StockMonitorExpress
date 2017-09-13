package com.wly.stock.common;

import com.wly.common.LogUtils;
import com.wly.database.DBPool;
import com.wly.database.DBQuery;

import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Created by Administrator on 2017/2/9.
 */
public class OrderInfo
{
    static public final int OrderStat_None = 0; //未知状态
    static public final int OrderStat_Ready = 1; //待下单状态
    static public final int OrderStat_Order_Succ = 2; //已下单
    static public final int OrderStat_Order_Failed = 3; //下单失败
    static public final int OrderStat_Deal = 4;  //已成交
    static public final int OrderStat_Half = 5; //部分成交
    static public final int OrderStat_Cancel_Ready = 6; //待撤单
    static public final int OrderStat_Cancel_Waiting = 7; //撤单请求中
    static public final int OrderStat_Cancel_Succ = 8; //已撤销
    static public final int OrderStat_Cancel_Failed = 9; //撤单失败
    static public final int OrderStat_Hang = 10; //隔日处理挂起

    public static String GetSOrderInfoStatDesc(int stat)
    {
        String statDesc = "None";
        switch (stat)
        {
            case OrderStat_Ready:
                statDesc = "ready";
                break;
            case OrderStat_Order_Succ:
                statDesc = "orderSucc";
                break;
            case OrderStat_Order_Failed:
                statDesc = "OrderFailed";
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

    static public void UpdateOrderStatByPlatOrderId(String platOrderId, int orderStat)
    {
        final String sqlFormat = "update order_book set order_stat = %d where plat_order_id = '%s'";
        String sqlStr = String.format(sqlFormat, orderStat, platOrderId);
        DBPool.GetInstance().ExecuteNoQuerySqlAsync(sqlStr);
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
        } catch (Exception ex) {
            LogUtils.GetLogger(LogUtils.LOG_REALTIME).error(ex.getMessage());
            return orderStat;
        } finally {
            dbQuery.Close();
            return orderStat;
        }
    }

    static public ArrayList<String> GetQueryPlatOrderId(int userId, int platId)
    {
        ArrayList<String> platOrderList = new ArrayList<>();
        DBPool dbPool = DBPool.GetInstance();
        DBQuery dbQuery = dbPool.ExecuteQuerySync(String.format("select plat_order_id from order_book where user_id=%d " +
                        "and plat_id=%d and order_stat in (%d, %d,%d)", userId, platId, OrderStat_Order_Succ, OrderStat_Half,
                OrderStat_Cancel_Waiting));
        try {
            ResultSet rs = dbQuery.resultSet;
            while (rs.next())
            {
                platOrderList.add(rs.getString("plat_order_id"));
            }
        }
        catch (Exception ex)
        {
            LogUtils.GetLogger(LogUtils.LOG_REALTIME).error(ex.getMessage());
        }
        finally
        {
            dbQuery.Close();
            return platOrderList;
        }
    }

    static public void ResetOrder()
    {
        final String sqlFormat = "update order_book set order_stat=%d where order_stat in (%d, %d, %d,%d)";
        String strSql = String.format(sqlFormat, OrderStat_Hang, OrderStat_Cancel_Waiting,
                OrderStat_Cancel_Failed, OrderStat_Half, OrderStat_Cancel_Ready);
        DBPool.GetInstance().ExecuteNoQuerySqlSync(strSql);

        final String sqlFormatReset = "update order_book set order_stat=%d where order_stat in (%d)";
        String strSqlReset = String.format(sqlFormatReset, OrderStat_Ready, OrderStat_Order_Succ);
        DBPool.GetInstance().ExecuteNoQuerySqlSync(strSqlReset);
    }

    static public boolean CheckHasHalfOrder()
    {
        boolean ret = false;
        final String strSqlFormat = "select count(*) as count from order_book where order_stat=%d";
        String strSql = String.format(strSqlFormat, OrderStat_Half);
        DBQuery dbQuery = DBPool.GetInstance().ExecuteQuerySync(strSql);
        int count= 0;
        try {
            ResultSet rs = dbQuery.resultSet;
            if(rs.next())
            {
                count = rs.getInt("count");
                ret = count != 0;
            }
        } catch (Exception ex) {
            LogUtils.GetLogger(LogUtils.LOG_REALTIME).error(ex.getMessage());
        } finally {
            dbQuery.Close();
            return ret;
        }
    }
}
