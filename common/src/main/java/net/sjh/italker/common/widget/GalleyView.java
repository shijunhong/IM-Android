package net.sjh.italker.common.widget;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import net.sjh.italker.common.R;
import net.sjh.italker.common.widget.recycler.RecyclerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;


public class GalleyView extends RecyclerView {
    private static final int LOADER_ID = 0x0100;
    private LoaderCallback loaderCallback = new LoaderCallback();
    private Adapter myAdaptr = new Adapter();
    private List<Image> mSelectedImages = new LinkedList<>();
    private static final int MAX_IMAGE_COUNT = 3;//最大的选中图片数量
    private static final int MIN_IMAGE_FILE_SIZE = 10 * 1024;//最小图片大小
    private SelectedChangeListener mListener;

    public GalleyView(Context context) {
        super(context);
        init();
    }

    public GalleyView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public GalleyView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setLayoutManager(new GridLayoutManager(getContext(), 4));
        setAdapter(myAdaptr);
        myAdaptr.setListener(new RecyclerAdapter.AdapterListenerImpl<Image>() {
            @Override
            public void onItemClick(RecyclerAdapter.ViewHolder holder, Image image) {
                //Cell点击操作，如果我们的点击是允许的，则更新对应的Cell的状态。
                //然后更新界面，如果说不能点击，(比如已经达到最大的选中数量，则就不刷新界面)
                if (onItemSelectClick(image)) {
                    holder.updateData(image);
                }
            }
        });
    }


    /**
     * 初始化方法
     *
     * @param loaderManager Loader管理器
     * @return 任何一个LoaderId 可用于销毁
     */
    public int setUp(LoaderManager loaderManager, SelectedChangeListener listener) {
        mListener = listener;
        loaderManager.initLoader(LOADER_ID, null, loaderCallback);
        return LOADER_ID;
    }


    /**
     * Cell点击的具体逻辑
     *
     * @param image Image
     * @return True 代表我进行了数据更改，需要刷新，反之
     */
    private boolean onItemSelectClick(Image image) {
        //是否需要进行刷新
        boolean notifyRefresh;
        if (mSelectedImages.contains(image)) {
            //如果之前在，现在就移除
            mSelectedImages.remove(image);
            image.isSelect = false;
            //状态已经改变，需要更新
            notifyRefresh = true;
        } else {
            if (mSelectedImages.size() >= MAX_IMAGE_COUNT) {
                //Toast 一个提示
                //得到文本
                String str = getResources().getString(R.string.label_gallery_select_max_size);
                //格式化填充
                str = String.format(str, MAX_IMAGE_COUNT);
                //显示
                Toast.makeText(getContext(),str,Toast.LENGTH_SHORT).show();
                notifyRefresh = false;
            } else {
                mSelectedImages.add(image);
                image.isSelect = true;
                notifyRefresh = true;
            }
        }

        //如果数据有更改，那么我们需要通知外面的监听者我们的数据选中改变了
        if (notifyRefresh) {
            notifySelectChanged();
        }

        return true;
    }

    /**
     * 得到选中的图片的全部地址
     *
     * @return 返回一个数据
     */
    public String[] getSelectPath() {
        String[] paths = new String[mSelectedImages.size()];
        int index = 0;
        for (Image image : mSelectedImages) {
            paths[index++] = image.path;
        }
        return paths;
    }

    /**
     * 可以进行清空选中的图片
     */
    public void clear() {
        for (Image image : mSelectedImages) {
            //一定要先重置状态
            image.isSelect = false;
        }
        mSelectedImages.clear();
        //通知更新
        myAdaptr.notifyDataSetChanged();
    }

    /**
     * 通知选中状态改变
     */
    private void notifySelectChanged() {
        //得到监听，并判断是否存在监听，回调数量
        SelectedChangeListener listener = mListener;
        if (listener != null) {
            listener.onSelectedCountChanges(mSelectedImages.size());
        }
    }


    /**
     * 通知adapger数据更改的方法
     *
     * @param images 新的数据
     */
    private void upDateSource(List<Image> images) {
        myAdaptr.replace(images);
    }

    /**
     * 用于实际的数据加载的Loader Callback
     */
    private class LoaderCallback implements LoaderManager.LoaderCallbacks<Cursor> {

        private final String[] IMAGE_PROJECTION = new String[]{
                MediaStore.Images.Media._ID,//id
                MediaStore.Images.Media.DATA,//图片路径
                MediaStore.Images.Media.DATE_ADDED //图片创建时间
        };

        @NonNull
        @Override
        public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
            //创建一个Loader
            if (i == LOADER_ID) {
                //如果是我们的ID  则可以舒适化
                return new CursorLoader(getContext(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        IMAGE_PROJECTION,
                        null,
                        null,
                        IMAGE_PROJECTION[2] + " DESC");//倒序查询
            }
            return null;
        }

        @Override
        public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
            //当loader加载完成时
            List<Image> images = new ArrayList<>();
            //判断是否有数据
            if (cursor != null) {
                int count = cursor.getCount();
                if (count > 0) {
                    //移动游标到开始
                    cursor.moveToFirst();
                    //得到对应的列的坐标
                    int indexId = cursor.getColumnIndex(IMAGE_PROJECTION[0]);
                    int indexPath = cursor.getColumnIndex(IMAGE_PROJECTION[1]);
                    int indexDate = cursor.getColumnIndex(IMAGE_PROJECTION[2]);
                    do {
                        int id = cursor.getInt(indexId);
                        String path = cursor.getString(indexPath);
                        long dataTime = cursor.getInt(indexDate);

                        File file = new File(path);
                        if (!file.exists() || file.length() < MIN_IMAGE_FILE_SIZE) {
                            //如果没有图片，或者图片大小太小，则跳出
                            continue;
                        }

                        //添加一条新的数据
                        Image image = new Image();
                        image.id = id;
                        image.path = path;
                        image.data = dataTime;
                        images.add(image);


                        //循环读取，直到没有下一条数据
                    } while (cursor.moveToNext());
                }
            }

            upDateSource(images);
        }

        @Override
        public void onLoaderReset(@NonNull Loader<Cursor> loader) {
            //当Loader销毁或者重置,进行界面清空
            upDateSource(null);
        }
    }

    /**
     * 内部数据结构
     */
    private static class Image {
        int id;//数据id
        String path;//图片路径
        long data;//图片的创建日期
        boolean isSelect;//是否选中

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Image image = (Image) o;
            return Objects.equals(path, image.path);
        }

        @Override
        public int hashCode() {

            return Objects.hash(path);
        }
    }

    /**
     * 适配器
     */
    private class Adapter extends RecyclerAdapter<Image> {

        @Override
        protected int getItemViewType(int position, Image image) {
            return R.layout.cell_galley;
        }

        @Override
        protected ViewHolder<Image> onCreateViewHolder(View root, int viewType) {
            return new GalleyView.ViewHolder(root);
        }
    }


    /**
     * Cell对应的Holeder
     */
    private class ViewHolder extends RecyclerAdapter.ViewHolder<Image> {
        private ImageView mPic;
        private View mShade;
        private CheckBox mSelected;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mPic = itemView.findViewById(R.id.im_image);
            mShade = itemView.findViewById(R.id.view_shade);
            mSelected = itemView.findViewById(R.id.cb_select);
        }

        @Override
        protected void onBind(Image image) {
            Glide.with(getContext())
                    .load(image.path)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)//本地图片，不使用缓存
                    .centerCrop()//居中剪切
                    .placeholder(R.color.grey_200)//默认颜色
                    .into(mPic);

            mShade.setVisibility(image.isSelect ? VISIBLE : INVISIBLE);
            mSelected.setChecked(image.isSelect);
            mSelected.setVisibility(VISIBLE);
        }
    }


    /**
     * 对外的监听器
     */
    public interface SelectedChangeListener {
        void onSelectedCountChanges(int count);
    }
}
