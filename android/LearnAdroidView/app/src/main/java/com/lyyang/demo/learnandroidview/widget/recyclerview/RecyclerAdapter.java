package com.lyyang.demo.learnandroidview.widget.recyclerview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lyyang.demo.learnandroidview.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by LeeYang
 */
public abstract class RecyclerAdapter<Data>
        extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder<Data>>
        implements View.OnClickListener, View.OnLongClickListener, AdapterCallback<Data> {
    private final List<Data> mDataList;
    private AdapterListener<Data> mListener;

    /**
     * g构造函数代码
     */
    public RecyclerAdapter() {
        this(null);
    }

    public RecyclerAdapter(AdapterListener<Data> listener) {
        this(new ArrayList<Data>(),listener);
    }

    public RecyclerAdapter(List<Data> dataList, AdapterListener<Data> listener) {
        this.mDataList = dataList;
        this.mListener = listener;
    }

    /**
     * 复写默认布局类型返回
     *
     * @param position
     * @return xml文件
     */
    @Override
    public int getItemViewType(int position) {
        return getItemViewType(position, mDataList.get(position));
    }

    /**
     * 得到布局类型
     *
     * @param position
     * @param data
     * @return xml的id 用于创建ViewHolder
     */
    protected abstract int getItemViewType(int position, Data data);

    /**
     * 创建一个ViewHolder
     *
     * @param parent   RecyclerView
     * @param viewType 界面类型,约定xml布局的id
     * @return
     */
    @Override
    public ViewHolder<Data> onCreateViewHolder(ViewGroup parent, int viewType) {
        // 得到LayoutInflater用于把XML初始化为View
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        // 把xml i为viewType的文件初始化为一个 root view
        View root = inflater.inflate(viewType, parent, false);
        //通过子类方法，得到一个viewholder
        ViewHolder<Data> holder = onCreatViewHolder(root, viewType);

        //设置View的tag为viewHolder，进行双向绑定
        root.setTag(R.id.tag_recycler_holder);

        // 设置点击事件
        root.setOnClickListener(this);
        root.setOnLongClickListener(this);


        //进行界面数据绑定
        holder.unbinder = ButterKnife.bind(holder, root);
        //绑定callback
        holder.callback = this;
        return holder;
    }


    /**
     * 得到新的viewHolder
     *
     * @param root     根布局
     * @param viewType xml的id
     * @return
     */
    protected abstract ViewHolder<Data> onCreatViewHolder(View root, int viewType);

    /**
     * 绑定数据到一个Holder上
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolder<Data> holder, int position) {
        //得到需要绑定的数据
        Data data = mDataList.get(position);
        //除发Holder的绑定方法
        holder.bind(data);
    }


    // 得到当前集合的数据量
    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    /**
     * 插入一条数据并通知插入
     *
     * @param data
     */
    public void add(Data data) {
        mDataList.add(data);
//        notifyDataSetChanged();
        notifyItemInserted(mDataList.size() - 1);
    }

    /**
     * 插入多条数据，并通知更新
     *
     * @param dataList
     */
    public void add(Data... dataList) {
        if (dataList != null && dataList.length > 0) {
            int startPos = mDataList.size();
            Collections.addAll(mDataList, dataList);
            notifyItemRangeInserted(startPos, dataList.length);
        }
    }

    public void add(Collection<Data> dataList) {
        if (dataList != null && dataList.size() > 0) {
            int startPos = mDataList.size();
            mDataList.addAll(dataList);
            notifyItemRangeInserted(startPos, dataList.size());
        }
    }

    public void clear() {
        mDataList.clear();
        notifyDataSetChanged();
    }

    /**
     * 替换为一个新的集合，包括清空
     *
     * @param dataList 一个新的集合
     */
    public void replace(Collection<Data> dataList) {
        mDataList.clear();
        if (dataList == null || dataList.size() == 0)
            return;
        mDataList.addAll(dataList);
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        ViewHolder viewHolder = (ViewHolder) view.getTag(R.id.tag_recycler_holder);
        if (this.mListener != null) {
            //得到Viewholder 当前适配器的坐标
            int pos = viewHolder.getAdapterPosition();
            //回调方法
            this.mListener.onItemClick(viewHolder, mDataList.get(pos));
        }
    }

    @Override
    public boolean onLongClick(View view) {
        ViewHolder viewHolder = (ViewHolder) view.getTag(R.id.tag_recycler_holder);
        if (this.mListener != null) {
            //得到Viewholder 当前适配器的坐标
            int pos = viewHolder.getAdapterPosition();
            //回调方法
            this.mListener.onItemClick(viewHolder, mDataList.get(pos));
            return true;
        }
        return false;
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
        //当cell点击触发
        void onItemClick(RecyclerAdapter.ViewHolder holder, Data data);

        //当cell长按触发
        void onItenLongClick(RecyclerAdapter.ViewHolder holder, Data data);
    }

    /**
     * 自定义的ViewHolder
     *
     * @param <Data>
     */
    public static abstract class ViewHolder<Data> extends RecyclerView.ViewHolder {
        private Unbinder unbinder;
        private AdapterCallback callback;
        protected Data mData;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        /**
         * 绑定数据的触发
         *
         * @param data
         */
        void bind(Data data) {
            this.mData = data;
        }

        /**
         * 当触发绑定数据时，回调，必须复写
         *
         * @param data
         */
        protected abstract void onBind(Data data);

        /**
         * hodler对自己对应的holder更新
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
