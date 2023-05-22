package com.damon.springboot.treestructure.dao.base;


import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

public abstract class BaseRepository<T, K extends Serializable> {
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private Class<T> entityClass;

    public BaseRepository() {
    }

    protected JdbcTemplate getJdbcTemplate() {
        return this.jdbcTemplate;
    }

    protected NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
        return this.namedParameterJdbcTemplate;
    }

    public Class<T> getEntityClass() {
        if (this.entityClass == null) {
            Type[] types = ((ParameterizedType)this.getClass().getGenericSuperclass()).getActualTypeArguments();
            this.entityClass = (Class)types[0];
        }

        return this.entityClass;
    }

    public EntityManager getEntityManager() {
        return this.entityManager;
    }

    public T load(K id) {
        return this.getEntityManager().find(this.getEntityClass(), id);
    }

    public T loadAndLock(K id) {
        this.getEntityManager().flush();
        return this.getEntityManager().find(this.getEntityClass(), id, LockModeType.PESSIMISTIC_WRITE);
    }

    public List<T> loadAll() {
        return this.loadAll(null, null);
    }

    public List<T> loadAll(List<K> idList) {
        return this.loadAll(idList, 500);
    }

    public List<T> loadAll(List<K> idList, int batchSize) {
        Session session = this.getEntityManager().unwrap(Session.class);
        return session.byMultipleIds(this.getEntityClass()).withBatchSize(batchSize).multiLoad(idList);
    }

    public List<T> loadAll(FlushModeType flushMode) {
        return this.loadAll(null, null, flushMode);
    }

    public List<T> loadAll(Integer start, Integer limit) {
        return this.loadAll(start, limit, FlushModeType.AUTO);
    }

    public List<T> loadAll(Integer start, Integer limit, FlushModeType flushMode) {
        CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<T> criteria = builder.createQuery(this.getEntityClass());
        Root<T> root = criteria.from(this.getEntityClass());
        criteria.select(root);
        TypedQuery<T> query = this.getEntityManager().createQuery(criteria);
        if (start != null) {
            query.setFirstResult(start);
        }

        if (limit != null) {
            query.setMaxResults(limit);
        }

        query.setFlushMode(flushMode);
        return query.getResultList();
    }

    public int countAll() {
        CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> criteria = builder.createQuery(Long.class);
        Root<T> root = criteria.from(this.getEntityClass());
        criteria.select(builder.count(root));
        return this.getEntityManager().createQuery(criteria).getSingleResult().intValue();
    }

    public void delete(T t) {
        this.getEntityManager().remove(t);
    }

    public void insert(T t) {
        this.getEntityManager().persist(t);
    }

    public void insertOrUpdate(T t) {
        this.getEntityManager().persist(t);
    }
}