package com.ruoyi.pension.nursing.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ruoyi.common.annotation.Excel;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NursingRecordVo {
    @Excel(name = "单号", type = Excel.Type.EXPORT)
    private String orderNumber;
    @Excel(name = "申请人", type = Excel.Type.EXPORT)
    private String applyName;
    @Excel(name = "电话", type = Excel.Type.EXPORT)
    private String applyPhone;
    @Excel(name = "归属", type = Excel.Type.EXPORT)
    private String deptName;
    @Excel(name = "申请时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss", type = Excel.Type.EXPORT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime applyTime;
    @Excel(name = "开始时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss", type = Excel.Type.EXPORT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime beginTime;
    @Excel(name = "结束时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss", type = Excel.Type.EXPORT)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime endTime;
    @Excel(name = "服务项目", type = Excel.Type.EXPORT)
    private String items;
    @Excel(name = "备注", type = Excel.Type.EXPORT)
    private String remark;
    @Excel(name = "护理人员", type = Excel.Type.EXPORT)
    private String name;
    @Excel(name = "护理电话", type = Excel.Type.EXPORT)
    private String phone;
    @Excel(name = "护理级别", type = Excel.Type.EXPORT)
    private String levelName;
    @Excel(name = "地址", type = Excel.Type.EXPORT)
    private String address;
    @Excel(name = "病历信息", type = Excel.Type.EXPORT)
    private String medicalHistory;
    @Excel(name = "监护人", type = Excel.Type.EXPORT)
    private String guardian;
    @Excel(name = "监护电话", type = Excel.Type.EXPORT)
    private String guardianPhone;
    @Excel(name = "订单ID", type = Excel.Type.EXPORT)
    private Integer orderId;
    @Excel(name = "订单号", type = Excel.Type.EXPORT)
    private String orderSn;
}
