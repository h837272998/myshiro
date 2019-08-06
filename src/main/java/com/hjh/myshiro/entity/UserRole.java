package com.hjh.myshiro.entity;

public class UserRole {
    private Long id;

    private Long uid;

    private Long rid;

    public UserRole(Long id, Long uid, Long rid) {
        this.id = id;
        this.uid = uid;
        this.rid = rid;
    }

    public UserRole() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public Long getRid() {
        return rid;
    }

    public void setRid(Long rid) {
        this.rid = rid;
    }
}