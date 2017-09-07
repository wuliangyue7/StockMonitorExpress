package com.wly.stock;

import com.wly.common.ITickable;
import com.wly.stock.service.ServiceStockRuntimeInfo;
import com.wly.user.UserManager;

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

    public void Start()
    {
        if(timer == null)
        {
            timer = new Timer();
        }
        timer.schedule(this, 0, 1000);
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
