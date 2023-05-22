package com.damon.springboot.treestructure.dao;


import com.damon.springboot.treestructure.dao.base.BaseRepository;
import com.damon.springboot.treestructure.model.bo.DamonAccount;
import org.springframework.stereotype.Repository;

@Repository
public class DamonAccountDao extends BaseRepository<DamonAccount,Long> {

}
