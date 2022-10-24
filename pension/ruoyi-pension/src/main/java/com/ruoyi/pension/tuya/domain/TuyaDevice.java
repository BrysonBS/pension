package com.ruoyi.pension.tuya.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ruoyi.pension.tuya.converter.TuyaSecondsDeserializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 
 * @TableName tuya_device
 */
@TableName(value ="tuya_device")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class TuyaDevice implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Long deptId;
    private Long userId;
    private String uid;
    private String category;
    @JsonAlias({"categoryName","category_name"})
    private String categoryName;
    @JsonAlias({"createTime","create_time"})
    @JsonDeserialize(using = TuyaSecondsDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime createTime;
    private String icon;
    private String ip;
    private String lat;
    @JsonAlias({"localKey","local_key"})
    private String localKey;
    private String lon;
    private String name;
    private String model;
    private Boolean online;
    @JsonAlias({"productId","product_id"})
    private String productId;
    @JsonAlias({"ownerId","owner_id"})
    private String ownerId;
    private Boolean sub;
    @JsonAlias({"deviceId","device_id"})
    private String deviceId;
    @JsonAlias({"time_zone","timeZone"})
    private String timeZone;

    @TableField(exist = false)
    @JsonIgnore
    private Map<String, String> params;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TuyaDevice that = (TuyaDevice) o;
        return Objects.equals(uid, that.uid) && Objects.equals(category, that.category) && Objects.equals(productId, that.productId) && Objects.equals(deviceId, that.deviceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uid, category, productId, deviceId);
    }
}