package com.longx.intelligent.android.imessage.net;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by LONG on 2024/10/28 at 6:37 AM.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServerLocation {
    private String host;
    private Integer port;
    private String baseUrl;

    public ServerLocation() {
    }

    public ServerLocation(String host, Integer port, String baseUrl) {
        this.host = host;
        this.port = port;
        this.baseUrl = baseUrl;
    }

    public String getHost() {
        return host;
    }

    public Integer getPort() {
        return port;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    @Override
    public String toString() {
        return "ServerLocation{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", baseUrl='" + baseUrl + '\'' +
                '}';
    }
}
