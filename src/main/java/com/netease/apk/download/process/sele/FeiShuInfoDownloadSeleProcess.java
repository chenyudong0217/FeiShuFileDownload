package com.netease.apk.download.process.sele;

import com.netease.apk.download.cache.Cache;
import com.netease.apk.download.cache.Constants;
import com.netease.apk.download.util.FileUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.util.List;
import java.util.Map;

public class FeiShuInfoDownloadSeleProcess extends Thread{

    private static Log logger = LogFactory.getLog(FeiShuInfoDownloadSeleProcess.class);
    private ChromeDriver webDriver = null;

    public FeiShuInfoDownloadSeleProcess(ChromeDriver webDriver){
        this.webDriver = webDriver;
    }

    /**
     * 根据文档infoUrl 队列
     * 首页翻页结束，状态标记位
     */
    @Override
    public void run() {

        String defaultType = "word";
        while(true){
            Map<String,String> infoDict = null;
            try{
            	while(!Constants.SERVER_STATUS) {
            		Thread.sleep(2000);
                    continue;
            	}
                if(Cache.downloadTaskQueue.size()<=0){
                    try {
                    	
                    	webDriver.quit();
                        if(Constants.SYSTEM_TYPE.equals("window")) {
                        	Runtime.getRuntime().exec("tasklist /F /im chromedriver.exe");                        	
                        }else {
                        	Runtime.getRuntime().exec("ps -ef|grep 'chromedriver'|awk '{print $2}'|xargs kill");
                        	Runtime.getRuntime().exec("ps -ef|grep 'webdriver'|awk '{print $2}'|xargs kill");
                        }
                    }catch(Exception e) {
                    	e.printStackTrace();
                    }
                    System.out.println("download files ok, end server...");
                    Constants.SERVER_STATUS=false;
                    Constants.SERVER_MAIN_PROCESS = false;
                }
                infoDict = (Map<String, String>) Cache.downloadTaskQueue.pollFirst();
                if(infoDict != null){
                    openInfoUrlAndDownloadFile(infoDict,0,0);
                }
                Thread.sleep(2000);
            }catch (Exception e){
                
            }
        }
    }

    private void openInfoUrlAndDownloadFile(Map<String, String> infoDict,int waitTime,int times) {
        String fileType = "";
        try{

            String url = infoDict.get("url");
            String fileFolderPath = "";
            String title = "";
            if(infoDict.containsKey("fileFolderPath")){
                fileFolderPath = infoDict.get("fileFolderPath");
            }
            if(infoDict.containsKey("title")){
                
            }
            if(url.contains("/docs/")){
                fileType = "word";
            }else if(url.contains("/sheets/")){
                fileType = "excel";
            }else if(url.contains("/mindnotes/")) {
            	fileType= "FreeMind";
            }else if(url.contains("base")) {
            	fileType= "excel";
            }else if(url.contains("file") || url.contains("图片")){
                /**
                 * 直接点击下载按钮执行下载
                 */
            	fileType="file";
            }
            webDriver.get(url);
            Thread.sleep(2000);
            /**
             * 判断页面核心标签是否加载完毕，如果加载完毕则不等待，动态控制等待间隔时长
             */
            
            if(!onlickDownloadButton(fileType)) {
            	return;
            }
            Thread.sleep(2000);
            /**
             * 根据title， 判断默认系统路径是否存在对应文件，如果不存在需要重新点击下载按钮
             *
             */ 
            title = webDriver.getTitle();
            System.out.println("open docs :"+title);
            if(title.contains("快速了解飞书文档") || title.contains("快速了解云文档")){
                return ;
            }
            if(title.startsWith(" ")) {
            	title = title.substring(1,title.length());
            	title = "_"+title;
            }
            if(title.contains("- 飞书云文档")) {
            	title = title.replace("- 飞书云文档", "");
            }
            if(title.contains("/")) {
            	title = title.replace("/", "_");
            }
            if(title.contains("|")) {
            	title = title.replace("\\|", "_");
            }
            if(title.contains(":")) {
            	title = title.replace(":", "_");
            }
            if(title.startsWith("未命名")) {
            	title = "Untitled";
            }
            String downloadFileName = title;
            String downloadFileName2 = title.trim();
            if(fileType.equals("word")) {
            	downloadFileName = downloadFileName+".docx";
            	downloadFileName2 = downloadFileName2+".docx";
            }else if(fileType.equals("excel")) {
            	downloadFileName = downloadFileName+".xlsx";
            	downloadFileName2 = downloadFileName2+".xlsx";
            }else if(fileType.equals("freemind")) {
            	downloadFileName = downloadFileName+".mm";
            	downloadFileName2 = downloadFileName2+".mm";
            }
            String downloadFilePath = Cache.serverConf.get(Constants.SERVER_CONF_DOWNLOAD_CACHE_PATH);
            
            if(downloadFilePath.contains("\\") && !downloadFilePath.endsWith("\\")) {
            	downloadFilePath = downloadFilePath+"\\";
            }else if(downloadFilePath.contains("/") && !downloadFilePath.endsWith("/")) {
            	downloadFilePath = downloadFilePath+"/";
            }
            String filePath = downloadFilePath+downloadFileName;
            String filePath2 = downloadFilePath+downloadFileName2;
            File file = new File(filePath);
            File file2 = new File(filePath2);
            while(!file.exists()&&!file2.exists() && times<2) {
            	
            	if(waitTime>10 && times <2) {
            		times++;
            		//openInfoUrlAndDownloadFile(infoDict,0,times);
                    webDriver.get(url);
                    Thread.sleep(2000);
                    /**
                     * 判断页面核心标签是否加载完毕，如果加载完毕则不等待，动态控制等待间隔时长
                     */

                    if(!onlickDownloadButton(fileType)) {
                        return;
                    }
            	}
            	System.out.println("wait for file "+file.getPath());
            	if(waitTime>5) {
            		Thread.sleep(3000);
            	}else {
            		Thread.sleep(1500);
            	}
            	waitTime++;   	
            }
            if(!fileFolderPath.equals("")) {
            	if(!FileUtil.moveFileToPath(file,fileFolderPath)) {
                	FileUtil.moveFileToPath(file2,fileFolderPath);            	
                }
            }
            file = null;
            file2= null;
        }catch (Exception e){
            
        }
    }

    private boolean onlickDownloadButton(String fileType) {
        try{
        	
        	if(fileType.equals("file")) {
        		List<WebElement> downloadButtons = webDriver.findElements(By.xpath("//button[@class=\"suite-download-btn\"]"));
        		if(downloadButtons.size()>0) {
        			downloadButtons.get(0).click();
        			Thread.sleep(1000);
        			return true;
        		}
        	}
        	
            List<WebElement> moreMenuEles = webDriver.findElements(By.xpath("//button[@data-selector=\"more-menu\"]"));
            /**
             * 定位下载功能按钮，一步步渲染出下载类型按钮标签
             */
            if(moreMenuEles.size()>0){
                moreMenuEles.get(0).click();
                Thread.sleep(1000);
                List<WebElement> downloadButtons = webDriver.findElements(By.xpath("//li[@text=\"下载\"]"));
                
                if(downloadButtons.size()>0){
                    downloadButtons.get(0).click();
                    Thread.sleep(1000);
                    List<WebElement> divNodeEles = downloadButtons.get(0).findElements(By.xpath("./div[@role=\"menuitem\"]"));
                    if(divNodeEles.size()>0){
                        String eleId =  divNodeEles.get(0).getAttribute("aria-controls");
                        WebElement ulEle = webDriver.findElement(By.id(eleId));
                        List<WebElement> downloadTypes = ulEle.findElements(By.xpath("//li[@role=\"menuitem\"]"));
                        /**
                         * 点击下载类型按钮执行下载
                         */
                        for(WebElement downloadType: downloadTypes){
                            String type = downloadType.getText().toString().toLowerCase();
                            if(type.equals(fileType)){
                                downloadType.click();
                                return true;
                            }
                        }
                    }
                }else if(fileType.equals("file")) {
                	List<WebElement> menuitemEles = webDriver.findElements(By.xpath("//li[@role=\"menuitem\"]"));
                	for(WebElement ele:menuitemEles) {
                		try {
                			if(ele.getText().contains("下载")){
                    			ele.click();
                    			Thread.sleep(2000);
                    			return true;
                    		}
                		}catch(Exception e) {
                			
                		}
                		
                	}
                }
            }
            return false;
        }catch (Exception e){
            
        }
        return false;
    }
}
