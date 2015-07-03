package io.rong.imlib;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.IOException;
import io.rong.imlib.message.GroupInvitationNotification;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.message.CommandNotificationMessage;
import io.rong.message.ContactNotificationMessage;
import io.rong.message.ImageMessage;
import io.rong.message.InformationNotificationMessage;
import io.rong.message.ProfileNotificationMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;

public class DemoContext {

    private static final String TAG = "DemoContext";

    private static DemoContext self;

    private SharedPreferences sharedPreferences;

    public Context mContext;

    public RongIMClient mRongIMClient;

    public String userId;


    public static DemoContext getInstance() {

        if (self == null) {
            self = new DemoContext();
        }

        return self;
    }

    public DemoContext() {
    }

    public DemoContext(Context context) {
        self = this;
    }

    public void init(Context context) {

        mContext = context;

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

    }

    public void setRongIMClient(RongIMClient rongIMClient) {
        mRongIMClient = rongIMClient;
    }

    public void registerReceiveMessageListener() {

        mRongIMClient.setOnReceiveMessageListener(onReceiveMessageListener);
    }

    RongIMClient.OnReceiveMessageListener onReceiveMessageListener = new RongIMClient.OnReceiveMessageListener() {

        @Override
        public boolean onReceived(Message message, int left) {

            Log.e("------", "0605--------onReceived--------------------");


            if (message.getContent() instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) message.getContent();

                Log.d("onReceived", "TextMessage---收收收收--接收到一条【文字消息】-----" + textMessage.getContent() + ",getExtra:" + textMessage.getExtra());

            } else if (message.getContent() instanceof ImageMessage) {

                final ImageMessage imageMessage = (ImageMessage) message.getContent();
                Log.d("onReceived", "ImageMessage--收收收收--接收到一条【图片消息】---ThumUri--" + imageMessage.getLocalUri());
                Log.d("onReceived", "ImageMessage--收收收收--接收到一条【图片消息】----Uri--" + imageMessage.getRemoteUri());

                new Thread(new Runnable() {
                    @Override
                    public void run() {

                        mRongIMClient.downloadMedia(Conversation.ConversationType.PRIVATE, userId, RongIMClient.MediaType.IMAGE, imageMessage.getRemoteUri().toString(), new RongIMClient.DownloadMediaCallback() {

                            @Override
                            public void onProgress(int i) {
                                Log.d("downloadMedia", "onProgress:" + i);
                            }

                            @Override
                            public void onSuccess(String s) {
                                Log.d("downloadMedia", "onSuccess:" + s);
                            }

                            @Override
                            public void onError(RongIMClient.ErrorCode errorCode) {
                                Log.d("downloadMedia", "onError:" + errorCode.getValue());
                            }
                        });
                    }
                }).start();

            } else if (message.getContent() instanceof VoiceMessage) {

                final VoiceMessage voiceMessage = (VoiceMessage) message.getContent();

//                Log.d("onReceived", "VoiceMessage--收收收收--接收到一条【语音消息】-----" + voiceMessage.getUri());
                Log.e("onReceived", "VoiceMessage--收收收收--接收到一条【语音消息】 voiceMessage.getExtra-----" + voiceMessage.getExtra());

                new Thread(new Runnable() {

                    @Override
                    public void run() {

                        MediaPlayer mMediaPlayer = new MediaPlayer();
                        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                mp.start();
                            }
                        });

                        try {
                            mMediaPlayer.setDataSource(mContext, voiceMessage.getUri());
                            mMediaPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            } else if (message.getContent() instanceof GroupInvitationNotification) {

                GroupInvitationNotification groupInvitationNotification = (GroupInvitationNotification) message.getContent();

                Log.d("onReceived", "GroupInvitationNotification--收收收收--接收到一条【群组邀请消息】-----" + groupInvitationNotification.getMessage());

            } else if (message.getContent() instanceof ContactNotificationMessage) {
                ContactNotificationMessage mContactNotificationMessage = (ContactNotificationMessage) message.getContent();
                Log.d("onReceived", "mContactNotificationMessage--收收收收--接收到一条【联系人（好友）操作通知消息】-----" + mContactNotificationMessage.getMessage() + ",getExtra:" + mContactNotificationMessage.getExtra());

            } else if (message.getContent() instanceof ProfileNotificationMessage) {
                ProfileNotificationMessage mProfileNotificationMessage = (ProfileNotificationMessage) message.getContent();
                Log.d("onReceived", "GroupNotificationMessage--收收收收--接收到一条【资料变更通知消息】-----" + mProfileNotificationMessage.getData() + ",getExtra:" + mProfileNotificationMessage.getExtra());

            } else if (message.getContent() instanceof CommandNotificationMessage) {
                CommandNotificationMessage mCommandNotificationMessage = (CommandNotificationMessage) message.getContent();
                Log.d("onReceived", "GroupNotificationMessage--收收收收--接收到一条【命令通知消息】-----" + mCommandNotificationMessage.getData() + ",getName:" + mCommandNotificationMessage.getName());
            } else if (message.getContent() instanceof InformationNotificationMessage) {
                InformationNotificationMessage mInformationNotificationMessage = (InformationNotificationMessage) message.getContent();
                Log.d("onReceived", "InformationNotificationMessage--收收收收--接收到一条【小灰条消息】-----" + mInformationNotificationMessage.getMessage() + ",getName:" + mInformationNotificationMessage.getExtra());

            }


            return false;
        }

    };


}