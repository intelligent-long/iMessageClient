package com.longx.intelligent.android.imessage.data;

/**
 * Created by LONG on 2024/10/27 at 7:45 AM.
 */
public class ReleaseFile {
    public static int TYPE_CLIENT_ANDROID = 1;
    public static int TYPE_SERVER = -1;

    private String fileId;
    private int type;
    private int versionCode;
    private String notes;
    private String fileName;

    public ReleaseFile() {
    }

    public ReleaseFile(String fileId, int type, int versionCode, String notes, String fileName) {
        this.fileId = fileId;
        this.type = type;
        this.versionCode = versionCode;
        this.notes = notes;
        this.fileName = fileName;
    }

    public String getFileId() {
        return fileId;
    }

    public int getType() {
        return type;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public String getNotes() {
        return notes;
    }

    public String getFileName() {
        return fileName;
    }
}
