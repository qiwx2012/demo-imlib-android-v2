package io.rong.imlib.message;

import android.os.Parcel;

import io.rong.imlib.MessageTag;
import io.rong.message.CommandNotificationMessage;


@MessageTag(value = "RC:CmdNtf", flag = MessageTag.NONE)
public class DemoCommandNotificationMessage extends CommandNotificationMessage {

    public DemoCommandNotificationMessage(Parcel in) {
        super(in);
    }
}
