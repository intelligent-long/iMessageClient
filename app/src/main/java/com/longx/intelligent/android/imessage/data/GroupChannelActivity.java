package com.longx.intelligent.android.imessage.data;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Date;

/**
 * Created by LONG on 2025/5/14 at 11:28 PM.
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = GroupChannelAddition.class, name = "GroupChannelAddition"),
        @JsonSubTypes.Type(value = GroupChannelInvitation.class, name = "GroupChannelInvitation")
})
public interface GroupChannelActivity {
    String getUuid();
    String getMessage();
    Date getRequestTime();
    Date getRespondTime();
    boolean isAccepted();
    boolean isViewed();
    boolean isExpired();
}
