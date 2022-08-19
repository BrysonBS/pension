package com.ruoyi.pension.nursing.domain.po;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.ruoyi.pension.common.domain.po.BasePensionEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

/**
 * 
 * @TableName nursing_order
 */
@TableName(value ="nursing_order")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class NursingOrder extends BasePensionEntity implements Serializable {
    /**
     * 订单id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 所属机构id
     */
    private Long deptId;

    /**
     * 优惠券id
     */
    private Integer couponId;

    /**
     * 订单编号
     */
    private String orderSn;

    /**
     * 订单总金额
     */
    private BigDecimal totalAmount;

    /**
     * 应付金额（实际支付金额）
     */
    private BigDecimal payAmount;

    /**
     * 优惠券抵扣金额
     */
    private BigDecimal couponAmount;

    /**
     * 管理员后台调整订单使用的折扣金额
     */
    private BigDecimal discountAmount;
    /**
     * 退款总金额
     */
    private BigDecimal refundAmount;

    /**
     * 支付方式：0->未支付；1->支付宝；2->微信
     */
    @NotNull(message = "支付方式必填!")
    private Integer payType;

    /**
     * 订单来源：0->PC订单；1->app订单
     */
    @NotNull(message = "订单来源必填!")
    private Integer sourceType;

    /**
     * 订单状态：0->未支付；1->已支付；2->已完成；3->已关闭；
     */
    private Integer status;

    /**
     * 省份/直辖市
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 区
     */
    private String district;

    private String address;
    /**
     * 详细地址
     */
    private String detailAddress;

    /**
     * 纬度
     */
    @NotNull(message = "地址必填!")
    private BigDecimal lat;

    /**
     * 经度
     */
    @NotNull(message = "地址必填!")
    private BigDecimal lng;

    /**
     * 评价时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime commentTime;

    /**
     * 评价内容
     */
    private String commentText;

    /**
     * 评价星数：0->5
     */
    private Integer star;

    /**
     * 删除标志（0代表存在 2代表删除）
     */
    @TableLogic(value = "0",delval = "2")
    private String delFlag;

    /**
     * 备注
     */
    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @NotNull(message = "护理时间必填!")
    private LocalDateTime beginTime;
    @NotNull(message = "老人必填!")
    private Integer personId;
    private Integer workerId;
    private String qrCode;
    private Integer recordId;


    @TableField(exist = false)
    private List<Integer> statusList;
    @TableField(exist = false)
    private List<Integer> workerIds;
    @TableField(exist = false)
    private List<NursingOrderItems> orderItems = Collections.emptyList();
    @JsonAlias({"user_name","userName"})
    @TableField(exist = false)
    private String userName;
    @JsonAlias({"dept_name","deptName"})
    @TableField(exist = false)
    private String deptName;
    /** 失能等级 */
    @JsonAlias({"dict_disability_level","dictDisabilityLevel"})
    @TableField(exist = false)
    private String dictDisabilityLevel;
    /** 护理级别 */
    @JsonAlias({"dict_level_id","dictLevelId"})
    @TableField(exist = false)
    private String dictLevelId;
    @TableField(exist = false)
    /** 老人姓名... */
    private String name;
    /** 老人电话 */
    @TableField(exist = false)
    private String phone;
    /** 监护人 */
    @TableField(exist = false)
    private String guardian;
    /** 监护人电话 */
    @JsonAlias({"guardian_phone","guardianPhone"})
    @TableField(exist = false)
    private String guardianPhone;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @TableField(exist = false)
    private LocalDateTime startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @TableField(exist = false)
    private LocalDateTime endTime;
    @TableField(exist = false)
    private String fullAddress;
    //服务项目价格列表
    @TableField(exist = false)
    List<NursingOrderItems> nursingOrderItems;
    @NotNull(message = "护理项目必填")
    @TableField(exist = false)
    @JsonInclude
    private List<Integer> serviceItemsIds;
    @TableField(exist = false)
    private Map<String, String> params;
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}