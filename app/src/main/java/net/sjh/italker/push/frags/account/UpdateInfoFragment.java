package net.sjh.italker.push.frags.account;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.yalantis.ucrop.UCrop;

import net.sjh.italker.common.app.Application;
import net.sjh.italker.common.app.Fragment;
import net.sjh.italker.common.widget.PortraitView;
import net.sjh.italker.factory.Factory;
import net.sjh.italker.factory.net.UploadHelper;
import net.sjh.italker.push.R;
import net.sjh.italker.push.frags.media.GalleryFragment;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;


/**
 * 更新信息的界面
 */
public class UpdateInfoFragment extends Fragment {

    @BindView(R.id.im_portrait)
    PortraitView mPortrait;

    @Override
    protected int getContentLayoutId() {
        return R.layout.fragment_update_info;
    }

    @OnClick(R.id.im_portrait)
    void onPortraitClick() {
        new GalleryFragment().setListener(new GalleryFragment.OnSelectedListener() {
            @Override
            public void onSelectedImages(String path) {
                UCrop.Options options = new UCrop.Options();
                //设置图片处理的格式
                options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
                //设置压缩后的图片精度
                options.setCompressionQuality(96);

                //头像缓存地址
                File file = Application.getPortraitTmpFile();
                UCrop.of(Uri.fromFile(new File(path)),Uri.fromFile(file))
                        .withAspectRatio(1,1)//1:1比例
                        .withMaxResultSize(520,520)//返回最大尺寸
                        .withOptions(options)//设置参数
                        .start(getActivity());
            }
        }).show(getChildFragmentManager(),GalleryFragment.class.getName());//show的时候建议使用childFragmentManager
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == UCrop.REQUEST_CROP) {
            final Uri resultUri = UCrop.getOutput(data);
            if (resultUri != null) {
                loadPortrait(resultUri);
            }
        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }
    }

    private void loadPortrait(final Uri uri){
        Glide.with(this)
                .load(uri)
                .asBitmap()
                .centerCrop()
                .into(mPortrait);
        final String path = uri.getPath();

        Factory.runOnAsync(new Runnable() {
            @Override
            public void run() {
               String url =  UploadHelper.uploadPortrait(path);
                Log.e("TAG","URL:"+url);
            }
        });
    }


}
