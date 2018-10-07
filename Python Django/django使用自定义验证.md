    当我们想使用诸如邮箱或者手机作为登录名进行登录的时候（只是一个需求例子），Django源码中只对用户表的username进行认证，想要扩展认证就需要重载authenticate方法。下面是简单的分析和实现步骤。

1 在setting.py中加入：

	AUTH_USER_MODEL = 'users.UserProfile' //一个自定义的User Model

	AUTHENTICATION_BACKENDS = (
    'users.views.CustomBackend',	//这里假定我们要写的自定义类名字为CustomBackend

)

2 在views.py（这里的view文件按个人情况选择）中，书写类：

class CustomBackend(ModelBackend):
    """
    当我们想要扩展校验条件时，可以重载authenticate方法，该方法需要：
        1）from django.contrib.auth.backends import ModelBackend

        2）在setting中加入：
        AUTHENTICATION_BACKENDS = (
        'users.views.CustomBackend',  #加入支持的类

        )
    """
    def authenticate(self, username=None, password=None, **kwargs):
        try:
            # user = UserProfile.objects.get(username=username)
            user = UserProfile.objects.get(Q(username=username) | Q(email=username))
            if user.check_password(password):
                return user
        except Exception as e:
            return None


相比于ModelBackend中的authenticate方法, 下面有两点提一下：

	1）settings.AUTH_USER_MODEL导向User（如果自己封装了新的User需要在settings.py里声明，例如：
	AUTH_USER_MODEL = 'users.UserProfile'	# users app里的UserProfile model
	）：

	2）authenticate方法里，user = UserModel._default_manager.get_by_natural_key(username)默认只取得表中的username，
	当想使用邮箱或者手机等等进行登录名输入时，这时候就要进行重载了。


下面是一些源码逻辑，不难看懂：


UserModel = get_user_model()

...


def get_user_model():
    
    # Returns the User model that is active in this project.
    
    try:
        return django_apps.get_model(settings.AUTH_USER_MODEL, require_ready=False)
    except ValueError:
        raise ImproperlyConfigured("AUTH_USER_MODEL must be of the form 'app_label.model_name'")
    except LookupError:
        raise ImproperlyConfigured(
            "AUTH_USER_MODEL refers to model '%s' that has not been installed" % settings.AUTH_USER_MODEL
        )


...

    def authenticate(self, request, username=None, password=None, **kwargs):
        if username is None:
            username = kwargs.get(UserModel.USERNAME_FIELD)
        try:
            user = UserModel._default_manager.get_by_natural_key(username)
        except UserModel.DoesNotExist:
            # Run the default password hasher once to reduce the timing
            # difference between an existing and a non-existing user (#20760).
            UserModel().set_password(password)
        else:
            if user.check_password(password) and self.user_can_authenticate(user):
                return user

