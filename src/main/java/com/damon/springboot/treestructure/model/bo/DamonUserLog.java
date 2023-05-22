package com.damon.springboot.treestructure.model.bo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.damon.springboot.treestructure.model.base.BaseEntityImpl;

import javax.persistence.*;


@Entity
@Table(name = "TEST_USER_LOG")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DamonUserLog extends BaseEntityImpl {

    @Id
    @Column(name = "LOG_ID", nullable = false)
    private Long logId;

    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "USER_DATA", columnDefinition = "json")
    private String userData;

    @Version
    @Column(name = "VERSION", nullable = false)
    private Long version;


    @Override
    public String getSequenceName() {
        return null;
    }

    @Override
    public Long getPrimaryKey() {
        return this.logId;
    }

    @Override
    public void setPrimaryKey(Long key) {
        this.logId = key;

    }

    public Long getLogId() {
        return logId;
    }

    public void setLogId(Long logId) {
        this.logId = logId;
    }

    public String getUserData() {
        return userData;
    }

    public void setUserData(String userData) {
        this.userData = userData;
    }

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
