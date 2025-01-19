package com.longx.intelligent.android.imessage.activity.edituser;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.data.AmapDistrict;
import com.longx.intelligent.android.imessage.data.Self;
import com.longx.intelligent.android.imessage.data.request.ChangeRegionPostBody;
import com.longx.intelligent.android.imessage.data.response.OperationData;
import com.longx.intelligent.android.imessage.data.response.OperationStatus;
import com.longx.intelligent.android.imessage.databinding.ActivityChangeRegionBinding;
import com.longx.intelligent.android.imessage.dialog.MessageDialog;
import com.longx.intelligent.android.imessage.net.retrofit.caller.RetrofitApiCaller;
import com.longx.intelligent.android.imessage.net.retrofit.caller.UserApiCaller;
import com.longx.intelligent.android.imessage.util.UiUtil;
import com.longx.intelligent.android.imessage.yier.AutoCompleteTextViewAutoSelectOnItemClickYier;
import com.longx.intelligent.android.imessage.yier.ResultsYier;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class ChangeRegionActivity extends BaseActivity {
    private ActivityChangeRegionBinding binding;
    private List<AmapDistrict> allFirstRegions;
    private List<AmapDistrict> allSecondRegions;
    private List<AmapDistrict> allThirdRegions;
    private Integer currentFirstRegionAdcode;
    private Integer currentSecondRegionAdcode;
    private Integer currentThirdRegionAdcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangeRegionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupToolbar();
        setupDefaultBackNavigation(binding.toolbar);
        setupRegionOnItemClickYiers();
        startFetchDataAndShow();
    }

    private void setupToolbar() {
        binding.toolbar.setOnMenuItemClickListener(item -> {
            if(item.getItemId() == R.id.change){
                ChangeRegionPostBody postBody = new ChangeRegionPostBody(currentFirstRegionAdcode, currentSecondRegionAdcode, currentThirdRegionAdcode);
                UserApiCaller.changeRegion(this, postBody, new RetrofitApiCaller.CommonYier<OperationStatus>(this){
                    @Override
                    public void ok(OperationStatus data, Response<OperationStatus> raw, Call<OperationStatus> call) {
                        super.ok(data, raw, call);
                        data.commonHandleResult(ChangeRegionActivity.this, new int[]{-101, -102, -103, -104, -105}, () -> {
                            new MessageDialog(ChangeRegionActivity.this, "修改成功").create().show();
                        });
                    }
                });
            }
            return true;
        });
    }

    private void startFetchDataAndShow() {
        setRegionLayoutsAndChangeMenuItemEnabled(false);
        fetchAndSetupFirstRegionAutoCompleteTextView(results -> {
            setRegionLayoutsAndChangeMenuItemEnabled(true);
        });
    }

    private void fetchAndSetupFirstRegionAutoCompleteTextView(ResultsYier resultsYier){
        UserApiCaller.fetchAllFirstRegions(this, new RetrofitApiCaller.DelayedShowDialogCommonYier<OperationData>(this, 2000, true){
            @Override
            public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                super.ok(data, raw, call);
                data.commonHandleResult(ChangeRegionActivity.this, new int[]{}, () -> {
                    allFirstRegions = data.getData(new TypeReference<List<AmapDistrict>>() {
                    });
                    List<String> allFirstRegionNames = new ArrayList<>();
                    if(allFirstRegions.size() == 0){
                        allFirstRegionNames.add("无");
                    }else {
                        allFirstRegionNames.add("不设置");
                        allFirstRegions.forEach(amapDistrict -> {
                            allFirstRegionNames.add(amapDistrict.getName());
                        });
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(ChangeRegionActivity.this,
                            R.layout.layout_auto_complete_text_view_text, allFirstRegionNames);
                    Self.Region firstRegion = SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(ChangeRegionActivity.this).getFirstRegion();
                    int position = 0;
                    if(firstRegion != null) {
                        for (int i = 0; i < allFirstRegions.size(); i++) {
                            if (allFirstRegions.get(i).getAdcode().equals(firstRegion.getAdcode())){
                                position = i + 1;
                            }
                        }
                    }
                    binding.firstRegionAutoCompleteTextView.setText(adapter.getItem(position));
                    binding.firstRegionAutoCompleteTextView.setAdapter(adapter);
                    resultsYier.onResults();
                    binding.firstRegionAutoCompleteTextView.getOnItemClickListener().onItemClick(null, null, position, -1);
                });
            }
        });
    }

    private void fetchAndSetupSecondRegionAutoCompleteTextView(Integer firstRegionAdcode, ResultsYier resultsYier){
        if(firstRegionAdcode == null){
            List<String> allSecondRegionNames = new ArrayList<>();
            allSecondRegionNames.add("请选择一级区域");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(ChangeRegionActivity.this,
                    R.layout.layout_auto_complete_text_view_text, allSecondRegionNames);
            int position = 0;
            binding.secondRegionAutoCompleteTextView.setText(adapter.getItem(position));
            binding.secondRegionAutoCompleteTextView.setAdapter(adapter);
            resultsYier.onResults();
            binding.secondRegionAutoCompleteTextView.getOnItemClickListener().onItemClick(null, null, position, -1);
        }else {
            UserApiCaller.fetchAllSecondRegions(this, firstRegionAdcode, new RetrofitApiCaller.DelayedShowDialogCommonYier<OperationData>(this, 2000, true){
                @Override
                public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                    super.ok(data, raw, call);
                    data.commonHandleResult(ChangeRegionActivity.this, new int[]{}, () -> {
                        allSecondRegions = data.getData(new TypeReference<List<AmapDistrict>>() {
                        });
                        List<String> allSecondRegionNames = new ArrayList<>();
                        if(allSecondRegions.size() == 0){
                            allSecondRegionNames.add("无");
                        }else {
                            allSecondRegionNames.add("不设置");
                            allSecondRegions.forEach(amapDistrict -> {
                                allSecondRegionNames.add(amapDistrict.getName());
                            });
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(ChangeRegionActivity.this,
                                R.layout.layout_auto_complete_text_view_text, allSecondRegionNames);
                        Self.Region secondRegion = SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(ChangeRegionActivity.this).getSecondRegion();
                        int position = 0;
                        if(secondRegion != null) {
                            for (int i = 0; i < allSecondRegions.size(); i++) {
                                if (allSecondRegions.get(i).getAdcode().equals(secondRegion.getAdcode())){
                                    position = i + 1;
                                }
                            }
                        }
                        binding.secondRegionAutoCompleteTextView.setText(adapter.getItem(position));
                        binding.secondRegionAutoCompleteTextView.setAdapter(adapter);
                        resultsYier.onResults();
                        binding.secondRegionAutoCompleteTextView.getOnItemClickListener().onItemClick(null, null, position, -1);
                    });
                }
            });
        }
    }

    private void fetchAndSetupThirdRegionAutoCompleteTextView(Integer secondRegionAdcode, ResultsYier resultsYier){
        if(secondRegionAdcode == null){
            List<String> allThirdRegionNames = new ArrayList<>();
            allThirdRegionNames.add("请选择二级区域");
            ArrayAdapter<String> adapter = new ArrayAdapter<>(ChangeRegionActivity.this,
                    R.layout.layout_auto_complete_text_view_text, allThirdRegionNames);
            int position = 0;
            binding.thirdRegionAutoCompleteTextView.setText(adapter.getItem(position));
            binding.thirdRegionAutoCompleteTextView.setAdapter(adapter);
            resultsYier.onResults();
            binding.thirdRegionAutoCompleteTextView.getOnItemClickListener().onItemClick(null, null, position, -1);
        }else {
            UserApiCaller.fetchAllThirdRegions(this, secondRegionAdcode, new RetrofitApiCaller.DelayedShowDialogCommonYier<OperationData>(this, 2000, true){
                @Override
                public void ok(OperationData data, Response<OperationData> raw, Call<OperationData> call) {
                    super.ok(data, raw, call);
                    data.commonHandleResult(ChangeRegionActivity.this, new int[]{}, () -> {
                        allThirdRegions = data.getData(new TypeReference<List<AmapDistrict>>() {
                        });
                        List<String> allThirdRegionNames = new ArrayList<>();
                        if(allThirdRegions.size() == 0){
                            allThirdRegionNames.add("无");
                        }else {
                            allThirdRegionNames.add("不设置");
                            allThirdRegions.forEach(amapDistrict -> {
                                allThirdRegionNames.add(amapDistrict.getName());
                            });
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(ChangeRegionActivity.this,
                                R.layout.layout_auto_complete_text_view_text, allThirdRegionNames);
                        Self.Region thirdRegion = SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(ChangeRegionActivity.this).getThirdRegion();
                        int position = 0;
                        if(thirdRegion != null) {
                            for (int i = 0; i < allThirdRegions.size(); i++) {
                                if (allThirdRegions.get(i).getAdcode().equals(thirdRegion.getAdcode())){
                                    position = i + 1;
                                }
                            }
                        }
                        binding.thirdRegionAutoCompleteTextView.setText(adapter.getItem(position));
                        binding.thirdRegionAutoCompleteTextView.setAdapter(adapter);
                        resultsYier.onResults();
                        binding.thirdRegionAutoCompleteTextView.getOnItemClickListener().onItemClick(null, null, position, -1);
                    });
                }
            });
        }
    }

    private void setupRegionOnItemClickYiers() {
        binding.firstRegionAutoCompleteTextView.setOnItemClickListener(new AutoCompleteTextViewAutoSelectOnItemClickYier(binding.firstRegionAutoCompleteTextView){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                super.onItemClick(parent, view, position, id);
                setRegionLayoutsAndChangeMenuItemEnabled(false);
                currentSecondRegionAdcode = null;
                currentThirdRegionAdcode = null;
                int realPosition = position - 1;
                if(realPosition < 0){
                    currentFirstRegionAdcode = null;
                }else {
                    currentFirstRegionAdcode = allFirstRegions.get(realPosition).getAdcode();
                }
                fetchAndSetupSecondRegionAutoCompleteTextView(currentFirstRegionAdcode, results -> {
                    setRegionLayoutsAndChangeMenuItemEnabled(true);
                });
            }
        });
        binding.secondRegionAutoCompleteTextView.setOnItemClickListener(new AutoCompleteTextViewAutoSelectOnItemClickYier(binding.secondRegionAutoCompleteTextView){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                super.onItemClick(parent, view, position, id);
                setRegionLayoutsAndChangeMenuItemEnabled(false);
                currentThirdRegionAdcode = null;
                int realPosition = position - 1;
                if(realPosition < 0){
                    currentSecondRegionAdcode = null;
                }else {
                    currentSecondRegionAdcode = allSecondRegions.get(realPosition).getAdcode();
                }
                fetchAndSetupThirdRegionAutoCompleteTextView(currentSecondRegionAdcode, results -> {
                    setRegionLayoutsAndChangeMenuItemEnabled(true);
                });
            }
        });
        binding.thirdRegionAutoCompleteTextView.setOnItemClickListener(new AutoCompleteTextViewAutoSelectOnItemClickYier(binding.thirdRegionAutoCompleteTextView) {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                super.onItemClick(parent, view, position, id);
                int realPosition = position - 1;
                if(realPosition < 0){
                    currentThirdRegionAdcode = null;
                }else {
                    currentThirdRegionAdcode = allThirdRegions.get(realPosition).getAdcode();
                }
            }
        });
    }

    private void setRegionLayoutsAndChangeMenuItemEnabled(boolean enable) {
        binding.firstRegionLayout.setEnabled(enable);
        binding.secondRegionLayout.setEnabled(enable);
        binding.thirdRegionLayout.setEnabled(enable);
        UiUtil.setIconMenuEnabled(binding.toolbar.getMenu().findItem(R.id.change), enable);
    }
}