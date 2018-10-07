# 0 碎碎念
关于支付宝扫码支付的沙箱对接测试，主要围绕支付宝对接、支付回调、查询支付状态这三大块来描述。下面一步一步通过测试支付宝官方demo。  

这里有所有的文档，还有沙箱登录：
https://docs.open.alipay.com/200/105311/  
关于当面付（本文主要看扫码支付）：  
https://docs.open.alipay.com/194/105072  
快速接入：  
https://docs.open.alipay.com/194/105170/  
接入必读：  
https://docs.open.alipay.com/194/105322/  
进阶功能：  
https://docs.open.alipay.com/194/105190/  
异步通知：  
https://docs.open.alipay.com/194/103296/

# 1 支付宝扫码支付流程
该业务的大体流程如下：  
1. 首先本地项目生成一个订单；
2. 向支付宝后台请求预下单（alipay.trade.precreate），预下单需要传递部分参数，例如产品信息、金额、公钥等等，这样支付宝后台会生成一个外部订单号对应于自己项目这个内部订单号；
3. 在这之后，支付宝会生成一个二维码串，返回给该项目；
4. 拿到二维码信息后使用一个二维码图片生成工具包生成一个二维码展示给前端或者做持久化存储；
5. 买家通过支付宝沙箱进行扫码（在这之前本地系统还会做查询 alipay.trade.query，虽然有回调但主动查询交易状态和等待回调结合会更加稳定）；
6. 扫码后会收到支付宝后台第一次回调，信息包括商品信息和要求密码输入；
7. 输入密码后，支付宝会进行第二次回调，信息包括支付结果，付款金额等；
8. 第2步的时候可以规定一个超时时间，如果在时间内未支付，那么本地系统可以调用alipay.trade.cancel撤销交易,并同步返回撤销结果；

# 2 扫码支付重要字段
## 2.1 关键入参

参数名称 | 参数说明
---|---
out_trade_no | 本地生成订单号，需要保证不重复
total_amount| 订单金额
subject | 订单标题
store_id | 商户门店编号
timeout_express | 交易超时时间

## 2.2 关键出参

参数名称 | 参数说明
---|---
qr_code | 订单二维码图片地址

## 2.3 其他字段

关于其他字段可以上支付宝开发者网站内查找到对应的文档内容查看。


# 3 扫码支付重要细节
## 3.1 主动轮询和回调
这里引用项目讲师比喻的一个例子，比如要烧开水，主动轮询就类似于，每隔一段时间，我们去看水壶里的水是否烧开了；而回调类似于，我们这个水壶比较高级，水烧开的时候会告知水烧开了。  

回到扫码支付，主动轮询就是每隔一段时间，向支付宝后台发送请求，看看订单交易成功与否，这是为了防止万一支付宝后台或者网络中突然地异常，没有收到回调；而回调就是支付宝后台对本地做一次请求，本地要实现这个回调接口，支付宝会通过这个回调接口返回一些订单成功与否的信息。

## 3.2 避免单边账
单边账意思就是，本地生成了订单账单信息，或者交易完成，而支付宝没有，或者支付宝有，本地没有，账单对不上。

## 3.3 同步请求的签名和验证
通过和支付宝后台组织的公私钥对进行信息加签，验签。加签的核心方法是支付宝SDK中的AlipaySignature.rsaSign，验签的核心方法是AlipaySignature.rsaCheckContent。通过这两个方法保证数据传输的安全性。

## 3.4 确保通知是支付宝发出
通过AlipaySignature.rsaCheckV2来验证签名内容。（这里v2关键要看在支付宝沙箱申请公钥的时候是用RSA1还是2）。

## 3.5 支付宝回调本地返回
程序执行完后必须打印输出“success”，如果商户反馈给支付宝的字符不是success这7个字符，支付宝服务器会不断重发通知，知道超过24小时22分钟，一般情况下，25小时以内完成八次通知。（通知的间隔频率一般为：4m,10m,1h,2h,6h,15h）

# 4 回调调试
这里使用natapp做回调调试。登录natapp.cn购买一个隧道，很便宜。这个东西的作用就是，当我们本地开发了一个项目，想要做外网展示时，可以用该隧道绑定本机，natapp会提供一个域名，而这个域名可以配置成自己的8080端口，这样本地的项目通过外网就可以简单访问到了。这样的做法相对于去配置阿里云更快速地能看到结果，但是阿里云毕竟是服务器级别的，需要更多功能可以使用部署到阿里云，然后暂时开放远程debug接口进行联调。

# 5 Demo使用
首先需要下载Demo：  
https://docs.open.alipay.com/54/104506/  
用idea打开demo。
  
## 5.1 导入Demo
一开始导入项目，项目的Main里可能会有报错，这时候按照如下操作：  
1. 首先打开ProjectStructure--Modules--选中中间一栏TradePayDemo--在Sources/Paths/Dependencies标签中选中Depedencies,把SDK改成本地使用的SDK版本（1.7、1.8）。
2. 之后删除所有带红的jar，重新导入WEB-INF/lib下除了带source后缀的包以外的其他包.apply完成。


## 5.2 配置文件修改
这时候run一下Main会报错，主要是我们的配置文件zfbinfo.properties要修改。这里需要登录沙箱环境--沙箱应用，里面是针对每个人不同的数据，需要拷贝一些信息到目前demo文件里的这个配置文件上。


配置参数 | 配置内容说明
---|---
open_api_domain | 支付宝网关（登录沙箱环境沙箱应用后可以看到，直接拷贝）
mcloud_api_domain | 使用默认配置，不修改。
pid | 商户UID（登录沙箱环境沙箱应用后可以看到，直接拷贝）
appid | APPID（登录沙箱环境沙箱应用后可以看到，直接拷贝）
private_key | 打开支付宝密钥工具（可到我的github上自提：https://github.com/Palameng/MyDoc ），密钥工具选中PKCS8，密钥长度选2048，点击生成，把私钥复制到配置文件这一项中。
public_key | 上一步中会生成公钥，把公钥也复制到配置文件中
alipay_public_key | 我们用的是RSA2的支付宝公钥。打开沙箱环境--沙箱应用，选择RSA2（SHA256）密钥那栏，把刚刚复制的public_key复制到那上面去。然后确定，页面会刷新一下，这时候出现一个支付宝公钥查看，点击进去，复制出来粘贴到这个配置文件的这个配置项上。注意是RSA2（SHA256）。
sign_type | RSA2
max_query_retry | 默认不改动
max_cancel_retry | 默认不改动
cancel_duration | 默认不改动
heartbeat_delay | 默认不改动
heartbeat_duration | 默认不改动



```

# 支付宝网关名、partnerId和appId
open_api_domain = https://openapi.alipaydev.com/gateway.do
mcloud_api_domain = http://mcloudmonitor.com/gateway.do
pid = 2088102176009976
appid = 2016091700530924

# RSA私钥、公钥和支付宝公钥
private_key = 
public_key = 

#SHA1withRsa对应支付宝公钥
#alipay_public_key = 

#SHA256withRsa对应支付宝公钥
alipay_public_key = 
# 签名类型: RSA->SHA1withRsa,RSA2->SHA256withRsa
sign_type = RSA2
# 当面付最大查询次数和查询间隔（毫秒）
max_query_retry = 5
query_duration = 5000

# 当面付最大撤销次数和撤销间隔（毫秒）
max_cancel_retry = 3
cancel_duration = 2000

# 交易保障线程第一次调度延迟和调度间隔（秒）
heartbeat_delay = 5
heartbeat_duration = 900

```

## 5.3 支付宝沙箱应用内容修改
登录自己的沙箱应用，上述更改了“RSA2(SHA256)密钥(推荐)”这一栏，接着还要修改回调地址这一栏，这一栏需要项目开发好了回调接口，我的是使用本机测试，打开natapp接收回调，所以例子为：	
http://ironmeng.s1.natapp.cc/order/alipay_callback.do ，这个暂时不填也行。  

之后AES密钥那一栏默认点击生成就好。

## 5.4 运行Demo
运行Main，我这里得出了这样的信息：

```
九月 10, 2018 3:02:37 下午 com.alipay.demo.trade.config.Configs init
信息: 配置文件名: zfbinfo.properties
九月 10, 2018 3:02:37 下午 com.alipay.demo.trade.config.Configs init
信息: Configs{支付宝openapi网关: https://openapi.alipaydev.com/gateway.do
, 支付宝mcloudapi网关域名: http://mcloudmonitor.com/gateway.do
, pid: 2088102176009976
, appid: 2016091700530924
, 商户RSA私钥: MIIEvg******/K5apD
, 商户RSA公钥: MIIBIj******IDAQAB
, 支付宝RSA公钥: MIIBIj******IDAQAB
, 签名类型: RSA2
, 查询重试次数: 5
, 查询间隔(毫秒): 5000
, 撤销尝试次数: 3
, 撤销重试间隔(毫秒): 2000
, 交易保障调度延迟(秒): 5
, 交易保障调度间隔(秒): 900
}
九月 10, 2018 3:02:37 下午 com.alipay.demo.trade.service.impl.AbsAlipayTradeService tradePrecreate
信息: trade.precreate bizContent:{"out_trade_no":"tradeprecreate1536562957475347334","seller_id":"","total_amount":"0.01","undiscountable_amount":"0","subject":"xxx品牌xxx门店当面付扫码消费","body":"购买商品3件共20.00元","goods_detail":[{"goods_id":"goods_id001","goods_name":"xxx小面包","quantity":1,"price":"10"},{"goods_id":"goods_id002","goods_name":"xxx牙刷","quantity":2,"price":"5"}],"operator_id":"test_operator_id","store_id":"test_store_id","extend_params":{"sys_service_provider_id":"2088100200300400500"},"timeout_express":"120m"}
九月 10, 2018 3:02:39 下午 com.alipay.api.internal.util.AlipayLogger logBizSummary
信息: Summary^_^10000^_^null^_^ProtocalMustParams:charset=utf-8&method=alipay.trade.precreate&sign=Pc7EJsFp80LMyEcilqicnl22fLUQm/BY4286LIfhdF7isvNsn8fnA/PTUPGQ+Z3uMFQHzUrRxAmrria/MHT3TY2tp94iWKp7YCaAIFlwCIa1je4NzmvffsdgzHQTLlvAhDgrS6zvQ08pBSCYSY+gf6DtM4230CR2Y4gu+c7UOzI9g8WF0GgFSRgznf1ZDFKTN0odCB3mwOXirJHcFVcHssV39nO77fjM4OvISeHCCkWm0sno7fPspLy+4UwNaDBQbPkMEMLfbURus+mvwtf/pVesGbCpRkCGNEz4C6eCSuM/6FAd5UkLnho7bSOD0zYSxyhWLgLHEFCYFJ8lP/3rMg==&version=1.0&app_id=2016091700530924&sign_type=RSA2&timestamp=2018-09-10 15:02:37^_^ProtocalOptParams:alipay_sdk=alipay-sdk-java-3.3.0&format=json^_^ApplicationParams:biz_content={"out_trade_no":"tradeprecreate1536562957475347334","seller_id":"","total_amount":"0.01","undiscountable_amount":"0","subject":"xxx品牌xxx门店当面付扫码消费","body":"购买商品3件共20.00元","goods_detail":[{"goods_id":"goods_id001","goods_name":"xxx小面包","quantity":1,"price":"10"},{"goods_id":"goods_id002","goods_name":"xxx牙刷","quantity":2,"price":"5"}],"operator_id":"test_operator_id","store_id":"test_store_id","extend_params":{"sys_service_provider_id":"2088100200300400500"},"timeout_express":"120m"}^_^860ms,1106ms,35ms
九月 10, 2018 3:02:39 下午 com.alipay.demo.trade.service.impl.AbsAlipayService getResponse
信息: {"alipay_trade_precreate_response":{"code":"10000","msg":"Success","out_trade_no":"tradeprecreate1536562957475347334","qr_code":"https:\/\/qr.alipay.com\/bax07565ohqbzfqiglp8003c"},"sign":"nt6CmN8kwIjbEBoAdbw2Ev7CXxTz1uc0sBcZ+P8gU+dMRs+kQwmC0vpqB2UCsGxZhKCbyFsFn1ufoqrA1rZ1v0aMXz29k5nTD/ypOtrU0SrzvUqBY8Yv91gRzPJVoNCDyp1EHMpPEEs08gEgAMxVPuDsSGxu4QxtQZNxLYCdA6Z6A3KeaavrY5b+mthplqKr9nAjekNY4I1BmYMAeA8QrAe01TRwdsehlNF+zZwynh2aWE5PyNT52Sua6ARm8z0Ljtn8hQrU4KaysTpAdR5WJm1ZowliR4Lf/IirRZNepu/GsjlOg9nbcwO3zVB4BFGsnhpSkg3afOW3JyDQ20NLvA=="}
九月 10, 2018 3:02:39 下午 com.alipay.demo.trade.Main test_trade_precreate
信息: 支付宝预下单成功: )
九月 10, 2018 3:02:39 下午 com.alipay.demo.trade.Main dumpResponse
信息: code:10000, msg:Success
九月 10, 2018 3:02:39 下午 com.alipay.demo.trade.Main dumpResponse
信息: body:{"alipay_trade_precreate_response":{"code":"10000","msg":"Success","out_trade_no":"tradeprecreate1536562957475347334","qr_code":"https:\/\/qr.alipay.com\/bax07565ohqbzfqiglp8003c"},"sign":"nt6CmN8kwIjbEBoAdbw2Ev7CXxTz1uc0sBcZ+P8gU+dMRs+kQwmC0vpqB2UCsGxZhKCbyFsFn1ufoqrA1rZ1v0aMXz29k5nTD/ypOtrU0SrzvUqBY8Yv91gRzPJVoNCDyp1EHMpPEEs08gEgAMxVPuDsSGxu4QxtQZNxLYCdA6Z6A3KeaavrY5b+mthplqKr9nAjekNY4I1BmYMAeA8QrAe01TRwdsehlNF+zZwynh2aWE5PyNT52Sua6ARm8z0Ljtn8hQrU4KaysTpAdR5WJm1ZowliR4Lf/IirRZNepu/GsjlOg9nbcwO3zVB4BFGsnhpSkg3afOW3JyDQ20NLvA=="}
九月 10, 2018 3:02:39 下午 com.alipay.demo.trade.Main test_trade_precreate
信息: filePath:/Users/sudo/Desktop/qr-tradeprecreate1536562957475347334.png
```

仔细寻找，我们可以找到一个：

```
"qr_code":"https:\/\/qr.alipay.com\/bax07565ohqbzfqiglp8003c"
```

百度找一个二维码生成器生成二维码。用手机下载沙箱，扫描二维码，付款。

这样整个流程就完成了。


# 6 小结
这是一个demo运行的过程，根据最后的结果可以知道，json数据中那些商品信息时demo写死的，我们可以将我们的订单信息包装上去替换代码就可以了，这样当我们执行下单付款动作的时候就会返回一个二维码，通过扫码即可以完成支付。
