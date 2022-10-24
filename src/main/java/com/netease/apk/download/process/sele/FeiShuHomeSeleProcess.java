package com.netease.apk.download.process.sele;

import com.alibaba.fastjson.JSON;
import com.netease.apk.download.cache.Cache;
import com.netease.apk.download.cache.Constants;
import com.netease.apk.download.util.WebDriverEngine;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FeiShuHomeSeleProcess extends Thread{

    private ChromeDriver webDriver = null;
    public FeiShuHomeSeleProcess(ChromeDriver webDriver){
        this.webDriver = webDriver;
    }

    @Override
    public void run() {

        try{
            /**
             * 操作滚动条实现页面下拉
             */
        	int time = 0;
            while(!scrollTo(300,webDriver,0) && time<5){
            	time++;
            }
            /**
             * 下拉结束后，获取当前所有详情页的url，title添加到队列中
             */
            try{
            	List<WebElement> ulNodeObjs = webDriver.findElements(By.xpath("//*[@id=\"mainContainer\"]/div[3]/div/div[2]/div[2]/div[1]/div[3]/div[3]/ul"));
                if(ulNodeObjs.size()==0) {
                	ulNodeObjs = webDriver.findElements(By.xpath("//ul[@class=\"sc-dtMiey iRlemf\"]"));
                }                
                /**
                 * 添加更多兼容的解析策略，使代码更加健壮。。。
                 */
                for(int i=0;i<ulNodeObjs.size();i++){
                    WebElement ulNode = ulNodeObjs.get(i);
                    List<WebElement> docANodeObjs = ulNode.findElements(By.xpath("//a[@draggable=\"false\"]"));
                    
                    for(WebElement docAEle : docANodeObjs){
                        Map<String,String> info = null;
                        info = new HashMap<String,String>();
                        String infoUrl = docAEle.getAttribute("href");
                        if(infoUrl.startsWith("/")){
                            infoUrl = "https://netease-we.feishu.cn"+infoUrl;
                        }
                        info.put("url",infoUrl);

                        /**
                         * 将详情页任务添加到详情页操作任务队列中
                         */
                        System.out.println("add info task url : "+ JSON.toJSONString(info));
                        Cache.downloadTaskQueue.add(info);
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            Constants.SERVER_STATUS = true;
        }catch (Exception e){
            e.printStackTrace();
            /**
             * 如果异常退出，如何实现断点继续操作
             */
        }

    }

    /**
     *
     * @param webDriver
     * @return
     */
    private boolean scrollTo(int down ,ChromeDriver webDriver, int infoNumber) {

        try{
            /**
             * 将滚动条拖到底部
             */
        	List<WebElement> headerEle = webDriver.findElements(By.xpath("//header"));
        	for(WebElement header : headerEle) {
        		if(header.getText().contains("最近")) {
        			WebDriverEngine.slide(1, webDriver, header);
        		}
        	}
        	List<WebElement> docANodeObjs = webDriver.findElements(By.xpath("//div[@class=\"dnd-connect-source\"]"));
            if(docANodeObjs.size()>infoNumber){
            	WebDriverEngine.slide(1, webDriver, docANodeObjs.get(docANodeObjs.size()-1));
            	System.out.println("Jump to the bottom of the page");
            }
            
            /**
             * 判断当前最近浏览文档个数是否有新增
             * 如果有新增再次尝试进入下一页，否则尝试重试
             */
            docANodeObjs = webDriver.findElements(By.xpath("//div[@class=\"dnd-connect-source\"]"));
            if(docANodeObjs.size()>infoNumber){
            	WebDriverEngine.slide(1, webDriver, docANodeObjs.get(docANodeObjs.size()-1));
                Thread.sleep(2500);
            	System.out.println("Jump to the bottom of the page, get more file");
            	scrollTo(down+500, webDriver, docANodeObjs.size());    
            	
            }else{
            	System.out.println("Jump to the bottom of the page, have no more file, but try again");	
                
            	return false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

}
