package com.wly.user;

import com.wly.common.IAsyncCallBack;
import com.wly.common.LogUtils;
import com.wly.database.DBPool;
import com.wly.database.DBQuery;
import com.wly.stock.common.StockConst;
import com.wly.stock.common.*;
import com.wly.stock.tradeplat.eastmoney.TradeEastmoney;
import com.wly.stock.tradeplat.eastmoney.TradeEastmoneyImpl;
import com.wly.stock.tradeplat.simulate.TradeSimulateImpl;
import com.wly.stock.tradeplat.ITradeInterface;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/2/8.
 */
public class UserInfo implements IAsyncCallBack, IStockOrderManager
{
    public int id;
    public String name;
    public int platId;
    public String platAcct;
    public String platPsw;
    public RmbAsset rmbAsset;
    public HashMap<String, StockAsset> stockAssetHashMap = new HashMap<>();
//    public List<StrategyBase> policySteps = new ArrayList<>();
    private ArrayList<OrderInfo> orderInfos = new ArrayList<>();

    public ITradePlatform tradeInterface;

    public UserInfo()
    {
        rmbAsset = new RmbAsset();
        rmbAsset.code = StockConst.RmbCode;
        rmbAsset.code = StockConst.RmbName;
    }

    public void Init()
    {
        switch (platId)
        {
            case StockConst.PlatEastmoney:
                tradeInterface = new TradeEastmoney();
//                tradeInterface = new TradeEastmoneyImpl();
                break;
            case StockConst.PlatSimulate:
//                tradeInterface = new TradeSimulateImpl();
                break;
        }
        tradeInterface.SetStockOrderManager(this);
        InitPolicySteps();
//        Login(platAcct, platPsw);
    }

    private boolean InitPolicySteps()
    {
        try {
//            StrategyStepAll policy;
            DBPool dbPool = DBPool.GetInstance();
            DBQuery dbQuery = dbPool.ExecuteQuerySync(String.format("select * from policy_step where user_id=%d", id));
            ResultSet rs = dbQuery.resultSet;
            while (rs.next())
            {
//                policy = new StrategyStepAll(this);
//                policy.id = rs.getInt("id");
//                policy.code = rs.getString("code");
//                policy.priceInit = rs.getFloat("price_init");
//                policy.initCount = rs.getInt("count_init");
//                policy.priceUnit = rs.getFloat("price_unit");
//                policy.stepUnit = rs.getInt("step_unit");
//                policy.buyOffset = rs.getFloat("buy_offset");
//                policy.sellOffset = rs.getFloat("sell_offset");
//                policy.minPrice = rs.getFloat("min_price");
//                policy.maxPrice = rs.getFloat("max_price");
//                policy.policyStat = rs.getInt("policy_stat");
//                policy.priceLast = rs.getFloat("price_last");
//                policy.buyOrderId = rs.getString("buyorder_id");
//                policy.buyLastPrice = rs.getFloat("buylast_price");
//                policy.sellOrderId = rs.getString("sellOrder_id");
//                policy.sellLastPrice = rs.getFloat("selllast_price");
//
//                if(!policy.sellOrderId.equals("0") && !rs.getString("sellorder_date").equals(Utils.GetDate()))
//                {
//                    policy.sellOrderId = "0";
//                    policy.sellLastPrice = 0f;
//                }
//
//                if(!policy.buyOrderId.equals("0") && !rs.getString("buyorder_date").equals(Utils.GetDate()))
//                {
//                    policy.buyOrderId = "0";
//                    policy.buyLastPrice = 0f;
//                }
//
//                if(policy.policyStat == StrategyStepAll.PolicyStat_None)
//                {
//                    LogUtils.LogRealtime("strategy is stop "+id);
//                    continue;
//                }
//
//                policySteps.add(policy);

//                StockMarketInfoManager.GetInstance().AddMonitorCode(policy.code);
                //StockPriceMonitorManager.GetInstance().AddMonitor(policy);
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

    private Timer timerQueryOrderStat;
    public void StartQueryOrderStat()
    {

        boolean bNeedQuery = false;
        int i, orderStat;
        for(i=0; i<orderInfos.size(); ++i)
        {
            orderStat = orderInfos.get(i).GetOrderStat();
            if(orderStat == OrderInfo.OrderStat_Half || orderStat==OrderInfo.OrderStat_Cancel_Failed
                    || orderStat == OrderInfo.OrderStat_Order_Failed || orderStat == OrderInfo.OrderStat_Order_Succ)
            {
                bNeedQuery = true;
                break;
            }
        }

        if(bNeedQuery)
        {
            timerQueryOrderStat = new Timer();
            timerQueryOrderStat.schedule(new QueryOrderInfo(), 0, 1000);
        }
    }

    class QueryOrderInfo extends TimerTask
    {
        @Override
        public void run()
        {
            tradeInterface.DoQueryOrderStat();
        }
    }

    @Override
    public void SetOrderStat(String platOrderId, int stat)
    {
        int i;
        OrderInfo orderInfo;
        for(i=0; i<orderInfos.size(); ++i)
        {
            orderInfo = orderInfos.get(i);
            if(orderInfo.platOrderId == platOrderId)
            {
                orderInfo.SetOrderStat(stat);
                break;
            }
        }
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
