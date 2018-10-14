# 1 什么是事务
先来一段哪里都能找到的概念：  

*事务是一种机制、是一种操作序列，它包含了一组数据库操作命令，这组命令要么全部执行，要么全部不执行。因此事务是一个不可分割的工作逻辑单元。在数据库系统上执行并发操作时事务是作为最小的控制单元来使用的。这特别适用于多用户同时操作的数据通信系统。*

类比到现实生活中，例如互相转账，火车票购买（这里面也会有锁的概念），证券交易等等，都会用到事务的概念。

常用的例子比如两个人互相转账，甲扣除金额转给乙，甲扣除和乙增加这两个操作必须确保全部执行，如果甲扣了钱而乙没有加钱则说明转账出现了问题。又例如购买火车票，甲购买票和需要减少余票数是要全部在一个单位内执行的动作，如果余票没减而又正好是最后一张，乙发现还有余票结果也买入了，就会出现问题了。
# 2 事务四要素（转度娘）
数据库事务正确执行的四个基本要素包括原子性（Atomicity）、一致性（Consistency）、隔离性（Isolation）、持久性（Durability），简称ACID。目前要实现ACID主要有两种方式：一种是Write ahead logging，也就是日志式的方式(现代数据库均基于这种方式)；另一种是Shadow paging。

- 原子性：整个事务中的所有操作，要么全部完成，要么全部不完成，不可能停滞在中间某个环节。事务在执行过程中发生错误，会被回滚（Rollback）到事务开始前的状态
- 一致性：事务执行前与执行后都必须始终保持系统处于一致的状态
- 隔离性：并发事务之间不会相互干扰，彼此独立执行
- 持久性：在事务完成以后，该事务对数据库所作的更改便持久的保存在数据库之中
# 3 事务结合程序的例子
## 1 SQL操作
1. 首先创建一个测试数据库：

```
show databases;
create test;
use test;
```
2. 创建表：

```
create table account (
id int primary key auto_increment;
name varchar(10);
money float
)；
```

3. 插入数据，假设有三个账户A,B,C,D，每个账户有1000存款：

```
insert into account(name,money) values('A',1000);
insert into account(name,money) values('B',1000);
insert into account(name,money) values('C',1000);
insert into account(name,money) values('D',1000);
```
4. 开启事务

```
start transaction;
```
这里如果不开启事务，那么执行后面的转账操作直接变更数据库数据（默认自动提交为true），当然数据库引擎也要注意用innodb，myISAM引擎不支持事务，但好像对文本搜索内存啊索引啊的支持好一些，还有一些行锁也不支持，印象里书中有说过。
5. 模拟转账

```
update account set money=money-100 where name='A';
update account set money=money+100 where name='B';
commit;
```
这时候，不执行事务提交，手动关闭终端，select from一下该表发现数据没有变化；或者在执行第二条update之前，查看A账户发现数据库数据已经变为900了，随后一样不执行事务提交，关闭终端，终止未提交的事务，最后再看A还是有1000元，这说明开启事务的作用，验证了开头的概念描述。

执行事务提交后，上述操作就能持久化到数据库中了。

6. 回滚

```
rollback;
```
未提交事务之前，通过手动回滚事务，让所有的操作都失效，这样数据就会回到最初的初始状态，比如用在上述未提交前，可以让AB的存款回到开始执行事务时的状态。当然也可设置回滚点等。

## 2 JDBC操作
通过JDBC的方式，做几个场景模拟。JdbcUtils就不展示了，百度很多，都是一些模板代码，再拉一个数据库驱动jar到lib里就好。
1. 事务执行成功的情况：

```
public void transactionSuccess() {
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            // 获取连接
            conn = JdbcUtils.getConnection();

            conn.setAutoCommit(false);// 关闭自动提交，相当开启事务

            // 编写sql
            String sql1 = "update account set money=money-100 where name='D'";

            // 创建语句执行者
            st= conn.prepareStatement(sql1);

            // 执行sql1
            st.executeUpdate();

            // 编写sql
            String sql2 = "update account set money=money+100 where name='C'";

            // 创建语句执行者
            st= conn.prepareStatement(sql2);

            // 执行sql2
            st.executeUpdate();

            //如果不执行事务提交则之前打开事务后执行的部分不会持久化到数据库中
            conn.commit();


        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            JdbcUtils.colseResource(conn, st, rs);
        }

    }
```

2. 异常触发事务终止，自动回滚：

```
    public void transactionFail1() {
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            // 获取连接
            conn = JdbcUtils.getConnection();

            //模拟一个D转账给C 100 的场景
            conn.setAutoCommit(false);// 关闭自动提交，相当开启事务

            // 编写sql
            String sql1 = "update account set money=money-100 where name='D'";

            // 创建语句执行者
            st= conn.prepareStatement(sql1);

            // 执行sql1
            st.executeUpdate();

            //runtime异常
            int a = 1/0;

            // 编写sql
            String sql2 = "update account set money=money+100 where name='C'";

            // 创建语句执行者
            st= conn.prepareStatement(sql2);

            // 执行sql2
            st.executeUpdate();

            //如果不执行事务提交则之前打开事务后执行的部分不会持久化到数据库中
            conn.commit();


        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            JdbcUtils.colseResource(conn, st, rs);
        }

    }
```

3. 异常触发，手动回滚，之后记得做事务提交：

```
public void transactionFail2() {
        Connection conn = null;
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            // 获取连接
            conn = JdbcUtils.getConnection();

            //模拟一个D转账给C 100 的场景
            conn.setAutoCommit(false);// 关闭自动提交，相当开启事务

            // 编写sql
            String sql1 = "update account set money=money-100 where name='D'";

            // 创建语句执行者
            st = conn.prepareStatement(sql1);

            // 执行sql1
            st.executeUpdate();

            int a = 2 / 0;

            // 编写sql
            String sql2 = "update account set money=money+100 where name='C'";

            // 创建语句执行者
            st = conn.prepareStatement(sql2);

            // 执行sql2
            st.executeUpdate();

            //如果不执行事务提交则之前打开事务后执行的部分不会持久化到数据库中
            conn.commit();
        }catch (Exception e){
            try{
                System.out.println("手动回滚触发");
                conn.rollback();
            } catch (SQLException e1) {
            e1.printStackTrace();
        }
        e.printStackTrace();
        } finally {
            JdbcUtils.colseResource(conn, st, rs);
        }

    }
```
才疏学浅，关于更多的事务还需要大量场景进行学习和总结。


# 关于乐观锁和悲观锁
那天上班百度一下，很多博客文章都是复制粘贴，我就索性口语化一下。
- 乐观锁这个概念总是乐观看待问题，认为拿数据时别人不会修改，就不会上锁，并在更新时检查别人是否修改过数据。
- 悲观锁即总往坏处想，认为拿数据时数据已经不是期望数据，所以针对需要操作的数据上锁，一个线程独占数据后其他线程要挂起等待，例如很多并发编程书里的对共享变量叠加的例子，synchronized关键字就是其中一个悲观锁概念。

## 乐观锁 CAS 和 version控制
# 1 version控制的场景
1. 操作员 A 此时将其读出（version=1），并从其帐户余额中扣除 50（50（100-$50 ）。
2. 在操作员 A 操作的过程中，操作员B 也读入此用户信息（ version=1 ），并从其帐户余额中扣除 20（20（100-$20 ）。
3. 操作员 A 完成了修改工作，将数据版本号加一（ version=2 ），连同帐户扣除后余额（ balance=$50 ），提交至数据库更新，此时由于提交数据版本大于数据库记录当前版本，数据被更新，数据库记录 version 更新为 2 。
4. 操作员 B 完成了操作，也将版本号加一（ version=2 ）试图向数据库提交数据（ balance=$80 ），但此时比对数据库记录版本时发现，操作员 B 提交的数据版本号为 2 ，数据库记录当前版本也为 2 ，不满足 “ 提交版本必须大于记录当前版本才能执行更新 “ 的乐观锁策略，因此，操作员 B 的提交被驳回。

# 2 CAS
贴一段概念：

CAS即compare and swap（比较与交换），是一种有名的无锁算法。无锁编程，即不使用锁的情况下实现多线程之间的变量同步，也就是在没有线程被阻塞的情况下实现变量的同步，所以也叫非阻塞同步（Non-blocking Synchronization）。CAS算法涉及到三个操作数

- 需要读写的内存值 V
- 进行比较的值 A
- 拟写入的新值 B

当且仅当 V 的值等于 A时，CAS通过原子方式用新值B来更新V的值，否则不会执行任何操作（比较和替换是一个原子操作）。一般情况下是一个自旋操作，即不断的重试。

道理我们都懂，但是V,A,B具体反映是如何的呢？我查阅了一些资料，然后凑了段代码分析分析：

```
public class CASTest {


    public static AtomicInteger race = new AtomicInteger(0);

    public static void increase(){
        race.getAndIncrement();
    }


    public static void main(String[] args){

        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i=0; i<5; i++){
                    increase();
                    System.out.println("线程1：" + race);
                }
            }
        });

        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i=0; i<6; i++){
                    increase();
                    System.out.println("线程2：" + race);
                }
            }
        });


            thread1.start();
            thread2.start();
}
```
这里的AtomicInteger类使用了CAS的思想，那么进入这个类看看，找到下面调用的getAndIncrement方法：

```
    /**
     * Atomically increments by one the current value.
     *
     * @return the previous value
     */
    public final int getAndIncrement() {
        return unsafe.getAndAddInt(this, valueOffset, 1);
    }
```
这里我的理解是，根据类在内存的地址找到值得偏移量并加1。接着进入getAndAddInt方法后，有一个方法值得注意：

```
public final boolean compareAndSet(int expect, int update) {  
        return unsafe.compareAndSwapInt(this, valueOffset, expect, update);  
    }  
```
这里我的理解就是，this+valueOffset就是所谓共享变量当前值，然后expect是我们线程对其的“猜测值”，update是线程想要将这个变量变更的值。

假设A有100元，这时候线程1读取，知道A有100元，于是传入期望值100，因为乐观锁CAS没有真正上锁，所以这里逻辑比较了期望值和原值，如果此时有线程B通过CAS成功修改了A的额度，那么A线程下的期望就不等了（当然有等的情况，就是ABA问题，后面扯淡时再说），这时候就会挂起，线程想要进行修改还需再次尝试期望和内存值匹配，一直循环（还有个自旋锁概念，懵逼懵逼的）。

# 参考文章
下面是第一天上班找到的一些比较好的文章，区别于一些简单的贴概念博文。

关于乐观锁和悲观锁排版讲解比较好的：  
https://www.cnblogs.com/qjjazry/p/6581568.html  
关于悲观锁乐观锁自旋锁，github里我经常翻阅的博主：  
https://github.com/Snailclimb/JavaGuide/blob/master/%E9%9D%A2%E8%AF%95%E5%BF%85%E5%A4%87/%E9%9D%A2%E8%AF%95%E5%BF%85%E5%A4%87%E4%B9%8B%E4%B9%90%E8%A7%82%E9%94%81%E4%B8%8E%E6%82%B2%E8%A7%82%E9%94%81.md  
关于事务的参考：  
https://www.cnblogs.com/xdp-gacl/p/3984001.html




