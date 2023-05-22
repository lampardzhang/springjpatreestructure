package com.damon.springboot.treestructure.model.bo;


import com.damon.springboot.treestructure.model.base.annotation.ParentId;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.damon.springboot.treestructure.model.base.BaseTreeEntityImpl;

import javax.persistence.*;


@Entity
@Table(name="TEST_ADDRESS")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DamonAddress extends BaseTreeEntityImpl {

    @Id
    @Column(name = "ADDRESS_ID", nullable = false)
    private Long addressId;


    @Column(name = "LOCATION", nullable = false)
    private String location;

    @ParentId
    @Column(name="ACCOUNT_ID",nullable = true)
    private Long accountId;


    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    @Override
    public String getSequenceName() {
        return null;
    }

    @Override
    public Long getPrimaryKey() {
        return this.addressId;
    }

    @Override
    public void setPrimaryKey(Long key) {
        this.addressId =key;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
