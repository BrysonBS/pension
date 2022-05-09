package com.ruoyi.pension.owon.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

/**
 * 
 * @TableName v_ble_data_report
 */
@TableName(value ="v_ble_data_report")
@Data
public class VBleDataReport implements Serializable {
    private Integer id;
    private String code;
    private String mac;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime ts;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime created;
    private Integer bid;
    private String type;
    private String command;
    private Integer cid;
    private String ieee;
    private Integer ep;
    private Integer status;
    private Integer devType;
    private Integer heartRate;
    private Integer respiratoryRate;
    private Integer sleepFlag;
    private Integer statusValue;
    private Integer dataType;
    private Integer awakeFlag;
    private Integer batCharge;
    private Integer batLvl;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}