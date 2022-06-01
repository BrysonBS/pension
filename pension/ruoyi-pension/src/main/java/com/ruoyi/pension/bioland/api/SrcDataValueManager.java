package com.ruoyi.pension.bioland.api;

import com.ruoyi.pension.bioland.domain.po.SrcDataValue;
import com.ruoyi.pension.common.domain.enums.Operation;
import com.ruoyi.pension.common.domain.vo.NoticeVo;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class SrcDataValueManager {
    public static NoticeVo getNoticeInfo(SrcDataValue srcDataValue) {
        NoticeVo noticeVo = NoticeVo.builder()
                .tags(new ArrayList<>())
                .enable(false)//默认不通知
                .ieee(srcDataValue.getSerialNumber())
                .ts(srcDataValue.getCreated())
                .time(srcDataValue.getCheckTime())
                .build();
        switch (srcDataValue.getDeviceType()){
            //血压计
            case BLPRESSURE -> setBloodPressure(noticeVo,srcDataValue);
            //血糖仪
            case BLGLUCOSE -> setBloodGlucose(noticeVo,srcDataValue);
            default -> {}
        }
        return noticeVo;
    }

    //血压计信息设置
    private static void setBloodPressure(NoticeVo noticeVo,SrcDataValue srcDataValue){
        int hbp = Integer.parseInt(srcDataValue.getData1());
        int lbp = Integer.parseInt(srcDataValue.getData2());
        int heartRate = Integer.parseInt(srcDataValue.getData3());
        if(hbp > 0 || lbp > 0 || heartRate > 0 )
            noticeVo.setEnable(true);
        StringBuilder builder = new StringBuilder();
        builder.append("[血高压: ").append(hbp).append("]")
                .append("[血低压: ").append(lbp).append("]")
                .append("[心率: ").append(heartRate).append("]");
        noticeVo.setHbp(hbp);
        noticeVo.setLbp(lbp);
        noticeVo.setHeartRate(heartRate);
        noticeVo.setInfo(builder.toString());
        noticeVo.setOperation(Operation.REPORT_BLOOD_PRESSURE);
    }
    //血糖仪信息设置
    private static void setBloodGlucose(NoticeVo noticeVo,SrcDataValue srcDataValue){
        int glucose = Integer.parseInt(srcDataValue.getData3());
        if(glucose > 0) noticeVo.setEnable(true);
        noticeVo.setGlucose(glucose);
        noticeVo.setInfo("[血糖: " + new DecimalFormat("#.##").format(glucose/18D) + "]");
        noticeVo.setOperation(Operation.REPORT_BLOOD_GLUCOSE);
    }
}
