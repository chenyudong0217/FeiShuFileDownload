package com.netease.apk.download.util;

import com.netease.apk.download.cache.Cache;
import com.netease.apk.download.cache.Constants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;



/**
 *	核心无头浏览操作实现类：
 *	包含基于vnc有界面chrome启动（包扩加载代理插件配置，webdriver模式选择）
 *	@author yudong
 */
public class WebDriverEngine {

	private static Log logger = LogFactory.getLog(WebDriverEngine.class);
	private static List<String> userAgents = null;
	
	static {
		userAgents = new ArrayList<String>();
        userAgents.add("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.159 Safari/537.36");
	}

	public ChromeDriver startEngine(String proxyZipPath) {

		ChromeDriver webDriver = null;

		if (webDriver == null) {
			try {
				String ua = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.198 Safari/537.36";

				/**
				 * 设置线上环境chromedriver驱动地址
				 */
				System.setProperty("webdriver.chrome.driver", Cache.serverConf.get(Constants.SERVER_CONF_WEBDRIVER_PATH));
				ChromeOptions options = new ChromeOptions();
				/**
				 * -disable-notifications 禁止关闭chrome消息弹窗
				 * -disable-infobars 关于chrome提示受自动软件控制提示
				 */
				if(!Cache.serverConf.get(Constants.SERVER_CONF_WEBDRIVER_PATH).endsWith("exe")) {
					options.addArguments("-disable-gpu");
				}
				options.addArguments("-disable-notifications");
				options.addArguments("-disable-infobars");
				options.addArguments("user-agent=" + userAgents.get(new Random().nextInt(userAgents.size())));
				options.setExperimentalOption("excludeSwitches", new String[] { "enable-automation" });
				/**
				 * 隐藏webdriver控制标识
				 */
				options.addArguments("-disable-blink-features=AutomationControlled");
				/**
				 * linux启动版本
				 */
				options.addArguments("no-sandbox");
				options.addArguments("disable-dev-shm-usage");
				/**
				 * 添加代理配置信息
				 */
				
				/**
				 *配置webDriver驱动默认文件下载地址
				 */
				Map<String, Object> prefs = new HashMap<String,Object>(12);
				prefs.put("download.default_directory",Cache.serverConf.get(Constants.SERVER_CONF_DOWNLOAD_CACHE_PATH));
				prefs.put("profile.default_content_settings.popups",0);
				options.setExperimentalOption("prefs",prefs);

				webDriver = new ChromeDriver(options);
				/**
				 * 加载配置stealth.min.js
				 */
				//loadStealthJs(webDriver);

				webDriver.get("https://bot.sannysoft.com/");
				Thread.sleep(2000);
				return webDriver;
			} catch (IllegalStateException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			webDriver.manage().deleteAllCookies();
			webDriver.get("https://bot.sannysoft.com/");
		}
		return webDriver;
	}

	/**
	 * 加载stealth.min.js 用于遮盖chromedriver 受sele控制痕迹
	 * 以便于绕过大部分网站前端对chrome状态位监控
	 */
	private void loadStealthJs(ChromeDriver webDriver) {
		try{
			File jsFile = null;
			jsFile = new File(Cache.serverConf.get(Constants.SERVER_CONF_CHROME_CDP_STEALTH_JS));
			BufferedReader breader = new BufferedReader(new FileReader(jsFile));
			String jsSource  = breader.readLine();

			Map<String,Object> cdpDict = new HashMap<String,Object>(12);
			cdpDict.put("source",jsSource);
			webDriver.executeCdpCommand("Page.addScriptToEvaluateOnNewDocument",cdpDict);
			logger.debug("chrome driver load stealth.min.js success !!");

		}catch (Exception e){
			e.printStackTrace();
		}
	}

	public static String getImgBase64(WebElement imgElel) {
	
		try {
			return imgElel.getScreenshotAs(OutputType.BASE64);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 滑动滚动条
	 *
	 * @param start
	 * @param end
	 * @param implicitlyWaitSeconds
	 */
	public static void slide(int start, int end, long implicitlyWaitSeconds, ChromeDriver webDriver) {
		//preparation(webDriver, implicitlyWaitSeconds);
		JavascriptExecutor js = (JavascriptExecutor) webDriver;
		js.executeScript("scrollTo(" + start + "," + end + ")");
	}

	/**
	 * 移动元素到指定像素点位置
	 * @param implicitlyWaitSeconds
	 * @param webDriver
	 * @param element
	 */
	public static void slide(long implicitlyWaitSeconds, ChromeDriver webDriver,WebElement element){
		preparation(webDriver, implicitlyWaitSeconds);
		webDriver.executeScript("arguments[0].scrollIntoView();", element);
	}
	private static void preparation(ChromeDriver webDriver, long implicitlyWaitSeconds) {
		if (webDriver == null) {
			System.err.println("webdriver 未打开");
			System.exit(-1);
		}
		implicitlyWait(webDriver, implicitlyWaitSeconds);
	}

	public static void implicitlyWait(ChromeDriver webDriver,long implicitlyWaitSeconds) {
		webDriver.manage().timeouts().implicitlyWait(implicitlyWaitSeconds, TimeUnit.SECONDS);
	}

	public static void destroyDriver(ChromeDriver webDriver){
		try{
			if(webDriver!=null){
				webDriver.close();
			}

			/**
			 *
			 */
		}catch (Exception e){
			e.printStackTrace();
		}

	}

}