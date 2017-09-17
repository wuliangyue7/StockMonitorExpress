package com.wly.stock.strategy;

import com.wly.common.LogUtils;
import com.wly.common.Utils;
import com.wly.database.DBPool;
import com.wly.database.DBQuery;
import com.wly.stock.StockContext;
import com.wly.stock.common.*;
import com.wly.stock.tradeplat.eastmoney.EastmoneyUtils;
import com.wly.user.RmbAsset;
import com.wly.user.UserInfo;

import java.sql.ResultSet;
import java.util.ArrayList;

/**
 * Created by wuly on 2017/8/26.
 */
public class StragegyStep
{
    static public final int StragegyStepStatClose = 0;
    static public final int StragegyStepStatWaitInit = 1;
    static public final int StragegyStepStatNormal = 2;

    public int id;
    public int userId;
    public int platId;
    public String code;
    public int stragegyStat;
    public float priceInit;
    public int countInit;
    public int countStepUnit;
    public float priceStepUint;
    public float buyOffset;
    public float sellOffset;
    public float priceMin;
    public float priceMax;
    public float priceLast; //上一次交易的参考价格

    public int orderIdBuy;
    public int orderIdSell;

    private UserInfo userInfo;

    public StragegyStep(UserInfo userInfo)
    {
        this.userInfo = userInfo;
    }

    public void OnTick()
    {
        if(stragegyStat == StragegyStepStatClose)
        {
            return;
        }

        StockRuntimeInfo stockRuntimeInfo = StockContext.GetInstance().GetServiceStockRuntimeInfo().GetStockRuntimeInfoByCode(code);
        if(stockRuntimeInfo == null)
        {
            return;
        }

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

        int orderStat = OrderInfo.OrderStat_None;
        if (orderIdBuy != 0)
        {
            orderStat = OrderInfo.GetOrderStat(orderIdBuy);
            if(orderStat == OrderInfo.OrderStat_Deal)
            {
                SetOrderIdBuy(0);
                SetStragegyStat(StragegyStepStatNormal);
            }
            return;
        }
        LogUtils.LogRealtime(String.format("ProcessInit: %d code=%s %s priceNow=%.2f priceBuy=%.2f", id,
                code, stockRuntimeInfo.name, stockRuntimeInfo.priceNew, priceInit));
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

        LogUtils.LogRealtime(String.format("ProcessNormal: %d code=%s %s priceNow=%.2f priceBuy=%.2f priceSell=%.2f", id,
                code, stockRuntimeInfo.name, stockRuntimeInfo.priceNew, priceBuy, priceSell));
        if(priceBuy >= priceMin)
        {
            ProcessBuy(stockRuntimeInfo, priceBuy);
        }

        //如果买单交易成功会修改参考价格
        priceSell = priceLast+ priceStepUint + sellOffset;
        ProcessSell(stockRuntimeInfo, priceSell);
    }

    private void ProcessBuy(StockRuntimeInfo stockRuntimeInfo, float priceBuy)
    {
        if(orderIdBuy != 0)
        {
            int orderStat = OrderInfo.GetOrderStat(orderIdBuy);
            if(orderStat != OrderInfo.OrderStat_Deal)
            {
                return;
            }

            SetPriceLast(priceLast-priceStepUint);
            SetOrderIdBuy(0);
            if(orderIdSell != 0)
            {
                OrderInfo.UpdateOrderStat(orderIdSell, OrderInfo.OrderStat_Cancel_Ready);
                SetOrderIdSell(0);
            }
            return;
        }
        else if (StockUtils.TestTrade(stockRuntimeInfo, StockConst.TradeBuy, priceBuy, countStepUnit))
        {
            TryDoBuyOrder(priceBuy, countStepUnit);
        }
    }

    private void ProcessSell(StockRuntimeInfo stockRuntimeInfo, float priceSell)
    {
        if(orderIdSell != 0)
        {
            int orderStat = OrderInfo.GetOrderStat(orderIdSell);
            if(orderStat != OrderInfo.OrderStat_Deal)
            {
                return;
            }

            SetPriceLast(priceLast+priceStepUint);
            SetOrderIdSell(0);
            if(orderIdBuy != 0)
            {
                OrderInfo.UpdateOrderStat(orderIdBuy, OrderInfo.OrderStat_Cancel_Ready);
                SetOrderIdBuy(0);
            }
            return;
        }

        TryDoSellOrder(priceSell, countStepUnit);
    }

    private void TryDoBuyOrder(float price, int count)
    {
        float fee = EastmoneyUtils.CaculateTradeFee(code, StockConst.TradeBuy, price, count);
        float cost = price * count + fee;
        RmbAsset rmbAsset = userInfo.GetRmbAsset(platId);
        if(rmbAsset!= null && cost <= rmbAsset.activeAmount)
        {
            SetOrderIdBuy(CreateOrder(StockConst.TradeBuy, price, count));
        }
    }

    private void TryDoSellOrder(float price, int count)
    {
        StockAsset stockAsset = userInfo.GetStockAsset(platId, code);
        if(stockAsset == null)
        {
            return;
        }

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

        SetOrderIdSell(CreateOrder(StockConst.TradeSell, price, count));
    }

    private int CreateOrder(int tradeFlag, float price, int count)
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
        return orderInfo.id;
    }

    private void SetStragegyStat(int stragegyStat)
    {
      this.stragegyStat = stragegyStat;
        UpdateStragegyStat(id, this.stragegyStat);
    }

    private void SetPriceLast(float price)
    {
        priceLast = price;
        UpdateStagegyLastPrice(id, priceLast);
    }

    private void SetOrderIdBuy(int orderId)
    {
        orderIdBuy = orderId;
        UpdateOrderIdBuy(id, orderIdBuy);
    }

    private void SetOrderIdSell(int orderId)
    {
        orderIdSell = orderId;
        UpdateOrderIdSell(id, orderIdSell);
    }

    static public ArrayList<StragegyStep> GetStagegyStepList(UserInfo userInfo)
    {
        ArrayList<StragegyStep> stragegyStepArrayList = new ArrayList<>();
        DBPool dbPool = DBPool.GetInstance();
        DBQuery dbQuery = dbPool.ExecuteQuerySync(String.format("select * from policy_step where user_id=%d", userInfo.GetUserId()));
        ResultSet rs = dbQuery.resultSet;
        StragegyStep stragegyStep;
        try {
            while (rs.next())
            {
                stragegyStep = new StragegyStep(userInfo);
                stragegyStep.id = rs.getInt("id");
                stragegyStep.userId = rs.getInt("user_id");
                stragegyStep.platId = rs.getInt("plat_id");
                stragegyStep.code = rs.getString("code");
                stragegyStep.priceInit = rs.getFloat("price_init");
                stragegyStep.countInit = rs.getInt("count_init");
                stragegyStep.priceStepUint = rs.getFloat("price_unit");
                stragegyStep.countStepUnit = rs.getInt("step_unit");
                stragegyStep.buyOffset = rs.getFloat("buy_offset");
                stragegyStep.sellOffset = rs.getFloat("sell_offset");
                stragegyStep.priceMin = rs.getFloat("min_price");
                stragegyStep.priceMax = rs.getFloat("max_price");
                stragegyStep.stragegyStat = rs.getInt("policy_stat");
                stragegyStep.priceLast = rs.getFloat("price_last");
                stragegyStep.orderIdBuy = rs.getInt("buyorder_id");
                stragegyStep.orderIdSell = rs.getInt("sellorder_id");
                stragegyStepArrayList.add(stragegyStep);
                StockContext.GetInstance().GetServiceStockRuntimeInfo().AddQueryCode(stragegyStep.code);
            }
        }
        catch (Exception ex)
        {
            LogUtils.GetLogger(LogUtils.LOG_REALTIME).error(ex.getMessage());
        }
        finally
        {
            dbQuery.Close();
            return stragegyStepArrayList;
        }
    }

    static public void UpdateStragegyStat(int id, int stat)
    {
        final String sqlFormat = "update policy_step set policy_stat = %d where id = %d";
        String sqlStr = String.format(sqlFormat, stat, id);
        DBPool.GetInstance().ExecuteNoQuerySqlSync(sqlStr);
    }

    static public void UpdateStagegyLastPrice(int id, float price)
    {
        final String sqlFormat = "update policy_step set price_last = %.2f where id = %d";
        String sqlStr = String.format(sqlFormat, price, id);
        DBPool.GetInstance().ExecuteNoQuerySqlSync(sqlStr);
    }

    static public void UpdateOrderIdBuy(int id, int orderId)
    {
        final String sqlFormat = "update policy_step set buyorder_id = %d where id = %d";
        String sqlStr = String.format(sqlFormat, orderId, id);
        DBPool.GetInstance().ExecuteNoQuerySqlSync(sqlStr);
    }

    static public void UpdateOrderIdSell(int id, int orderId)
    {
        final String sqlFormat = "update policy_step set sellorder_id = %d where id = %d";
        String sqlStr = String.format(sqlFormat, orderId, id);
        DBPool.GetInstance().ExecuteNoQuerySqlSync(sqlStr);
    }

    static public void SaveToDB(StragegyStep stragegyStep)
    {
        final String SqlFormat = "insert into policy_step (id, user_id, code, policy_stat, price_last, " +
                "price_init, count_init, price_unit, step_unit, buy_offset, sell_offset, min_price, max_price)" +
                "values(%d, %d, '%s', %d, %.2f,  %.2f, %d, %.2f, %d, %.2f, %.2f, %.2f, %.2f)";
        String sqlStr = String.format(SqlFormat, stragegyStep.id, stragegyStep.userId, stragegyStep.code, stragegyStep.stragegyStat,
                stragegyStep.priceLast, stragegyStep.priceInit, stragegyStep.countInit, stragegyStep.priceStepUint,
                stragegyStep.countStepUnit, stragegyStep.buyOffset, stragegyStep.sellOffset, stragegyStep.priceMin,
                stragegyStep.priceMax);
        DBPool.GetInstance().ExecuteNoQuerySqlSync(sqlStr);
    }
}
