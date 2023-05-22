package com.damon.springboot.treestructure.model.base;

import com.damon.springboot.treestructure.util.SnowFlakeIdWorker;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@MappedSuperclass
public abstract class BaseEntityImpl implements BaseEntity {
    private static final long serialVersionUID = 1L;
    @JsonIgnore
    @Column(
            name = "INSERT_BY",
            nullable = false
    )
    private Long insertBy;
    @JsonIgnore
    @Column(
            name = "INSERT_TIME",
            nullable = false
    )
    private LocalDateTime insertTime;
    @JsonIgnore
    @Column(
            name = "UPDATE_BY",
            nullable = false
    )
    private Long updateBy;


    @Column(
            name = "UPDATE_TIME",
            nullable = false
    )
    private LocalDateTime updateTime;


    public BaseEntityImpl() {
    }


    @JsonIgnore
    public abstract String getSequenceName();

    @PostLoad
    protected void postLoad() {
    }

    public Long preparePrimaryKey() {
        if (this.getPrimaryKey() == null) {
            Long createdPrimaryKey = SnowFlakeIdWorker.generateId();
            this.setPrimaryKey(createdPrimaryKey);
        }

        return this.getPrimaryKey();
    }

    @PrePersist
    protected void prePersist() {
        Long currentUserId;

        currentUserId = 401L;


        if (null == currentUserId) {
            currentUserId = Long.valueOf(360083991L);
        }
        LocalDateTime localTimeForCurrentUser = LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.of("Asia/Shanghai"));
        this.insertBy = currentUserId;


        this.updateBy = currentUserId;


        this.insertTime = localTimeForCurrentUser;


        this.updateTime = localTimeForCurrentUser;


        this.preparePrimaryKey();

    }

    @PreUpdate
    protected void preUpdate() {
        Long currentUserId = 401L;
        LocalDateTime localTimeForCurrentUser = LocalDateTime.now();

        this.updateBy = currentUserId;


        this.updateTime = localTimeForCurrentUser;


    }


    public Long getInsertBy() {
        return this.insertBy;
    }


    public void setInsertBy(Long insertBy) {
        this.insertBy = insertBy;
    }


    public LocalDateTime getInsertTime() {
        return this.insertTime;
    }


    public void setInsertTime(LocalDateTime insertTime) {
        this.insertTime = insertTime;
    }


    public Long getUpdateBy() {
        return this.updateBy;
    }


    public void setUpdateBy(Long updateBy) {
        this.updateBy = updateBy;
    }


    public LocalDateTime getUpdateTime() {
        return this.updateTime;
    }


    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }


    public abstract Long getPrimaryKey();

    public abstract void setPrimaryKey(Long key);


    public boolean isPropertyExcludedInDataSync_(String propertyName) {
        return "primaryKey".equals(propertyName) || "insertBy".equals(propertyName) || "insertTime".equals(propertyName) || "updateBy".equals(propertyName) || "updateTime".equals(propertyName) || "CGLibBeanMap_".equals(propertyName) || "tempEntityCopyFrom_".equals(propertyName);
    }

    public boolean isPropertyIncludedInDataSync_(String propertyName) {
        return false;
    }


}