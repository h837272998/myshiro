package com.hjh.myshiro.entity;

public class RolePermission {
    private Long id;

    private Long rid;

    private Long pid;

    public RolePermission(Long id, Long rid, Long pid) {
        this.id = id;
        this.rid = rid;
        this.pid = pid;
    }

    public RolePermission() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRid() {
        return rid;
    }

    public void setRid(Long rid) {
        this.rid = rid;
    }

    public Long getPid() {
        return pid;
    }

    public void setPid(Long pid) {
        this.pid = pid;
    }
}