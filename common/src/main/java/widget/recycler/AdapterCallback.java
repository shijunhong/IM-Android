package widget.recycler;

import android.support.v7.widget.RecyclerView;

/**
 * @author shijunhong
 * @version 1.0.0
 */
public interface AdapterCallback<Data> {
    void update(Data data, RecyclerAdapter.ViewHolder<Data> viewHolder);
}
