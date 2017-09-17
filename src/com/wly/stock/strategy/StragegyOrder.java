package com.wly.stock.strategy;

import com.wly.common.LogUtils;
import com.wly.database.DBPool;
import com.wly.database.DBQuery;
import com.wly.stock.StockContext;
import com.wly.stock.common.OrderInfo;
import com.wly.stock.common.StockConst;
import com.wly.stock.common.StockRuntimeInfo;
import com.wly.user.UserInfo;

import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Created by wuly on 2017/9/17.
 */
public class StragegyOrder extends StragegyBase
{
    static public final int OrderStatClose = 0;
    static public final int OrderStatNormal = 1;
    static public final int OrderStatDeal = 2;

    public int stagegyOrderStat;
    public String code;
    public int tradeFlag;
    public float priceTrade;
    public int  countTrade;
    public int orderId;

    private UserInfo userInfo;

    public StragegyOrder(UserInfo userInfo)
    {
        super(userInfo);
    }

    @Override
    public void OnTick()
    {
        if (stagegyOrderStat != OrderStatNormal)
        {
            return;
        }

        if (orderId != 0)
        {
            int orderStat = OrderInfo.GetOrderStat(orderId);
            if (orderStat == OrderInfo.OrderStat_Deal)
            {
                stagegyOrderStat = OrderStatDeal;
                UpdateStagegyOrderStat(id, stagegyOrderStat);
            }
            return;
        }

        StockRuntimeInfo stockRuntimeInfo = StockContext.GetInstance().GetServiceStockRuntimeInfo().GetStockRuntimeInfoByCode(code);
        if (stockRuntimeInfo == null)
        {
            LogUtils.LogRealtime(String.format("StockRuntimeInfo not found for StragegyOrder code:%s stragegyId:%d", code, id));
            return;
        }

        LogUtils.LogRealtime(String.format("StragegyOrder id:%d code:%s name:%s tradeFlag:%d priceTarget:%.2f priceNow:%.2f", id,
                code, stockRuntimeInfo.name, tradeFlag, priceTrade, stockRuntimeInfo.priceNew));
        if (tradeFlag == StockConst.TradeSell)
        {
            if(GetStockAssetCountEffective(code) < countTrade)
            {
                LogUtils.LogRealtime(String.format("Stock not enough for StragegyOrder code: %s stragegyId %d", code, id));
                return;
            }

            OrderInfo orderInfo = CreateOrder(code, tradeFlag, priceTrade, countTrade);
            orderId = orderInfo.id;
            UpdateStagegyOrderOrderId(id, orderId);
        }
        else if (tradeFlag == StockConst.TradeBuy)
        {
            if(CanBuy(stockRuntimeInfo, code, priceTrade, countTrade))
            {
                OrderInfo orderInfo = CreateOrder(code, StockConst.TradeBuy, priceTrade, countTrade);
                if(orderInfo != null)
                {
                    orderId = orderInfo.id;
                    UpdateStagegyOrderOrderId(id, orderId);
                }
            }
        }

    }

    static public ArrayList<StragegyOrder> GetStagegyOrderList(UserInfo userInfo)
    {
        ArrayList<StragegyOrder> stragegyOrders = new ArrayList<>();
        DBPool dbPool = DBPool.GetInstance();
        DBQuery dbQuery = dbPool.ExecuteQuerySync(String.format("select * from stragegy_order where user_id=%d", userInfo.GetUserId()));
        ResultSet rs = dbQuery.resultSet;
        StragegyOrder stragegyOrder;
        try {
            while (rs.next())
            {
                stragegyOrder = new StragegyOrder(userInfo);
                stragegyOrder.id = rs.getInt("id");
                stragegyOrder.userId = rs.getInt("user_id");
                stragegyOrder.platId = rs.getInt("plat_id");
                stragegyOrder.stagegyOrderStat = rs.getInt("stragegy_stat");
                stragegyOrder.code = rs.getString("code");
                stragegyOrder.tradeFlag = rs.getInt("trade_flag");
                stragegyOrder.priceTrade = rs.getFloat("price_trade");
                stragegyOrder.countTrade = rs.getInt("count_trade");
                stragegyOrder.orderId = rs.getInt("order_id");
                stragegyOrders.add(stragegyOrder);
                StockContext.GetInstance().GetServiceStockRuntimeInfo().AddQueryCode(stragegyOrder.code);
            }
        }
        catch (Exception ex)
        {
            LogUtils.GetLogger(LogUtils.LOG_REALTIME).error(ex.getMessage());
        }
        finally
        {
            dbQuery.Close();
            return stragegyOrders;
        }
    }

    static public void UpdateStagegyOrderStat(int id, int orderStat)
    {
        final String sqlFormat = "update stragegy_order set stragegy_stat = %d where id = %d";
        String sqlStr = String.format(sqlFormat, orderStat, id);
        DBPool.GetInstance().ExecuteNoQuerySqlSync(sqlStr);
    }

    static public void UpdateStagegyOrderOrderId(int id, int orderId)
    {
        final String sqlFormat = "update stragegy_order set order_id = %d where id = %d";
        String sqlStr = String.format(sqlFormat, orderId, id);
        DBPool.GetInstance().ExecuteNoQuerySqlSync(sqlStr);
    }

    static public int SaveStragegyOrderDB(StragegyOrder stragegyOrder)
    {
        final String sqlFormat = "insert into stragegy_order (user_id, plat_id, code, stragegy_stat, trade_flag, " +
                "price_trade, count_trade, order_id) " +
                "values(%d, %d, '%s', %d, %d, %.2f, %d, %d)";
        String strSql = String.format(sqlFormat, stragegyOrder.userId, stragegyOrder.platId, stragegyOrder.code,
                stragegyOrder.stagegyOrderStat, stragegyOrder.tradeFlag, stragegyOrder.priceTrade,
                stragegyOrder.countTrade, 0);
        stragegyOrder.id = DBPool.GetInstance().ExecuteNoQuerySqlSync(strSql, true);
        return stragegyOrder.id;
    }
}
