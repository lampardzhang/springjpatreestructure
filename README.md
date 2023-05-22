
# 基于Spring boot JPA 实现复杂多层级业务实体的保存修改



## 功能要点

​	很多时候我们的业务实体比较复杂, 可能有多个层级，以用户为例，一个用户可能有多个地址信息， 多个邮件地址信息, 而一个地址信息又有可能拥有多个邮编信息. 整体来看, 用户实体就是一个树状结构, 但如果我们后台使用oracle /mysql 这类行存储结构的数据库时, 用户, 地址,邮编,邮件可能要分别对应一张实体表,需要开发人员在业务逻辑层实现大量代码来实现用户对象的创建,修改等操作. 

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



### Installation

You'll need maven, npm and JAVA pre-installed, which most probably you'll already have.  To run the application, run the following commands from the console.

```sh
$ mvn clean
$ mvn package
$ java -jar target/springboot-0.0.1-SNAPSHOT.jar
```



Happy coding 😃
