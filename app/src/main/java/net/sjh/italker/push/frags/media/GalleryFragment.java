package net.sjh.italker.push.frags.media;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import net.qiujuer.genius.ui.Ui;
import net.sjh.italker.common.app.Fragment;
import net.sjh.italker.common.tools.UiTool;
import net.sjh.italker.common.widget.GalleyView;
import net.sjh.italker.push.R;

import butterknife.BindView;

/**
 * 图片选择fragment
 */
public class GalleryFragment extends BottomSheetDialogFragment implements GalleyView.SelectedChangeListener{

    private GalleyView mGallery;

    private OnSelectedListener mListener;

    public GalleryFragment(){
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //返回一个我们复写的
        return new TransStatusBottomSheetDialog(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_gallery,container,false);
        mGallery = root.findViewById(R.id.galleyView);
        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        mGallery.setUp(getLoaderManager(),this);
    }

    @Override
    public void onSelectedCountChanges(int count) {
        //如果选中了一张图片
        if (count>0){
            //隐藏自己
            dismiss();
            if (mListener!=null){
                //得到所有的选中的图片的路径
                String[] paths = mGallery.getSelectPath();
                //返回第一张
                mListener.onSelectedImages(paths[0]);
                //取消和唤起之间的引用，加快内存回收
                mListener = null;
            }
        }
    }

    /**
     * 设置事件监听并返回自己
     * */
    public GalleryFragment setListener(OnSelectedListener listener){
        mListener = listener;
        return this;
    }

    /**
     * 选中图片的监听器
     * */
    public interface OnSelectedListener{
        void onSelectedImages(String path);
    }


    //为了解决顶部状态栏变黑而做的处理
    public static class TransStatusBottomSheetDialog extends BottomSheetDialog{

        public TransStatusBottomSheetDialog(@NonNull Context context) {
            super(context);
        }

        public TransStatusBottomSheetDialog(@NonNull Context context, int theme) {
            super(context, theme);
        }

        protected TransStatusBottomSheetDialog(@NonNull Context context, boolean cancelable, OnCancelListener cancelListener) {
            super(context, cancelable, cancelListener);
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            final Window window = getWindow();
            if (window==null){
                return;
            }

            //得到屏幕高度
            int screenHeight = UiTool.getScreenHeight(getOwnerActivity());
            //得到状态栏的高度
            int statusHeight = UiTool.getStatusBarHeight(getOwnerActivity());
            //计算dialog的高度并设置
            int dialogHeight = screenHeight - statusHeight;
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,dialogHeight<=0?ViewGroup.LayoutParams.MATCH_PARENT:dialogHeight);


        }
    }
}
