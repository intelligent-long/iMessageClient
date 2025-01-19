package com.longx.intelligent.android.imessage.net;

/**
 * Created by LONG on 2024/1/15 at 6:54 PM.
 */
public class ServerConfig {
    private final String host;
    private final int port;
    private final String baseUrl;
    private final String dataFolder;
    public static final String DATA_FOLDER_SUFFIX = "#";

    public static String buildDataFolderWithoutSuffix(String host, String port){
        if(port == null || port.isEmpty()){
            return host;
        }else {
            return host + "$" + port;
        }
    }

    public ServerConfig(String host, int port, String baseUrl, String dataFolder) {
        this.host = host;
        this.port = port;
        this.baseUrl = baseUrl;
        this.dataFolder = dataFolder;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getDataFolder() {
        return dataFolder;
    }

    public String getDataFolderWithoutSuffix() {
        return dataFolder.substring(0, dataFolder.length() - DATA_FOLDER_SUFFIX.length());
    }
}
