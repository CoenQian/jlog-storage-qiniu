# jlog-storage-qiniu

[![License](https://img.shields.io/badge/License-Apache%202.0-brightgreen.svg)](https://github.com/JiongBull/jlog-storage-qiniu/blob/master/LICENSE.md)
[![Download](https://jitpack.io/v/JiongBull/jlog-storage-qiniu.svg)](https://jitpack.io/#JiongBull/jlog-storage-qiniu)
[![Build Status](https://travis-ci.org/JiongBull/jlog-storage-qiniu.svg?branch=master)](https://travis-ci.org/JiongBull/jlog-storage-qiniu)

jlog-storage-qiniu是[jlog](https://github.com/JiongBull/jlog)的扩展插件，可以自动把日志同步到[七牛](http://www.qiniu.com)上。

## 特点

* 定时搜索未同步的日志并上传
* 可配置使用压缩模式(zip格式)
* 可配置非wifi模式下同步上传
* 文件同步到七牛后清除终端文件

> **注意**：所有的日志只会在时间切片所在的时间之后上传 ，例如2016-02-18.log会在2016-02-18之后上传，2016-02-18_0607.log会在2016-02-18 07:00:00之后上传。

## 依赖

在根目录的build.gradle里添加仓库。

```groovy
allprojects {
 repositories {
    jcenter()
    maven { url "https://jitpack.io" }
 }
```

在模块的build.gradle中添加依赖。

```groovy
dependencies {
     compile 'com.github.JiongBull:jlog-storage-qiniu:0.0.1'
}
```

## 配置

### 初始化

建议在你的application的`onCreate()`方法里初始化jlog和jlog-storage-qiniu的全局配置，设置一次终身受用。

```java
public class RootApp extends Application {

    private static Logger sLogger;

    @Override
    public void onCreate() {
        super.onCreate();

        QiniuConfigs qiniuConfigs = QiniuConfigs.Builder.newBuilder()
                /* 下面的属性都是默认值，你可以根据需求决定是否修改他们. */
                .setZipLog(false)
                .setAvailableInMobile(false)
                .build();

        sLogger = Logger.Builder.newBuilder(getApplicationContext(), "jlog")
                .setWriteToFile(true)
                .setStorage(new QiniuStorage(qiniuConfigs) {
                    @Override
                    public String getToken() {
                        /* Access your remote server to get token of qiniu. */
                        return null;
                    }
                }).build();
    }

    public static Logger getLogger() {
        return sLogger;
    }
}
```

## 关于

[![GitHub](https://img.shields.io/badge/GitHub-JiongBull-blue.svg)](https://github.com/JiongBull)
[![WeiBo](https://img.shields.io/badge/weibo-JiongBull-blue.svg)](https://weibo.com/jiongbull)
[![Blog](https://img.shields.io/badge/Blog-JiongBull-blue.svg)](http://jiongbull.com)
