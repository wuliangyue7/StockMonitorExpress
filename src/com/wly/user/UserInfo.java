package com.wly.user;

import com.google.gson.JsonObject;
import com.wly.common.IAsyncCallBack;
import com.wly.common.ITickable;
import com.wly.common.LogUtils;
import com.wly.common.Utils;
import com.wly.database.DBPool;
import com.wly.database.DBQuery;
import com.wly.stock.StockMarketInfoManager;
import com.wly.stock.common.StockConst;
import com.wly.stock.common.*;
import com.wly.stock.strategy.StragegyStep;
import com.wly.stock.tradeplat.eastmoney.TradeEastmoney;
import com.wly.stock.tradeplat.simulate.TradeSimulate;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Administrator on 2017/2/8.
 */
public class UserInfo implements IAsyncCallBack, ITradeManager, ITickable
{
    private int id;
    public String name;
    public RmbAsset rmbAsset;
    public HashMap<String, StockAsset> stockAssetHashMap = new HashMap<>();
    private ArrayList<OrderInfo> orderInfos = new ArrayList<>();
    private Lock lockOrderList = new ReentrantLock();

    private HashMap<Integer, ITradePlatform> tradePlatformHashMap = new HashMap<>();
    public ITradePlatform tradeInterface;
    private ArrayList<StragegyStep> stragegySteps = new ArrayList<>();

    public UserInfo()
    {
        rmbAsset = new RmbAsset();
        rmbAsset.code = StockConst.RmbCode;
        rmbAsset.name = StockConst.RmbName;
        tradePlatformHashMap.put(StockConst.PlatSimulate, new TradeSimulate(this));
        tradePlatformHashMap.put(StockConst.PlatEastmoney, new TradeEastmoney(this));
    }

    public void Init(int id)
    {
        this.id = id;
        InitPolicySteps();
//        Login(platAcct, platPsw);
    }

    public void InitTradeContext(int platId, JsonObject context)
    {
        ITradePlatform tradePlatform = tradePlatformHashMap.get(platId);
        if(tradePlatform != null)
        {
            tradePlatform.SetContext(context);
        }
    }

    public int GetUserId()
    {
        return id;
    }

    private boolean InitPolicySteps()
    {
        try {
            DBPool dbPool = DBPool.GetInstance();
            DBQuery dbQuery = dbPool.ExecuteQuerySync(String.format("select * from policy_step where user_id=%d", id));
            ResultSet rs = dbQuery.resultSet;
            StragegyStep stragegyStep;
            while (rs.next())
            {
                stragegyStep = new StragegyStep(this);
                stragegyStep.id = rs.getInt("id");
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
                stragegyStep.platOrderIdBuy = rs.getString("buyorder_id");
                stragegyStep.platOrderIdSell = rs.getString("sellOrder_id");

                if(!rs.getString("buyorder_date").equals(Utils.GetDate()))
                {
                    stragegyStep.platOrderIdBuy = null;
                }

                if(!rs.getString("sellorder_date").equals(Utils.GetDate()))
                {
                    stragegyStep.platOrderIdSell = null;
                }

                stragegySteps.add(stragegyStep);

                StockMarketInfoManager.GetInstance().AddMonitorCode(stragegyStep.code);
            }
            dbQuery.Close();
            return true;
        }
        catch (Exception ex)
        {
            LogUtils.GetLogger(LogUtils.LOG_REALTIME).error(ex.getMessage());
            return  false;
        }
    }

    public void AddOrder(OrderInfo orderInfo)
    {
        orderInfos.add(orderInfo);
    }

    @Override
    public void OnTick()
    {
        tradeInterface.DoQueryOrderStat();
        lockOrderList.lock();
        int i;
        for(i=0; i<stragegySteps.size(); ++i)
        {
            stragegySteps.get(i).OnTick();
        }
        lockOrderList.unlock();
    }

    @Override
    public void RrefreshOrderList(ArrayList<OrderInfo> orderInfos)
    {
        lockOrderList.lock();
        this.orderInfos = orderInfos;
        lockOrderList.unlock();
    }

    public  int GetOrderStat(int platId, String platOrderId)
    {
        int i;
        int stat = OrderInfo.OrderStat_None;
        OrderInfo orderInfo;
        for(i=0; i<orderInfos.size(); ++i)
        {
            orderInfo = orderInfos.get(i);
            if(orderInfo.platOrderId == platOrderId && orderInfo.platId == platId)
            {
                stat = orderInfo.GetOrderStat();
                break;
            }
        }
        return stat;
    }

    public StockAsset GetStockAsset(String code)
    {
        return stockAssetHashMap.get(code);
    }

    public OrderInfo GetOrderInfoByPlatId(String orderId)
    {
        int i;
        OrderInfo ret = null;
        for(i=0; i<orderInfos.size(); ++i)
        {
            if(orderId == orderInfos.get(i).platOrderId)
            {
                ret = orderInfos.get(i);
                break;
            }
        }

        return ret;
    }

    public void UpdateOrderStat(OrderInfo orderInfo)
    {
        try {
            final String UpdateFormat = "update trade_book SET policy_stat = %d WHERE id = %d";
            DBPool.GetInstance().ExecuteNoQuerySqlAsync(String.format(UpdateFormat, orderInfo.id, orderInfo.GetOrderStat()));
        }
        catch (Exception ex)
        {
            LogUtils.GetLogger(LogUtils.LOG_REALTIME).error(ex.getMessage());
        }
    }

    public void UpdateOrderPlatOrderId(OrderInfo orderInfo)
    {
        try
        {
            final String UpdateFormat = "update trade_book SET plat_order_id = %d WHERE id = %d";
            DBPool.GetInstance().ExecuteNoQuerySqlAsync(String.format(UpdateFormat, orderInfo.id, orderInfo.platOrderId));
        }
        catch (Exception ex)
        {
            LogUtils.GetLogger(LogUtils.LOG_REALTIME).error(ex.getMessage());
        }
    }

    @Override
    public void OnCallback(int id, Object obj)
    {

    }
}
