package com.damon.springboot.treestructure.model.vo;

public class DamonUserReverseRequest {

    private Long version;
    private Long userId;

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
