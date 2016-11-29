/*
 * Copyright 2016 JiongBull
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jiongbull.jlog.storage.qiniu;

import com.jiongbull.jlog.IStorage;
import com.jiongbull.jlog.Logger;
import com.jiongbull.jlog.util.FileUtils;
import com.jiongbull.jlog.util.LogUtils;
import com.jiongbull.jlog.util.NetUtils;
import com.qiniu.android.common.Zone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.KeyGenerator;
import com.qiniu.android.storage.Recorder;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.persistent.FileRecorder;

import org.json.JSONObject;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import static android.content.ContentValues.TAG;

/**
 * 日志存储到七牛.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class QiniuStorage implements IStorage {

    /** 七牛上传凭证. */
    private String mToken;
    /** 七牛配置. */
    private QiniuConfigs mQiniuConfigs;
    /** 七牛文件上传管理器. */
    private UploadManager mUploadManager;

    public QiniuStorage(@NonNull QiniuConfigs qiniuConfigs) {
        mQiniuConfigs = qiniuConfigs;
        Recorder recorder = null;
        try {
            recorder = new FileRecorder(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "jlog");
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        }
        KeyGenerator keyGen = new KeyGenerator() {
            @Override
            public String gen(String key, File file) {
                return key + "_._" + new StringBuffer(file.getAbsolutePath()).reverse();
            }
        };
        Configuration configuration = new Configuration.Builder()
                .recorder(recorder, keyGen)
                .zone(Zone.httpAutoZone)
                .build();
        mUploadManager = new UploadManager(configuration);
    }

    /**
     * 连接业务服务器获取七牛上传的token.
     *
     * @return token
     */
    public abstract String getToken();

    @Override
    public void upload(@NonNull Logger logger) {
        Context context = logger.getContext();
        String logDirName = logger.getLogDir();
        String logDirPath = LogUtils.genDirPath(logDirName);
        File logDir = new File(logDirPath);

        if (FileUtils.isExist(logDir)) {
            /* 搜索满足条件的日志文件. */
            File[] logFiles = LogUtils.getLogFiles(logDir);
            if (logFiles != null) {
                logFiles = LogUtils.filterLogFiles(logFiles, logger.getZoneOffset(), logger.getLogPrefix(), logger.getLogSegment());
            } else {
                logFiles = new File[0];
            }

            /* 压缩日志，删除原始文件. */
            if (mQiniuConfigs.isZipLog()) {
                for (File logFile : logFiles) {
                    try {
                        LogUtils.zipLogs(logFile);
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
            }

            if (!NetUtils.isNetConnected(context)) {
                return; // 当前网络不可用
            }

            if (!mQiniuConfigs.isAvailableInMobile()
                    && !NetUtils.isWifiConnected(context)
                    && !NetUtils.isEthernetConnected(context)) {
                return; // 在移动状态下禁止上传日志，当前wifi和ethernet不可用
            }

            File[] uploadFiles;
            if (mQiniuConfigs.isZipLog()) {
                uploadFiles = FileUtils.getZipFiles(logDir);
            } else {
                uploadFiles = logFiles;
            }

            if (uploadFiles != null) {
                for (final File uploadFile : uploadFiles) {
                    UpCompletionHandler upCompletionHandler = new UpCompletionHandler() {
                        @Override
                        public void complete(String key, ResponseInfo info, JSONObject response) {
                            if (info.isOK()) {
                                boolean deleteResult = uploadFile.delete(); // 删除已上传的文件
                                if (!deleteResult) {
                                    Log.e(TAG, "delete file failed, " + uploadFile.getAbsolutePath());
                                }
                            } else {
                                int statusCode = info.statusCode;
                                Log.e(TAG, "日志上传失败，七牛HTTP状态码: " + statusCode
                                        + ", 状态码字典: https://github.com/qiniu/android-sdk/blob/c4cd1437aa1f2a0d68122e46b83580facdf1b74a/library/src/main/java/com/qiniu/android/http/ResponseInfo.java");
                                if (statusCode == ResponseInfo.InvalidToken) {
                                    mToken = getToken();
                                }
                            }
                        }
                    };
                    mUploadManager.put(uploadFile, uploadFile.getName(), mToken, upCompletionHandler, null);
                }
            }
        }
    }
}