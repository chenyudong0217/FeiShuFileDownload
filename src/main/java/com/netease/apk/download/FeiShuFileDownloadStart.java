package com.netease.apk.download;

import com.alibaba.fastjson.JSON;
import com.netease.apk.download.cache.Cache;
import com.netease.apk.download.cache.Constants;
import com.netease.apk.download.process.sele.FeiShuHomeSeleProcess;
import com.netease.apk.download.process.sele.FeiShuInfoDownloadSeleProcess;
import com.netease.apk.download.util.FileUtil;
import com.netease.apk.download.util.PropertiesUtil;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.util.List;

import com.netease.apk.download.util.WebDriverEngine;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 *  飞书文档下载备份工具服务启动类
 * @author  yudong.chen
 */
public class FeiShuFileDownloadStart extends Thread{

    private static Log logger = LogFactory.getLog(FeiShuFileDownloadStart.class);

    public static ChromeDriver webDriver = null;

    public static void main(String[] args) {
        String systemVersion = "";
        String chromeVersion = "";
        String downloadPath = "";
        String chromeDriverPath = "";
        try{
            logger.info("ApkDownloadServer start ...");
            /**
             * 初始化服务配置文件
             */
            Cache.serverConf = PropertiesUtil.getProperties(Constants.SERVER_CONF_FILE);
            URL url = FeiShuFileDownloadStart.class.getProtectionDomain().getCodeSource().getLocation();
            String serverJarPath = URLDecoder.decode(url.getPath(),"utf8");
            /**
             * 加载服务配置
             * 1. -chromeVersion 92-96
             * 2. -systemVersion window,mac
             * 3. -downloadPath
             */
            System.out.println(JSON.toJSONString(args));
            if(args.length<=0){
                System.out.println("no params -driverPath, -downloadPath, exit server...");
                Thread.sleep(5000);
                return;
            }
            if(args.length == 6){
                int i=0;
                String driverParam = args[0];
                String drvierValue = args[1];
                String downloadFileParam = args[2];
                String downloadFileValue = args[3];

                if(driverParam.equals("-chromeVersion")){

                }else{
                    System.out.println("no param -chromeVersion, exit server...");
                    Thread.sleep(5000);
                    return ;
                }
            }else if(args.length==3){
                systemVersion = args[0];
                chromeVersion = args[1];
                if(systemVersion.equals("window")){
                    if(chromeVersion.equals("90")){
                        chromeDriverPath = "../driver/chrome_90/chromedriver.exe";
                    }else if(chromeVersion.equals("91")){
                        chromeDriverPath = "../driver/chrome_91/chromedriver.exe";
                    }else if(chromeVersion.equals("92")){
                        chromeDriverPath = "../driver/chrome_92/chromedriver.exe";
                    }else if(chromeVersion.equals("93")){
                        chromeDriverPath = "../driver/chrome_93/chromedriver.exe";
                    }else if(chromeVersion.equals("94")){
                        chromeDriverPath = "../driver/chrome_94/chromedriver.exe";
                    }else if(chromeVersion.equals("95")){
                        chromeDriverPath = "../driver/chrome_95/chromedriver.exe";
                    }else if(chromeVersion.equals("96")){
                        chromeDriverPath = "../driver/chrome_96/chromedriver.exe";
                    }else {
                        System.out.println("so, Does not support this driver version,");
                        Thread.sleep(5000);
                        return ;
                    }
                    Constants.SYSTEM_TYPE="window";
                }else if(systemVersion.equals("mac")){
                    if(chromeVersion.equals("90")){
                        chromeDriverPath = "../driver/chrome_90/chromedriver_mac";
                    }else if(chromeVersion.equals("91")){
                        chromeDriverPath = "../driver/chrome_91/chromedriver_mac";
                    }else if(chromeVersion.equals("92")){
                        chromeDriverPath = "../driver/chrome_92/chromedriver_mac";
                    }else if(chromeVersion.equals("93")){
                        chromeDriverPath = "../driver/chrome_93/chromedriver_mac";
                    }else if(chromeVersion.equals("94")){
                        chromeDriverPath = "../driver/chrome_94/chromedriver_mac";
                    }else if(chromeVersion.equals("95")){
                        chromeDriverPath = "../driver/chrome_95/chromedriver_mac";
                    }else if(chromeVersion.equals("96")){
                        chromeDriverPath = "../driver/chrome_96/chromedriver_mac";
                    }else {
                        System.out.println("so, Does not support this driver version,");
                        Thread.sleep(5000);
                        return ;
                    }
                    Constants.SYSTEM_TYPE = "linux";
                }else {
                    System.out.println("system version param error, exit server...");
                    Thread.sleep(5000);
                    return;
                }
                downloadPath = args[2];
                try{
                    if(downloadPath.contains("\\") && !downloadPath.endsWith("\\")) {
                        downloadPath = downloadPath+"\\";
                    }else if(downloadPath.contains("/") && !downloadPath.endsWith("/")) {
                        downloadPath = downloadPath+"/";
                    }
                    File serverJarFile = new File(serverJarPath);
                    String serverPath = serverJarFile.getParentFile().getParentFile().getPath();
                    String testFilePath = serverPath+downloadPath+"file.txt";
                    File file = new File(testFilePath);
                    if(!file.getParentFile().exists()){
                        System.out.println("no this path : "+args[2]);
                        System.out.println("no this path : "+testFilePath);
                        Thread.sleep(5000);
                        return ;
                    }
                    downloadPath = serverPath+downloadPath;
                    System.out.println(downloadPath);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else{
                System.out.println("server  params error, exit server...");
                Thread.sleep(5000);
                return ;
            }

            Cache.serverConf.put(Constants.SERVER_CONF_WEBDRIVER_PATH,chromeDriverPath);
            /**
             * 校验文件目录是否存在，如果不存在提示
             */
            Cache.serverConf.put(Constants.SERVER_CONF_DOWNLOAD_CACHE_PATH,downloadPath);
            /**
             * 启动服务主线程逻辑进去飞书
             */

            WebDriverEngine webdriverEngine = new WebDriverEngine();
            webDriver = webdriverEngine.startEngine("");
            Thread.sleep(2000);
            webDriver.get("https://bot.sannysoft.com/");
            Thread.sleep(2000);
            webDriver.get("https://netease-we.feishu.cn/drive/home/");
            Thread.sleep(2000);
            /**
             * 判断是否登录
             */
            List<WebElement> headerDivObjs = webDriver.findElements(By.xpath("//div[@class=\"_pp-header-avatar\"]"));
            while(headerDivObjs.size()<=0){
                Thread.sleep(2000);
                headerDivObjs = webDriver.findElements(By.xpath("//div[@class=\"_pp-header-avatar\"]"));
            }
            webDriver.get("https://netease-we.feishu.cn/drive/home/");
            Thread.sleep(2000);
            /**
             * 启动主页最近浏览列表滚动翻页
             */
            /**
             * 判断是否指定了下载文件夹
             */
            if(!isFolderDownload(downloadPath)){
                FeiShuHomeSeleProcess homeSeleProcess = new FeiShuHomeSeleProcess(webDriver);
                homeSeleProcess.start();
            }
            
            FeiShuInfoDownloadSeleProcess infoDownloadSeleProcess = new FeiShuInfoDownloadSeleProcess(webDriver);
            infoDownloadSeleProcess.start();
            
            while (Constants.SERVER_MAIN_PROCESS){
                /**
                 * 添加守护监听线程，添加服务停止处理逻辑
                 */
                Thread.sleep(100000);
            }


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static boolean isFolderDownload(String path){

        try{
            File file = new File(path);
            if(file.exists() && file.isDirectory()){
                File[] childFiles = file.listFiles();
                if(childFiles.length>0){
                    FileUtil.findAllFiles(childFiles);
                    Constants.SERVER_STATUS = true;
                    return true;
                }else{
                    System.out.println("downloadFile folder no other doc url need download...");
                    return false;
                }
            }else{
                System.out.println("error");
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

}
