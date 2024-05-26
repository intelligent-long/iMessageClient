package com.longx.intelligent.android.ichat2.activity;

import androidx.activity.OnBackPressedCallback;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.imageview.ShapeableImageView;
import com.longx.intelligent.android.ichat2.Application;
import com.longx.intelligent.android.ichat2.R;
import com.longx.intelligent.android.ichat2.activity.helper.ActivityOperator;
import com.longx.intelligent.android.ichat2.activity.helper.BaseActivity;
import com.longx.intelligent.android.ichat2.activity.settings.RootSettingsActivity;
import com.longx.intelligent.android.ichat2.behavior.GlideBehaviours;
import com.longx.intelligent.android.ichat2.behavior.MessageDisplayer;
import com.longx.intelligent.android.ichat2.behavior.ContentUpdater;
import com.longx.intelligent.android.ichat2.da.database.manager.OpenedChatDatabaseManager;
import com.longx.intelligent.android.ichat2.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.ichat2.data.OpenedChat;
import com.longx.intelligent.android.ichat2.data.Self;
import com.longx.intelligent.android.ichat2.databinding.ActivityMainBinding;
import com.longx.intelligent.android.ichat2.dialog.ConfirmDialog;
import com.longx.intelligent.android.ichat2.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.ichat2.permission.BatteryRestrictionOperator;
import com.longx.intelligent.android.ichat2.permission.LinkPermissionOperatorActivity;
import com.longx.intelligent.android.ichat2.permission.PermissionOperator;
import com.longx.intelligent.android.ichat2.permission.PermissionUtil;
import com.longx.intelligent.android.ichat2.permission.ToRequestPermissionsItems;
import com.longx.intelligent.android.ichat2.service.ServerMessageService;
import com.longx.intelligent.android.ichat2.ui.BadgeDisplayer;
import com.longx.intelligent.android.ichat2.util.ColorUtil;
import com.longx.intelligent.android.ichat2.util.ErrorLogger;
import com.longx.intelligent.android.ichat2.util.TimeUtil;
import com.longx.intelligent.android.ichat2.util.UiUtil;
import com.longx.intelligent.android.ichat2.util.WindowAndSystemUiUtil;
import com.longx.intelligent.android.ichat2.yier.GlobalYiersHolder;
import com.longx.intelligent.android.ichat2.yier.ChangeUiYier;
import com.longx.intelligent.android.ichat2.yier.NewContentBadgeDisplayYier;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import q.rorbin.badgeview.Badge;

public class MainActivity extends BaseActivity implements ContentUpdater.OnServerContentUpdateYier,
        ServerMessageService.OnOnlineStateChangeYier, View.OnClickListener, NewContentBadgeDisplayYier,
        LinkPermissionOperatorActivity {
    private ActivityMainBinding binding;
    private NavHostFragment navHostFragment;
    private Badge messageNavBadge;
    private Badge channelNavBadge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (checkAndSwitchToAuth()) return;
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container_view);
        setContentView(binding.getRoot());
        setupNavigation();
        startServerMessageService();
        setupYier();
        setupUi();
        showNavHeaderInfo();
        GlobalYiersHolder.holdYier(this, ContentUpdater.OnServerContentUpdateYier.class, this);
        GlobalYiersHolder.holdYier(this, ServerMessageService.OnOnlineStateChangeYier.class, this);
        GlobalYiersHolder.holdYier(this, NewContentBadgeDisplayYier.class, this, ID.MESSAGES);
        GlobalYiersHolder.holdYier(this, NewContentBadgeDisplayYier.class, this, ID.CHANNEL_ADDITION_ACTIVITIES);
        animateNavIconVisibility(navHostFragment);
        requestPermissions();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        GlobalYiersHolder.removeYier(this, ContentUpdater.OnServerContentUpdateYier.class, this);
        GlobalYiersHolder.removeYier(this, ServerMessageService.OnOnlineStateChangeYier.class, this);
        GlobalYiersHolder.removeYier(this, NewContentBadgeDisplayYier.class, this, ID.MESSAGES);
        GlobalYiersHolder.removeYier(this, NewContentBadgeDisplayYier.class, this, ID.CHANNEL_ADDITION_ACTIVITIES);
    }

    private void requestPermissions(){
        if(BatteryRestrictionOperator.isIgnoringBatteryOptimizations(this)){
            SharedPreferencesAccessor.DefaultPref.enableRequestIgnoreBatteryOptimize(this);
        }
        if(SharedPreferencesAccessor.DefaultPref.isRequestIgnoreBatteryOptimizeStateEnabled(this)){
            if(!BatteryRestrictionOperator.isIgnoringBatteryOptimizations(this)){
                new ConfirmDialog(this, "取消此应用的电池用量限制，应用的功能才能全部正常运行")
                        .setPositiveButton("确定", (dialog, which) -> {
                            boolean success = BatteryRestrictionOperator.requestIgnoreBatteryOptimizations(this);
                            if(!success){
                                MessageDisplayer.autoShow(this, "错误", MessageDisplayer.Duration.LONG);
                            }
                        })
                        .setNegativeButton("下次提醒", (dialog, which) -> {
                        })
                        .setNeutralButton("忽略", (dialog, which) -> {
                            SharedPreferencesAccessor.DefaultPref.disableRequestIgnoreBatteryOptimize(this);
                        })
                        .show();
            }
        }
        if (PermissionUtil.needNotificationPermission()) {
            if(!PermissionOperator.hasPermissions(this, ToRequestPermissionsItems.showNotification)){
                new PermissionOperator(this, ToRequestPermissionsItems.showNotification,
                        new PermissionOperator.ShowCommonMessagePermissionResultCallback(this))
                        .requestPermissions(this);
            }
        }
        if (PermissionUtil.needReadMediaImageAndVideoPermission()) {
            if(!PermissionOperator.hasPermissions(this, ToRequestPermissionsItems.readMediaImagesAndVideos)){
                new PermissionOperator(this, ToRequestPermissionsItems.readMediaImagesAndVideos,
                        new PermissionOperator.ShowCommonMessagePermissionResultCallback(this))
                        .requestPermissions(this);
            }
        } else if(PermissionUtil.needExternalStoragePermission()) {
            if (!PermissionOperator.hasPermissions(this, ToRequestPermissionsItems.writeAndReadExternalStorage)) {
                new PermissionOperator(this, ToRequestPermissionsItems.writeAndReadExternalStorage,
                        new PermissionOperator.ShowCommonMessagePermissionResultCallback(this))
                        .requestPermissions(this);
            }
        }
    }

    private void startServerMessageService() {
        boolean loginState = SharedPreferencesAccessor.NetPref.getLoginState(this);
        if(loginState) {
            ServerMessageService.work((Application) getApplicationContext());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    binding.drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    public ActivityMainBinding getViewBinding(){
        return binding;
    }

    private void showNavHeaderInfo() {
        View headerView1 = binding.navigationDrawer1.getHeaderView(0);
        Self self = SharedPreferencesAccessor.UserInfoPref.getCurrentUserInfo(this);
        ShapeableImageView avatarImageView = headerView1.findViewById(R.id.avatar);
        if (self.getAvatar() == null || self.getAvatar().getHash() == null) {
            GlideBehaviours.loadToImageView(getApplicationContext(), R.drawable.default_avatar, avatarImageView);
        } else {
            GlideBehaviours.loadToImageView(getApplicationContext(), NetDataUrls.getAvatarUrl(this, self.getAvatar().getHash()), avatarImageView);
        }
        String username = self.getUsername();
        String ichatIdUser = self.getIchatIdUser();
        String email = self.getEmail();
        Integer sex = self.getSex();
        String regionDesc = self.buildRegionDesc();
        ((TextView)headerView1.findViewById(R.id.username)).setText(username);
        ((TextView)headerView1.findViewById(R.id.ichat_id_user)).setText(ichatIdUser);
        ((TextView)headerView1.findViewById(R.id.email)).setText(email);
        RelativeLayout sexLayout = headerView1.findViewById(R.id.layout_sex);
        ImageView sexImageView = headerView1.findViewById(R.id.sex);
        RelativeLayout regionLayout = headerView1.findViewById(R.id.layout_region);
        TextView regionTextView = headerView1.findViewById(R.id.region);
        if(sex == null || (sex != 0 && sex != 1)){
            sexLayout.setVisibility(View.GONE);
        }else {
            sexLayout.setVisibility(View.VISIBLE);
            if(sex == 0){
                sexImageView.setImageResource(R.drawable.female_24px);
            }else {
                sexImageView.setImageResource(R.drawable.male_24px);
            }
        }
        if(regionDesc == null){
            regionLayout.setVisibility(View.GONE);
        }else {
            regionLayout.setVisibility(View.VISIBLE);
            regionTextView.setText(regionDesc);
        }
        headerView1.findViewById(R.id.user_info_page).setOnClickListener(v -> {
            Intent intent = new Intent(this, ChannelActivity.class);
            intent.putExtra(ExtraKeys.ICHAT_ID, self.getIchatId());
            startActivity(intent);
        });
    }

    private boolean checkAndSwitchToAuth() {
        boolean loginState = SharedPreferencesAccessor.NetPref.getLoginState(this);
        if (!loginState) {
            ActivityOperator.switchToAuth(this);
            return true;
        }
        return false;
    }

    private void setupNavigation() {
        NavController navController = Objects.requireNonNull(navHostFragment).getNavController();
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
    }

    protected void animateNavIconVisibility(NavHostFragment navHostFragment){
        final Drawable[] navIcon = {ContextCompat.getDrawable(this, R.drawable.menu_24px)};
        if(navIcon[0] == null) return;
        navIcon[0].setAlpha(0);
        ValueAnimator animator = ValueAnimator.ofInt(navIcon[0].getAlpha(), 30);
        animator.setDuration(700);
        animator.addUpdateListener(animation -> {
            navIcon[0].setAlpha((Integer) animation.getAnimatedValue());
            changeMainFragmentsNavIcon(navHostFragment, navIcon[0]);
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator showAnim) {
                if (navIcon[0] == null) return;
                navIcon[0].setAlpha(navIcon[0].getAlpha());
                ValueAnimator animator = ValueAnimator.ofInt(navIcon[0].getAlpha(), 0);
                animator.setDuration(800);
                animator.addUpdateListener(hideAnim -> {
                    navIcon[0].setAlpha((Integer) hideAnim.getAnimatedValue());
                    changeMainFragmentsNavIcon(navHostFragment, navIcon[0]);
                });
                animator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        navIcon[0] = null;
                        changeMainFragmentsNavIcon(navHostFragment, null);
                    }
                });
                animator.start();
            }
        });
        animator.start();
    }

    private void changeMainFragmentsNavIcon(NavHostFragment navHostFragment, Drawable navIcon) {
        if (!navHostFragment.isAdded()) return;
        navHostFragment.getChildFragmentManager().getFragments().forEach(fragment -> {
            if (fragment instanceof ChangeUiYier) {
                runOnUiThread(() -> ((ChangeUiYier) fragment).changeUi("hide_nav_icon", navIcon));
            }
        });
    }

    private void setupYier() {
        binding.navigationDrawer2.setNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.nav_settings) {
                Intent intent = new Intent(MainActivity.this, RootSettingsActivity.class);
                intent.putExtra(ExtraKeys.NEED_RESTORE_INSTANCE_STATE, false);
                startActivity(intent);
            }
            return true;
        });
        View headerView = binding.navigationDrawer1.getHeaderView(0);
        ShapeableImageView avatarImageView = headerView.findViewById(R.id.avatar);
        avatarImageView.setOnClickListener(v -> {
            Self self = SharedPreferencesAccessor.UserInfoPref.getCurrentUserInfo(this);
            if(self.getAvatar() != null && self.getAvatar().getHash() != null) {
                Intent intent = new Intent(this, AvatarActivity.class);
                intent.putExtra(ExtraKeys.ICHAT_ID, self.getIchatId());
                intent.putExtra(ExtraKeys.AVATAR_HASH, self.getAvatar().getHash());
                intent.putExtra(ExtraKeys.AVATAR_EXTENSION, self.getAvatar().getExtension());
                startActivity(intent);
            }
        });
    }

    private void setupUi() {
        boolean translucentNavigation = WindowAndSystemUiUtil.checkAndExtendContentUnderSystemBars(this,
                null, new View[]{binding.navigationDrawer1},
                ColorUtil.getAttrColor(this, com.google.android.material.R.attr.colorSurfaceContainer));
        if(translucentNavigation) {
            UiUtil.setViewMargin(binding.onlineStateIndicator, -1, -1, -1, (int) (WindowAndSystemUiUtil.getNavigationBarHeight(this) / 2.0));
        }
    }

    @Override
    public void onStartUpdate(String id) {
        runOnUiThread(() -> {
            if (!isFinishing() && binding != null) {
                binding.updateIndicator.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onUpdateComplete(String id) {
        runOnUiThread(() -> {
            try{
                binding.updateIndicator.setVisibility(View.GONE);
            }catch (NullPointerException ignore){}
            if(id.equals(ContentUpdater.OnServerContentUpdateYier.ID_CURRENT_USER_INFO)){
                showNavHeaderInfo();
            }
        });
    }

    @Override
    public void onOnline() {
        runOnUiThread(() -> {
            try{
                binding.onlineStateIndicator.setVisibility(View.GONE);
                View headerView1 = binding.navigationDrawer1.getHeaderView(0);
                headerView1.findViewById(R.id.layout_offline_time).setVisibility(View.GONE);
            }catch (NullPointerException ignore){}
        });
    }

    @Override
    public void onOffline() {
        runOnUiThread(() -> {
            try{
                binding.onlineStateIndicator.setVisibility(View.VISIBLE);
                View headerView1 = binding.navigationDrawer1.getHeaderView(0);
                TextView offlineTimeTextView = headerView1.findViewById(R.id.offline_time);
                long offlineTime = SharedPreferencesAccessor.NetPref.getOfflineTime(this);
                String formattedOfflineTime = TimeUtil.formatRelativeTime(new Date(offlineTime));
                offlineTimeTextView.setText(formattedOfflineTime);
                headerView1.findViewById(R.id.layout_offline_time).setVisibility(View.VISIBLE);
            }catch (NullPointerException ignore){}
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.start_chat_fab){
            binding.bottomNavigation.setSelectedItemId(R.id.navigation_channel);
        }
    }

    public synchronized void showNavigationMessageBadge(){
        if(messageNavBadge == null) {
            View view = ((BottomNavigationMenuView) binding.bottomNavigation.getChildAt(0)).getChildAt(0);
            messageNavBadge = BadgeDisplayer.initIndicatorBadge(this, view, Gravity.START | Gravity.BOTTOM, 73, 56, true);
        }
    }

    public synchronized void hideNavigationMessageBadge(){
        if(messageNavBadge != null) {
            messageNavBadge.hide(true);
            messageNavBadge = null;
        }
    }

    public synchronized void showNavigationChannelBadge(){
        if(channelNavBadge == null) {
            View view = ((BottomNavigationMenuView) binding.bottomNavigation.getChildAt(0)).getChildAt(1);
            channelNavBadge = BadgeDisplayer.initIndicatorBadge(this, view, Gravity.START | Gravity.BOTTOM, 73, 56, true);
        }
    }

    public synchronized void hideNavigationChannelBadge(){
        if(channelNavBadge != null) {
            channelNavBadge.hide(true);
            channelNavBadge = null;
        }
    }

    @Override
    public void showNewContentBadge(ID id, int newContentCount) {
        switch (id){
            case MESSAGES:
                AtomicBoolean hideNavigationMessageBadge = new AtomicBoolean(true);
                List<OpenedChat> showOpenedChats = OpenedChatDatabaseManager.getInstance().findAllShow();
                showOpenedChats.forEach(showOpenedChat -> {
                    if(showOpenedChat.getNotViewedCount() > 0) hideNavigationMessageBadge.set(false);
                });
                if(hideNavigationMessageBadge.get()){
                    hideNavigationMessageBadge();
                }else {
                    showNavigationMessageBadge();
                }
                break;
            case CHANNEL_ADDITION_ACTIVITIES:
                if(newContentCount > 0) {
                    showNavigationChannelBadge();
                } else {
                    hideNavigationChannelBadge();
                }
                break;
        }
    }
}