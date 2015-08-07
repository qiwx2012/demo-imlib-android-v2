package io.rong.imlib;

import android.app.Application;

import io.rong.imlib.message.DemoCommandNotificationMessage;

/**
 * Created by Bob on 15/6/5.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        /**
         *  IMLib SDK调用第一步 初始化
         * context上下文
         */
        RongIMClient.init(this);



        DemoContext.getInstance().init(this);

    }
}
