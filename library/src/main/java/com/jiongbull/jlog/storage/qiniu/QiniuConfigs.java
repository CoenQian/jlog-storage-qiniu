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

import android.support.annotation.NonNull;

/**
 * 七牛配置.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class QiniuConfigs {
    /** 移动网络模式下是否可用 */
    private boolean mAvailableInMobile;
    /** 是否压缩日志. */
    private boolean mZipLog;

    private QiniuConfigs(@NonNull Builder builder) {
        mAvailableInMobile = builder.mAvailableInMobile;
        mZipLog = builder.mZipLog;
    }

    public boolean isAvailableInMobile() {
        return mAvailableInMobile;
    }

    public boolean isZipLog() {
        return mZipLog;
    }

    public static class Builder {
        private boolean mAvailableInMobile;
        private boolean mZipLog;

        private Builder() {
        }

        public static Builder newBuilder() {
            return new Builder();
        }

        public Builder setAvailableInMobile(boolean availableInMobile) {
            mAvailableInMobile = availableInMobile;
            return this;
        }

        public Builder setZipLog(boolean zipLog) {
            mZipLog = zipLog;
            return this;
        }

        public QiniuConfigs build() {
            return new QiniuConfigs(this);
        }
    }
}