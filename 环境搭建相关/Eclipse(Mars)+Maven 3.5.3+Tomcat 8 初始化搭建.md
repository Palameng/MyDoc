## 0 简述
	本人学习课程后的总结，亲测有效，如有描述不清晰的地方欢迎随时留言交流。
	
## 1 环境版本
	1) jdk 1.8.0_162
	2) apache-maven-3.5.3
	3) apache-tomcat-8.5.11
	4) Eclipse version: Mars.2 Release (4.5.2)

## 2 环境搭建

### 2.1 jdk 1.8.0_162
jdk下载非常方便，度娘或者官网都可以下载，注意选择匹配的机型以及注意选择32bit/64bit进行下载。安装过程中我习惯把jdk和jre安装在同一个JAVA目录里，方便后面设置环境变量。环境变量的搭建和测试也比较简单，度娘解决。
	
	[问题1]：如果安装jdk的时候出现如下报错:
		invalid characters in hostname.
	[解决1]：断开网络，点击确定继续安装或者重新安装。


### 2.2 apache-maven-3.5.3
Maven是一个包管理工具，这里可以再官网上下载，地址如下：
https://maven.apache.org/download.cgi
注意官网上对应的机器型号和格式，另外就是下载的是bin后缀的压缩文件，src是这个工具的工程源码。
![](https://raw.githubusercontent.com/Palameng/MyDoc/master/%E7%8E%AF%E5%A2%83%E6%90%AD%E5%BB%BA%E7%9B%B8%E5%85%B3/pics/1.png)  

下载之后解压到任意目录下即可。
环境变量的配置度娘有很详细的了，配置个MAVEN_HOME，再指向解压出来目录里的/bin，添加到path里就行。
接下来的配置我们统一在eclipse上操作。

### 2.3 apache-tomcat-8.5.11
Tomcat的安装也相对简单，有压缩包手动配置和执行程序安装版本的，我选用安装版本的，中间的一系列操作比较多，但是不难，这里转接几篇文章参考。

**注意！！！**  

安装的时候可能要测试Tomcat，之后再用eclipse关联tomcat的时候会发现无法开启，因为此时已经开启并占据端口了，所以建议安装完tomcat之后不要先开启（即使用startup），直接上eclipse关联，用eclipse来操作开启关闭统一会减少不必要的麻烦。当然，如果想尝试，自己手动关闭也是可以的。

**百度，关于安装：**  
https://jingyan.baidu.com/article/4b52d702a5eaa0fc5d774b72.html

### 2.4 Eclipse version: Mars.2 Release (4.5.2)
Eclipse的安装也非常容易，官网或者度娘指定版本搜索下载都可以快速玩转起来，我用的是Mars版本的。安装好后自己可以创建属于自己的workspace，然后第一次打开eclipse时设置好就可以，当然这并非必须。  

**1）关联jdk**  
eclipse默认会使用自身捆绑的jdk，当然我们一般都会设置为使用自己的。首先打开eclipse，选择windows->Preferences，出现如下：  
![](https://raw.githubusercontent.com/Palameng/MyDoc/master/%E7%8E%AF%E5%A2%83%E6%90%AD%E5%BB%BA%E7%9B%B8%E5%85%B3/pics/2.png)  
在左上角搜索框搜索JDK，出现如下后选择Installed JREs：  
![](https://raw.githubusercontent.com/Palameng/MyDoc/master/%E7%8E%AF%E5%A2%83%E6%90%AD%E5%BB%BA%E7%9B%B8%E5%85%B3/pics/3.png)  
之后点击Add（我这里添加过了所以有），把路径引向自己安装的javaJDK下的jre文件，其他栏目都自动填充，设置好后回到Installed JREs，选择下拉的Execution Environments选项，看到如下图所示，将环境选择到1.8并且勾上勾勾。  

![](https://raw.githubusercontent.com/Palameng/MyDoc/master/%E7%8E%AF%E5%A2%83%E6%90%AD%E5%BB%BA%E7%9B%B8%E5%85%B3/pics/4.png)  

最后再设置JAVA的Compiler：  
![](https://raw.githubusercontent.com/Palameng/MyDoc/master/%E7%8E%AF%E5%A2%83%E6%90%AD%E5%BB%BA%E7%9B%B8%E5%85%B3/pics/5.png)    
到这里jdk就关联好了。  
  
**2）关联tomcat**  
**eclipse关联tomcat：**https://www.cnblogs.com/basilguo/p/tomcat_01.html
  
**3）关联Maven**  
接下来配置Maven部分的，同样的在Preferences中找到Maven，然后选择User Setting，出现如下：  
![](https://raw.githubusercontent.com/Palameng/MyDoc/master/%E7%8E%AF%E5%A2%83%E6%90%AD%E5%BB%BA%E7%9B%B8%E5%85%B3/pics/6.png)    
首先，我们把User Settings一栏的路径关联到2.2一节中解压出来的路径里的conf/settings.xml，这时候回头点击蓝色下划线open file，我们需要设置一下这个xml文件。在这个文件中，我们需要配置我们的本地仓库，也就是未来存储下载jar包的位置，默认是存在C盘，但是C盘都是咱们习惯的系统盘，所以为了更好管理，我们自行设置一个路径保存，插入代码即可：  
![](https://raw.githubusercontent.com/Palameng/MyDoc/master/%E7%8E%AF%E5%A2%83%E6%90%AD%E5%BB%BA%E7%9B%B8%E5%85%B3/pics/7.png)  

```
	<localRepository>
	D:\maven-jar-localRepository\.m2\repository
	</localRepository>
```
此时回到Maven的User Setting里Update一下即可。  
  
接着设置Maven->Installtions，点击Add添加自己解压的maven目录即可：  
![](https://raw.githubusercontent.com/Palameng/MyDoc/master/%E7%8E%AF%E5%A2%83%E6%90%AD%E5%BB%BA%E7%9B%B8%E5%85%B3/pics/8.png)  

  
## 3 创建Maven工程

新建步骤如下，New一个Maven工程，然后选择webapp，填写一些包信息，这里没有什么繁琐操作。  
![](https://raw.githubusercontent.com/Palameng/MyDoc/master/%E7%8E%AF%E5%A2%83%E6%90%AD%E5%BB%BA%E7%9B%B8%E5%85%B3/pics/9.png)  
![](https://raw.githubusercontent.com/Palameng/MyDoc/master/%E7%8E%AF%E5%A2%83%E6%90%AD%E5%BB%BA%E7%9B%B8%E5%85%B3/pics/10.png)  
![](https://raw.githubusercontent.com/Palameng/MyDoc/master/%E7%8E%AF%E5%A2%83%E6%90%AD%E5%BB%BA%E7%9B%B8%E5%85%B3/pics/11.png)  

创建好之后会发现，我们的工程会打红叉，此时我们右击工程项目，选择项目的Properties->JAVA Build Path->Add Library->ServerRuntime，点击Next，然后会有Tomcat选择，选上就行。  
![](https://raw.githubusercontent.com/Palameng/MyDoc/master/%E7%8E%AF%E5%A2%83%E6%90%AD%E5%BB%BA%E7%9B%B8%E5%85%B3/pics/12.png)  
  
  
## 4 配置Maven工程
接下来配置Maven的编译依赖工具，在http://mvnrepository.com/ 中搜索maven-compiler-plugin，选择这一项：  
![](https://raw.githubusercontent.com/Palameng/MyDoc/master/%E7%8E%AF%E5%A2%83%E6%90%AD%E5%BB%BA%E7%9B%B8%E5%85%B3/pics/13.png)  
选择最新版本：  
![](https://raw.githubusercontent.com/Palameng/MyDoc/master/%E7%8E%AF%E5%A2%83%E6%90%AD%E5%BB%BA%E7%9B%B8%E5%85%B3/pics/14.png)  
将这里的代码拷贝添加到项目的Pom.xml里：  
![](https://raw.githubusercontent.com/Palameng/MyDoc/master/%E7%8E%AF%E5%A2%83%E6%90%AD%E5%BB%BA%E7%9B%B8%E5%85%B3/pics/15.png)  
注意先在Pom.xml中的fileName标签下增加plugins/plugin，然后再复制刚才的内容进去，去掉dependency标签。  
修改好后如下，增加configuration，如下即可：  

```
  <build>
    <finalName>o2o</finalName>
    <plugins>
    	<plugin>
			<!-- https://mvnrepository.com/artifact/org.apache.maven.plugins/maven-compiler-plugin -->
		    <groupId>org.apache.maven.plugins</groupId>
		    <artifactId>maven-compiler-plugin</artifactId>
		    <version>3.7.0</version>
		    <configuration>
		        <source>1.8</source>
		        <target>1.8</target>
		        <encoding>UTF8</encoding>
		    </configuration>
    	</plugin>
    </plugins>
  </build>
```
**注意！！修改以上后，记得update一下maven的setting，就在Windows->Preferences->Maven，然后选择User Setting就能看到。**  

接着我们会注意到，工程项目里的结构图如下：  
![](https://raw.githubusercontent.com/Palameng/MyDoc/master/%E7%8E%AF%E5%A2%83%E6%90%AD%E5%BB%BA%E7%9B%B8%E5%85%B3/pics/16.png)  
此时我们还少一个匹配的resources的test包，右键项目（注意是项目），new一个**Source Folder**，取名为src/test/resources，点击确认完成之后就添加了一个包。  
  
到这里我们还需要设置这个新增SourceFolder的输出文件路径，右击项目选择Properties，找到java build path（熟悉的操作，很多配置都是这里），选到Source选项卡，将src/test/java的 **Output folder** 信息复制到src/test/resources的 **Output folder** 上，如图所示：  

![](https://raw.githubusercontent.com/Palameng/MyDoc/master/%E7%8E%AF%E5%A2%83%E6%90%AD%E5%BB%BA%E7%9B%B8%E5%85%B3/pics/17.png)  

到这里我们还需要针对Project Facets进行一些配置。根据上图可以找到左边选项里有Project Facets 打开后如图所示：  
![](https://raw.githubusercontent.com/Palameng/MyDoc/master/%E7%8E%AF%E5%A2%83%E6%90%AD%E5%BB%BA%E7%9B%B8%E5%85%B3/pics/18.png)  
我们可以看到打上勾的选项后面对应版本号，这里我们把Dynamic Web Module的模式改到更高，但是在这里改呢会出现报错，eclipse不让这么改，那我们就得直接到具体的配置文件里手动改了。  
  
首先找到我们的工程项目在计算机上的地址，这个可以根据eclipse上右击项目->properties里第一项看得到，然后进入工程项目的源地址，进入.setting文件夹，找到org.eclipse.wst.common.project.facet.core.xml这个文件，用文本编辑工具打开，打开后编辑我们的jst.web行，改成在eclipse当中看到的最新版本号，保存即可：  
![](https://raw.githubusercontent.com/Palameng/MyDoc/master/%E7%8E%AF%E5%A2%83%E6%90%AD%E5%BB%BA%E7%9B%B8%E5%85%B3/pics/19.png)  
  
回到eclipse，我们最后对web.xml进行改动，因为我们更换了上诉的版本，所以复制如下全部修改即可：  

```
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee
                      http://xmlns.jcp.org/xml/ns/javaee/web-app_3_1.xsd"
	version="3.1" metadata-complete="true">
  <display-name>Archetype Created Web Application</display-name>
  <welcome-file-list>
  	<welcome-file>index.jsp</welcome-file>
  	<welcome-file>index.html</welcome-file>
  </welcome-file-list>
</web-app>

```

到此，就可以尝试启动项目了。到这，最基本的搭建就算告一段落。  






