package com.damon.springboot.treestructure.model.bo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.damon.springboot.treestructure.model.base.BaseTreeEntityImpl;
import com.damon.springboot.treestructure.model.base.annotation.ParentId;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "TEST_ZIP_INFO")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DamonZipInfo extends BaseTreeEntityImpl {




    @Id
    @ParentId
    @Column(name = "LIST_ID", nullable = true)
    private Long listId;

    @Column(name = "ZIP_CODE")
    private String zipCode;



    /*@JsonIgnore
    @ParentId
    @Column(name = "ACCOUNT_ID")
    private Long accountId;

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }*/



    public Long getListId() {
        return listId;
    }

    public void setListId(Long listId) {
        this.listId = listId;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }




    @Override
    public String getSequenceName() {
        return null;
    }

    @Override
    public Long getPrimaryKey() {
        return this.listId;
    }

    @Override
    public void setPrimaryKey(Long key) {
        this.listId = key;

    }




/*
    public DomainModel getParent_() {
        return null;
    }


    public void setParent_(DomainModel parent) {

    }


    public String getBusinessModelName() {
        return "DamonZipInfo";
    }*/
}
