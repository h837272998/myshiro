package com.hjh.myshiro.entity;

public class Permission {
    private Long id;

    private String name;

    private String url;

    private String describe;

    public Permission(Long id, String name, String url, String describe) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.describe = describe;
    }

    public Permission() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe == null ? null : describe.trim();
    }
}