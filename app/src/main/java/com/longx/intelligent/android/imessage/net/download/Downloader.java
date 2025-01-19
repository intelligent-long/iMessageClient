package com.longx.intelligent.android.imessage.net.download;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import cn.zhxu.okhttps.Download;
import cn.zhxu.okhttps.HTTP;
import cn.zhxu.okhttps.HttpResult;
import cn.zhxu.okhttps.Process;

/**
 * Created by LONG on 2024/10/30 at 上午6:41.
 */
public class Downloader {
    private final HTTP HTTP = cn.zhxu.okhttps.HTTP.builder()
            .config(builder -> {
                builder
                        .callTimeout(60, TimeUnit.SECONDS)
                        .connectTimeout(60, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS)
                        .writeTimeout(60, TimeUnit.SECONDS);
            })
            .build();
    private final String url;
    private final String path;
    private Download.Ctrl ctrl;

    private Consumer<Process> progressYier;
    private Consumer<File> successYier;
    private Consumer<Download.Failure> failureYier;
    private Consumer<Download.Status> completeYier;

    public Downloader(String url, String path) {
        this.url = url;
        this.path = path;
    }

    public void start(){
        HttpResult httpResult = HTTP.async(url).get().getResult();
        ctrl = httpResult.getBody()
                .setOnProcess(progressYier)
                .toFile(path)
                .setOnSuccess(successYier)
                .setOnFailure(failureYier)
                .setOnComplete(completeYier)
                .start();
    }

    public void pause(){
        if(ctrl == null){
            throw new RuntimeException("下载还未开始");
        }
        ctrl.pause();
    }

    public void resume(){
        if(ctrl == null){
            throw new RuntimeException("下载还未开始");
        }
        ctrl.resume();
    }

    public void stop(){
        if(ctrl == null){
            throw new RuntimeException("下载还未开始");
        }
        ctrl.cancel();
    }

    public Downloader setProgressYier(Consumer<Process> progressYier) {
        this.progressYier = progressYier;
        return this;
    }

    public Downloader setSuccessYier(Consumer<File> successYier) {
        this.successYier = successYier;
        return this;
    }

    public Downloader setFailureYier(Consumer<Download.Failure> failureYier) {
        this.failureYier = failureYier;
        return this;
    }

    public Downloader setCompleteYier(Consumer<Download.Status> completeYier) {
        this.completeYier = completeYier;
        return this;
    }
}
