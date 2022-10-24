package com.ruoyi.pension.common.domain.po;


import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ruoyi.common.core.domain.model.LoginUser;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class BasePensionEntity {
    /**
     * 创建者
     */
    private String createBy;
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime createTime;
    /**
     * 更新者
     */
    private String updateBy;
    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime updateTime;

    @TableField(exist = false)
    @JsonIgnore
    private LoginUser loginUser;
    @TableField(exist = false)
    @JsonIgnore
    private Map<String, Object> params;
}
