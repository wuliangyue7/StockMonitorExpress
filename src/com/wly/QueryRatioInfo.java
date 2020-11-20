package com.wly;

import com.wly.common.LogUtils;
import com.wly.common.Utils;
import com.wly.stock.common.StockRuntimeInfo;
import com.wly.stock.infoplat.sina.StockInfoProviderSina;
import jdk.nashorn.internal.ir.Flags;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/9/20.
 */
public class QueryRatioInfo {
    static public void main(String[] args)
    {
        LogUtils.Init("config/log4j.properties");
        if(args.length < 3)
        {
            Utils.Log("please input stock code debt and debt_base");
            return;
        }

        Timer timer = new Timer();
        QueryStockRatioInfo task = new QueryStockRatioInfo(args[0], args[1], Float.parseFloat(args[2]));
        if (args.length >= 4)
        {
            task.SetPremiumRatioTarget(Float.parseFloat(args[3]));
        }
        timer.schedule(task, 0, 2000);
    }
}

class QueryStockRatioInfo extends TimerTask
{
    private String _stockCode;
    private String _debtCode;
    private float _standardPrice;
    private float _premiumRatioTarget = Float.NaN;

    public ArrayList<String> queryCodeList = new ArrayList<>();
    private StockInfoProviderSina provider = new StockInfoProviderSina();

    public QueryStockRatioInfo(String stockCode, String debtCode, float standardPrice)
    {
        _stockCode = stockCode;
        _debtCode = debtCode;
        _standardPrice = standardPrice;
        queryCodeList.add(_stockCode);
        queryCodeList.add(_debtCode);
    }

    public void SetPremiumRatioTarget(float premiumRatioTarget)
    {
        _premiumRatioTarget = premiumRatioTarget;
    }

    @Override
    public void run() {

        try {
            ArrayList<StockRuntimeInfo> stockRuntimeInfos = provider.GetStockInfoByCode(queryCodeList);
            StockRuntimeInfo stock = null;
            StockRuntimeInfo debt = null;
            int i;
            String code;
            for(i=0; i<stockRuntimeInfos.size(); ++i)
            {
                code = stockRuntimeInfos.get(i).code;
                if(code.equals(_stockCode))
                {
                    stock = stockRuntimeInfos.get(i);
                }
                else if(_debtCode.equals(code))
                {
                    debt = stockRuntimeInfos.get(i);
                }
//                System.out.println(stockRuntimeInfos.get(i).toDesc());
            }
            if(stock == null || debt==null)
            {
                return;
            }
            float equalStock = _standardPrice*debt.priceNew/100;
            float premiumRatio = (equalStock-stock.priceNew)*100/stock.priceNew;
            System.out.println(String.format("%s  premiumRatio: %.2f%% stock:%.2f  %.2f %.2f%% debt:%.2f %.2f %.2f%%",
                    stock.name, premiumRatio, stock.priceNew, stock.GetChange(), stock.GetRatio(), debt.priceNew, debt.GetChange(), debt.GetRatio()));
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

    }
}