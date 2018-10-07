# 碎碎念
开发项目的时候，一般会有几个开发阶段，而每个开发阶段涉及到的一些环境配置不一相同，如果不做管理，就需要手动拷贝后逐一修改。之前做底层开发的时候依赖于C语言的优势宏命令预编译等可以很好的通过夺命连环#define控制好各类配置开关，但是如今尝试学习互联网开发，也得需要业界惯用的方法来管理我们的项目环境配置。  

下面就来总结一下使用Maven进行环境配置隔离。

# 需求
假设项目开发中需要分开发环境（DEV），测试环境（BETA），线上运行环境（PROD）。而不同环境有一些配置文件需要管理，这时候可以通过Maven来做隔离。

# 实操
## 1 修改pom.xml
在<build>标签里增加如下内容（注意是标签里）：
```
        <!-- Maven隔离环境配置,具体配置到哪一个隔离环境应该读取resources下哪个文件夹 -->
        <resources>
            <resource>
                <directory>src/main/resources.${deploy.type}</directory>
                <excludes>
                    <exclude>*.jsp</exclude>
                </excludes>
            </resource>

            <resource>
                <directory>src/main/resources</directory>
            </resource>

        </resources>
```
这里指明了我们的resources该去哪里拿以及默认的一份配置在哪，并且考虑到前端页面文件可能也存在这一级目录下，可以用<excludes>标签或者<includes>标签去除或包括一些过滤内容。  

接着还需要在和<build>同级下增加内容（注意是标签同级）：  

```
    <profiles>
        <!-- 默认环境 -->
        <profile>
            <id>dev</id>
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
            <properties>
                <deploy.type>dev</deploy.type>
            </properties>
        </profile>

        <profile>
            <id>beta</id>
            <properties>
                <deploy.type>beta</deploy.type>
            </properties>
        </profile>

        <profile>
            <id>prod</id>
            <properties>
                <deploy.type>prod</deploy.type>
            </properties>
        </profile>
    </profiles>
```
这里我们详细制定了<deploy.type>的类别，这里定义了三个隔离环境：dev,beta和prod。这样和上面的内容结合使用就能定义到不同配置不同文件夹了。

## 2 选择需要区别配置的文件
1. 首先我们要在原resources同级目录下创建：  
resources.dev  
resources.beta   
resources.prod  
三个文件夹；
2. IDE的右边竖下来的一栏，点开Maven Projects，左上角有蓝色刷新图标，点击刷新。注意，这时候会多出一个Profiles文件夹，点开里面可以打钩，注意实勾和虚勾和不选中，给dev打上实勾，那么当我们使用idea自动部署（而不是通过命令行构建），就会使用这里的配置。
3. 这里我们将需要根据不同环境更改不同配置的配置文件，分别从原来的resources中拷贝到上述三个文件夹里，并且随意找三个文件夹中的任意一个文件注释上（例如在resources.dev中有一个A文件，里面加一行注释：# 这是A；resources.dev写一行注释：# 这是B，类推）。这是便于构建后查看验证。 


## 3 用Maven构建工程
命令行调至项目目录下，使用命令：  
mvn clean package -Dmaven.test.skip=true -Pdev  
或  
mvn clean package -Dmaven.test.skip=true -Pbeta  
或  
mvn clean package -Dmaven.test.skip=true -Pprod

这三个命令意味着选中某一个环境里的配置文件进行构建，并且跳过测试代码。  

这时候等待构建结束，在项目目录下会有一个target文件夹，进入classes文件夹，我们会看到那些配置文件，打开同名注释过的文件你会发现，这里根据不同环境加载了对应的配置文件（查看自己的注释）。这样就完成了一种环境隔离操作。

## 4 再尝试
如果执行：  
mvn clean package -Dmaven.test.skip=true   
这个后面没有-P参数了呢？答案是肯定的，因为我们配置了：

```
        <!-- 默认环境 -->
        <profile>
            <id>dev</id>
            <activation>
                <activeByDefault>true</activeByDefault>
            </activation>
            <properties>
                <deploy.type>dev</deploy.type>
            </properties>
        </profile>
```
所以默认会从sources.dev下获取配置文件。

## 5 疯狂试探
此时可以不用命令构建，用IDEA挂一个tomcat构建部署。此时IDEA就依赖于标题2里说的，在Maven Projects 里勾选的环境配置进行自动构建部署。如果把三个勾全部勾上呢？那么最好不要这么做，这时候IDEA构建出来的部署可能不是你想要的配置。

## 6 小结
使用Maven隔离环境确实更易于开发中遇到不同环境做不同配置，并且对其进行统一管理。



