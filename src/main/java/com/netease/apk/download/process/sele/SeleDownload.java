package com.netease.apk.download.process.sele;

import org.openqa.selenium.chrome.ChromeDriver;

public interface SeleDownload {

	ChromeDriver seleDoDownloadUrl(String appUrl, String apkName, String apkDownloadUrl);
}
