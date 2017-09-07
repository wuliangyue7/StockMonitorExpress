package com.wly.user;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.wly.common.ITickable;
import com.wly.common.LogUtils;
import com.wly.common.Utils;
import com.wly.stock.common.StockConst;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Administrator on 2017/9/5.
 */
public class UserManager implements ITickable
{
    private Lock lockUserInfoHashMap = new ReentrantLock();
    private HashMap<Integer, UserInfo> userInfoHashMap = new HashMap<>();

    public UserInfo GetUser(int id)
    {
        return userInfoHashMap.get(id);
    }

    public String AddUser(String jsonStr)
    {
        int code = 0;
        String msg = "succ";
        try
        {
            JsonObject jsonObject = new JsonParser().parse(jsonStr).getAsJsonObject();
            int userId = jsonObject.get("userId").getAsInt();

            UserInfo userInfo = null;
            if(userInfoHashMap.containsKey(userId))
            {
                userInfo = userInfoHashMap.get(userId);
            }
            else
            {
                userInfo = new UserInfo(userId);
                userInfoHashMap.put(userId, userInfo);
            }

            int platId = jsonObject.get("platId").getAsInt();
            userInfo.InitTradeContext(platId, jsonObject);
        }
        catch (JsonSyntaxException ex)
        {
            code = 1;
            msg = "format error";
        }
        finally
        {
            return Utils.FormatResult(code, msg);
        }
    }

    public void AddUser(UserInfo userInfo)
    {
        if(userInfoHashMap.containsKey(userInfo.GetUserId()))
        {
            LogUtils.LogRealtime("user has alread in usermanager");
            return;
        }
        lockUserInfoHashMap.lock();
        userInfoHashMap.put(userInfo.GetUserId(), userInfo);
        lockUserInfoHashMap.unlock();
    }

    @Override
    public void OnTick()
    {
        lockUserInfoHashMap.lock();
        Iterator iter = userInfoHashMap.entrySet().iterator();
        while (iter.hasNext())
        {
            Map.Entry entry = (Map.Entry) iter.next();
            UserInfo userInfo = (UserInfo)entry.getValue();
            userInfo.OnTick();
        }
        lockUserInfoHashMap.unlock();
    }
}
