# MethodCost
用Transform+Javassist动态注入代码实现的方法耗时打印
# 使用
## 添加插件
```
buildscript {
    repositories {

        google()
        jcenter()
        maven{
            url uri("./repos")
        }
        
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.5.0'
        //添加cost插件
        classpath 'com.pxq.cost:cost-plugin:1.0.2'
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

apply plugin: 'com.android.application'
apply plugin: 'com.pxq.cost'
...

```
## 添加库依赖
```
allprojects {
    repositories {
        google()
        jcenter()
        maven{
            //具体的maven地址
            url 'file://xxx/xxx/MethodCost/repos/'
        }
    }
}

implementation 'com.pxq.cost:cost-api:1.0.0'
```
## 添加方法注解
```
    @MethodCost
    public void testCost(int x) throws InterruptedException {
        Thread.sleep(x);
    }

    @MethodCost
    public JavaBean testCostWithReturn(int x) throws InterruptedException {
        Thread.sleep(x);
        return new JavaBean("testCostReturn", 1);
    }
```
## build.gradle中控制
```
apply plugin: 'com.android.application'
apply plugin: 'com.pxq.cost'

cost{
    injectCost = false
}
...
```
