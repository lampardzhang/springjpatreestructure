package com.damon.springboot.treestructure.model.bo;


import com.damon.springboot.treestructure.model.base.annotation.LogicDeletionField;
import com.damon.springboot.treestructure.model.base.annotation.ParentId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.damon.springboot.treestructure.model.base.BaseTreeEntityImpl;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "TEST_ACCOUNT")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DamonAccount extends BaseTreeEntityImpl {
    @Id
    @Column(name = "ACCOUNT_ID", nullable = false)
    private Long accountId;

    @Column(name = "ACCOUNT_NAME")
    private String accountName;

    @JsonIgnore
    @ParentId
    @Column(name = "USER_ID")
    private Long userId;




    @Column(name="IS_DELETE")
    @LogicDeletionField
    private String isDelete;

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete;
    }

   /* @OneToMany(targetEntity = DamonZipInfo.class ,mappedBy = "accountId",
            fetch = FetchType.LAZY, cascade = CascadeType.ALL,orphanRemoval = true)
    //@Fetch(FetchMode.SELECT)
    private List<DamonZipInfo> zipInfoList;

    public List<DamonZipInfo> getZipInfoList() {
        return zipInfoList;
    }

    public void setZipInfoList(List<DamonZipInfo> zipInfoList) {
        this.zipInfoList = zipInfoList;
    }*/


    @OneToMany(targetEntity = DamonAddress.class ,mappedBy = "accountId",
            fetch = FetchType.LAZY, cascade = CascadeType.ALL,orphanRemoval = true)
    //@Fetch(FetchMode.SELECT)
    private List<DamonAddress> addressList;

    public List<DamonAddress> getAddressList() {
        return addressList;
    }

    public void setAddressList(List<DamonAddress> addressList) {
        this.addressList = addressList;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String getSequenceName() {
        return null;
    }

    @Override
    public Long getPrimaryKey() {
        return this.accountId;
    }

    @Override
    public void setPrimaryKey(Long key) {
        this.accountId = key;

    }

/*

    public DomainModel getParent_() {
        return null;
    }


    public void setParent_(DomainModel parent) {

    }


    public String getBusinessModelName() {
        return "DamonAccount";
    }*/
}
