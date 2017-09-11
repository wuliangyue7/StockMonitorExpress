package com.wly.stock.tradeplat.eastmoney;

import com.wly.stock.common.OrderInfo;
import com.wly.stock.common.StockConst;
import com.wly.stock.common.StockUtils;
import com.wly.stock.common.eStockPlate;

/**
 * Created by wuly on 2017/8/12.
 */
public class EastmoneyUtils
{
    static public final String OrderStat_Order = "已报";
    static public final String OrderStat_Cancle = "已撤";
    static public final String OrderStat_WaitForCancel = "已报待撤";
    static public final String OrderStat_Half = "部成";
    static public final String OrderStat_Done = "成交";

    static public int GetStatByPlatStat(String str)
    {
        int stat = OrderInfo.OrderStat_None;
        switch (str)
        {
            case OrderStat_Order:
                stat = OrderInfo.OrderStat_Order_Succ;
                break;
            case OrderStat_Cancle:
                stat = OrderInfo.OrderStat_Cancel_Succ;
                break;
            case OrderStat_Half:
                stat = OrderInfo.OrderStat_Half;
                break;
            case OrderStat_WaitForCancel:
                stat = OrderInfo.OrderStat_Cancel_Waiting;
                break;
            case OrderStat_Done:
                stat = OrderInfo.OrderStat_Deal;
                break;
        }
        return  stat;
    }

    static public final float FeeRate = 0.00025f;
    static public final float ChangeUnit = 0.045f; //上证每100股 0.45
    static public final float StampTaxRate = 0.001f; //交易印花税 交易总额的千分之一

    //计算交易手续费
    static public float CaculateTradeFee(String code, int tradeFlag, float price, int count)
    {
        float feeRet = 0f;
        float tradeAmount = price*count;
        feeRet += GetCountFee(tradeAmount);    //佣金
        if(tradeFlag == StockConst.TradeSell)
        {
            feeRet += GetTransferFee(code,  count);    //过户费
            feeRet += GetStampTax(tradeAmount);
        }

        return feeRet;
    }

    //佣金计算 万分之五（5元起）
    //买卖都收
    static public float GetCountFee(float amount)
    {
        float counterFee = StockUtils.TrimValueRound(amount*FeeRate);    //佣金
        counterFee = counterFee <= 5f ?5f:counterFee;
        return counterFee;
    }

    ///过户费 上证过户费0.035每100股 向下取整精确到分
    ///买卖都收
    static public float GetTransferFee(String code, int num)
    {
        float transferFee = 0f;
        eStockPlate plate = StockUtils.GetPlateByCode(code);
        switch (plate)
        {
            case PlateSH:
                transferFee = StockUtils.TrimValueFloor(num/100*ChangeUnit);
                break;
        }
        return transferFee;
    }

    //印花税 交易金额千分之一精确到分向上取整
    //卖出时收取
    static public float GetStampTax(float amount)
    {
        return StockUtils.TrimValueRound(StampTaxRate * amount);
    }
}
