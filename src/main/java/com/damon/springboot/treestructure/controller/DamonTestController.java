package com.damon.springboot.treestructure.controller;


import cn.hutool.json.JSONUtil;
import com.damon.springboot.treestructure.dao.*;
import com.damon.springboot.treestructure.model.vo.DamonUserForJsonDiff;
import com.damon.springboot.treestructure.model.vo.DamonUserReverseRequest;
import com.damon.springboot.treestructure.util.DeepCopyUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.flipkart.zjsonpatch.JsonDiff;
import com.damon.springboot.treestructure.model.bo.DamonAccount;
import com.damon.springboot.treestructure.model.bo.DamonAddress;
import com.damon.springboot.treestructure.model.bo.DamonUser;
import com.damon.springboot.treestructure.model.bo.DamonUserLog;
import com.damon.springboot.treestructure.util.JSONUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping(value = "/damon")
public class DamonTestController {


    @Autowired
    private DamonUserRepo damonUserRepo;
    @Autowired
    private DamonUserDao damonUserDao;
    @Autowired
    private DamonUserLogRepo damonUserLogRepo;
    @Autowired
    private DamonAccountRepo damonAccountRepo;
    @Autowired
    private DamonAccountDao damonAccountDao;


    @Transactional
    @PostMapping(value = "/save")
    public DamonUser save(
            @RequestBody DamonUser damonUser,
            HttpServletRequest httpRequest, HttpServletResponse httpResponse, BindingResult result) {


        if (damonUser.getPkId() != null) {


            Example<DamonUser> example = Example.of(damonUser);
            DamonUser existUser = this.damonUserDao.load(damonUser.getPkId());
            //check version info
            if (damonUser.getVersion() != existUser.getVersion()) {
                throw new RuntimeException("Object " + this.getClass() + " version should be same");
            }
            if (existUser != null) {
                DeepCopyUtil.deepCopyObject(damonUser, existUser, false);
                //EntitySyncUtils.syncData(damonUser,existUser,true);
                return damonUserDao.save(existUser);
            } else {

                return damonUserDao.save(damonUser);
            }
        } else {
            return null;
        }


    }

    @Transactional
    @PostMapping(value = "/create")
    public DamonUser create(
            @RequestBody DamonUser damonUser,
            HttpServletRequest httpRequest, HttpServletResponse httpResponse, BindingResult result) {
        DamonUser u = new DamonUser();
        DeepCopyUtil.deepCopyObject(damonUser,u,true);
        u.setVersion(0l);
        return this.damonUserDao.save(u);
    }

    @Transactional
    @PostMapping(value = "/load")
    public DamonUser load(
            @RequestBody DamonUser damonUser,
            HttpServletRequest httpRequest, HttpServletResponse httpResponse, BindingResult result) {

        DamonUser p1 = new DamonUser();
        BeanUtils.copyProperties(damonUser, p1);
        Example<DamonUser> example = Example.of(p1);
        DamonUser damonUser2 = this.damonUserRepo.findOne(example).orElse(null);
        String damonUserStr = JSONUtils.toJSON(damonUser2);
        String damonUserStr2 = JSONUtil.toJsonStr(damonUser2);

        return damonUser2;

    }


    @Transactional
    @PostMapping(value = "/createAccount")
    public DamonAccount createAccount(
            @RequestBody DamonAccount damonAccount,
            HttpServletRequest httpRequest, HttpServletResponse httpResponse, BindingResult result) {
        this.damonAccountDao.insert(damonAccount);
        return damonAccount;

    }


    @Transactional
    @PostMapping(value = "/deleteAccount")
    public DamonUser deleteAccount(
            @RequestBody DamonUser damonUser,
            HttpServletRequest httpRequest, HttpServletResponse httpResponse, BindingResult result) {

        Example<DamonUser> example = Example.of(damonUser);
        DamonUser existsDamonUser = this.damonUserRepo.findOne(example).orElse(null);
        existsDamonUser.getAccountList().remove(0);//parent 控制 child list 删除动作,需要在oneToMany 声明 orphanRemoval = true  , cascade = CascadeType.ALL 只表明parent 被delete时, child 同时被delete

        this.damonUserDao.save(existsDamonUser);
        return existsDamonUser;
    }

    @Transactional
    @PostMapping(value = "/deleteUser")
    public DamonUser deleteUser(
            @RequestBody DamonUser damonUser,
            HttpServletRequest httpRequest, HttpServletResponse httpResponse, BindingResult result) {
        Example<DamonUser> example = Example.of(damonUser);
        DamonUser existsDamonUser = this.damonUserRepo.findOne(example).orElse(null);
        this.damonUserDao.delete(existsDamonUser);
        return existsDamonUser;
    }

    @PostMapping(value = "/diff")
    public JsonNode diff(
            @RequestBody DamonUser damonUser,
            HttpServletRequest httpRequest, HttpServletResponse httpResponse, BindingResult result) {
        Example<DamonUser> example = Example.of(damonUser);
        DamonUser existsDamonUser = this.damonUserRepo.findOne(example).orElse(null);


        DamonUser user2 = new DamonUser();
        DeepCopyUtil.deepCopyObject(existsDamonUser, user2, false);

        DamonAccount account = new DamonAccount();
        account.setAccountName("account3");
        account.setUserId(user2.getPkId());
        DamonAccount account2 = new DamonAccount();
        account2.setAccountName("account4");
        account2.setUserId(user2.getPkId());

        List<DamonAddress> addressList = new ArrayList<>();
        DamonAddress address = new DamonAddress();
        address.setLocation("shanghai");
        address.setAccountId(user2.getAccountList().get(0).getAccountId());
        addressList.add(address);
        account.setAddressList(addressList);
        user2.getAccountList().add(account);
        user2.getAccountList().add(account2);
        user2.setName("changeName");

        DamonUserForJsonDiff damonUserForJsonDiff1 = new DamonUserForJsonDiff();
        damonUserForJsonDiff1.getUserList().add(existsDamonUser);

        DamonUserForJsonDiff damonUserForJsonDiff2 = new DamonUserForJsonDiff();
        damonUserForJsonDiff2.getUserList().add(user2);

        JsonNode oldJsonNode = JSONUtils.toJsonNode(damonUserForJsonDiff1);
        JsonNode newJsonNode = JSONUtils.toJsonNode(damonUserForJsonDiff2);
        JsonNode patch = JsonDiff.asJson(oldJsonNode, newJsonNode); //使用  com.flipkart.zjsonpatch 的方案. 主要区别在对于list 序列的显示.
        String diffStr2 = patch.toString();

        return patch;


    }


    @Transactional
    @PostMapping(value = "/cloneUser")
    public DamonUser cloneUser(
            @RequestBody DamonUser damonUser,
            HttpServletRequest httpRequest, HttpServletResponse httpResponse, BindingResult result) {
        Example<DamonUser> example = Example.of(damonUser);
        DamonUser existsDamonUser = this.damonUserRepo.findOne(example).orElse(null);
        DamonUser user = DeepCopyUtil.clone(existsDamonUser);
        this.damonUserDao.save(user);
        return user;
    }

    @Transactional
    @PostMapping(value = "/reverse")
    public DamonUser reverse(
            @RequestBody DamonUserReverseRequest damonUserReverseRequest,
            HttpServletRequest httpRequest, HttpServletResponse httpResponse, BindingResult result) {

        DamonUser existsDamonUser = this.damonUserRepo.findById(damonUserReverseRequest.getUserId()).orElse(null);
        if (existsDamonUser != null) {
            DamonUserLog queryLog = new DamonUserLog();
            queryLog.setUserId(damonUserReverseRequest.getUserId());
            queryLog.setVersion(damonUserReverseRequest.getVersion());
            Example<DamonUserLog> example = Example.of(queryLog);
            DamonUserLog log = this.damonUserLogRepo.findOne(example).orElse(null);
            if (log != null) {
                String userData = log.getUserData();
                DamonUser historyDamonUser = JSONUtils.fromJSON(userData, DamonUser.class);
                DeepCopyUtil.deepCopyObject(historyDamonUser, existsDamonUser, false);
                this.damonUserDao.save(existsDamonUser);
                return existsDamonUser;

            }
        }

        return null;
    }

    @Transactional
    @PostMapping(value = "/accountSave")
    public DamonAccount saveAccount(
            @RequestBody DamonAccount damonAccount,
            HttpServletRequest httpRequest, HttpServletResponse httpResponse, BindingResult result) {

        DamonAccount exisDamonAccount = this.damonAccountRepo.findById(damonAccount.getAccountId()).orElse(null);
        if (exisDamonAccount != null) {
            DeepCopyUtil.deepCopyObject(damonAccount, exisDamonAccount, false);
            this.damonAccountRepo.save(exisDamonAccount);
        }
        return exisDamonAccount;
    }


}
