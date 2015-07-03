package io.rong.imlib;

import android.app.Application;
import io.rong.imlib.message.GroupInvitationNotification;

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

//        try {
//            //注册自定义消息类型
//            RongIMClient.registerMessageType(GroupInvitationNotification.class);
//
//        } catch (AnnotationNotFoundException e) {
//            e.printStackTrace();
//        }

        DemoContext.getInstance().init(this);

    }
}
