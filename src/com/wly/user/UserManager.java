package com.wly.user;

import com.wly.common.LogUtils;

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

    public void AddUser(UserInfo userInfo)
    {
        if(userInfoHashMap.containsKey(userInfo.id))
        {
            LogUtils.LogRealtime("user has alread in usermanager");
            return;
        }
        lockUserInfoHashMap.lock();
        userInfoHashMap.put(userInfo.id, userInfo);
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
