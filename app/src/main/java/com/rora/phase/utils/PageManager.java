package com.rora.phase.utils;

public class PageManager {

    private int totalItem;
    private int page;
    private int pageSize;
    private String previous;
    private String next;

    public PageManager() {
        reset();
    }

    public PageManager(int totalItem, int page, int pageSize, String previous, String next) {
        this.totalItem = totalItem;
        this.page = page;
        this.pageSize = pageSize;
        this.previous = previous;
        this.next = next;
    }

    public int getTotalItem() {
        return totalItem;
    }

    public void setTotalItem(int totalItem) {
        this.totalItem = totalItem;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public int nextPage() {
        if(hasNext()) {
            page += 1;
        } else {
            page = -1;
        }

        return page;
    }

    public boolean hasNext() {
        return totalItem > page * pageSize;
    }

    public void reset() {
        this.totalItem = 1;
        this.page = 0;
        this.pageSize = 20;
        this.next = "";
        this.previous = "";
    }
}
