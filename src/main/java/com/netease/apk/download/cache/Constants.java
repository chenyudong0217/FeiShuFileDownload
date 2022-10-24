package com.netease.apk.download.cache;

public class Constants {

    /**
     * 服务状态标志位
     */
    @SuppressWarnings("CanBeFinal")
    public static boolean SERVER_STATUS = false;
    public static boolean SERVER_MAIN_PROCESS = true;
    public static String SYSTEM_TYPE = "window";

    /**
     * 服务抓取配置文件默认地址
     */
    public static final String SERVER_CONF_FILE = "../etc/crawl.properties";
    /**
     * chrome 驱动相关文件地址配置项
     */
    public static final String SERVER_CONF_WEBDRIVER_PATH = "crawl.server.conf.chrome.webdriver.path";
    public static final String SERVER_CONF_CHROME_CDP_STEALTH_JS = "crawl.server.conf.chrome.stealth.js";
    public static final String SERVER_CONF_DOWNLOAD_CACHE_PATH = "crawl.server.conf.chrome.download.path";

}
