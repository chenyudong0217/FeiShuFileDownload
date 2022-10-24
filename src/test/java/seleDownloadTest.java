import com.alibaba.fastjson.JSON;
import org.htmlcleaner.TagNode;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import javax.swing.text.html.parser.Element;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class seleDownloadTest {

	public static void main(String[] args) {

		List<Map<String,String>> infoUrlList = null;
		infoUrlList = new ArrayList<Map<String,String>>();
		try {
			/**
			 * 设置线上环境chromedriver驱动路径
			 */
			System.setProperty("webdriver.chrome.driver", "E:/git location branch/appstorecrawl/etc/chromedriver.exe");
			ChromeOptions options = new ChromeOptions();
			/**
			 * 无痕模式
			 */
//            options.addArguments("-incognito");
			/**
			 * 禁止显示消息的弹窗
			 */
			options.addArguments("-disable-notifications");
			/**
			 * 关闭使用ChromeDriver打开浏览器时上部提示语"Chrome正在受到自动软件的控制"
			 */
			//options.setHeadless(true);

			options.addArguments("-disable-infobars");
			//options.addArguments("lang=en-US.UTF-8");
			options.addArguments("user-agent="
					+ "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36");
			options.addArguments("-disable-blink-features=AutomationControlled");
			/**
			 * linux启动版本
			 */
			options.addArguments("no-sandbox");
			options.addArguments("disable-dev-shm-usage");

			Map<String, Object> prefs = new HashMap<String, Object>(12);
			prefs.put("download.default_directory", "E:\\apkdownload\\com.netease.cloudmusic");
			prefs.put("profile.default_content_settings.popups", 0);
			options.setExperimentalOption("prefs", prefs);
			options.setExperimentalOption("excludeSwitches", new String[] { "enable-automation" });
			//options.addExtensions(new File("../etc/proxyIp.zip"));
			
			
			ChromeDriver webDriver = new ChromeDriver(options);
			webDriver.get("https://bot.sannysoft.com/");
			webDriver.get("https://netease-we.feishu.cn/drive/home/");
			Thread.sleep(500);

			/**
			 * 判断是否登录
			 */
			List<WebElement> headerDivObjs = webDriver.findElements(By.xpath("//div[@class=\"_pp-header-avatar\"]"));
			while(headerDivObjs.size()<=0){
				Thread.sleep(2000);
				headerDivObjs = webDriver.findElements(By.xpath("//div[@class=\"_pp-header-avatar\"]"));
			}
			webDriver.get("https://netease-we.feishu.cn/drive/home/");
			Thread.sleep(4000);
			List<WebElement> ulNodeObjs = webDriver.findElements(By.xpath("//ul[@class=\"sc-dtMiey iRlemf\"]"));
			if(ulNodeObjs.size()>1){
				WebElement ulNode = ulNodeObjs.get(1);
				List<WebElement> docANodeObjs = ulNode.findElements(By.xpath("//a[@draggable=\"false\"]"));

				for(WebElement docAEle : docANodeObjs){
					Map<String,String> info = null;
					info = new HashMap<String,String>();

					String infoUrl = docAEle.getAttribute("href");
					if(infoUrl.startsWith("/")){
						infoUrl = "https://netease-we.feishu.cn"+infoUrl;
					}
					List<WebElement> spanEles = docAEle.findElements(By.xpath("//span[@type=\"main\"]"));
					String title = "";
					if(spanEles.size()>0){
						WebElement spanEle = spanEles.get(0);
						title = spanEle.getAttribute("title");
					}
					info.put("title",title);
					info.put("url",infoUrl);
					
					/**
					 * 将详情页任务添加到详情页操作任务队列中
					 */
					System.out.println("add info task url : "+ JSON.toJSONString(info));
					infoUrlList.add(info);
				}
			}
			String fileType = "word";
			for(Map<String,String> infoDict: infoUrlList){

				String url = infoDict.get("url");
				if(url.contains("docs")){
					fileType = "word";
				}else if(url.contains("sheets")){
					fileType = "excel";
				}else if(url.contains("file") || url.contains("图片")){
					/**
					 * 直接点击下载按钮执行下载
					 */
				}
				String title = infoDict.get("title");
				webDriver.get(url);
				Thread.sleep(2000);
				String htmlSource = webDriver.getPageSource();
				if(htmlSource.contains(title)){

					List<WebElement> moreMenuEles = webDriver.findElements(By.xpath("//button[@data-selector=\"more-menu\"]"));
					/**
					 * 定位下载功能按钮，一步步渲染出下载类型按钮标签
					 */
					if(moreMenuEles.size()>0){
						moreMenuEles.get(0).click();
						List<WebElement> downloadButtons = webDriver.findElements(By.xpath("//li[@text=\"下载\"]"));
						if(downloadButtons.size()>0){
							downloadButtons.get(0).click();
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
									}
								}
								/**
								 * 判断默认地址是否存在该文件夹
								 */
							}


						}

					}

				}
			}
			List<WebElement> aNodeObjs = webDriver.findElements(By.xpath("//div[@class=\"apk_topba_appinfo\"]//a"));
			WebElement downloadButton = null;
			for (WebElement aNodeObj : aNodeObjs) {
				String nodeText = aNodeObj.getText().toString();
				if (nodeText.contains("下载APK")) {
					String downloadUrl = aNodeObj.getAttribute("href");
					downloadButton = aNodeObj;
					break;
				}
			}
			downloadButton.click();
			Thread.sleep(10000);
			webDriver.quit();
			webDriver.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
