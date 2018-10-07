**1. app下的models中某个数据库字段错误（比如少了个vbname=而直接写了个“xxx”），重新makemigrations  再migrate后出现问题：**

目前已知的几种方法：
1）每个app下的migrations目录中会有000x.xxxxxx这样的文件，文件记录了每次models的修改，可以根据其中的内容分析；
2）尝试删除每个创建的app下的migrations目录，重新makemigrations；
3）如果表还是未生成，目前可行的是备份数据库后，删除数据库，再重建数据库，makemigrations -> migrate后  导入备份数据；
4）最后最后，首先分析出问题，不是表重复的话不要急着删除数据库的表。

**2.no model named 'xxxx'：**

目前我遇到的情况是，我在虚拟环境中工作，但是cmd中没有进入虚拟环境操作，所以在执行xx.py时会提示这样的错误。

另一方面就是，如果在使用pycharm时在IDE的编辑器中更改了路径或者目录，那么在setting下注意insert或者append相应的路径，该问题请参考https://my.oschina.net/leejun2005/blog/109679有更为详细的解答。

例如：
```
import os
import sys

# Build paths inside the project like this: os.path.join(BASE_DIR, ...)
BASE_DIR = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
**sys.path.insert(0, os.path.join(BASE_DIR, "xxx"))  #xxx为你的目录名**
```

**3.pip install xadmin  和从github上克隆下来的版本不一致：**

最好是从github上克隆下最新的版本，截止到2017/07/23目前pip下来的xadmin会出现 Unicode 解码错误，来源于一个README文件，搜到的几个答案是推荐删除该文件，说是此文件不重要。但是本人没有删除而是直接pip install xadmin-master.tar(github克隆下来的)，是没有报错的。

在此之外如果想要在xadmin源码上进行更改，可以将克隆下来的压缩包解压后，把内部的xadmin目录拷贝到自己的工程下，然后卸载之前安装的xadmin（如果之前install了），配置好url即可使用。

手动安装命令：
xadmin 目录下 python install setup.py

```
# 如果xadmin拷贝到了某个目录而非根目录下，pycharm中mark一下目录，添加好路径后直接使用下方也是可行的
import xadmin

urlpatterns = [
    url(r'^xadmin/', xadmin.site.urls),
]
```

**4.在django的xadmin里，当想回显某个成员的单项信息（而不是XXX object）：**
python2.x中，可以在model里重载  unicode

```
    def __unicode__(self):
        return self.xxx

```

这个在python3.x(目前使用3.6发现的)不起作用，这时候可以重载__str__:

```
    def __str__(self):
        return self.xxx
```


**5.setting中配置上传文件路径的问题：**

一开始我觉得问题是：

因为之前配置static的时候os.path.join(BASE_DIR, "static") 中的"static"并没有给/，但是无碍静态文件的加载，所以在设置文件上传时，如果 os.path.join(BASE_DIR, 'media/') 中的  'media/' 不加/会报错，所以这里需要加/
…………………………

**但是仔细观察会发现，os.path.join(BASE_DIR, ‘media/’)中media用了单引号，而os.path.join(BASE_DIR, "static")中的static用了双引号，问题其实在这里，所以如果用单引号则需要加 /
下面是两种可行方式的例子：**

```
STATIC_URL = '/static/'
STATICFILES_DIRS = (
    os.path.join(BASE_DIR, "static"),
)

MEDIA_ROOT = os.path.join(BASE_DIR, 'media/')
MEDIA_URL = '/media/'
```


**6.使用uwsgi + Nginx代理静态资源时一直显示欢迎页/无法显示静态资源：**
1）首先检查/etc/nginx/conf 下的xxx.conf文件（nginx配置文件）里对项目路径有没有添加
	root 项目路径（绝对）；
2）尝试换用端口，比如在server 127.0.0.1 80 切换成 81；
3）在默认的nginx.conf文件中注释掉引用default资源的代码，并remove相关文件（度娘很多这个操作）；
4）uswgi.ini左对齐不要尝试缩进。

**7.生成 requirements.txt 文件以备换用机器时快速部署Django环境**
注意：前提是virtualenv已经装好，环境名起为A,B
场景：在A机器做的项目，想挪到B机器上继续工作

```
workon A
(A) $ pip freeze >requirements.txt
```
这时候会在目录下生成一个requirements.txt文件，然后拷贝到B机器的环境下：

```
workon B
(B) $ pip install -r requirements.txt
```

**8 an explicit app_label and isn't in an application in INSTALLED_APPS. 错误**

检查一下是否是因为工程已经登录，而后台admin还处于未登录状态，目前把工程中的登录状态注销再访问admin就不会有问题了。

**9 在pycharm中django无法加载的解决办法**

在pycharm中打开别人的项目时，编辑器中经常出现django模块无法加载的提示，但是系统中已经安装django了，只需要修改下面的设置即可解决：

**Settings -> Project Settings -> Python Integrated Tools，**右边的第一个选项 Package requirements file 留空即可

**如果Run里没有Django Server，创建一个就行了：**

1 首先在Pycharm中打开你的Django项目，点击上面编辑栏上的run->Edit Configurations；
2 选择 +；
3 增加一个Django server ，默认OK就行；