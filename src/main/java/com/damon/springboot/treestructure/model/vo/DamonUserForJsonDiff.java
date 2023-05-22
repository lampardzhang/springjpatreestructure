package com.damon.springboot.treestructure.model.vo;

import com.damon.springboot.treestructure.model.base.BaseTreeEntityImpl;
import com.damon.springboot.treestructure.model.bo.DamonUser;

import java.util.ArrayList;
import java.util.List;

public class DamonUserForJsonDiff extends BaseTreeEntityImpl {

    @Override
    public String getSequenceName() {
        return null;
    }

    @Override
    public Long getPrimaryKey() {
        return null;
    }

    @Override
    public void setPrimaryKey(Long key) {

    }

    private List<DamonUser> userList = new ArrayList<>();

    public List<DamonUser> getUserList() {
        return userList;
    }

    public void setUserList(List<DamonUser> userList) {
        this.userList = userList;
    }
}
