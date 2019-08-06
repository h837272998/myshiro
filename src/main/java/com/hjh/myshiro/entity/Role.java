package com.hjh.myshiro.entity;

public class Role {
    private Long id;

    private String name;

    private String describe;

    public Role(Long id, String name, String describe) {
        this.id = id;
        this.name = name;
        this.describe = describe;
    }

    public Role() {
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

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe == null ? null : describe.trim();
    }
}