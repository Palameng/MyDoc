# 碎碎念
当我们需要展示一些列表数据的时候，或多或少都需要一个分页功能来方便地让使用者查看展示的数据。因此，结合最近手头练手的几个项目来归纳归纳关于分页功能的开发和使用。

# 例子：对商铺列表做分页展示
列表主要存储商铺信息。这里给出实体类描述，无关紧要。可以提供一些在做有关商品信息时用到的一些提示。

```
public class Shop {
	private Long shopId;
	private String shopName;
	private String shopDesc;
	private String shopAddr;
	private String phone;
	private String shopImg;
	private Integer priority;
	private Integer enableStatus; //-1 不可用 0 审核中 1 可用
	private Date createTime;
	private Date lastEditTime;
	private String advice;		//提醒
	private Area area;
	private PersonInfo owner;
	private ShopCategory shopCategory;
	
	//get set方法省略
	//...
	}
```

## 1 页面展示部分
这里给出一个简单的html部分代码，列表列名（省去了大部分列名）用一个div包含，列表的信息这里通过js写入class为shop-wrap的div。


```
    <div class="content">
        <div class="content-block">
            <div class="row row-shop">
                <div class="col-40">商店名称</div>
                <div class="col-40">状态</div>
                <div class="col-20">操作</div>
            </div>
            <div class="shop-wrap">
            
            </div>
        </div>
    </div>
```

## 2 JS部分
这里定义了一个 getlist() 的函数，之后逐行运行，通过ajax给后台发送请求，返回的数据作为参数传递给另外一个处理函数：handleList。

handleList主要负责填充上述标签class为shop-wrap的内容，当我们传入list时我们可以通过.map方法遍历这个list，item指代每个list成员（关于.map的一点描述：https://www.cnblogs.com/exhuasted/p/7837849.html)，这样我们结合html就能遍历拼凑出list内容来了。

```
$(function() {
	getlist();
	function getlist(e) {
		$.ajax({
			url : "/getshoplist",
			type : "get",
			dataType : "json",
			success : function(data) {
				if (data.success) {
					handleList(data.shopList);
				}
			}
		});
	}

	function handleList(data) {
		var html = '';
		data.map(function(item, index) {
			html += '<div class="row row-shop"><div class="col-40">'
					+ item.shopName + '</div><div class="col-40">'
					+ shopStatus(item.enableStatus)
					+ '</div><div class="col-20">'
					+ goShop(item.enableStatus, item.shopId) + '</div></div>';

		});
		$('.shop-wrap').html(html);
	}

	function shopStatus(status) {
		if (status == 0) {
			return '审核中';
		} else if (status == -1) {
			return '店铺非法';
		} else if (status == 1) {
			return '审核通过';
		}
	}
});
```

## 3 Controller部分
这里定义的ShopExecution是一个返回类型处理的类，项目里经常需要封装返回类型，这个可以忽略，我写出来只是想表达这个层级逻辑下来会做的一些操作，这里显然主要操作是从数据库获取商铺list，并且值得注意的是，getShopList函数后面的两个参数。

```
    @RequestMapping(value = "/getshoplist", method = RequestMethod.GET)
    @ResponseBody
    private Map<String, Object> getShopList(HttpServletRequest request) {
        Map<String, Object> modelMap = new HashMap<String, Object>();
        
        try {
            Shop shopCondition = new Shop();
            ShopExecution shopExecution = shopService.getShopList(shopCondition, 0, 100);
            modelMap.put("shopList", shopExecution.getShopList());
            modelMap.put("success", true);
        } catch (Exception e) {
            modelMap.put("success", false);
            modelMap.put("errMsg", e.getMessage());
        }
        
        return modelMap;
        
    }
```

## 4 ServiceImpl部分
这里给出了getShopList函数的参数定义，也是涉及分页功能平时能遇到的比较多的两个参数————pageIndex和pageSize。
pageIndex指的是从当前那一页开始展示，这样当我们点击分页控件1，2，3，4时我们就可以定位到某一页上；pageSize表示每一页展示的数据行数，当然可以做的更好一些，就是把这些内容封装起来变成可配置的。

```
    public ShopExecution getShopList(Shop shopCondition, int pageIndex, int pageSize) {
        int rowIndex = PageCalculator.calculateRowIndex(pageIndex, pageSize);
        //从第rowIndex行开始，查询pagesize大小的个数出来
        List<Shop> shopList = shopDao.queryShopList(shopCondition, rowIndex, pageSize);
        int count = shopDao.queryShopCount(shopCondition);
        ShopExecution se = new ShopExecution();
        
        if(shopList != null){
            se.setShopList(shopList);
            se.setCount(count);
        }else{
            se.setState(ShopStateEnum.INNER_ERROR.getState());
        }
        return se;
    }
```
那么，如何定位到某一页呢？由于数据库中的LIMIT关键字只支持行偏移查找，此时这里通过calculateRowIndex函数可以计算出实际开始展示的index页，不过还需要具体算出从哪一行开始才行，并传入DAO层的queryShopList函数。

```
    public static int calculateRowIndex(int pageIndex, int pageSize){
        return(pageIndex>0) ? (pageIndex-1)*pageSize : 0;
    }
```

## 5 DAO部分

最后我们通过写SQL中的limit限定，就能查出对应页下的，对应每页大小的数据了。

```
LIMIT #{rowIndex},#{pageSize};
```

## 6 总结
关于分页，最最基本的简单的逻辑就如上述，当然在这样的基础上还可以做很多的配置开发，对分页进行各种各样性能和功能上的提升。



