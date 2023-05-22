package com.damon.springboot.treestructure.util;



import cn.hutool.core.util.ReflectUtil;
import com.damon.springboot.treestructure.model.base.BaseTreeEntityImpl;
import com.damon.springboot.treestructure.model.base.annotation.LogicDeletionField;
import com.damon.springboot.treestructure.model.base.annotation.ParentId;
import com.damon.springboot.treestructure.model.base.annotation.RootId;
import net.sf.cglib.beans.BeanMap;
import org.apache.commons.collections.CollectionUtils;


import javax.persistence.Id;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class DeepCopyUtil {

    private static final Map<Class<?>, Map<String, Integer>> classPropertyValueTypeCaches=new ConcurrentHashMap();
    private static final Map<Class<?>, Map<String, Boolean>> classPropertyWritableCaches=new ConcurrentHashMap();

    private static final String[] ignoreProperties = new String[]{"insertBy", "updateBy", "insertTime", "updateTime","tempEntityCopyFrom","tempEntityCopyFrom_","oldPrimaryKey","tempData","cglibBeanMap","tempDataMap_","sequenceName","version","CGLibBeanMap_","primaryKey"};

    static int VALUE_TYPE__PRIMITIVE=1;
    static int VALUE_TYPE__MAP=2;
    static int VALUE_TYPE__LIST=3;
    static int VALUE_TYPE__SET=4;
    static int VALUE_TYPE__BASE_ENTITY = 5;
    static int VALUE_TYPE__OTHER_EBAO_CLASS = 6;

    public static <T extends BaseTreeEntityImpl> T clone(BaseTreeEntityImpl source){
        BaseTreeEntityImpl  destOjbect = createNewInstance(source);
       deepCopyObject(source,destOjbect,true);
       return (T) destOjbect;
    }

    public static void deepCopyObject(Object source, Object target, Boolean resetPK)  {

        //BeanUtils.copyProperties(source, target, ignoreProperties);   这里不用Spring beanUtils copyProperties 方法,因为会将目标对象的hibernate session attach的对象给替换掉
        //copy from EntitySyncUtils
        Class<?> modelClass = source.getClass();
        BeanMap srcBeanMap = CGLibBeanMapHelper.createBeanMap(source);
        BeanMap dstBeanMap = CGLibBeanMapHelper.createBeanMap(target);
        Map<String, Integer> classPropertyValueTypeCache = getClassPropertyValueTypeCache(modelClass);
        Map<String, Boolean> classPropertyWritableCache = getClassPropertyWritableCache(modelClass);

        Iterator srcIterator = srcBeanMap.keySet().iterator();
        String idFieldName = "";
        String parentIdFieldName = "";
        String rootIdFieldName = "";
        if(resetPK) {
            Field[] fields = ReflectUtil.getFields(source.getClass());
            for (Field field : fields) {
                Class<?> fieldType = field.getType();
                String fieldName = field.getName();
                if (field.getAnnotation(Id.class) != null ) {
                    idFieldName =fieldName;
                }
                if(field.getAnnotation(ParentId.class)!=null) {
                    parentIdFieldName =fieldName;
                }
                if(field.getAnnotation(RootId.class)!=null) {
                    rootIdFieldName =fieldName;
                }
            }
        }

        while(srcIterator.hasNext()) {
            String propertyName  = srcIterator.next().toString();
            if(Arrays.stream(ignoreProperties).anyMatch(e->e.equals(propertyName))) {
                //do nothing
            } else {
                Class<?> valueClass = srcBeanMap.getPropertyType(propertyName);
                int propertyType = getPropertyValueType(classPropertyValueTypeCache, propertyName, valueClass);

                Object srcPropertyValue;
                if (propertyType == VALUE_TYPE__PRIMITIVE) {
                    srcPropertyValue = srcBeanMap.get(propertyName);
                    if(resetPK &&( idFieldName.equals(propertyName) || parentIdFieldName.equals(propertyName) || rootIdFieldName.equals(propertyName))) {
                        dstBeanMap.put(propertyName, null);
                    } else {
                        dstBeanMap.put(propertyName, srcPropertyValue);
                    }
                } else {
                    srcPropertyValue = srcBeanMap.get(propertyName);
                    boolean needSetTargetValue = false;
                    Object dstPropertyValue = dstBeanMap.get(propertyName);
                    if (srcPropertyValue == null) {
                        if (dstPropertyValue != null) {
                            if (propertyType == VALUE_TYPE__MAP) {
                                ((Map)dstPropertyValue).clear();
                            } else if (propertyType != VALUE_TYPE__LIST && propertyType != VALUE_TYPE__SET) {
                                dstPropertyValue = null;
                                needSetTargetValue = true;
                            } else {
                                //((Collection)dstPropertyValue).clear();
                                copyCollection((Collection)srcPropertyValue, (Collection)dstPropertyValue, resetPK);
                            }
                        }
                    } else {
                        needSetTargetValue = dstPropertyValue == null;
                        if (propertyType == VALUE_TYPE__BASE_ENTITY) {
                            if (dstPropertyValue == null) {
                                dstPropertyValue = createNewInstance((BaseTreeEntityImpl)srcPropertyValue);
                            }
                            deepCopyObject(srcPropertyValue, dstPropertyValue, resetPK);
                        } else if (propertyType == VALUE_TYPE__LIST || propertyType==VALUE_TYPE__SET) {
                            if(propertyType == VALUE_TYPE__LIST) {
                                if (dstPropertyValue == null) {
                                    dstPropertyValue = new ArrayList();
                                }
                            }
                            if(propertyType == VALUE_TYPE__SET) {
                                if (dstPropertyValue == null) {
                                    dstPropertyValue = new HashSet<>();
                                }
                            }
                            copyCollection((Collection)srcPropertyValue, (Collection)dstPropertyValue, resetPK);
                        }
                    }
                    if (needSetTargetValue) {
                        dstBeanMap.put(propertyName, dstPropertyValue);
                    }
                }
            }
        }
    }




    private static void copyCollection(Collection source, Collection target,  Boolean resetPk)  {
        if (!CollectionUtils.isEmpty(source) || !CollectionUtils.isEmpty(target)) {
            Object firstElement;
            if (!CollectionUtils.isEmpty(source)) {
                firstElement = source.iterator().next();
            } else {
                firstElement = target.iterator().next();
            }
            String logicDeletionFieldName = null;
            Field[] fields = ReflectUtil.getFields(firstElement.getClass());
            for (Field field : fields) {
                Class<?> fieldType = field.getType();
                String fieldName = field.getName();
                if (field.getAnnotation(LogicDeletionField.class) != null ) {
                    logicDeletionFieldName =fieldName;
                }
            }

            Map<Object, BaseTreeEntityImpl> srcMap = collectionToMap(source);
            Map<Object, BaseTreeEntityImpl> dstMap = collectionToMap(target);
            Iterator dstIterator = dstMap.entrySet().iterator();
            while(dstIterator.hasNext()) {
                Map.Entry<Object, BaseTreeEntityImpl> dstEntry = (Map.Entry)dstIterator.next();
                if (!srcMap.containsKey(dstEntry.getKey())) {
                    //todo 在这里需要判断 target 的泛型类是否标注为Logic deletion, 如果可以才做remove, 否则标注is_delete字段为Y
                    if(logicDeletionFieldName!=null) {
                        try {
                            Object o =  dstEntry.getValue();
                            Field isDeleteField = o.getClass().getDeclaredField(logicDeletionFieldName);
                            isDeleteField.setAccessible(true);
                            isDeleteField.set(o,"Y");
                        } catch (NoSuchFieldException | IllegalAccessException e) {
                            throw new RuntimeException("Can not get logicDelete field of object "+firstElement.getClass());
                        }
                    } else {
                        target.remove(dstEntry.getValue());
                    }


                }
            }
            Iterator sourceIterator = srcMap.entrySet().iterator();
            while(sourceIterator.hasNext()) {
                Map.Entry<Object, BaseTreeEntityImpl> srcEntry = (Map.Entry)sourceIterator.next();

                BaseTreeEntityImpl srcEntity = srcEntry.getValue();
                if (srcEntity == null) {
                    throw new RuntimeException("Entity in collection can't be null!");
                }
                BaseTreeEntityImpl dstEntity = dstMap.get(srcEntry.getKey());
                boolean newElement = false;
                if (dstEntity == null) {
                    if (srcEntity.getPrimaryKey() != null && !resetPk ) {
                       // throw new RuntimeException("For new created collection element, primaryKey should not be set!");
                    }
                    newElement = true;
                    dstEntity = createNewInstance(srcEntity);
                }
                deepCopyObject(srcEntity,dstEntity,resetPk);
                if (newElement) {
                    target.add(dstEntity);
                }
            }
        }
    }

    private static Map<Object, BaseTreeEntityImpl> collectionToMap(Collection<BaseTreeEntityImpl> col) {
        try {
            Map<Object, BaseTreeEntityImpl> map = new LinkedHashMap();
            Object obj;
            Object key;
            if(col!=null) {
                for (Iterator var2 = col.iterator(); var2.hasNext(); map.put(key, (BaseTreeEntityImpl) obj)) {
                    obj = var2.next();
                    Long pk = ((BaseTreeEntityImpl) obj).getPrimaryKey();
                    if (pk != null) {
                        key = pk;
                    } else {
                        key = obj;
                    }
                }
            }

            return map;
        } catch (ClassCastException var6) {
            throw new RuntimeException("ClassCastException, please check whether the collection element is type of BaseTreeEntityImpl!", var6);
        }
    }

    private static BaseTreeEntityImpl createNewInstance(BaseTreeEntityImpl existingEntity) {
        try {
            return existingEntity.getClass().newInstance();
        } catch (Exception var2) {
            throw new RuntimeException(var2);
        }
    }



    private static int getPropertyValueType(Map<String, Integer> classPropertyValueTypeCache, String propertyName, Class<?> valueClass) {
        Integer type = classPropertyValueTypeCache.get(propertyName);
        if (type == null) {
            String valueClassName = valueClass.getName();
            if (isPrimitive(valueClass)) {
                type = VALUE_TYPE__PRIMITIVE;
            } else if (Map.class.isAssignableFrom(valueClass)) {
                type = VALUE_TYPE__MAP;
            } else if (List.class.isAssignableFrom(valueClass)) {
                type = VALUE_TYPE__LIST;
            } else if (Set.class.isAssignableFrom(valueClass)) {
                type = VALUE_TYPE__SET;
            } else if (BaseTreeEntityImpl.class.isAssignableFrom(valueClass)) {
                type = VALUE_TYPE__BASE_ENTITY;
            } else if (valueClassName.startsWith("com.ebao")) {
                type = VALUE_TYPE__OTHER_EBAO_CLASS;
            } else {
                if (!valueClass.isArray()) {
                    throw new RuntimeException("Property type:" + valueClassName + " is not supportted in Data Sync!");
                }

                if (!valueClass.isPrimitive()) {
                    throw new RuntimeException("Property type:" + valueClassName + " is not supportted in Data Sync!");
                }

                type = VALUE_TYPE__PRIMITIVE;
            }

            classPropertyValueTypeCache.put(propertyName, type);
        }

        return type;
    }

    private static boolean isPrimitive(Class<?> valueClass) {
        return valueClass.isPrimitive() || Number.class.isAssignableFrom(valueClass) || LocalDateTime.class.isAssignableFrom(valueClass) || LocalDate.class.isAssignableFrom(valueClass) || String.class.isAssignableFrom(valueClass) || Character.class.isAssignableFrom(valueClass) || Boolean.class.isAssignableFrom(valueClass) || Date.class.isAssignableFrom(valueClass) || valueClass.isEnum();
    }

    private static synchronized Map<String, Integer> getClassPropertyValueTypeCache(Class<?> clazz) {
        Map<String, Integer> classPropertyValueTypeCache = classPropertyValueTypeCaches.get(clazz);
        if (classPropertyValueTypeCache == null) {
            classPropertyValueTypeCache = new ConcurrentHashMap();
            classPropertyValueTypeCaches.put(clazz, classPropertyValueTypeCache);
        }

        return classPropertyValueTypeCache;
    }

    private static synchronized Map<String, Boolean> getClassPropertyWritableCache(Class<?> clazz) {
        Map<String, Boolean> classPropertyWritableCache = classPropertyWritableCaches.get(clazz);
        if (classPropertyWritableCache == null) {
            classPropertyWritableCache = new ConcurrentHashMap();
            classPropertyWritableCaches.put(clazz, classPropertyWritableCache);
        }

        return classPropertyWritableCache;
    }



}
