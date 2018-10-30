# （施工中）

# 1 什么是Redis
Redis-REmote DIctionary Server 远程字典服务  
高性能key-value数据库  
内存数据库 支持数据持久化  
http://redis.cn  

Redis是一个开源的使用ANSI C语言编写、支持网络、可基于内存亦可持久化的日志型、Key-Value数据库，并提供多种语言的API。从2010年3月15日起，Redis的开发工作由VMware主持。

redis是一个key-value存储系统。和Memcached类似，它支持存储的value类型相对更多，包括string(字符串)、list(链表)、set(集合)、zset(sorted set –有序集合)和hash（哈希类型）。这些数据类型都支持push/pop、add/remove及取交集并集和差集及更丰富的操作，而且这些操作都是原子性的。在此基础上，redis支持各种不同方式的排序。与memcached一样，为了保证效率，数据都是缓存在内存中。区别的是redis会周期性的把更新的数据写入磁盘或者把修改操作写入追加的记录文件，并且在此基础上实现了master-slave(主从)同步。

# 2 Redis 常用数据类型
Redis常用数据类型，这个对象模型可以划分为：
- 数据类型
- 编码方式
- 数据指针
- 虚拟内存
- 其他信息

首先是数据类型，数据类型包括string、hash、list、set、sorted set，编码方式有raw、int等。

# 3 Redis Linux下安装
登录 download.redis.io/releases/  
目前使用Redis 2.8，下载2.8的tar包。

下载后，按如下操作：  
(报错解决方案1：https://blog.csdn.net/luyee2010/article/details/18766911)  
(报错解决方案2：https://www.jianshu.com/p/76cb947fe492?from=timeline&isappinstalled=0)

```
tar -zxvf redis-xxxx
cd redis-xxxx
make
cd src
make test # 此时可能会报错

wget http://downloads.sourceforge.net/tcl/tcl8.6.1-src.tar.gz  
sudo tar xzvf tcl8.6.1-src.tar.gz  -C /usr/local/  
cd  /usr/local/tcl8.6.1/unix/  
sudo ./configure  
sudo make  
sudo make install

# 可能还会报错
cd /redis-xxxx/tests/unit
vim memefficiency.tcl

# 修改其中的16384，由原来的0.90变成0.80
    foreach {size_range expected_min_efficiency} {
        32    0.15
        64    0.25
        128   0.35
        1024  0.75
        16384 0.80

# cd 回到src下
make test

```

接着在Redis的src目录下，执行：

```
./redis-server # 启动redis服务，会看到PID和端口ID

./redis-server & # 后台常驻

kill -9 PID # 关闭redis

# 打开一个新的窗口，同样目录下

# Redis简单测试
./redis-cli
127.0.0.1:6379> set a b
OK
127.0.0.1:6379> keys *
1) "a"
127.0.0.1:6379> get a
"b"
```

# 4 Redis 基本操作
## 4.1 Redis服务启动的各种方式
分两个窗口，其中一个执行：
```
./redis-server
```
另一个执行：
```
./redis-cli
```
测试一下连接：

```
ping
> PONG
```
随便set一些键值对：

```
set a b
set b c
```
此时，在服务端窗口直接ctrl+c关闭服务，再次打开服务，使用keys *命令查看，会发现，刚才set的键值对并没有做持久化。这时候考虑使用save命令，也就是set完键值对后：

```
save
```
执行完这条命令后，服务端窗口会同步输出：

```
[32311] 30 Oct 21:06:16.046 * DB saved on disk
```

那此时应该如何关闭服务端程序呢？可以在客户端命令行执行如下命令：

```
./redis-cli shutdown
```
这时候服务端窗口则会自动退出：

```
[32311] 30 Oct 21:10:50.505 # User requested shutdown...
[32311] 30 Oct 21:10:50.505 * Saving the final RDB snapshot before exiting.
[32311] 30 Oct 21:10:50.507 * DB saved on disk
[32311] 30 Oct 21:10:50.507 # Redis is now ready to exit, bye bye...

```
我们还可以换端口启动redis-server：

```

```










