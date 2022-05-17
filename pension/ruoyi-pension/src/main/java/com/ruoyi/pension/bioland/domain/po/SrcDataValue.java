package com.ruoyi.pension.bioland.domain.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.ruoyi.pension.bioland.domain.enums.DeviceType;
import lombok.Data;

/**
 * 
 * @TableName bioland_srcdata
 */
@TableName(value ="bioland_srcdata")
@Data
public class SrcDataValue implements Serializable {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private LocalDateTime created;
    private String srcData;
    private String start;
    private String version;
    private String user;
    private String customer;
    private String modelCode;
    private DeviceType deviceType;
    private String serialNumber;
    private String data1;
    private String data2;
    private String data3;
    private LocalDateTime checkTime;
    private String checkCode;
    private String separator43;
    private String imsi;
    private String separator59;
    private String latiLongitude;
    private String crcCode;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;



    public SrcDataValue(String srcdata) {
        this.srcData = srcdata;
        this.created = LocalDateTime.now();
        parseData();
    }

    protected void parseData() {
        this.start = this.srcData.substring(0, 2);
        this.version = this.srcData.substring(2, 4);
        this.user = this.srcData.substring(4, 5);
        this.customer = this.srcData.substring(5, 7);
        this.modelCode = this.srcData.substring(7, 9);
        this.deviceType = DeviceType.getEnumByCode(this.srcData.substring(9, 11)); // 机种码
        this.serialNumber = this.srcData.substring(11, 20);
        this.data1 = this.srcData.substring(20, 23);
        this.data2 = this.srcData.substring(23, 26);
        // 血糖仪是以mg/dL为单位传送，如果要显示为mmol/dL的单位，则要除以18，并四舍五入才能与血糖仪端显示的数据一致
        this.data3 = this.srcData.substring(26, 29);

        //设置检查时间
        int year = 2000 + Integer.parseInt(this.srcData.substring(29, 31));
        int month = Integer.parseInt(this.srcData.substring(31, 33));
        int day = Integer.parseInt(this.srcData.substring(33, 35));
        int hour = Integer.parseInt(this.srcData.substring(35, 37));
        int minute = Integer.parseInt(this.srcData.substring(37, 39));
        this.checkTime = LocalDateTime.of(year,month,day,hour,minute,0);

        this.checkCode = this.srcData.substring(39, 43);
        this.separator43 = this.srcData.substring(43, 44);
        this.imsi = this.srcData.substring(44, 59);
        this.separator59 = this.srcData.substring(59, 60);
        this.latiLongitude = this.srcData.substring(60, 85);

        if (this.srcData.length() > 85) {
            this.crcCode = this.srcData.substring(85);
        }
    }

    public String toString() {
        return "SrcDataValue [srcData=" + this.srcData + ", start="
                + this.start + ", version=" + this.version + ", member="
                + this.user + ", customer=" + this.customer + ", modelCode="
                + this.modelCode + ", deviceType=" + this.deviceType.getCode()
                + ", serialNumber=" + this.serialNumber + ", data1="
                + this.data1 + ", data2=" + this.data2 + ", data3="
                + this.data3 + ", year=" + checkTime.getYear() + ", month=" + checkTime.getMonthValue()
                + ", day=" + checkTime.getDayOfMonth() + ", hour=" + checkTime.getHour() + ", minute="
                + checkTime.getMinute() + ", checkCode=" + this.checkCode
                + ", separator43=" + this.separator43 + ", imsi=" + this.imsi
                + ", separator59=" + this.separator59 + ", latiLongitude="
                + this.latiLongitude + ", crcCode=" + this.crcCode + "]";
    }
}