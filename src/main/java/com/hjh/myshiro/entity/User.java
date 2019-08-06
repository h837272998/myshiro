package com.hjh.myshiro.entity;

import java.util.Date;

public class User {
    private Long id;

    private String username;

    private String email;

    private String pswd;

    private String salt;

    private Date createTime;

    private Date lastLoginTime;

    private Long status;

    public User(Long id, String username, String email, String pswd, String salt, Date createTime, Date lastLoginTime, Long status) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.pswd = pswd;
        this.salt = salt;
        this.createTime = createTime;
        this.lastLoginTime = lastLoginTime;
        this.status = status;
    }

    public User(User user) {
        this.id = user.id;
        this.username = user.getUsername();
        this.pswd = user.getPswd();
        this.email = user.getEmail();
        this.salt = user.getSalt();
        this.lastLoginTime = user.getLastLoginTime();
        this.status = user.getStatus();
    }

    public User() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getPswd() {
        return pswd;
    }

    public void setPswd(String pswd) {
        this.pswd = pswd == null ? null : pswd.trim();
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt == null ? null : salt.trim();
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }
}