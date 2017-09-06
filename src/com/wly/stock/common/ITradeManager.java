package com.wly.stock.common;

import com.wly.user.RmbAsset;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by wuly on 2017/8/28.
 */
public interface ITradeManager
{
    void RrefreshOrderList(ArrayList<OrderInfo> orderInfos);
}
