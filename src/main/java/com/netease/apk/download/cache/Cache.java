package com.netease.apk.download.cache;

import java.util.Map;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 服务内部缓存管理类，用于管理内部队列等
 *
 * @author yudong.chen
 */
public class Cache {

    public static Map<String, String> serverConf = null;
    /**
     * 列表页任务装载队列
     */
    public static LinkedBlockingDeque<Object> downloadTaskQueue = new LinkedBlockingDeque<Object>();
    

}
