package com.netease.apk.download.util;

import com.alibaba.fastjson.JSON;
import com.netease.apk.download.cache.Cache;
import com.netease.apk.download.cache.Constants;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtil {

    private static Pattern docUrlReg = Pattern.compile("URL=(.*)");
    /**
     * 遍历文件夹下所有需要下载的url文件
     * @param file
     */
    public static void findAllFiles(File file){
        try{
            if(file.exists()){
                File[] childFiles = file.listFiles();
                for(File childFile: childFiles){
                    if(childFile.isDirectory()){
                        findAllFiles(childFile);
                    }else{
                        if(childFile.getName().endsWith("url")){
                            String fileName = childFile.getName();
                            fileName = fileName.replace(".url","");
                            if(isExistOtherFiles(fileName, childFile.getParentFile().getPath())){
                                continue;
                            }
                            BufferedReader reader = new BufferedReader(new FileReader(childFile));
                            String line = null;
                            String doc = "";
                            while((line = reader.readLine()) != null){
                                doc = doc+"\r\n"+line;
                            }
                            Matcher matcher = docUrlReg.matcher(doc);
                            if(matcher.find()){
                                System.out.println("url : "+matcher.group(1));
                                Map<String,Object> docInfo = null;
                                docInfo = new HashMap<String,Object>();
                                docInfo.put("url",matcher.group(1));
                                docInfo.put("fileFolderPath",childFile.getParentFile().getPath());
                                docInfo.put("title",fileName);
                                Cache.downloadTaskQueue.put(docInfo);
                            }
                            reader.close();
                        }
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public static void findAllFiles(File[] files){
       try{
           for(File childFile :files){
               if(childFile.isDirectory()){
                   findAllFiles(childFile);
               }else{
                   if(childFile.getName().endsWith("url")){
                       /**
                        * 判断和文件同文件名的存在docx,xlsx,mm,等文档文件
                        */
                       String fileName = childFile.getName();
                       fileName = fileName.replace(".url","");
                       if(isExistOtherFiles(fileName, childFile.getParentFile().getPath())){
                           continue;
                       }
                       BufferedReader reader = new BufferedReader(new FileReader(childFile));
                       String line = null;
                       String doc = "";
                       while((line = reader.readLine()) != null){
                           doc = doc+"\r\n"+line;
                       }
                       Matcher matcher = docUrlReg.matcher(doc);
                       if(matcher.find()){
                           System.out.println("url : "+matcher.group(1));
                           Map<String,Object> docInfo = null;
                           docInfo = new HashMap<String,Object>();
                           docInfo.put("url",matcher.group(1));
                           docInfo.put("fileFolderPath",childFile.getParentFile().getPath());
                           docInfo.put("title",fileName);
                           System.out.println("find file download task : "+JSON.toJSONString(docInfo));
                           Cache.downloadTaskQueue.put(docInfo);
                       }
                       reader.close();

                   }
               }
           }
       }catch (Exception e){
           e.printStackTrace();
       }
    }

    /**
     * 根据文件名判断是否存在相似文件
     * docx, xlsx, mm,
     * @param fileName
     * @return
     */
    private static boolean isExistOtherFiles(String fileName,String path) {
        try{
            File docxFile = new File(path+fileName+".docx");
            File xlsxFile = new File(path+fileName+".xlsx");
            File mmFile = new File(path+fileName+".mm");

            if(docxFile.exists()){
                return true;
            }else if(xlsxFile.exists()){
                return true;
            }else if(mmFile.exists()){
                return true;
            }
            return false;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 将文件下载到指定文件夹
     * @param file
     * @param path
     */
    public static boolean moveFileToPath(File file, String path){

        try{
        	String folderLine = "\\";
        	if(Constants.SYSTEM_TYPE.equals("window")) {
        		folderLine = "\\";
        	}else if(Constants.SYSTEM_TYPE.equals("linux")) {
        		folderLine = "/";
        	}
        	System.out.println(path+folderLine+file.getName());
            if(file.renameTo(new File(path+folderLine+file.getName()))){
                System.out.println("move file "+file.getName()+" to path "+path+" success !");
                return true;
            }else{
                System.out.println("move file "+file.getName()+" to path "+path+" error !");
                return false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

}
