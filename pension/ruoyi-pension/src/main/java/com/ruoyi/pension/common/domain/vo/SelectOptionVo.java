package com.ruoyi.pension.common.domain.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SelectOptionVo<K,V> {
    @TableId(type = IdType.NONE)
    private K key;
    private String label;
    private V value;
    private String info;

    @TableField(exist = false)
    @JsonIgnore
    private Map<String, String> params;
}
