package com.liyyang.yunnote.domain;

/**
 * Created by leeyang on 16/7/5.
 */
public class ItemData implements Comparable<ItemData>{

    public ItemData(String title, String content, String data, String size, String dataid){
        this.content = content;
        this.data = data;
        this.dataid = dataid;
        this.size = size;
        this.title = title;
    }
    private String title,content,data,size,indexn,dataid;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getDataid() {
        return dataid;
    }

    public void setDataid(String dataid) {
        this.dataid = dataid;
    }


    @Override
    public int compareTo(ItemData another) {//排序，按时间大小
        long n = Long.parseLong(another.getDataid().trim());
        long i = Long.parseLong(this.getDataid().trim());
        if (i < n){
            return 1;
        }else {
            return -1;
        }
    }
}
