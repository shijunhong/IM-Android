package net.sjh.italker.push.assist;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.sjh.italker.common.app.Application;
import net.sjh.italker.push.R;
import net.sjh.italker.push.frags.media.GalleryFragment;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 权限申请弹出框
 */
public class PermissionsFragment extends BottomSheetDialogFragment implements EasyPermissions.PermissionCallbacks{
    //权限回调标识
    private static final int RC = 0x0100;


    public PermissionsFragment() {
        // Required empty public constructor
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //直接复用
        return new GalleryFragment.TransStatusBottomSheetDialog(getContext());
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 获取布局中的空间
        View root = inflater.inflate(R.layout.fragment_permissions, container, false);
        refreshState(root);
        root.findViewById(R.id.btn_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPerm();
            }
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshState(getView());

    }

    /**
     * 刷新我们的布局中的图片状态
     *
     * @param root 根布局
     */
    private void refreshState(View root) {
        Context context = getContext();
        root.findViewById(R.id.im_state_permission_network).setVisibility(haveNetwork(context) ? View.VISIBLE : View.GONE);
        root.findViewById(R.id.im_state_permission_read).setVisibility(haveRead(context) ? View.VISIBLE : View.GONE);
        root.findViewById(R.id.im_state_permission_write).setVisibility(haveWrite(context) ? View.VISIBLE : View.GONE);
        root.findViewById(R.id.im_state_permission_record_audio).setVisibility(haveRecordAudioPerm(context) ? View.VISIBLE : View.GONE);
    }

    /**
     * 获取是否有网络权限
     *
     * @param context 上下文
     */
    private static boolean haveNetwork(Context context) {
        //需要检查的网路权限
        String[] perms = new String[]{
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE
        };
        return EasyPermissions.hasPermissions(context, perms);
    }

    /**
     * 获取是否有读取权限
     *
     * @param context 上下文
     */
    private static boolean haveRead(Context context) {
        //需要检查的读取权限
        String[] perms = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        return EasyPermissions.hasPermissions(context, perms);
    }


    /**
     * 获取是否有写入权限
     *
     * @param context 上下文
     */
    private static boolean haveWrite(Context context) {
        //需要检查的写入权限
        String[] perms = new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        return EasyPermissions.hasPermissions(context, perms);
    }


    /**
     * 获取是否有录音
     *
     * @param context 上下文
     */
    private static boolean haveRecordAudioPerm(Context context) {
        //需要检查的录音权限
        String[] perms = new String[]{
                Manifest.permission.RECORD_AUDIO
        };
        return EasyPermissions.hasPermissions(context, perms);
    }


    /**
     * 私有的show方法
     */
    private static void show(FragmentManager manager) {
        //调用BottomSheetDialogFragment以及准备好的显示方法
        new PermissionsFragment().show(manager, PermissionsFragment.class.getName());
    }


    public static boolean haveAll(Context context, FragmentManager manager) {
        //检查是否具有所有的权限
        boolean haveAll = haveNetwork(context) && haveRead(context) && haveWrite(context) && haveRecordAudioPerm(context);
        if (!haveAll) {
            show(manager);
        }
        return haveAll;
    }

    /**
     * 申请权限的方法
     */
    @AfterPermissionGranted(RC)
    private void requestPerm() {
        String[] perms = new String[]{
                Manifest.permission.INTERNET,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        };

        if (EasyPermissions.hasPermissions(getContext(), perms)) {
            Application.showToast(R.string.label_permission_ok);
            //Fragment中调用getView调用根布局
            refreshState(getView());
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.title_assist_permissions), RC, perms);
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        //如果有权限没有申请成功的情况，则弹出弹出框，用户点击设置后到设置界面自己打开权限
        if (EasyPermissions.somePermissionPermanentlyDenied(this,perms)){
            new AppSettingsDialog.Builder(this).build().show();

        }
    }

    /**
     * 权限申请时候的回调方法，在这个方法中吧对应的申请权限交给他
     * */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //传递对应的参数，并且告知接受处理
        EasyPermissions.onRequestPermissionsResult(requestCode,permissions,grantResults,this);
    }
}

