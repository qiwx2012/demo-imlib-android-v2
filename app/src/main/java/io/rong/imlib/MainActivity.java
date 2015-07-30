package io.rong.imlib;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.rong.imlib.message.GroupInvitationNotification;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.MessageContent;
import io.rong.message.CommandNotificationMessage;
import io.rong.message.ContactNotificationMessage;
import io.rong.message.ImageMessage;
import io.rong.message.InformationNotificationMessage;
import io.rong.message.LocationMessage;
import io.rong.message.ProfileNotificationMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;


public class MainActivity extends Activity implements View.OnClickListener, Handler.Callback {

    //    public static final String TOKEN = "HtymJWYc8lTwfKgcAN9P57I6ZiT8q7s0UEaMPWY0lMw1SnA9yXU+KsOb5slbLWhxvJ6WgjQYA7h94DvkFpmc5g==0";//112
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String TOKEN = "0XRSHouXpDpdfcbjpUXKyqxeVjCC83e72XBjxjbFuNDeOqR3P30ymwoyIQZThs0aAXt1zs928r3o89zxiRZbdg==";//12345


    public static RongIMClient mRongIMClient;
    private Button connectButton;
    private Button button1;
    private Button button2;
    private Button button3;
    //    private Button button4;
    private Button reqFriendButton;
    private Button profileNotificationButton;
    private Button commandeNotificationButton;
    private Button informationNotificationButton;
    private String mUserId;
    private Handler mHandler;
    private Handler mWorkHandler;
    private final static int IMAGEMESSAGE = 1;
    private final static int VOICEMESSAGE = 2;
    /**
     * 接收方Id,用于测试
     */
    private String mUserIdTest = "26590";//114
//    private String mUserIdTest = "26600";//119


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        HandlerThread mHandlerThread = new HandlerThread("SendMessage");
        mHandlerThread.start();
        mWorkHandler = new Handler(mHandlerThread.getLooper());
        mHandler = new Handler(this);

        connectButton = (Button) findViewById(R.id.connect_button);
        button1 = (Button) findViewById(android.R.id.button1);
        button2 = (Button) findViewById(android.R.id.button2);
        button3 = (Button) findViewById(android.R.id.button3);
//        button4 = (Button) findViewById(R.id.group_invitation_notification);
        reqFriendButton = (Button) findViewById(R.id.req_friend_notification);
        profileNotificationButton = (Button) findViewById(R.id.profile_notification);
        commandeNotificationButton = (Button) findViewById(R.id.command_notification);
        informationNotificationButton = (Button) findViewById(R.id.information_notification);

        connectButton.setOnClickListener(this);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
//        button4.setOnClickListener(this);
        reqFriendButton.setOnClickListener(this);
        profileNotificationButton.setOnClickListener(this);
        commandeNotificationButton.setOnClickListener(this);
        informationNotificationButton.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.connect_button:

                try {

                    mRongIMClient = RongIMClient.connect(TOKEN, new RongIMClient.ConnectCallback() {
                        @Override
                        public void onTokenIncorrect() {
                            Log.e(TAG, "--connect--onTokenIncorrect-------");
                        }

                        @Override
                        public void onSuccess(String userId) {
                            Log.e(TAG, "--connect--onSuccess----userId---" + userId);
                            mUserId = userId;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "连接成功！", Toast.LENGTH_LONG).show();
                                    connectButton.setText("连接服务器成功!");
                                    mRongIMClient.joinChatRoom("chatroom001", 10, new RongIMClient.OperationCallback() {
                                        @Override
                                        public void onSuccess() {
                                            Log.e(TAG, "--joinChatRoom--onSuccess----chatroom001---");
                                        }

                                        @Override
                                        public void onError(RongIMClient.ErrorCode errorCode) {
                                            Log.e(TAG, "--joinChatRoom--onError----chatroom001---" + errorCode);
                                        }
                                    });
                                }
                            });
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode errorCode) {
                            Log.e(TAG, "--connect--errorCode-------" + errorCode);
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "连接失败！", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    });

                    DemoContext.getInstance().setRongIMClient(mRongIMClient);
                    DemoContext.getInstance().registerReceiveMessageListener();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;

            case android.R.id.button1:

                TextMessage textMessage = TextMessage.obtain("这是消息。。。。。。端午节快乐！！！！发送时间:" + System.currentTimeMillis());
                textMessage.setExtra("文字消息Extra");
                sendMessage(textMessage);

                break;
            case android.R.id.button2:
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {


                        mWorkHandler.post(new sendImageMessage());
                    }
                });

                break;
            case android.R.id.button3:

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {

                        mWorkHandler.post(new SendVoiceMessageRunnable());
                    }
                });

                break;
//            case R.id.group_invitation_notification:
//
//                GroupInvitationNotification group = new GroupInvitationNotification(mUserIdTest, "张三邀请你加入xxx群");
//                group.setMessage("张三邀请你加 PushContent");
//                sendMessage(group);
//
//                break;
            case R.id.req_friend_notification://联系人（好友）操作通知消息

                ContactNotificationMessage contact = ContactNotificationMessage.obtain(ContactNotificationMessage.CONTACT_OPERATION_REQUEST, mUserId, mUserIdTest, "请加我好友");
                contact.setExtra("I'm Bob");
                sendMessage(contact);

                break;
            case R.id.profile_notification://资料变更通知消息

                ProfileNotificationMessage profile = ProfileNotificationMessage.obtain(mUserIdTest, "资料变更数据");
                profile.setExtra("资料变更通知消息");
                sendMessage(profile);

                break;
            case R.id.command_notification://命令通知消息，可以实现任意指令操作

                CommandNotificationMessage command = CommandNotificationMessage.obtain("删除", "command delete");
                sendMessage(command);

                break;
            case R.id.information_notification:

                InformationNotificationMessage information = InformationNotificationMessage.obtain("I'm bob");
                information.setExtra("hehe");
                sendMessage(information);
                break;

            default:
                break;
        }

    }

    private void sendMessage(final MessageContent msg) {
        if (mRongIMClient != null) {
            mRongIMClient.sendMessage(Conversation.ConversationType.PRIVATE, "12345", msg, null, null, new RongIMClient.SendMessageCallback() {
                @Override
                public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {
                    Log.d("sendMessage", "----发发发发发--发送消息失败----ErrorCode----" + errorCode.getValue());
                }

                @Override
                public void onSuccess(Integer integer) {
                    if (msg instanceof TextMessage) {
                        TextMessage textMessage = (TextMessage) msg;
                        Log.d("sendMessage", "TextMessage---发发发发发--发送了一条【文字消息】-----" + textMessage.getContent());
                    } else if (msg instanceof ImageMessage) {
                        ImageMessage imageMessage = (ImageMessage) msg;
                        Log.d("sendMessage", "ImageMessage--发发发发发--发送了一条【图片消息】--uri---" + imageMessage.getThumUri());
                    } else if (msg instanceof VoiceMessage) {
                        VoiceMessage voiceMessage = (VoiceMessage) msg;
                        Log.e("sendMessage", "VoiceMessage--发发发发发--发送了一条【语音消息】---getExtra--" + voiceMessage.getExtra());
                        Log.d("sendMessage", "VoiceMessage--发发发发发--发送了一条【语音消息】--长度---" + voiceMessage.getDuration());
                    } else if (msg instanceof LocationMessage) {
                        LocationMessage location = (LocationMessage) msg;
                        Log.d("sendMessage", "VoiceMessage--发发发发发--发送了一条【语音消息】---uri--" + location.getPoi());
                    } else if (msg instanceof GroupInvitationNotification) {
                        GroupInvitationNotification groupInvitationNotification = (GroupInvitationNotification) msg;
                        Log.d("sendMessage", "VoiceMessage--发发发发发--发送了一条【群组邀请消息】---message--" + groupInvitationNotification.getMessage());
                    } else if (msg instanceof ContactNotificationMessage) {
                        ContactNotificationMessage mContactNotificationMessage = (ContactNotificationMessage) msg;
                        Log.d("sendMessage", "ContactNotificationMessage--发发发发发--发送了一条【联系人（好友）操作通知消息】---message--" + mContactNotificationMessage.getMessage());
                    } else if (msg instanceof ProfileNotificationMessage) {
                        ProfileNotificationMessage mProfileNotificationMessage = (ProfileNotificationMessage) msg;
                        Log.d("sendMessage", "ProfileNotificationMessage--发发发发发--发送了一条【资料变更通知消息】---message--" + mProfileNotificationMessage.getData());
                    } else if (msg instanceof CommandNotificationMessage) {
                        CommandNotificationMessage mCommandNotificationMessage = (CommandNotificationMessage) msg;
                        Log.d("sendMessage", "CommandNotificationMessage--发发发发发--发送了一条【命令通知消息】---message--" + mCommandNotificationMessage.getData());
                    } else if (msg instanceof InformationNotificationMessage) {
                        InformationNotificationMessage mInformationNotificationMessage = (InformationNotificationMessage) msg;
                        Log.d("sendMessage", "InformationNotificationMessage--发发发发发--发送了一条【小灰条消息】---message--" + mInformationNotificationMessage.getMessage());

                    }
                }
            });


        } else {
            Toast.makeText(this, "请先连接。。。", Toast.LENGTH_LONG).show();
        }

    }


    @Override
    public boolean handleMessage(Message msg) {

        if (msg.what == IMAGEMESSAGE) {
            ImageMessage imageMessage = (ImageMessage) msg.obj;
            sendMessage(imageMessage);
        } else if (msg.what == VOICEMESSAGE) {

        }

        return false;
    }

    private class SendVoiceMessageRunnable implements Runnable {

        @Override
        public void run() {

            try {
                File voiceFile = new File(getCacheDir(), "voice.amr");
                try {
                    // 读取音频文件。
                    InputStream is = getResources().openRawResource(R.raw.huihui);
                    OutputStream os = new FileOutputStream(voiceFile);
                    byte[] buffer = new byte[1024];

                    int bytesRead;

                    // 写入缓存文件。
                    while ((bytesRead = is.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                    is.close();
                    os.flush();
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                VoiceMessage vocMsg = VoiceMessage.obtain(Uri.fromFile(voiceFile), 10);
                sendMessage(vocMsg);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class sendImageMessage implements Runnable {

        @Override
        public void run() {
            File imageFileSource = new File(getCacheDir(), "source.jpg");
            File imageFileThumb = new File(getCacheDir(), "thumb.jpg");

            try {
                // 读取图片。
                InputStream is = getResources().openRawResource(R.raw.pics);

                Bitmap bmpSource = BitmapFactory.decodeStream(is);

                imageFileSource.createNewFile();

                FileOutputStream fosSource = new FileOutputStream(imageFileSource);

                // 保存原图。
                bmpSource.compress(Bitmap.CompressFormat.JPEG, 100, fosSource);

                // 创建缩略图变换矩阵。
                Matrix m = new Matrix();
                m.setRectToRect(new RectF(0, 0, bmpSource.getWidth(), bmpSource.getHeight()), new RectF(0, 0, 160, 160), Matrix.ScaleToFit.CENTER);

                // 生成缩略图。
                Bitmap bmpThumb = Bitmap.createBitmap(bmpSource, 0, 0, bmpSource.getWidth(), bmpSource.getHeight(), m, true);

                imageFileThumb.createNewFile();

                FileOutputStream fosThumb = new FileOutputStream(imageFileThumb);

                // 保存缩略图。
                bmpThumb.compress(Bitmap.CompressFormat.JPEG, 60, fosThumb);

            } catch (IOException e) {
                e.printStackTrace();
            }

            ImageMessage imgMsg = ImageMessage.obtain(Uri.fromFile(imageFileThumb), Uri.fromFile(imageFileSource));

            sendMessage(imgMsg);
        }
    }

}