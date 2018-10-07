今天跟网课做练习项目的时候使用到了该库，下面做一下使用总结。

django-pure-pagination用于WEB的分页功能，在github上可以看到相关描述：
https://github.com/jamespacileo/django-pure-pagination

该库基于django.core.pagination，并在这个基础上做了一些改进。具体的内容可以访问上述地址查看作者的说明文档。


一些前置操作链接里描述的很傻瓜式了，这里不再阐述而关注的是View里和前端如何处理：


1）View
```
    def get(self, request):
        all_A = ClassA.objects.all()
        
        # 对所有A进行分页
        try:
            page = request.GET.get('page', 1)
        except PageNotAnInteger:
            page = 1

        # 第二个参数代表每一页显示的个数
        p = Paginator(all_A , 2, request=request)
        allA = p.page(page)

        return render(request, "org-list.html", {
            "all_A ": allA,
        })
```

 首先在get方法中，首先获取到所有ClassA的实例，然后是一个异常处理（关于GET/POST的一些内容可以在这里得到一些了解：http://blog.csdn.net/parrot18/article/details/8617405，感谢这位博主的总结），如果没有给page赋值，默认为1，即首页；异常处理类似。

然后，调用Paginator接口获得一个实例（我的理解是类似分页控制器），构造函数如下：
```
    def __init__(self, object_list, per_page, orphans=0, allow_empty_first_page=True, request=None):
        self.object_list = object_list
        self.per_page = per_page
        self.orphans = orphans
        self.allow_empty_first_page = allow_empty_first_page
        self._num_pages = self._count = None
        self.request = request
```

这里一般传递三个参数，分别是：

**所有ClassA的实例，
每一页显示A的数量，
request**

```
p = Paginator(all_A , 2, request=request)
```
当我们拿到这个分页控制器实例后，调用page方法，加上page参数（由前端URL传递下来的）即可得到分页实例：

http://127.0.0.1:8000/urlname/?page=3（此处page=3传递到get方法里的page），即

```
allA = p.page(page)
```

最后再把 allA 传递回去给前端，View中的基本处理就是这样。


2）前端页面

因为在github上，作者给出的前端样式不一定和我们自己实现的样式一样，所以这里只需要理清它的条件，插入我们自己的样式就可以了。

逻辑大概如下：
1）先判断有没有前一页，如果有，则显示“前一页”这个按钮链接，否则不显示；
2）一个for循环判断当前页，如果是当前页我们就改变他的显示样式（比如给按钮挂个灰色什么，这里是class="active"），否则就是普通样式；
3）最后判断有没有下一页，如果有，则显示“下一页”这个按钮链接，否则不显示；

所以，实际上我们只要提供前一页，常规页码，后一页这三个类型的样式，按照判断格式填充替换就能达到效果。当然如果都没有可以直接使用github上作者给出的第一个方案，只不过有点丑。

下面是一个例子，只要替换掉`<li>`部分，根据结构填充就可以了。


```
		 <div class="pageturn">
            <ul class="pagelist">
                    {% if all_A.has_previous %}
                        <li class="long"><a href="?{{ all_A.previous_page_number.querystring }}">上一页</a></li>
                    {% endif %}
                    {% for page in all_A.pages %}
                        {% if page %}
                            {% ifequal page all_A.number %}
                                <li class="active"><a href="?{{ page.querystring }}">{{ page }}</a></li>
                            {% else %}
                                <li><a href="?{{ page.querystring }}" class="page">{{ page }}</a></li>
                            {% endifequal %}
                        {% else %}
                            <li class="none"><a href="">...</a> </li>
                        {% endif %}
                    {% endfor %}
                    {% if all_A.has_next %}
                        <li class="long"><a href="?{{ all_A.next_page_number.querystring }}">下一页</a></li>
                    {% endif %}
            </ul>
        </div>
```

