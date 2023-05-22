package com.damon.springboot.treestructure.dao;

import com.damon.springboot.treestructure.model.bo.DamonUserLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DamonUserLogRepo extends JpaRepository<DamonUserLog, Long>, JpaSpecificationExecutor<DamonUserLog> {
}
