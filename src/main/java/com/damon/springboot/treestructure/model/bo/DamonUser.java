package com.damon.springboot.treestructure.model.bo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.damon.springboot.treestructure.model.base.BaseTreeEntityImpl;

import javax.persistence.*;
import java.util.List;


@Entity
@Table(name = "TEST_USER")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DamonUser extends BaseTreeEntityImpl {

    @Id
    @Column(name = "LIST_ID", nullable = false)
    private Long pkId;

    @Column(name = "USER_NAME", nullable = false)
    private String name;


    @Version
    @Column(name = "VERSION", nullable = false)
    private Long version;


    @OneToOne(targetEntity = DamonZipInfo.class,
            fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "LIST_ID", referencedColumnName = "LIST_ID")
    private DamonZipInfo zip;

    public DamonZipInfo getZip() {
        return zip;
    }

    public void setZip(DamonZipInfo zip) {
        this.zip = zip;
    }

    /*  @OneToOne(targetEntity = DamonAccount.class ,mappedBy = "userId",
            fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private DamonAccount account;
*/

   @OneToMany(targetEntity = DamonAccount.class ,mappedBy = "userId",
            fetch = FetchType.LAZY, cascade = CascadeType.ALL,orphanRemoval = true)
    private List<DamonAccount> accountList;


   /* @OneToOne(targetEntity = DamonAddress.class,
            fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "ADDRESS_ID", referencedColumnName = "ADDRESS_ID")
    private DamonAddress address;

    public DamonAddress getAddress() {
        return address;
    }

    public void setAddress(DamonAddress address) {
        this.address = address;
    }*/

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DamonAccount> getAccountList() {
        return accountList;
    }

    public void setAccountList(List<DamonAccount> accountList) {
        this.accountList = accountList;
    }

    public Long getPkId() {
        return pkId;
    }

    public void setPkId(Long pkId) {
        this.pkId = pkId;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public String getSequenceName() {
        return null;
    }

    @Override
    public Long getPrimaryKey() {
        return this.pkId;
    }

    @Override
    public void setPrimaryKey(Long key) {
        this.pkId = key;

    }
    @Override
    public boolean isPropertyIncludedInDataSync_(String propertyName) {
        return "zip".equals(propertyName) || "address".equals(propertyName);
    }

}
