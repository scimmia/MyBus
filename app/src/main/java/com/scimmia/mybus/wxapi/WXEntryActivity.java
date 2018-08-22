/*
 * Copyright (C) 2015 Bilibili <jungly.ik@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.scimmia.mybus.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.scimmia.mybus.utils.DebugLog;
import com.scimmia.mybus.utils.GlobalData;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
    private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
    private IWXAPI mIWXAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mIWXAPI == null) {
            mIWXAPI = WXAPIFactory.createWXAPI(getApplicationContext(), GlobalData.INVITE_WEIXIN_API, true);
            if (mIWXAPI.isWXAppInstalled()) {
                mIWXAPI.registerApp(GlobalData.INVITE_WEIXIN_API);
            }
            if (!mIWXAPI.handleIntent(getIntent(), this)){
                DebugLog.e("error");
                finish();
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
        mIWXAPI.handleIntent(intent, this);
    }
    // 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
    @Override
    public void onResp(BaseResp resp) {
        finish();
    }

    // 微信发送请求到第三方应用时，会回调到该方法
    @Override
    public void onReq(BaseReq req) {
        finish();
    }

}