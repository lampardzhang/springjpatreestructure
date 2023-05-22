package com.damon.springboot.treestructure.dao;


import com.damon.springboot.treestructure.dao.base.BaseRepository;
import com.damon.springboot.treestructure.model.bo.DamonUser;
import com.damon.springboot.treestructure.model.bo.DamonUserLog;
import com.damon.springboot.treestructure.util.JSONUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class DamonUserDao extends BaseRepository<DamonUser,Long> {

    @Autowired
    DamonUserLogRepo damonUserLogRepo;


    public DamonUser save(DamonUser damonUser) {
        if(damonUser.getPrimaryKey()==null) {
            damonUser.setVersion(0L);
            this.getEntityManager().persist(damonUser);

            DamonUserLog damonUserLog = new DamonUserLog();
            damonUserLog.setVersion(damonUser.getVersion());
            damonUserLog.setUserData(JSONUtils.toJSON(damonUser));
            damonUserLog.setUserId(damonUser.getPkId());
            damonUserLogRepo.save(damonUserLog);

            return damonUser;
        } else {
            Long oldVersion = damonUser.getVersion();
            damonUser.setVersion(oldVersion+1);
            this.getEntityManager().persist(damonUser);

            DamonUserLog damonUserLog = new DamonUserLog();
            damonUserLog.setVersion(damonUser.getVersion());
            damonUserLog.setUserData(JSONUtils.toJSON(damonUser));
            damonUserLog.setUserId(damonUser.getPkId());
            damonUserLogRepo.save(damonUserLog);


            return damonUser;
        }


    }

}
