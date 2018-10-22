package net.sjh.italker.push.helper;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;

/**
 * 完成对Fragment的调度与重用问题
 * 达到最优的Fragment切换
 */
public class NavHelper<T> {
    private final SparseArray<Tab<T>> tabs = new SparseArray();//所有的Tab集合
    private final Context context;
    private final int containerId;
    private final FragmentManager fragmentManager;
    private OnTabChangedListener<T> listener;
    private Tab<T> currentTab;//当前选中的Tab

    public NavHelper(Context context, int containerId,
                     FragmentManager fragmentManager,
                     OnTabChangedListener<T> listener) {
        this.context = context;
        this.containerId = containerId;
        this.fragmentManager = fragmentManager;
        this.listener = listener;
    }

    /**
     * 添加tab
     * @param menuId 添加对应的菜单ID
     * @param tab Tab
     * */
    public NavHelper<T> add(int menuId,Tab<T> tab){
        tabs.put(menuId,tab);
        return this;
    }

    /**
     * 获取当前显示的Tab
     * @return 当前的Tab
     * */
    public Tab<T> getCurrentTab(){
        return currentTab;
    }

    /**
     * 执行点击菜单操作
     *
     * @param menuId 菜单ID
     * @return 是否能够处理这个点击
     */
    public boolean performClickMenu(int menuId) {
        //集合中寻找点击的菜单对应的Tab
        //如果有就进行处理
        Tab<T> tab = tabs.get(menuId);
        if (tab!=null){
            doSelect(tab);
            return true;
        }

        return false;
    }

    /**
     * tab选中处理
     * @param tab Tab
     * */
    private void doSelect(Tab<T> tab){
        Tab<T> oldTab = null;
        if (currentTab!=null){
            oldTab = currentTab;
            if (oldTab == tab){
                //如果当前的tab就是点击的tab，则不处理
                notifyTabReselect(tab);
                return;
            }
        }

        //赋值并调用切换方法
        currentTab = tab;
        doTabChange(currentTab,oldTab);
    }

    /**
     * 处理fragment调度
     * */
    private void doTabChange(Tab<T> newTab,Tab<T> oldTab){
        FragmentTransaction ft = fragmentManager.beginTransaction();
        if (oldTab != null){
            if (oldTab.fragment != null){
                //从界面移除，但是还在Fragment的缓存空间中
                ft.detach(oldTab.fragment);
            }
        }

        if (newTab != null){
            if (newTab.fragment == null){
                //首次新建
                Fragment fragment = Fragment.instantiate(context,newTab.clx.getName(),null);
                //缓存起来
                newTab.fragment = fragment;
                //提交到FragmentManger
                ft.add(containerId,fragment,newTab.clx.getName());
            }else{
                //从FragmentManger缓存空间中重新加载到界面中
                ft.attach(newTab.fragment);
            }
        }
        //提交事务
        ft.commit();
        //通知回调
        notifyTabSelect(newTab,oldTab);

    }


    /**
     * 回调我们的监听器
     * @param newTab 新的Tab<T>
     * @param oldTab 旧的Tab<T>
     * */
    private void notifyTabSelect(Tab<T> newTab,Tab<T> oldTab){
        if (listener != null){
            listener.onTabCahnged(newTab,oldTab);
        }
    }


    private void notifyTabReselect(Tab<T> tab){
       //TODO 二次点击Tab所做的操作
    }


    /**
     * 我们所有的Tab基础属性
     *
     * @param <T> tab扩展属性
     */
    public static class Tab<T> {

        public Tab(Class<?> clx, T extra) {
            this.clx = clx;
            this.extra = extra;
        }

        //Fragment对应的class信息
        public Class<?> clx;
        //额外的字段，用户自己设定需要使用什么东西
        public T extra;
        //内部缓存的对应的Fragment ,package权限 外部无法使用
        private Fragment fragment;
    }

    /**
     * 定义事件处理完成后的回调接口
     */
    public interface OnTabChangedListener<T> {
        void onTabCahnged(Tab<T> newTab, Tab<T> oldTab);
    }
}
