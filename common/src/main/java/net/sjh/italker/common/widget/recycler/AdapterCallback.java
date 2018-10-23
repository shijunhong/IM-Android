package net.sjh.italker.common.widget.recycler;

/**
 * @author shijunhong
 * @version 1.0.0
 */
public interface AdapterCallback<Data> {
    void update(Data data, RecyclerAdapter.ViewHolder<Data> viewHolder);
}
