package com.ruoyi.pension.owon.api;

import com.ruoyi.pension.common.domain.consts.PensionBusiness;
import com.ruoyi.pension.common.domain.enums.Operation;
import com.ruoyi.pension.owon.domain.dto.Argument;
import com.ruoyi.pension.owon.domain.dto.Response;
import com.ruoyi.pension.owon.domain.po.Device;
import com.ruoyi.pension.common.domain.vo.NoticeVo;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class StatusManager {
    private static final Logger logger = LoggerFactory.getLogger(StatusManager.class);
   public static <T> NoticeVo getNoticeInfo(Device device, T object){
       Integer cid = device.getCid();
       NoticeVo noticeVo = NoticeVo.builder()
               .type(PensionBusiness.NOTIFICATION)
               .tags(new ArrayList<>())
               .enable(false)//默认无需报警
               .build();
       switch (cid){
           case 1://睡眠带
               if(object instanceof Argument argument)
                    setSleepace(argument,noticeVo);
               break;
           case 12://门磁
           case 13://门磁
               setSensorDoor(object,noticeVo);
               break;
           case 14://可燃气体报警器
           case 15://可燃气体报警器
           case 19://紧急按钮
           case 20://紧急按钮
           case 21://拉绳紧急呼叫器
           case 22://拉绳紧急呼叫器
           case 62://烟雾报警器
               if(object instanceof Response response)
                   setSensorSos(response,noticeVo);
               break;
           case 30://多功能传感器
               break;
           case 74://漏水传感器
               if(object instanceof Response response)
                   setSensorWater(response,noticeVo);
               break;
           case 89://跌倒报警器
               if(object instanceof Argument<?,?> argument)
                   setFallDetectNotify(argument,noticeVo);
               break;
           default:
               break;
       }
       return noticeVo;
   }

    /**
     * 跌倒报警器
     * @param argument
     * @param noticeVo
     */
   public static void setFallDetectNotify(Argument<?,?> argument,NoticeVo noticeVo){
       StringBuilder builder = new StringBuilder();
       int changes = argument.getChanges();
       int basicStatus = argument.getBasicStatus();
       int turnOver = argument.getTurnOver();
       int respiratoryRate = argument.getRespiratoryRate();
       int abedExceptionAlarm = argument.getAbedExceptionAlarm();
       int numOfPeopleDetected = argument.getNumOfPeopleDetected();
       if((changes & 1) == 1) builder.append(
               switch (basicStatus){
                   case 0 -> {
                       noticeVo.setEnable(true);
                       yield "[无人]";
                   }
                   case 1 -> {
                       //noticeVo.setEnable(true);
                       yield "活动";
                   }
                   case 2 -> {
                       noticeVo.setEnable(true);
                       yield "[坐姿]";
                   }
                   case 3 -> {
                       noticeVo.setEnable(true);
                       yield "[在床]";
                   }
                   case 4 -> {
                       noticeVo.setEnable(true);
                       yield "[低姿态]";
                   }
                   case 5 -> {
                       noticeVo.setEnable(true);
                       yield "[跌倒]";
                   }
                   default -> "";
               }
       );

       if(((changes >>> 1) & 1) == 1 && turnOver == 1){
          noticeVo.setEnable(true);
          builder.append("[翻身]");
       }
       if(((changes >>> 2) & 1) == 1 && respiratoryRate > 0) {
           noticeVo.setEnable(true);
           builder.append("[呼吸率: ").append(respiratoryRate).append("]");
       }
       if(((changes >>> 5) & 1) == 1 && respiratoryRate > 0) {
           builder.append(
               switch (abedExceptionAlarm){
                   case 0 -> "[正常]";
                   case 1 -> {
                       noticeVo.setEnable(true);
                       yield "[长时间无活动]";
                   }
                   case 2 -> {
                       noticeVo.setEnable(true);
                       yield "[长时间无呼吸]";
                   }
                   case 3 -> {
                       noticeVo.setEnable(true);
                       yield "[长时间无活动且无呼吸]";
                   }
                   default -> "";
               }
           );
       }
       if(((changes >>> 6) & 1) == 1){
           builder.append("[活动人数: ").append(numOfPeopleDetected).append("]");
       }
       noticeVo.setInfo(builder.toString());
   }
    /**
     * 漏水传感器
     * @param response
     * @param noticeVo
     */
   public static void setSensorWater(Response<?> response,NoticeVo noticeVo){
       String state = response.getStatus();
       StringBuilder builder = new StringBuilder();
       if(Strings.isNotBlank(state)) {
           int status = Integer.parseInt(response.getStatus());
           if((status & 1) == 1 || ((status >>> 1) & 1) == 1)
               builder.append("[接触到水!]");
           setSensorStatus(builder,status);
       }
       noticeVo.setEnable(true);
       noticeVo.setInfo(builder.toString());
   }
    /**
     * 门磁
     * @param object
     * @return
     */
   public static void setSensorDoor(Object object,NoticeVo noticeVo){
       Integer status = null;
       if(object instanceof Response)
           status = Integer.parseInt(((Response<?>)object).getStatus());
       else if(object instanceof Argument)
           status = ((Argument<?,?>)object).getStatus();
       StringBuilder builder = new StringBuilder();
       if(status != null) {
           if((status & 1) == 1 || ((status >>> 1) & 1) == 1)
               builder.append("[门开]");
           else builder.append("[门关]");
           setSensorStatus(builder,status);
       }
       noticeVo.setEnable(true);
       noticeVo.setInfo(builder.toString());
   }
    /**
     * 紧急按钮
     * @param response
     * @return
     */
   public static void setSensorSos(Response<?> response,NoticeVo noticeVo){
       String state = response.getStatus();
       StringBuilder builder = new StringBuilder();
       if(Strings.isNotBlank(state)) {
           int status = Integer.parseInt(response.getStatus());
           if((status & 1) == 1 || ((status >>> 1) & 1) == 1)
               builder.append("[报警!]");
           setSensorStatus(builder,status);
       }
       noticeVo.setEnable(true);
       noticeVo.setInfo(builder.toString());
   }
    /**
     * 睡眠检测带
     * @param argument
     * @return
     */
    public static void setSleepace(Argument<?,?> argument,NoticeVo noticeVo){
        noticeVo.setType(PensionBusiness.OWON_SLEEPACE);
        StringBuilder builder = new StringBuilder();
        Integer heartRate = argument.getHeartRate();
        Integer respiratoryRate = argument.getRespiratoryRate();
        Integer sleepFlag = argument.getSleepFlag();
        Integer awakeFlag = argument.getAwakeFlag();
        Integer batCharge = argument.getBatCharge();
        Integer batLvl = argument.getBatLvl();
        Integer status = argument.getStatus();
        Integer statusValue = argument.getStatusValue();

        if(sleepFlag != null){
            if(sleepFlag == 0) builder.append("[未入睡]");
            else if(sleepFlag == 1) builder.append("[入睡]");
        }
        if(awakeFlag != null){
            if(awakeFlag == 0) builder.append("[不清醒]");
            else if(awakeFlag == 1) builder.append("[清醒]");
        }
        if(heartRate != null){//50~140
            if((heartRate <= 130 && heartRate != 0) || heartRate > 140)
                noticeVo.setEnable(true);
            builder.append("[心率: ")
                    .append(heartRate)
                    .append("]");
            noticeVo.setHeartRate(heartRate);
        }
        if(respiratoryRate != null){//12~24
            if((respiratoryRate < 12 && respiratoryRate != 0) || respiratoryRate > 24)
                noticeVo.setEnable(true);
            builder.append("[呼吸率: ")
                    .append(respiratoryRate)
                    .append("]");
            noticeVo.setRespiratoryRate(respiratoryRate);
        }
        if(batCharge != null){
            builder.append("[充电状态: ")
                    .append(batCharge)
                    .append("] ");
        }
        if(batLvl != null){
            if(batLvl >=0 && batLvl < 10)
                noticeVo.setEnable(true);
            if(batLvl != 0XFF)
                builder.append("[电量百分比:")
                        .append(batLvl)
                        .append("]");
        }
        setContentOfStatus(status,statusValue,builder,noticeVo);
    }
    private static void setContentOfStatus(Integer status,Integer statusValue,StringBuilder builder,NoticeVo noticeVo){
        if (status != null) {
            builder.append("[状态: ");
            switch (status) {
                case 0x00:
                    builder.append("正常]");
                    break;
                case 0x01:
                    builder.append("初始化]");
                    break;
                case 0x02:
                    noticeVo.setEnable(true);
                    builder.append("呼吸暂停,暂停时间: ")
                            .append(statusValue)
                            .append("]");
                    break;
                case 0x03:
                    noticeVo.setEnable(true);
                    builder.append("心跳暂停,暂停时间: ")
                            .append(statusValue)
                            .append("]");
                    break;
                case 0x04:
                    builder.append("体动,体动次数: ")
                            .append(statusValue)
                            .append("]");
                    break;
                case 0x05:
                    builder.append("离床,离床持续时间: ")
                            .append(statusValue)
                            .append("]");
                    break;
                case 0x06:
                    builder.append("翻身,翻身次数: ")
                            .append(statusValue)
                            .append("]");
                    break;
                default:
                    break;
            }
        }
        noticeVo.setInfo(builder.toString());
    }
    /**
     * 安防通用状态
     */
    private static void setSensorStatus(StringBuilder builder,int status){
        if(((status >>> 2) & 1) == 1) builder.append("[设备被拆]");
        if(((status >>> 3) & 1) == 1) builder.append("[电量低]");
        if(((status >>> 4) & 1) == 1);// builder.append("[周期报告]");
        if(((status >>> 5) & 1) == 1);
        if(((status >>> 6) & 1) == 1) builder.append("[故障]");
        if(((status >>> 7) & 1) == 1) builder.append("[AC电源故障]");
        if(((status >>> 8) & 1) == 1) builder.append("[测试模式]");
        if(((status >>> 9) & 1) == 1) builder.append("[电池缺陷]");
    }
}
