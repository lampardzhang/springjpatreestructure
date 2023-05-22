package com.damon.springboot.treestructure.dao;



import com.damon.springboot.treestructure.model.bo.DamonUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface DamonUserRepo extends JpaRepository<DamonUser, Long>, JpaSpecificationExecutor<DamonUser> {
}
