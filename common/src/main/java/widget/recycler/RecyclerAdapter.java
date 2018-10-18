package widget.recycler;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import net.sjh.italker.common.R;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author shijunhong
 * @version 1.0.0
 */
public abstract class RecyclerAdapter<Data> extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder<Data>>
        implements View.OnClickListener,
        View.OnLongClickListener,
        AdapterCallback<Data> {

    private final List<Data> mDatalist;
    private AdapterListener<Data> mListener;

    /**
     * 构造函数模块
     * */
    public RecyclerAdapter() {
        this(null);
    }

    public RecyclerAdapter(AdapterListener<Data> listener) {
        this(new ArrayList<Data>(),listener);
        this.mListener = listener;
    }

    public RecyclerAdapter(List<Data> dataList, AdapterListener<Data> listener) {
        this.mDatalist = dataList;
        this.mListener = listener;
    }


    /**
     * 复写默认的布局类型返回
     *
     * @param position 坐标
     * @return 复写后返回的xml文件ID
     */
    @Override
    public int getItemViewType(int position) {
        return getItemViewType(position, mDatalist.get(position));
    }

    /**
     * 复写默认的布局类型返回
     *
     * @param position 坐标
     * @return 返回的xml文件ID, 用于创建viewHolder
     */
    @LayoutRes
    protected abstract int getItemViewType(int position, Data data);

    /**
     * 创建一个ViewHolder
     *
     * @param viewGroup RecyclerView
     * @param viewType  界面的类型,约定为XML布局的ID
     * @return viewHolder
     */
    @NonNull
    @Override
    public ViewHolder<Data> onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        //得到LayoutInflater用于把XML初始化为view
        LayoutInflater layoutInflater = LayoutInflater.from(viewGroup.getContext());
        //把xml id为viewType的文件初始化为一个root view
        View root = layoutInflater.inflate(viewType, viewGroup, false);
        //通过自雷必须实现的方法，得到一个ViewHolder;
        ViewHolder<Data> holder = onCreateViewHolder(root, viewType);
        //设置View的Tag为ViewHolder,进行双向绑定
        root.setTag(R.id.tag_recycler_holder, holder);
        //设置事件点击
        root.setOnClickListener(this);
        root.setOnLongClickListener(this);
        //进行界面注解绑定
        holder.unbinder = ButterKnife.bind(holder, root);
        //绑定callback
        holder.callback = this;
        return null;
    }


    /**
     * 得到一个新的viewHolder
     *
     * @param root     根布局
     * @param viewType 布局类型，限制为XML布局ID
     * @return ViewHolder
     */
    protected abstract ViewHolder<Data> onCreateViewHolder(View root, int viewType);


    /**
     * 绑定数据到一个holder上
     *
     * @param dataViewHolder ViewHolder
     * @param i              坐标
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder<Data> dataViewHolder, int i) {
        //得到需要绑定的数据
        Data data = mDatalist.get(i);
        //触发绑定
        dataViewHolder.bind(data);
    }

    /**
     * 得到当前集合的数据量
     */
    @Override
    public int getItemCount() {
        return mDatalist.size();
    }

    /**
     * 插入一条数据并通知界面刷新
     */
    public void add(Data data) {
        mDatalist.add(data);
        notifyItemInserted(mDatalist.size() - 1);
    }

    /**
     * 插入一堆数据并通知界面刷新
     */
    public void add(Data... dataList) {
        if (dataList != null && dataList.length > 0) {
            int startPos = mDatalist.size();
            Collections.addAll(mDatalist, dataList);
            notifyItemRangeInserted(startPos, dataList.length);
        }
    }

    /**
     * 插入一堆数据并通知界面刷新
     */
    public void add(Collection<Data> dataList) {
        if (dataList != null && dataList.size() > 0) {
            int startPos = mDatalist.size();
            mDatalist.addAll(dataList);
            notifyItemRangeInserted(startPos, dataList.size());
        }
    }

    /**
     * 删除操作
     */
    public void clear() {
        mDatalist.clear();
        notifyDataSetChanged();
    }

    /**
     * 替换为一个新的集合，其中包括清空
     *
     * @param dataList 一个新的集合
     */
    public void replase(Collection<Data> dataList) {
        mDatalist.clear();
        if (dataList != null && dataList.size() == 0) {
            return;
        }
        mDatalist.addAll(dataList);
        notifyDataSetChanged();
    }


    @Override
    public void onClick(View v) {
        ViewHolder viewHolder = (ViewHolder) v.getTag(R.id.tag_recycler_holder);
        if (this.mListener != null) {
            //得到ViewHolder当前对应的适配器中的坐标
            int pos = viewHolder.getAdapterPosition();
            //回调
            this.mListener.onItemClick(viewHolder, mDatalist.get(pos));
        }
    }

    @Override
    public boolean onLongClick(View v) {
        ViewHolder viewHolder = (ViewHolder) v.getTag(R.id.tag_recycler_holder);
        if (this.mListener != null) {
            //得到ViewHolder当前对应的适配器中的坐标
            int pos = viewHolder.getAdapterPosition();
            //回调
            this.mListener.onItemLongClick(viewHolder, mDatalist.get(pos));
        }
        return true;
    }


    /**
     * 设置适配器的监听
     *
     * @param adapterListener
     */
    public void setListener(AdapterListener<Data> adapterListener) {
        this.mListener = adapterListener;
    }

    /**
     * 自定义监听器
     *
     * @param <Data>
     */
    public interface AdapterListener<Data> {
        //当Cell 点击的时候触发
        void onItemClick(RecyclerAdapter.ViewHolder holder, Data data);

        //当Cell 长按的时候触发
        void onItemLongClick(RecyclerAdapter.ViewHolder holder, Data data);
    }

    /**
     * 自定义的ViewHolder
     *
     * @param <Data> 泛型类型
     */
    public static abstract class ViewHolder<Data> extends RecyclerView.ViewHolder {
        private Unbinder unbinder;
        private AdapterCallback<Data> callback;
        protected Data mData;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        void bind(Data data) {
            this.mData = data;
            onBind(data);
        }

        /**
         * 用于绑定数据触发
         *
         * @param data 绑定的数据
         */
        protected abstract void onBind(Data data);

        /**
         * Holder自己对自己对于的Data进行更新操作
         *
         * @param data Data数据
         */
        public void updateData(Data data) {
            if (this.callback != null) {
                this.callback.update(data, this);
            }
        }
    }
}
