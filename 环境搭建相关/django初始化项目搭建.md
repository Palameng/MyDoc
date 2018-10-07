**搭建django项目：
（以下操作仅仅针对简单创建项目（mkvirtualenv的配置参见博客里另一篇文章）以及简单的测试model。）**

1）mkvirtualenv xxxx （如果不想创建虚拟空间，该步骤可以忽略）

2)pip install django==1.x (这里版本号可以自行更改)

3)打开pyCharm,创建django工程，选择好上述自建的虚拟空间，路径一般为..Envs/Script/python.exe，如果没有创建，默认是哪就是哪。

4)pip install mysql-python (3.x中执行 pip install pymysql,3.x貌似对普通的mysql驱动不支持，详细可以自行搜索“Python3.x + mysql”)

5)3.x执行完上述后，还需要在pyCharm中的__init__.py中加入以下，如果你安装的是pymysql：
	import pymysql
	pymysql.install_as_MySQLdb()

6）打开settings.py,找到DATABASE，修改如下，主要是设置连接数据库方式为mysql，数据库名字，连接用户名和密码以及主机号,内容仅供参考：
	DATABASES = {
	    'default': {
	        'ENGINE': 'django.db.backends.mysql',
	        'NAME': "xxxxxx",
	        'USER': "root",
	        'PASSWORD': "123456",
	        'HOST': "127.0.0.1"
	    }
	}

7）pyCharm中，选择Tools --> Run manage.py Task

8) makemigrations

9) migrate

10) debug起来，浏览器打开127.0.0.1:8000

11）选择Tools --> Run manage.py Task ,输入startapp xxx , 创建xxx app;

12)书写models.py，因为model的书写需要多方面考虑，这里简单给个字段例子：

	例如：
		...
	    nick_name = models.CharField(max_length=50, verbose_name=u"昵称", default=u"")
	    birday = models.DateField(verbose_name=u"生日", null=True, blank=True)
		...
		
		Meta info...
		...
		
13）在settings.py中添加该app

	INSTALLED_APPS = [
	    'django.contrib.admin',
	    'django.contrib.auth',
	    'django.contrib.contenttypes',
	    'django.contrib.sessions',
	    'django.contrib.messages',
	    'django.contrib.staticfiles',
	    'users'
	]

14) makemigrations appname

15) migrate appname


以上操作仅仅针对简单创建项目（mkvirtualenv的配置参见博客里另一篇文章）以及简单的测试model。