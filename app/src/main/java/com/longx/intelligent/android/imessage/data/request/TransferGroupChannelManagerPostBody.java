package com.longx.intelligent.android.imessage.data.request;

/**
 * Created by LONG on 2025/6/20 at 5:22 AM.
 */
public class TransferGroupChannelManagerPostBody {
    private String toTransferGroupChannelId;
    private String transferToChannelId;

    public TransferGroupChannelManagerPostBody() {
    }

    public TransferGroupChannelManagerPostBody(String toTransferGroupChannelId, String transferToChannelId) {
        this.toTransferGroupChannelId = toTransferGroupChannelId;
        this.transferToChannelId = transferToChannelId;
    }

    public String getToTransferGroupChannelId() {
        return toTransferGroupChannelId;
    }

    public String getTransferToChannelId() {
        return transferToChannelId;
    }
}
