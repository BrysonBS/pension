package com.ruoyi.pension.bioland.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.websocket.WebSocketUsers;
import com.ruoyi.pension.bioland.api.SrcDataValueManager;
import com.ruoyi.pension.bioland.domain.po.BiolandDevice;
import com.ruoyi.pension.bioland.domain.po.SrcDataValue;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.pension.bioland.mapper.SrcDataValueMapper;
import com.ruoyi.pension.common.api.SendSms;
import com.ruoyi.pension.common.domain.enums.Platform;
import com.ruoyi.pension.owon.domain.po.OwonNotice;
import com.ruoyi.pension.common.domain.vo.NoticeVo;
import com.ruoyi.pension.owon.service.OwonNoticeService;
import com.ruoyi.pension.owon.service.SysUserOwonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
* @author Administrator
* @description 针对表【bioland_srcdata】的数据库操作Service
* @createDate 2022-05-13 15:24:44
*/
@Service
public class SrcDataValueService extends ServiceImpl<SrcDataValueMapper, SrcDataValue> implements IService<SrcDataValue> {
    @Autowired
    private BiolandDeviceService biolandDeviceService;
    @Autowired
    private SysUserOwonService sysUserOwonService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private OwonNoticeService owonNoticeService;

    public List<SrcDataValue> listLatest(String serialNumber, LocalDateTime beginTime, LocalDateTime endTime){
        if(endTime != null) endTime = endTime.plusDays(1);
        return this.baseMapper.listLatest(serialNumber,beginTime,endTime);
    }
    public boolean saveDataAndSendNotice(SrcDataValue srcDataValue) throws Exception {
        //先发送短信和通知前端
        OwonNotice owonNotice = sendSmsAndNotice(srcDataValue);
        //保存
        boolean result = super.save(srcDataValue);
        if(owonNotice != null && result){
            //上报记录保存成功,保存通知并记录id
            owonNotice.setReportId(srcDataValue.getId());
            result = owonNoticeService.save(owonNotice);
        }
        return result;
    }

    private OwonNotice sendSmsAndNotice(SrcDataValue srcDataValue) throws Exception {
        NoticeVo noticeVo = SrcDataValueManager.getNoticeInfo(srcDataValue);
        if(!noticeVo.getEnable()) return null; //正常不通知
        //获取设备信息
        BiolandDevice biolandDevice = biolandDeviceService.selectOneBySerialNumber(srcDataValue.getSerialNumber());
        if(biolandDevice == null || biolandDevice.getDeptId() == null || biolandDevice.getCategoriesId() == null)
            return null; //不在设备列表,未分配部门,未知设备类型则不通知
        //设置标签
        List<String> tags = noticeVo.getTags();
        tags.add(biolandDevice.getDeptName());
        tags.add(biolandDevice.getTypeName());
        //设置名称
        noticeVo.setName(biolandDevice.getName());

        //设置上报内容信息
        String template = "{}:{}[{}]#"+noticeVo.getInfo()+"#{}";
        String content = StringUtils.format(template,
                biolandDevice.getDeptName(),biolandDevice.getName(),biolandDevice.getTypeName(),
                noticeVo.getTs().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
        OwonNotice owonNotice = OwonNotice.builder()
                .deptId(biolandDevice.getDeptId())
                .source(Platform.BIOLAND)
                .createTime(LocalDateTime.now())
                .reportTime(noticeVo.getTs())
                .deviceIeee(noticeVo.getIeee())
                .deviceName(biolandDevice.getName())
                .displayName(biolandDevice.getTypeName())
                .noticeType(noticeVo.getOperation().getDescription())
                .noticeContent(content)
                .build();

        //先通知设备所属用户
        String noticeString = objectMapper.writeValueAsString(noticeVo);
        sysUserOwonService.getUsersByDeptIdOfSuperiors(biolandDevice.getDeptId())
                .forEach(user -> WebSocketUsers.sendMessageToUsersByText(user.getUserId() + "", noticeString));
        //短信通知
        List<String> phones = biolandDeviceService.getPhonesBySerialNumber(biolandDevice.getSerialNumber());
        if(phones != null && phones.size() > 0)
            SendSms.sendWarning(biolandDevice.getName(),phones.toArray(String[]::new));
        return owonNotice;
    }
}
