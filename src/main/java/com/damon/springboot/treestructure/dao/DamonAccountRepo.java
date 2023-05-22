package com.damon.springboot.treestructure.dao;

import com.damon.springboot.treestructure.model.bo.DamonAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DamonAccountRepo extends JpaRepository<DamonAccount, Long>, JpaSpecificationExecutor<DamonAccount> {
}
