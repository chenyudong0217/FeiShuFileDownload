飞书文档RPA机器人—使用手册（无需下载jdk）

浏览器版本要求：chrome浏览器，92版本及以上
MAC在执行过程中请不要使用浏览器，或者让其保持在最前端。
浏览器查看步骤：
在打开的Chrome浏览器主界面，点击右上角的“菜单”按钮

在打开的下拉菜单中依次点击“帮助/关于Google Chrome”菜单项

这时就会打开Chrome浏览器的关于窗口，在这里可以查看到浏览器的版本号：93.0.4577.82

windows篇（默认下载自己创建和浏览过的所有文件）
1、下载机器人工具包
下载window版包： FeishuFileDownload_window.zip
下载.zip工具包地址 https://docs.popo.netease.com/docs/eddc744d04d74bc5959933ceaca23629，放置到磁盘目录下，例如： E: 

2、安装机器人
解压压缩文件到当前路径：

安装完成

 3、 机器人使用
进入bin目录
根据自身环境chrome版本选择 WinChrome_90~WinChrome_96.bat 双击运行
      a.1 支持文件夹内weburl文件对应文档下载：
运行服务前将相关文件夹走飞书文件夹导出，将导出的文件夹放入downloadFile目录,工具将会递归遍历文件夹，并将所需要下载文件放入原目录结构；

浏览器会自动打开飞书登录界面，打开手机端飞书，扫码登录



下载后的文档路径：E:\FeishuFileDownload\downloadFile

MAC篇（默认下载自己创建和浏览过的所有文件）

下载机器人包FeishuFileDownload_mac.zip（默认在/Users/xxx/Downloads目录下 ,如果不是此目录，后续命令中的目录请更改成对应的目录 ）。下载地址：https://docs.popo.netease.com/docs/5a0de6ee73c94fedb36905520604fb74
解压到本地：双击压缩包，解压到当前下载目录
打开mac 终端



查看MAC电脑是否开启了SIP（已关闭的可直接跳过该环节）
因部分同学电脑SIP 系统安全性保护机制是开启状态，所以在终端没有chmod等命令操作权限，所以我们需要对该状态进行关闭；
1.在终端查看SIP状态： 终端输入 csrutil status   查看结果是enabled 还是disabled.
2.关闭SIP, 重启Mac,按住cmd+R 直到屏幕出现苹果logo和进度条，进入Recovery 保护模式
3.在屏幕最上方工具栏找到实用工具进入终端，输入csrutil disable;
4.重启mac 再次进入终端 输入csrutil status  确认是否正确关闭；

输入命令：
chmod -R 755 ~/Downloads/FeishuFileDownload_mac/bin
chmod -R 755 ~/Downloads/FeishuFileDownload_mac/driver （对driver下驱动进行mac安全授权）效果如下图

chmod -R 755 ~/Downloads/FeishuFileDownload_mac/jdk_mac  (使用服务内部jdk, 需要授权)
cd ~/Downloads/FeiShuFileDownload_mac/bin
然后运行脚本：选择当前目录下对应的chrome版本的sh脚本   sh MacChrome95start.sh
启动后过大概20s会出现飞书扫码页面：扫码登录即可

如何查看chrome版本： 对应步骤4当中的命令参数
chrome浏览器中输入：chrome://settings/
指定下载某个飞书文件夹下的所有文档
操作演示：
1.从飞书导出目标文件夹


2. 根据飞书客户端下载引导，找到已下载文件夹，并将文件夹放入下载工具的downloadFile目录，例如下图：

3. 步骤2可同时将多个文件夹放入downloadFile中， 启动服务：步骤参考上述window/mac篇
