
# 基于Spring boot JPA 实现复杂多层级业务实体的保存修改



## 功能要点

### 级联结构的实现	

​		很多时候我们的业务实体比较复杂, 可能有多个层级，以用户为例，一个用户可能有多个地址信息， 多个邮件地址信息, 而一个地址信息又有可能拥有多个邮编信息. 整体来看, 用户实体就是一个树状结构, 但如果我们后台使用oracle /mysql 这类行存储结构的数据库时, 用户, 地址,邮编,邮件可能要分别对应一张实体表,需要开发人员在业务逻辑层实现大量代码来实现用户对象的创建,修改等操作. 

一般来说实现这种父子关系,在JPA中通过声明oneToMany 以及 ManyToOne 两个relationship. 为了简化业务代码实现逻辑, 在本项目, 父对象中仍然会声明oneToMany的relationship, 比如:

```
@OneToMany(targetEntity = DamonAccount.class ,mappedBy = "userId",
         fetch = FetchType.LAZY, cascade = CascadeType.ALL,orphanRemoval = true)
 private List<DamonAccount> accountList;
```

而在子对象DamonAccount 中, 不声明ManyToOne 的relatationship, 而是通过一个新的Annotation **parentId** 来声明具体是那个field 作为指向父对象的外键. 代码范例如下

```
@JsonIgnore
@ParentId
@Column(name = "USER_ID")
private Long userId;
```

在JPA 进行数据库写入时, overwrite prePersist 方法, 遍历子对象的 parentId property 进行赋值.



#### 使用PostMan进行接口测试

------

以下是使用postman 创建一条新的user 记录的范例json数据

`{`    

​    `"name": "damone-commerce222",`    

​    `"zip": {` 

​        `"zipCode": "200433"`        

​    `},`

​    `"accountList": [`

​        `{`        

​            `"accountName": "account1"`                  

​        `}`

​    `],`    

​    `"testData":"test"`

`}`

业务对象结构为 User 对象有 Account 子对象, 关系为oneToMany .

```
@OneToMany(targetEntity = DamonAccount.class ,mappedBy = "userId",
         fetch = FetchType.LAZY, cascade = CascadeType.ALL,orphanRemoval = true)
 private List<DamonAccount> accountList;
```

而Account 对象有Address 子对象,关系同样为oneToMany

```
@OneToMany(targetEntity = DamonAddress.class ,mappedBy = "accountId",
        fetch = FetchType.LAZY, cascade = CascadeType.ALL,orphanRemoval = true)
//@Fetch(FetchMode.SELECT)
private List<DamonAddress> addressList;
```

通过以上级联关系定义, 后端业务代码无需实现复杂的业务逻辑来实现user,account, address 等多个业务对象的级联关系, 只需要如下简单代码即可:

```
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
```

### DeepCopy的实现

​	对于复杂业务对象进行save保存操作时,对于从前端接口传递的业务对象模型数据, 无需一个业务对象一个业务对象分别做copy, 只需要调用 DeepCopyUtil 工具类提供的deepCopy 方法即可. 该方法通过递归实现子对象的级联copy. 

​	需要注意的是对于List 对象,如果前端传递的Source Object 中删除了一条记录, save 之后该记录在数据表中会被物理删除掉. 可以通过声明 *@LogicDeletionField*来实现逻辑删除.范例代码:

```
@Column(name="IS_DELETE")
@LogicDeletionField
private String isDelete;
```

save 之后该对象的isDelete 会被赋值为“Y” 以实现逻辑删除效果. 





### Installation

You'll need maven, npm and JAVA pre-installed, which most probably you'll already have.  To run the application, run the following commands from the console.

```sh
$ mvn clean
$ mvn package
$ java -jar target/springboot-0.0.1-SNAPSHOT.jar
```



Happy coding 😃
