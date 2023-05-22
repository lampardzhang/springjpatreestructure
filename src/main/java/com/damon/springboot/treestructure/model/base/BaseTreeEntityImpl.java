package com.damon.springboot.treestructure.model.base;



import com.damon.springboot.treestructure.model.base.annotation.ParentId;
import com.damon.springboot.treestructure.model.base.annotation.RootId;

import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

@MappedSuperclass
public abstract  class BaseTreeEntityImpl extends BaseEntityImpl {

    @PrePersist
    public void prePersist() {
        super.prePersist();
        Long rootId = null;
        Long newValue = this.getPrimaryKey();
        if(isRoot()){
            rootId = this.getPrimaryKey();
        }
        try {
            this.maintainMissingParentId(newValue,rootId,false);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @PreUpdate
    public void preUpdate() {
        super.preUpdate();
        Long rootId = null;
        Long newValue = this.getPrimaryKey();
        if(isRoot()){
            rootId = this.getPrimaryKey();
        }
        try {
            this.maintainMissingParentId(newValue,rootId,false);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

    }

    private boolean isRoot() {
        //get field with annoation with ParentId and set this value.
        for(Field field : this.getClass().getDeclaredFields()){
            Class type = field.getType();
            String name = field.getName();
            if(field.getAnnotation(ParentId.class) != null) {
                return false;
            }
        }
        return true;
    }

    void maintainMissingParentId(Long parentId, Long rootId,Boolean triggerByParent) throws IllegalAccessException {

        //getPK of own
        Long pkId = this.getPrimaryKey();

        if(triggerByParent) {
            if (parentId != null && pkId != parentId) { //to avoid set parentId as it's PK, but the preCondition is  we must use only one sequence to get PK or use snawflake to get PK
                //get field with annoation with ParentId and set this value.
                for (Field field : this.getClass().getDeclaredFields()) {
                    Class type = field.getType();
                    String name = field.getName();
                    if (field.getAnnotation(ParentId.class) != null) {
                        field.setAccessible(true);
                        Object value = field.get(this);
                        //if (value == null) {    //if value is not null ,mean this is a subObject and it's parentId already maintained by it's parent. should not update this parentId by it's own pkId
                            field.set(this, parentId);
                        //}
                    }
                }
            }

            if (rootId != null && pkId != rootId) {
                for (Field field : this.getClass().getDeclaredFields()) {
                    Class type = field.getType();
                    String name = field.getName();
                    if (field.getAnnotation(RootId.class) != null) {
                        field.setAccessible(true);
                        Object value = field.get(this);
                        if (value == null) {
                            field.set(this, rootId);
                        }
                    }
                }
            }
        }

        //triger all abstractEnityt maintainMissingParentId method this own PK
        for(Field field : this.getClass().getDeclaredFields()){
            field.setAccessible(true);
            Class type = field.getType();
            String name = field.getName();
            Object value = field.get(this);
            if(value!=null) {
                if (BaseTreeEntityImpl.class.isAssignableFrom(field.getType())) {
                    BaseTreeEntityImpl value1 = (BaseTreeEntityImpl) value;
                    value1.maintainMissingParentId(pkId, rootId,true);
                }
                if (List.class.isAssignableFrom(field.getType())) {
                    for (Object o : (Collection) value) {
                        if (BaseTreeEntityImpl.class.isAssignableFrom(o.getClass())) {
                            ((BaseTreeEntityImpl) o).maintainMissingParentId(pkId, rootId,true);
                        }
                    }

                }
            }
        }

    }
}
