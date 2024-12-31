package com.longx.intelligent.android.ichat2.activity;

import android.os.Bundle;
import android.view.View;

import com.fasterxml.jackson.core.type.TypeReference;
import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.behaviorcomponents.AppUpdater;
import com.longx.intelligent.android.ichat2.data.Release;
import com.longx.intelligent.android.ichat2.data.ReleaseFile;
import com.longx.intelligent.android.ichat2.data.response.OperationData;
import com.longx.intelligent.android.ichat2.databinding.ActivityVersionBinding;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.IchatWebApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.UrlMapApiCaller;
import com.longx.intelligent.android.ichat2.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.ichat2.behaviorcomponents.MessageDisplayer;
import com.longx.intelligent.android.ichat2.util.AppUtil;
import com.longx.intelligent.android.ichat2.util.TimeUtil;
import com.longx.intelligent.android.ichat2.util.UiUtil;
import com.longx.intelligent.android.ichat2.util.Utils;

import org.apache.tika.utils.StringUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class VersionActivity extends BaseActivity {
    private ActivityVersionBinding binding;
    private String currentReleaseUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVersionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDefaultBackNavigation(binding.toolbar);
        init();
        showContent();
        fetchAndShowData();
        setupYiers();
    }

    private void init() {
        UiUtil.setViewEnabled(binding.linkButton, false, true);
        UiUtil.setIconMenuEnabled(binding.toolbar.getMenu().findItem(R.id.link), false);
    }

    private void showContent() {
        binding.thisVersionName.setText(AppUtil.getVersionName(this));
        binding.thisVersionCode.setText(String.valueOf(AppUtil.getVersionCode(this)));
    }

    private void fetchAndShowData() {
        UrlMapApiCaller.fetchIchatWebUpdatableReleaseDataUrl(this, new RetrofitApiCaller.BaseCommonYier<OperationData>(this, false){
            @Override
            public void start(Call<OperationData> call) {
                super.start(call);
                binding.updatableVersionLoadingIndicator.setVisibility(View.VISIBLE);
            }

            @Override
            public void notOk(int code, String message, Response<OperationData> raw, Call<OperationData> call) {
                super.notOk(code, message, raw, call);
                binding.updatableVersionLoadingIndicator.setVisibility(View.GONE);
                binding.updatableVersionLoadFailedText.setVisibility(View.VISIBLE);
                binding.updatableVersionLoadFailedText.setText("HTTP 状态码异常 > " + code);
            }

            @Override
            public void failure(Throwable t, Call<OperationData> call) {
                super.failure(t, call);
                binding.updatableVersionLoadingIndicator.setVisibility(View.GONE);
                binding.updatableVersionLoadFailedText.setVisibility(View.VISIBLE);
                binding.updatableVersionLoadFailedText.setText("出错了 > " + t.getClass().getName());
            }

            @Override
            public void complete(Call<OperationData> call) {
                super.complete(call);
                binding.updatableVersionLoadingIndicator.setVisibility(View.GONE);
            }

            @Override
            public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                super.ok(data, raw, call);
                UrlMapApiCaller.fetchIchatWebReleaseUrl(VersionActivity.this, AppUtil.getVersionCode(VersionActivity.this), new RetrofitApiCaller.BaseCommonYier<OperationData>(VersionActivity.this){
                    @Override
                    public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                        super.ok(data, raw, call);
                        data.commonHandleResult(VersionActivity.this, new int[]{}, () -> {
                            currentReleaseUrl = data.getData(String.class);
                            UiUtil.setIconMenuEnabled(binding.toolbar.getMenu().findItem(R.id.link), true);
                        });
                    }
                });
                data.commonHandleResult(VersionActivity.this, new int[]{}, () -> {
                    String updatableReleaseUrl = data.getData(String.class);
                    IchatWebApiCaller.fetchUpdatableReleaseData(VersionActivity.this, updatableReleaseUrl, new RetrofitApiCaller.BaseCommonYier<OperationData>(VersionActivity.this, false){
                        @Override
                        public void start(Call<OperationData> call) {
                            super.start(call);
                            binding.updatableVersionLoadingIndicator.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void notOk(int code, String message, Response<OperationData> raw, Call<OperationData> call) {
                            super.notOk(code, message, raw, call);
                            binding.updatableVersionLoadingIndicator.setVisibility(View.GONE);
                            binding.updatableVersionLoadFailedText.setVisibility(View.VISIBLE);
                            binding.updatableVersionLoadFailedText.setText("HTTP 状态码异常 > " + code);
                        }

                        @Override
                        public void failure(Throwable t, Call<OperationData> call) {
                            super.failure(t, call);
                            binding.updatableVersionLoadingIndicator.setVisibility(View.GONE);
                            binding.updatableVersionLoadFailedText.setVisibility(View.VISIBLE);
                            binding.updatableVersionLoadFailedText.setText("出错了 > " + t.getClass().getName());
                        }

                        @Override
                        public void complete(Call<OperationData> call) {
                            super.complete(call);
                            binding.updatableVersionLoadingIndicator.setVisibility(View.GONE);
                        }

                        @Override
                        public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                            data.commonHandleResult(VersionActivity.this, new int[]{}, () -> {
                                Release updatableRelease = data.getData(Release.class);
                                int appVersionCode = AppUtil.getVersionCode(VersionActivity.this);
                                if(appVersionCode < updatableRelease.getVersionCode()){
                                    binding.updateButton.setTag(updatableRelease);
                                    binding.updatableView.setVisibility(View.VISIBLE);
                                    binding.updatableVersion.setText(updatableRelease.getVersionName());
                                    binding.updatableVersionCode.setText(String.valueOf(updatableRelease.getVersionCode()));
                                    binding.releaseTime.setText(TimeUtil.formatRelativeTime(updatableRelease.getReleaseTime()));
                                    binding.updateNotes.setText(StringUtils.isEmpty(updatableRelease.getNotes()) ? "-" : updatableRelease.getNotes());
                                    UrlMapApiCaller.fetchIchatWebReleaseUrl(VersionActivity.this, updatableRelease.getVersionCode(), new RetrofitApiCaller.BaseCommonYier<OperationData>(VersionActivity.this){
                                        @Override
                                        public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                                            super.ok(data, raw, call);
                                            data.commonHandleResult(VersionActivity.this, new int[]{}, () -> {
                                                String releaseUrl = data.getData(String.class);
                                                binding.linkButton.setTag(releaseUrl);
                                                UiUtil.setViewEnabled(binding.linkButton, true, true);
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    });
                });
            }
        });
    }

    private void setupYiers() {
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.link){
                Utils.openBrowser(this, currentReleaseUrl);
            }
            return false;
        });
        binding.linkButton.setOnClickListener(v -> {
            String releaseUrl = (String) v.getTag();
            if(releaseUrl == null) return;
            Utils.openBrowser(this, releaseUrl);
        });
        binding.updateButton.setOnClickListener(v -> {
            Release updatableRelease = (Release) v.getTag();
            if(updatableRelease == null) return;
            UrlMapApiCaller.fetchIchatWebAllDownloadFilesUrl(this, updatableRelease.getVersionCode(), new RetrofitApiCaller.CommonYier<OperationData>(this){
                @Override
                public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                    super.ok(data, raw, call);
                    data.commonHandleResult(VersionActivity.this, new int[]{}, () -> {
                        String allDownloadFilesUrl = data.getData(String.class);
                        IchatWebApiCaller.fetchAllDownloadFiles(VersionActivity.this, allDownloadFilesUrl, new RetrofitApiCaller.CommonYier<OperationData>(VersionActivity.this){
                            @Override
                            public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                                super.ok(data, raw, call);
                                data.commonHandleResult(VersionActivity.this, new int[]{}, () -> {
                                    List<ReleaseFile> releaseFiles = data.getData(new TypeReference<List<ReleaseFile>>() {
                                    });
                                    String fileId = null;
                                    for (ReleaseFile releaseFile : releaseFiles) {
                                        if(releaseFile.getType() == ReleaseFile.TYPE_CLIENT_ANDROID){
                                            fileId = releaseFile.getFileId();
                                            break;
                                        }
                                    }
                                    if(fileId == null){
                                        MessageDisplayer.autoShow(VersionActivity.this, "未找到下载文件", MessageDisplayer.Duration.LONG);
                                        return;
                                    }
                                    UrlMapApiCaller.fetchIchatWebDownloadFileUrl(VersionActivity.this, fileId, new RetrofitApiCaller.CommonYier<OperationData>(VersionActivity.this){
                                        @Override
                                        public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                                            super.ok(data, raw, call);
                                            data.commonHandleResult(VersionActivity.this, new int[]{}, () -> {
                                                String downloadFileUrl = data.getData(String.class);
                                                AppUpdater appUpdater = new AppUpdater(VersionActivity.this, downloadFileUrl);
                                                appUpdater.start();
                                            });
                                        }
                                    });
                                });
                            }
                        });
                    });
                }
            });
        });
    }
}