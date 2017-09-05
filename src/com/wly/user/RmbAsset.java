package com.wly.user;

import com.wly.stock.common.StockConst;

/**
 * Created by Administrator on 2017/2/13.
 */
public class RmbAsset
{
    public String code;
    public String name;
    public float activeAmount;
    public float lockAmount;

    public RmbAsset()
    {
        this.code = StockConst.RmbCode;
        this.name = StockConst.RmbName;
    }
}
