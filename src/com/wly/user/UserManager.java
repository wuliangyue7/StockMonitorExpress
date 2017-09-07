package com.wly.user;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.wly.common.LogUtils;
import com.wly.common.Utils;
import com.wly.stock.common.StockConst;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by Administrator on 2017/9/5.
 */
public class UserManager
{
    static private UserManager s_instance;
    static public UserManager GetInstance()
    {
        if(s_instance == null)
        {
            s_instance = new UserManager();
        }

        return s_instance;
    }

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
            }

            int platId = jsonObject.get("platId").getAsInt();
            userInfo.InitTradeContext(platId, jsonObject.get("cookie").getAsJsonObject());
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

    private Timer timerUserTick;
    public void StartUserTick()
    {
        timerUserTick = new Timer();
        timerUserTick.schedule(new UserTick(), 0, 1000);
    }

    class UserTick extends TimerTask {
        @Override
        public void run()
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

}
