package com.hilary.web.utils;

import com.hilary.web.model.Robot;

public class ThreadLocalUtils {
    private final  static ThreadLocal<Robot> robotThreadLocal = new ThreadLocal<>();
    /**
     * 设置当前线程中的用户
     * @param robot
     */
    public static void setrobot(Robot robot){
        robotThreadLocal.set(robot);
    }
    /**
     * 获取线程中的用户
     * @return
     */
    public static Robot getrobot( ){
        return robotThreadLocal.get();
    }
    /**
     * 清空线程中的用户信息
     */
    public static void clear(){
        robotThreadLocal.remove();
    }
}
