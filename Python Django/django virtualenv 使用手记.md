-----------------------------------------
***virtualenv 搭建过程：***
1) 安装python;（自行百度）
2) pip install virtualenv;
3) 创建虚拟工作空间：virtualenv xxx ,xxx为空间名;
4) cd到工作空间文件夹下的Script文件夹目录，使用activate.bat进入虚拟环境,退出环境使用deactivate.bat;
5) pip list 查看当前空间安装哪些库;


***以上操作需要知道virtualenv虚拟空间的Script路径，所有操作可能略麻烦，下面可以使用：
virtualenvwrapper-win:***

1) install virtualenvwrapper-win 
(linux 下不加win);
2) mkvirtualenv xxx
此时会在CMD默认路径下建立一个Env文件夹，其中会有名字为xxx的虚拟环境,此时已经自动进入xxx的Script路径;
3) workon 
查看当前有哪些虚拟环境，activate/deactivate同上；
4) workon xxx
此时重新打开cmd可以直接workon进行查看了,并且进入xxx虚拟环境，不用再cd找路径了；

之后该装啥装啥了。
