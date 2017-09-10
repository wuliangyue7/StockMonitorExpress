package com.wly.stock;

import com.wly.common.ITickable;
import com.wly.common.LogUtils;
import com.wly.common.Utils;
import com.wly.database.DBPool;
import com.wly.database.DBQuery;
import com.wly.stock.common.OrderInfo;
import com.wly.stock.service.ServiceStockRuntimeInfo;
import com.wly.stock.strategy.StragegyStep;
import com.wly.user.UserManager;

import java.sql.ResultSet;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by wuly on 2017/6/26.
 */
public class StockContext extends TimerTask
{
    static private StockContext s_instace = null;
    static public StockContext GetInstance()
    {
        if(s_instace == null)
        {
            s_instace = new StockContext();
        }
        return  s_instace;
    }

    private StockContext(){}

    HashMap<Integer, String> configMap = new HashMap<>();
    private ServiceStockRuntimeInfo serviceStockRuntimeInfo;
    public ServiceStockRuntimeInfo GetServiceStockRuntimeInfo()
    {
        return serviceStockRuntimeInfo;
    }

    public void SetServiceStockRuntimeInfo(ServiceStockRuntimeInfo serviceStockRuntimeInfo)
    {
        this.serviceStockRuntimeInfo = serviceStockRuntimeInfo;
    }

//    private ServiceStockTrade serviceStockTrade;
//    public ServiceStockTrade GetServiceStockTrade()
//    {
//        return serviceStockTrade;
//    }
//    public void SetServiceStockTrade(ServiceStockTrade serviceStockTrade)
//    {
//        this.serviceStockTrade = serviceStockTrade;
//    }

    public UserManager userManager;
    public void SetUserManger(UserManager userManager)
    {
        this.userManager = userManager;
        AddTickObject(this.userManager);
    }

    public UserManager GetUserManager()
    {
        return userManager;
    }

    private ExecutorService executorService;
    public ExecutorService GetExecutorService()
    {
        return executorService;
    }

    public void SetExecutorService(ExecutorService executorService)
    {
        this.executorService = executorService;
    }

    private Timer timer;
    private Lock lockTickableHashMap = new ReentrantLock();
    private HashMap<Integer, ITickable> tickableHashMap = new HashMap<>();
    private ArrayList<Integer> removeIdList = new ArrayList<>();
    private int tickableIndex = 0;
    public int AddTickObject(ITickable tickable)
    {
        lockTickableHashMap.lock();
        tickableHashMap.put(tickableIndex++, tickable);
        lockTickableHashMap.unlock();
        return tickableIndex;
    }

    public void RemoveTickObject(int id)
    {
        removeIdList.add(id);
    }

    public void Init()
    {
        String strSql = "select * from config_global";
        DBQuery dbQuery = DBPool.GetInstance().ExecuteQuerySync(strSql);
        ResultSet rs = dbQuery.resultSet;
        try {
            int key;
            String value;

            while (rs.next())
            {
                key = rs.getInt("id");
                value = rs.getString("value");
                configMap.put(key, value);
            }

            ClearLastOrderInfo();

        } catch (Exception ex) {
            LogUtils.GetLogger(LogUtils.LOG_REALTIME).error(ex.getMessage());
        } finally {
            dbQuery.Close();
        }
    }

    public void Start()
    {
        if(timer == null)
        {
            timer = new Timer();
        }
        timer.schedule(this, 0, 1000);
    }

    private void ClearLastOrderInfo()
    {
        String lastDate = configMap.get(1);
        String nowDate = Utils.GetDate("yyyy-MM-dd");
        if(lastDate.equals(nowDate))
        {
            return;
        }

        StragegyStep.ClearOrderId();
        final String sqlFormat = "update config_global set value='%s' where id = 1";
        String strSql = String.format(sqlFormat, nowDate);
        DBPool.GetInstance().ExecuteNoQuerySqlSync(strSql);
    }

    @Override
    public void run()
    {
        for(int id : removeIdList)
        {
            if(tickableHashMap.containsKey(id))
            {
                tickableHashMap.remove(id);
            }
        }
        removeIdList.clear();

        for(Map.Entry<Integer, ITickable> entry: tickableHashMap.entrySet())
        {
            entry.getValue().OnTick();
        }
    }
}
