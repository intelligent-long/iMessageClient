package com.longx.intelligent.android.imessage.activity;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationBarView;
import com.longx.intelligent.android.imessage.Application;
import com.longx.intelligent.android.imessage.R;
import com.longx.intelligent.android.imessage.activity.helper.ActivityOperator;
import com.longx.intelligent.android.imessage.activity.helper.BaseActivity;
import com.longx.intelligent.android.imessage.activity.settings.RootSettingsActivity;
import com.longx.intelligent.android.imessage.behaviorcomponents.GlideBehaviours;
import com.longx.intelligent.android.imessage.behaviorcomponents.GlobalBehaviors;
import com.longx.intelligent.android.imessage.behaviorcomponents.MessageDisplayer;
import com.longx.intelligent.android.imessage.behaviorcomponents.ContentUpdater;
import com.longx.intelligent.android.imessage.da.database.manager.ChannelDatabaseManager;
import com.longx.intelligent.android.imessage.da.database.manager.OpenedChatDatabaseManager;
import com.longx.intelligent.android.imessage.da.sharedpref.SharedPreferencesAccessor;
import com.longx.intelligent.android.imessage.data.OpenedChat;
import com.longx.intelligent.android.imessage.data.Self;
import com.longx.intelligent.android.imessage.databinding.ActivityMainBinding;
import com.longx.intelligent.android.imessage.dialog.ConfirmDialog;
import com.longx.intelligent.android.imessage.dialog.CustomViewMessageDialog;
import com.longx.intelligent.android.imessage.fragment.main.BroadcastsFragment;
import com.longx.intelligent.android.imessage.fragment.main.ChannelsFragment;
import com.longx.intelligent.android.imessage.fragment.main.MessagesFragment;
import com.longx.intelligent.android.imessage.net.dataurl.NetDataUrls;
import com.longx.intelligent.android.imessage.permission.SpecialPermissionOperator;
import com.longx.intelligent.android.imessage.permission.LinkPermissionOperatorActivity;
import com.longx.intelligent.android.imessage.permission.PermissionOperator;
import com.longx.intelligent.android.imessage.permission.PermissionRequirementChecker;
import com.longx.intelligent.android.imessage.permission.ToRequestPermissions;
import com.longx.intelligent.android.imessage.permission.ToRequestPermissionsItems;
import com.longx.intelligent.android.imessage.service.ServerMessageService;
import com.longx.intelligent.android.imessage.ui.BadgeDisplayer;
import com.longx.intelligent.android.imessage.util.ColorUtil;
import com.longx.intelligent.android.imessage.util.TimeUtil;
import com.longx.intelligent.android.imessage.util.UiUtil;
import com.longx.intelligent.android.imessage.util.WindowAndSystemUiUtil;
import com.longx.intelligent.android.imessage.yier.BroadcastFetchNewsYier;
import com.longx.intelligent.android.imessage.yier.GlobalYiersHolder;
import com.longx.intelligent.android.imessage.yier.ChangeUiYier;
import com.longx.intelligent.android.imessage.yier.NewContentBadgeDisplayYier;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import q.rorbin.badgeview.Badge;

public class MainActivity extends BaseActivity implements ContentUpdater.OnServerContentUpdateYier,
        ServerMessageService.OnOnlineStateChangeYier, View.OnClickListener, NewContentBadgeDisplayYier,
        LinkPermissionOperatorActivity, BroadcastFetchNewsYier {
    private ActivityMainBinding binding;
    private Badge messageNavBadge;
    private Badge channelNavBadge;
    private Badge broadcastNavBadge;
    private MenuItem lastBottomNavSelectedItem;
    private boolean showNavIconAnimation = true;
    private ValueAnimator navIconVisibilityPlusAnimator;
    private ValueAnimator navIconVisibilityMinusAnimator;
    private int bottomNavigationViewLabelVisibilityMode;
    private int bottomNavigationViewIconStyle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (checkAndSwitchToAuth()) return;
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        int mainActivityFragmentSwitchMode = SharedPreferencesAccessor.DefaultPref.getMainActivityFragmentSwitchMode(this);
        if (mainActivityFragmentSwitchMode == 1) {
            setupNavigation();
        } else {
            setupNavigationKeepInMemory();
        }
        setupYier();
        GlobalYiersHolder.holdYier(this, ContentUpdater.OnServerContentUpdateYier.class, this);
        GlobalYiersHolder.holdYier(this, ServerMessageService.OnOnlineStateChangeYier.class, this);
        GlobalYiersHolder.holdYier(this, NewContentBadgeDisplayYier.class, this,
                ID.MESSAGES, ID.CHANNEL_ADDITION_ACTIVITIES, ID.GROUP_CHANNEL_ADDITION_ACTIVITIES);
        GlobalYiersHolder.holdYier(this, BroadcastFetchNewsYier.class, this);
        new Thread(() -> {
            runOnUiThread(() -> {
                startServerMessageService();
                setupUi();
                showNavHeaderInfo();
                requestPermissions();
                GlobalBehaviors.checkAndNotifySoftwareUpdate(this);
            });
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(navIconVisibilityPlusAnimator != null) navIconVisibilityPlusAnimator.cancel();
        GlobalYiersHolder.removeYier(this, ContentUpdater.OnServerContentUpdateYier.class, this);
        GlobalYiersHolder.removeYier(this, ServerMessageService.OnOnlineStateChangeYier.class, this);
        GlobalYiersHolder.removeYier(this, NewContentBadgeDisplayYier.class, this,
                ID.MESSAGES, ID.CHANNEL_ADDITION_ACTIVITIES, ID.GROUP_CHANNEL_ADDITION_ACTIVITIES);
        GlobalYiersHolder.removeYier(this, BroadcastFetchNewsYier.class, this);
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
        if(showNavIconAnimation) {
            animateNavIconVisibility();
            showNavIconAnimation = false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        navIconVisibilityPlusAnimator.cancel();
        navIconVisibilityMinusAnimator.cancel();
    }

    private void requestPermissions(){
        if(SpecialPermissionOperator.isIgnoringBatteryOptimizations(this)){
            SharedPreferencesAccessor.DefaultPref.enableRequestIgnoreBatteryOptimize(this);
        }
        if(SharedPreferencesAccessor.DefaultPref.isRequestIgnoreBatteryOptimizeStateEnabled(this)){
            if(!SpecialPermissionOperator.isIgnoringBatteryOptimizations(this)){
                new ConfirmDialog(this, "取消此应用的电池用量限制，应用的功能才能全部正常运行")
                        .setPositiveButton("确定", (dialog, which) -> {
                            boolean success = SpecialPermissionOperator.requestIgnoreBatteryOptimizations(this);
                            if(!success){
                                MessageDisplayer.autoShow(this, "错误", MessageDisplayer.Duration.LONG);
                            }
                        })
                        .setNegativeButton("下次提醒", (dialog, which) -> {
                            showUserGuide();
                        })
                        .setNeutralButton("忽略", (dialog, which) -> {
                            SharedPreferencesAccessor.DefaultPref.disableRequestIgnoreBatteryOptimize(this);
                            showUserGuide();
                        })
                        .create().show();
            }
        }
        List<ToRequestPermissions> toRequestPermissionsList = new ArrayList<>();
        if (PermissionRequirementChecker.needNotificationPermission()) {
            if(!PermissionOperator.hasPermissions(this, ToRequestPermissionsItems.showNotification)){
                toRequestPermissionsList.add(ToRequestPermissionsItems.showNotification);
            }
        }
        if (PermissionRequirementChecker.needReadMediaImageAndVideoPermission()) {
            if(!PermissionOperator.hasPermissions(this, ToRequestPermissionsItems.readMediaImagesAndVideos)){
                toRequestPermissionsList.add(ToRequestPermissionsItems.readMediaImagesAndVideos);
            }
        } else if(PermissionRequirementChecker.needExternalStoragePermission()) {
            if (!PermissionOperator.hasPermissions(this, ToRequestPermissionsItems.writeAndReadExternalStorage)) {
                toRequestPermissionsList.add(ToRequestPermissionsItems.writeAndReadExternalStorage);
            }
        }
        new PermissionOperator(this, toRequestPermissionsList,
                new PermissionOperator.ShowCommonMessagePermissionResultCallback(this))
                .startRequestPermissions(this);
        if (!SpecialPermissionOperator.isExternalStorageManager()) {
            MessageDisplayer.showToast(this, "请允许本应用的所有文件访问权限", Toast.LENGTH_LONG);
            boolean success = SpecialPermissionOperator.requestManageExternalStorage(this);
            if(!success){
                MessageDisplayer.autoShow(this, "错误", MessageDisplayer.Duration.LONG);
            }
        }
    }

    private void showUserGuide() {
        if(!SharedPreferencesAccessor.DefaultPref.getUserGuideShowed(this)) {
            new CustomViewMessageDialog(this, getString(R.string.user_guide_info)).create().show();
            SharedPreferencesAccessor.DefaultPref.saveUserGuideShowed(this, true);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SpecialPermissionOperator.IGNORE_BATTERY_OPTIMIZATIONS_REQUEST_CODE){
            showUserGuide();
        }
    }

    private void startServerMessageService() {
        boolean loginState = SharedPreferencesAccessor.NetPref.getLoginState(this);
        if(loginState) {
            ServerMessageService.work((Application) getApplicationContext());
        }
    }

    public ActivityMainBinding getViewBinding(){
        return binding;
    }

    private void showNavHeaderInfo() {
        View headerView1 = binding.navigationDrawer1.getHeaderView(0);
        Self self = SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(this);
        ShapeableImageView avatarImageView = headerView1.findViewById(R.id.avatar);
        if (self.getAvatar() == null || self.getAvatar().getHash() == null) {
            GlideBehaviours.loadToImageView(getApplicationContext(), R.drawable.default_avatar, avatarImageView);
        } else {
            GlideBehaviours.loadToImageView(getApplicationContext(), NetDataUrls.getAvatarUrl(this, self.getAvatar().getHash()), avatarImageView);
        }
        String username = self.getUsername();
        String imessageIdUser = self.getImessageIdUser();
        String email = self.getEmail();
        Integer sex = self.getSex();
        String regionDesc = self.buildRegionDesc();
        ((TextView)headerView1.findViewById(R.id.username)).setText(username);
        ((TextView)headerView1.findViewById(R.id.imessage_id_user)).setText(imessageIdUser);
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
            intent.putExtra(ExtraKeys.IMESSAGE_ID, self.getImessageId());
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
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        NavHostFragment navHostFragment;
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if(fragment instanceof NavHostFragment){
                navHostFragment = (NavHostFragment) fragment;
                transaction.show(navHostFragment);
            }else {
                transaction.remove(fragment);
            }
        }
        transaction.runOnCommit(() -> {
            NavHostFragment navHostFragment1 = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container_view);
            NavController navController = Objects.requireNonNull(navHostFragment1).getNavController();
            navController.setGraph(R.navigation.main_navigation);
            NavigationUI.setupWithNavController(binding.bottomNavigation, navController);
            binding.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
                if (lastBottomNavSelectedItem != null
                        && lastBottomNavSelectedItem.getItemId() == item.getItemId()
                        && item.getItemId() == R.id.navigation_broadcast
                ) {
                    Fragment fragment = navHostFragment1.getChildFragmentManager().getFragments().get(0);
                    if (fragment instanceof BroadcastsFragment) {
                        ((BroadcastsFragment) fragment).toStart();
                    }
                    return true;
                }
                lastBottomNavSelectedItem = item;
                return NavigationUI.onNavDestinationSelected(item, navController);
            });
        });
        transaction.commit();
    }

    private void setupNavigationKeepInMemory() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if(fragments.size() == 1 && fragments.get(0) instanceof NavHostFragment){
            transaction.hide(fragments.get(0));
        }
        Fragment messageFragment = getSupportFragmentManager().findFragmentByTag(MessagesFragment.class.getSimpleName());
        if (messageFragment == null) {
            transaction.add(R.id.fragment_container_view, new MessagesFragment(), MessagesFragment.class.getSimpleName());
        }
        transaction.commit();
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            FragmentTransaction transaction1 = getSupportFragmentManager().beginTransaction();
            Fragment currentFragment = null;
            FragmentManager fragmentManager = getSupportFragmentManager();
            for (Fragment fragment : fragmentManager.getFragments()) {
                if(fragment instanceof NavHostFragment){
                    transaction1.hide(fragment);
                }else if (fragment != null && fragment.isVisible()) {
                    currentFragment = fragment;
                    break;
                }
            }
            Fragment targetFragment1 = null;
            if (item.getItemId() == R.id.navigation_message) {
                targetFragment1 = getSupportFragmentManager().findFragmentByTag(MessagesFragment.class.getSimpleName());
                if (targetFragment1 == null) {
                    targetFragment1 = new MessagesFragment();
                    transaction1.add(R.id.fragment_container_view, targetFragment1, MessagesFragment.class.getSimpleName());
                }
            } else if (item.getItemId() == R.id.navigation_channel) {
                targetFragment1 = getSupportFragmentManager().findFragmentByTag(ChannelsFragment.class.getSimpleName());
                if (targetFragment1 == null) {
                    targetFragment1 = new ChannelsFragment();
                    transaction1.add(R.id.fragment_container_view, targetFragment1, ChannelsFragment.class.getSimpleName());
                }
            } else if (item.getItemId() == R.id.navigation_broadcast) {
                targetFragment1 = getSupportFragmentManager().findFragmentByTag(BroadcastsFragment.class.getSimpleName());
                if (targetFragment1 == null) {
                    targetFragment1 = new BroadcastsFragment();
                    transaction1.add(R.id.fragment_container_view, targetFragment1, BroadcastsFragment.class.getSimpleName());
                }
            }
            if(currentFragment == targetFragment1 && currentFragment instanceof BroadcastsFragment){
                ((BroadcastsFragment) currentFragment).toStart();
            }else if (currentFragment != targetFragment1) {
                transaction1.setCustomAnimations(
                        R.anim.fragment_fade_in,
                        R.anim.fragment_fade_out,
                        R.anim.fragment_fade_in,
                        R.anim.fragment_fade_out
                );
                if(currentFragment != null) transaction1.hide(currentFragment);
                transaction1.show(targetFragment1);
                transaction1.commit();
            }
            return true;
        });
    }

    protected void animateNavIconVisibility(){
        final Drawable[] navIcon = new Drawable[1];
        navIconVisibilityPlusAnimator = ValueAnimator.ofInt(0, 30);
        navIconVisibilityPlusAnimator.setDuration(700);
        navIconVisibilityPlusAnimator.addUpdateListener(animation -> {
            navIcon[0].setAlpha((Integer) animation.getAnimatedValue());
            changeMainFragmentsNavIcon(navIcon[0]);
        });
        navIconVisibilityPlusAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                navIcon[0] = ContextCompat.getDrawable(MainActivity.this, R.drawable.menu_24px);
                navIcon[0].setAlpha(0);
            }

            @Override
            public void onAnimationEnd(Animator showAnim) {
                navIcon[0].setAlpha(navIcon[0].getAlpha());
                navIconVisibilityMinusAnimator = ValueAnimator.ofInt(navIcon[0].getAlpha(), 0);
                navIconVisibilityMinusAnimator.setDuration(800);
                navIconVisibilityMinusAnimator.addUpdateListener(hideAnim -> {
                    navIcon[0].setAlpha((Integer) hideAnim.getAnimatedValue());
                    changeMainFragmentsNavIcon(navIcon[0]);
                });
                navIconVisibilityMinusAnimator.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        changeMainFragmentsNavIcon(null);
                    }
                });
                navIconVisibilityMinusAnimator.start();
            }
        });
        navIconVisibilityPlusAnimator.start();
    }

    private void changeMainFragmentsNavIcon(Drawable navIcon) {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if(fragments.size() == 1 && fragments.get(0) instanceof NavHostFragment){
            fragments = fragments.get(0).getChildFragmentManager().getFragments();
        }
        fragments.forEach(fragment -> {
            if (fragment instanceof ChangeUiYier) {
                runOnUiThread(() -> ((ChangeUiYier) fragment).changeUi(ChangeUiYier.ID_HIDE_NAV_ICON, navIcon));
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
            Self self = SharedPreferencesAccessor.UserProfilePref.getCurrentUserProfile(this);
            if(self.getAvatar() != null && self.getAvatar().getHash() != null) {
                Intent intent = new Intent(this, AvatarActivity.class);
                intent.putExtra(ExtraKeys.IMESSAGE_ID, self.getImessageId());
                intent.putExtra(ExtraKeys.AVATAR_HASH, self.getAvatar().getHash());
                intent.putExtra(ExtraKeys.AVATAR_EXTENSION, self.getAvatar().getExtension());
                startActivity(intent);
            }
        });
    }

    private void setupUi() {
        boolean translucentNavigation = WindowAndSystemUiUtil.extendContentUnderSystemBars(this,
                null, new View[]{binding.navigationDrawer1},
                ColorUtil.getAttrColor(this, com.google.android.material.R.attr.colorSurfaceContainer));
        if(translucentNavigation) {
            UiUtil.setViewMargin(binding.onlineStateIndicator, -1, -1, -1, (int) (WindowAndSystemUiUtil.getNavigationBarHeight(this) / 2.0));
        }
        setupBottomNavigationViewLabelVisibility();
        setupBottomNavigationViewIconStyle();
    }

    private void setupBottomNavigationViewLabelVisibility() {
        int bottomNavigationViewLabelVisibilityMode = SharedPreferencesAccessor.DefaultPref.getBottomNavigationViewLabelVisibilityMode(this);
        if(bottomNavigationViewLabelVisibilityMode == this.bottomNavigationViewLabelVisibilityMode){
            return;
        }
        this.bottomNavigationViewLabelVisibilityMode = bottomNavigationViewLabelVisibilityMode;
        switch (bottomNavigationViewLabelVisibilityMode){
            case 0:
                binding.bottomNavigation.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_LABELED);
                break;
            case 1:
                binding.bottomNavigation.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_UNLABELED);
                break;
            case 2:
                binding.bottomNavigation.setLabelVisibilityMode(NavigationBarView.LABEL_VISIBILITY_SELECTED);
                break;
        }
    }

    private void setupBottomNavigationViewIconStyle(){
        int bottomNavigationViewIconStyle = SharedPreferencesAccessor.DefaultPref.getBottomNavigationViewIconStyle(this);
        if(bottomNavigationViewIconStyle == this.bottomNavigationViewIconStyle){
            return;
        }
        this.bottomNavigationViewIconStyle = bottomNavigationViewIconStyle;
        switch (bottomNavigationViewIconStyle){
            case 0:
                binding.bottomNavigation.getMenu().findItem(R.id.navigation_message).setIcon(R.drawable.selector_bottom_nav_message);
                binding.bottomNavigation.getMenu().findItem(R.id.navigation_channel).setIcon(R.drawable.selector_bottom_nav_channel);
                binding.bottomNavigation.getMenu().findItem(R.id.navigation_broadcast).setIcon(R.drawable.selector_bottom_nav_broadcast);
                break;
            case 1:
                binding.bottomNavigation.getMenu().findItem(R.id.navigation_message).setIcon(R.drawable.chat_fill_24px);
                binding.bottomNavigation.getMenu().findItem(R.id.navigation_channel).setIcon(R.drawable.contacts_fill_24px);
                binding.bottomNavigation.getMenu().findItem(R.id.navigation_broadcast).setIcon(R.drawable.wifi_tethering_24px);
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupBottomNavigationViewLabelVisibility();
        setupBottomNavigationViewIconStyle();
    }

    @Override
    public void onStartUpdate(String id, List<String> updatingIds, Object... objects) {
        runOnUiThread(() -> {
            if (!isFinishing() && binding != null) {
                binding.updateIndicator.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onUpdateComplete(String id, List<String> updatingIds, Object... objects) {
        runOnUiThread(() -> {
            try{
                if(updatingIds.size() == 0) binding.updateIndicator.setVisibility(View.GONE);
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
            @SuppressLint("RestrictedApi") View view = ((BottomNavigationMenuView) binding.bottomNavigation.getChildAt(0)).getChildAt(0);
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
            @SuppressLint("RestrictedApi") View view = ((BottomNavigationMenuView) binding.bottomNavigation.getChildAt(0)).getChildAt(1);
            channelNavBadge = BadgeDisplayer.initIndicatorBadge(this, view, Gravity.START | Gravity.BOTTOM, 73, 56, true);
        }
    }

    public synchronized void hideNavigationChannelBadge(){
        if(channelNavBadge != null) {
            channelNavBadge.hide(true);
            channelNavBadge = null;
        }
    }

    public synchronized void showNavigationBroadcastBadge(){
        if(broadcastNavBadge == null) {
            @SuppressLint("RestrictedApi") View view = ((BottomNavigationMenuView) binding.bottomNavigation.getChildAt(0)).getChildAt(2);
            broadcastNavBadge = BadgeDisplayer.initIndicatorBadge(this, view, Gravity.START | Gravity.BOTTOM, 73, 56, true);
        }
    }

    public synchronized void hideNavigationBroadcastBadge(){
        if(broadcastNavBadge != null) {
            broadcastNavBadge.hide(true);
            broadcastNavBadge = null;
        }
    }

    boolean showChannelBadge = false;
    boolean showGroupChannelBadge = false;
    boolean showBroadcastLikesBadge = false;
    boolean showBroadcastCommentsBadge = false;
    boolean showBroadcastRepliesBadge = false;

    @Override
    public void showNewContentBadge(ID id, int newContentCount) {
        switch (id){
            case MESSAGES:
                AtomicBoolean hideNavigationMessageBadge = new AtomicBoolean(true);
                List<OpenedChat> showOpenedChats = OpenedChatDatabaseManager.getInstance().findAllShow();
                showOpenedChats.forEach(showOpenedChat -> {
                    if(ChannelDatabaseManager.getInstance().findOneChannel(showOpenedChat.getChannelImessageId()) != null) {
                        if (showOpenedChat.getNotViewedCount() > 0)
                            hideNavigationMessageBadge.set(false);
                    }
                });
                if(hideNavigationMessageBadge.get()){
                    hideNavigationMessageBadge();
                }else {
                    showNavigationMessageBadge();
                }
                break;
            case GROUP_CHANNEL_ADDITION_ACTIVITIES:
                showGroupChannelBadge = newContentCount > 0;
                break;
            case CHANNEL_ADDITION_ACTIVITIES:
                showChannelBadge = newContentCount > 0;
                break;
            case BROADCAST_LIKES:
                showBroadcastLikesBadge = newContentCount > 0;
                break;
            case BROADCAST_COMMENTS:
                showBroadcastCommentsBadge = newContentCount > 0;
                break;
            case BROADCAST_REPLIES:
                showBroadcastRepliesBadge = newContentCount > 0;
                break;
        }
        if(showChannelBadge || showGroupChannelBadge){
            showNavigationChannelBadge();
        }else {
            hideNavigationChannelBadge();
        }
        if(showBroadcastLikesBadge || showBroadcastCommentsBadge || showBroadcastRepliesBadge){
            showNavigationBroadcastBadge();
        }else {
            hideNavigationBroadcastBadge();
        }
    }

    @Override
    public void fetchNews(String imessageId) {
        Fragment fragment = getSupportFragmentManager().getFragments().get(0);
        if (fragment instanceof BroadcastsFragment) {
            BroadcastsFragment.needFetchNewBroadcasts = false;
            ((BroadcastsFragment) fragment).fetchNews();
        }else {
            BroadcastsFragment.needFetchNewBroadcasts = true;
        }
    }
}