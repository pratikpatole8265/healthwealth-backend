package com.healthwealth.dto;

import java.util.List;

public class PagedResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private int totalPages;
    private long totalElements;

    public PagedResponse() {
    }

    public PagedResponse(List<T> c, int p, int s, int tp, long te) {
        content = c;
        page = p;
        size = s;
        totalPages = tp;
        totalElements = te;
    }

    public List<T> getContent() {
        return content;
    }

    public int getPage() {
        return page;
    }

    public int getSize() {
        return size;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }
}
