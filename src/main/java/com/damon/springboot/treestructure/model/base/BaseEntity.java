package com.damon.springboot.treestructure.model.base;

import com.damon.springboot.treestructure.util.JSONUtils;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.Transient;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public interface BaseEntity extends Serializable {
    Logger logger = LoggerFactory.getLogger("BaseEntity");
    String JSON_KEY__PK = "@pk";

    @JsonProperty("@pk")
    Long getPrimaryKey();

    Long preparePrimaryKey();

    @JsonProperty("@pk")
    void setPrimaryKey(Long key);

    default String diff(BaseEntity baselineEntity) {
        String diff = "";
        return diff;
    }

    default String diff(BaseEntity baselineEntity, String propertyForArrayIndex) {
        String diff = "";
        return diff;
    }

    default String toJson() {
        return JSONUtils.toJSON(this);
    }

    boolean isPropertyExcludedInDataSync_(String propertyName);

    boolean isPropertyIncludedInDataSync_(String propertyName);

    @Transient
    Map<String, Object> tempData = new LinkedHashMap<>();

    @JsonAnySetter
    default void setTempData(String key, Object value) {
        tempData.put(key, value);
    }

    @JsonProperty
    default Map<String,Object> getTempData() {
        return this.tempData;
    }





}
