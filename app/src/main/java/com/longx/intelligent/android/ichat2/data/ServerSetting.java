package com.longx.intelligent.android.ichat2.data;

import com.longx.intelligent.android.ichat2.util.ErrorLogger;

/**
 * Created by LONG on 2024/1/15 at 6:54 PM.
 */
public class ServerSetting {
    private final boolean useCentral;
    private final String host;
    private final int port;
    private final String dataFolder;
    private static final String DATA_FOLDER_SUFFIX = "#";

    public static String buildDataFolderWithoutSuffix(String host, String port){
        if(port == null || port.equals("")){
            return host;
        }else {
            return host + "$" + port;
        }
    }

    public ServerSetting(boolean useCentral, String host, int port, String dataFolder, boolean dataFolderWithoutSuffix) {
        this.useCentral = useCentral;
        this.host = host;
        this.port = port;
        if(dataFolderWithoutSuffix) {
            this.dataFolder = dataFolder + DATA_FOLDER_SUFFIX;
        }else {
            this.dataFolder = dataFolder;
        }
    }

    public boolean isUseCentral() {
        return useCentral;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getDataFolder() {
        if(!dataFolder.endsWith(DATA_FOLDER_SUFFIX)) return null;
        return dataFolder;
    }

    public String getDataFolderWithoutSuffix() {
        if(!dataFolder.endsWith(DATA_FOLDER_SUFFIX)) return null;
        return dataFolder.substring(0, dataFolder.length() - DATA_FOLDER_SUFFIX.length());
    }
}
