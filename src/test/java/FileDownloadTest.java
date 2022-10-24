import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileDownloadTest {


    private static Pattern docUrlReg = Pattern.compile("URL=(.*)");

    /**
     * 遍历指定文件夹下所有internet文件，url内容
     * @param args
     */
    public static void main(String[] args) {

        try{

            File file = new File("./飞书");
            findAllFiles(file);
            file = new File("./飞书/亿博天下  客户情况 沟通纪要.docx");
            File file2 = new File("./飞书/亿博天下  客户情况 沟通纪要 .docx");
            String title = "博天下  客户情况 沟通纪要 - 飞书云文档";
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
            System.out.println("downloadFile"+downloadFileName);
            System.out.println("downloadFile2"+downloadFileName2);
            downloadFileName = downloadFileName+".docx";
            downloadFileName2 = downloadFileName2+".docx";
            String downloadFilePath = "./飞书/";
            String filePath = downloadFilePath+downloadFileName;
            String filePath2 = downloadFilePath+downloadFileName2;
            file = new File(filePath);
            file2 = new File(filePath2);
            int time=3;
            if(file.exists()){
                System.out.println("file exists");
            }else{
                System.out.println("file not exists");
            }
            if(file2.exists()){
                System.out.println("file2 exists");
            }else{
                System.out.println("file2 not exists");
            }

            while(!file.exists()&&!file2.exists() && time < 2) {
                System.out.println("......");
                time ++;
            }
            //moveFileToPath(file,"./飞书/主流应用商城apk抓包调研");
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 遍历文件夹下所有需要下载的url文件
     * @param file
     */
    public static void  findAllFiles(File file){
        try{
            if(file.exists()){
                File[] childFiles = file.listFiles();
                for(File childFile: childFiles){
                    if(childFile.isDirectory()){
                        findAllFiles(childFile);
                    }else{
                        if(childFile.getName().endsWith("url")){
                            System.out.println("path "+childFile.getPath());
                            System.out.println("file "+childFile.getName());
                            BufferedReader reader = new BufferedReader(new FileReader(childFile));
                            String line = null;
                            String doc = "";
                            while((line = reader.readLine()) != null){
                                doc = doc+"\r\n"+line;
                            }

                            System.out.println(doc);
                            Matcher matcher = docUrlReg.matcher(doc);
                            if(matcher.find()){

                                System.out.println("url : "+matcher.group(1));
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

    /**
     * 将文件下载到指定文件夹
     * @param file
     * @param path
     */
    public static void moveFileToPath(File file, String path){

        try{
            System.out.println(path+"\\"+file.getName());
            if(file.renameTo(new File(path+"\\"+file.getName()))){
                System.out.println("move file "+file.getName()+" to path "+path+" success !");
            }else{
                System.out.println("move file "+file.getName()+" to path "+path+" error !");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
