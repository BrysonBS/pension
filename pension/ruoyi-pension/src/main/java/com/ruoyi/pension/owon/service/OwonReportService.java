package com.ruoyi.pension.owon.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.websocket.WebSocketUsers;
import com.ruoyi.pension.common.api.SendSms;
import com.ruoyi.pension.owon.api.StatusManager;
import com.ruoyi.pension.owon.domain.dto.Argument;
import com.ruoyi.pension.owon.domain.dto.Datapacket;
import com.ruoyi.pension.owon.domain.dto.OwonReport;
import com.ruoyi.pension.owon.domain.dto.Response;
import com.ruoyi.pension.common.domain.enums.Operation;
import com.ruoyi.pension.common.domain.enums.Platform;
import com.ruoyi.pension.owon.domain.po.Device;
import com.ruoyi.pension.owon.domain.po.DeviceEp;
import com.ruoyi.pension.owon.domain.po.OwonNotice;
import com.ruoyi.pension.common.domain.vo.NoticeVo;
import com.ruoyi.pension.owon.mapper.OwonReportMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class OwonReportService extends ServiceImpl<OwonReportMapper, OwonReport> implements IService<OwonReport> {
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private DatapacketService datapacketService;
    @Autowired
    private DeviceService deviceService;
    @Qualifier("jmsTemplateTime")
    @Autowired
    private JmsTemplate jmsTemplate;
    @Autowired
    private OwonNoticeService owonNoticeService;
    @Autowired
    private SysUserOwonService sysUserOwonService;
    @Autowired
    private ObjectMapper objectMapper;

    @Transactional
    public boolean saveCascade(OwonReport owonReport) throws Exception {
        OwonNotice owonNotice = setNotice(owonReport);//先通知前端

        setUpdate(owonReport);//更新部分信息
        setMessage(owonReport);//请求返回消息设置

        if(Boolean.TRUE.equals(owonReport.getSjson().getIgnore())) {
            log.info("ignore: {}",objectMapper.writeValueAsString(owonReport));
            return true; //忽略数据不进行持久化
        }

        boolean result = this.save(owonReport);
        Datapacket<?,?> datapacket = owonReport.getSjson();
        if(datapacket != null) {
            datapacket.setReportId(owonReport.getId());
            result = datapacketService.saveCascade(datapacket);
        }
        if(result && owonNotice != null) {
            //上报记录保存成功,保存通知并记录id
            owonNotice.setReportId(owonReport.getId());
            owonNoticeService.save(owonNotice);
        }
        return result;
    }

    //级联删除小于指定日期的数据: 存储过程内已定义事务
    public void deleteLtCreatedCascade(String created){
        this.baseMapper.ProcDeleteLtCreatedCascade(created);
    }

    //消息
    private void setMessage(OwonReport owonReport) throws JsonProcessingException {
        String mac = owonReport.getMac();
        Operation operation = owonReport.getSjson().getOperation();
        String ieee = owonReport.getSjson().getIeee();
        Object response = owonReport.getSjson().getResponse();

        if(response == null) return;//没响应信息不缓存

        if(operation == Operation.RESULT_EP_LIST) {
            //EP设备列表消息
            jmsTemplate.convertAndSend(operation.getCode()+mac,response);
            //redisCache.setCacheObject(operation.getCode()+mac,response,10, TimeUnit.SECONDS);
        }
        else if(operation == Operation.RESULT_BLE_LIST){
            //BLE设备列表消息
            jmsTemplate.convertAndSend(operation.getCode()+mac,response);
        }
        else if(operation == Operation.RESULT_STATE_ZB){

            //设备状态消息
            jmsTemplate.convertAndSend(operation.getCode()+ieee,response);
            //redisCache.setCacheObject(operation.getCode()+ieee,response,10, TimeUnit.SECONDS);
        }
        else if(operation == Operation.RESULT_GET_WARNING){
            //报警器传感器状态消息
            jmsTemplate.convertAndSend(operation.getCode()+ieee,response);
            //redisCache.setCacheObject(operation.getCode()+ieee,response,10, TimeUnit.SECONDS);
        }
    }
    //更新设置
    private void setUpdate(OwonReport owonReport) throws JsonProcessingException {
        String mac = owonReport.getMac();
        Operation operation = owonReport.getSjson().getOperation();
        String ieee = owonReport.getSjson().getIeee();
        Object response = owonReport.getSjson().getResponse();
        Object argument = owonReport.getSjson().getArgument();

        if(response == null && argument == null) return;//没响应与上报信息不处理

        if(operation == Operation.REPORT_EP_LIST){
            //EP设备列表上报
            List<DeviceEp> epList = ((Argument< DeviceEp,Void>)argument).getEpList();
            //更新或者插入
            epList.forEach(ep -> {
                ep.setGwCode(mac);
                //deviceService.saveOrUpdateSelectiveByIeeeAndEp(ep);
                deviceService.saveOrUpdateByIeeeAndEpAndName(ep);
            });
        }
        else if(operation == Operation.RESULT_EP_LIST) {
            //EP设备列表查询返回
            List<DeviceEp> epList = ((Response<DeviceEp>)response).getEpList();
            epList.forEach(ep -> {
                ep.setGwCode(mac);
                //存入redis缓存
                redisCache.setCacheObject(ep.getIeee(),ep,1,TimeUnit.DAYS);
                //deviceService.saveOrUpdateSelectiveByIeeeAndEp(ep);
                deviceService.saveOrUpdateByIeeeAndEpAndName(ep);
            });
        }
        else if(operation == Operation.RESULT_BLE_LIST){
            //BLE设备列表查询返回
            List<DeviceEp> epList = ((Response<DeviceEp>)response).getBleDevList();
            epList.forEach(ep -> {
                ep.setGwCode(mac);
                //存入redis缓存
                redisCache.setCacheObject(ep.getIeee(),ep,1,TimeUnit.DAYS);
                //deviceService.saveOrUpdateSelectiveByIeeeAndEp(ep);
                deviceService.saveOrUpdateByIeeeAndEpAndName(ep);
            });
        }
        else if(operation == Operation.REPORT_BLE_CONNECT_CHANGE){
            //BLE设备状态变化,更新redis状态信息
            Argument<?,?> arg = (Argument<?, ?>) argument;
            String ieeeArg = arg.getIeee();
            //Integer epArg = arg.getEp();
            DeviceEp deviceEp = redisCache.getCacheObject(ieeeArg);
            if(deviceEp != null){
                deviceEp.setNetState(arg.getNetState());
                redisCache.setCacheObject(deviceEp.getIeee(),deviceEp,1,TimeUnit.DAYS);
            }
        }
    }
    //通知设置
    private OwonNotice setNotice(OwonReport owonReport) throws Exception {
        Operation operation = owonReport.getSjson().getOperation();
        LocalDateTime ts = owonReport.getTs();
        Argument<?,?> argument = (Argument<?, ?>) owonReport.getSjson().getArgument();
        Response<?> response = (Response<?>) owonReport.getSjson().getResponse();
        String ieee = null;
        Integer ep = null;
        Object object = null;

        if(operation == Operation.REPORT_SENSOR_WARNING){//安防设备报警
            ieee = response.getIeee();
            ep = response.getEp();
            ts = response.getUtc();
            object = response;
        }
        else if(operation == Operation.REPORT_SENSOR_UPDATE){//安防设备更新
            //判断是否为门磁(cid=12,13)
            int count = deviceService.getCountByIeeeAndCids(argument.getIeee(),List.of(12,13));
            if(count > 0){
                ieee = argument.getIeee();
                ep = argument.getEp();
                object = argument;
            }
        }
        else if(operation == Operation.REPORT_BATTERY_PERCENTAGE){ //电量上报

        }
        else if(operation == Operation.REPORT_BLE_DATA_UPDATE){ //BLE设备更新上报
            ieee = argument.getIeee();
            ep = argument.getEp();
            object = argument;
        }
        else if(operation == Operation.REPORT_FALL_DETECT_NOTIFY){//跌倒报警器上报
            ieee = argument.getIeee();
            ep = argument.getEp();
            object =argument;
        }


        if(ieee == null || ep == null) return null; //未记录设备不处理
        Device device = deviceService.getOneAndDeptNameByIeeeAndEp(ieee,ep);
        //未分配部门设备或者未分类设备不处理
        if(device == null || device.getDeptId() == null || device.getCid() == null) return null;

        NoticeVo noticeVo = StatusManager.getNoticeInfo(device,object);
        if(!noticeVo.getEnable()) {
            owonReport.getSjson().setIgnore(true); //非通报信息不需要持久化
            return null; //正常不报警
        }
        noticeVo.setIeee(ieee);//记录设备
        noticeVo.setTs(ts);
        return sendSmsAndNotice(device,operation,ts,noticeVo);
    }
    //发送短信和前端通知
    private OwonNotice sendSmsAndNotice(Device device, Operation operation, LocalDateTime ts, NoticeVo noticeVo) throws Exception {
        noticeVo.setName(device.getName());
        List<String> tags = noticeVo.getTags();
        tags.add(device.getDeptName());
        tags.add(device.getDisplayName());
        noticeVo.setTime(ts);
        String template = "{}:{}[{}]#"+noticeVo.getInfo()+"#{}";
        String content = StringUtils.format(template,
                device.getDeptName(),device.getName(),device.getDisplayName(),
                ts.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
        OwonNotice owonNotice = OwonNotice.builder()
                .deptId(device.getDeptId())
                .source(Platform.OWON)
                .createTime(LocalDateTime.now())
                .reportTime(ts)
                .deviceIeee(device.getIeee())
                .deviceName(device.getName())
                .displayName(device.getDisplayName())
                .noticeType(operation.getDescription())
                .noticeContent(content)
                .build();
        //先通知设备所属用户
        sysUserOwonService.getUsersByDeptIdOfSuperiors(device.getDeptId())
                .forEach(user -> WebSocketUsers.sendMessageToUsers(user.getUserId() + "",
                        AjaxResult.success()
                                .put(AjaxResult.OPERATE_TAG, noticeVo.getType())
                                .put(AjaxResult.DATA_TAG,noticeVo)));
        //短信通知
        List<String> phones = deviceService.getPhonesByIeeeAndEp(device.getIeee(),device.getEp());
        if(phones != null && phones.size() > 0)
            SendSms.sendWarning(device.getName(),phones.toArray(String[]::new));
        return owonNotice;
    }

}
