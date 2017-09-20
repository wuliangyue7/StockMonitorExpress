package com.wly;

import com.wly.common.LogUtils;
import com.wly.common.Utils;
import com.wly.stock.common.StockRuntimeInfo;
import com.wly.stock.infoplat.sina.StockInfoProviderSina;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/9/20.
 */
public class QuetyStockSimpleInfo {
    static public void main(String[] args)
    {
        LogUtils.Init("config/log4j.properties");
        if(args.length == 0)
        {
            Utils.Log("please input stock code");
            return;
        }

        Timer timer = new Timer();
        QueryStockSimpleInfo task = new QueryStockSimpleInfo();
        int i;
        for(i=0; i<args.length; ++i)
        {
            task.queryCodeList.add(args[i]);
        }
        timer.schedule(task, 0, 2000);
    }
}

class QueryStockSimpleInfo extends TimerTask
{
    public ArrayList<String> queryCodeList = new ArrayList<>();
    private StockInfoProviderSina provider = new StockInfoProviderSina();
    @Override
    public void run() {

        try {
            ArrayList<StockRuntimeInfo> stockRuntimeInfos = provider.GetStockInfoByCode(queryCodeList);
            int i;
            for(i=0; i<stockRuntimeInfos.size(); ++i)
            {
                System.out.println(stockRuntimeInfos.get(i).toDesc());
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }
}
