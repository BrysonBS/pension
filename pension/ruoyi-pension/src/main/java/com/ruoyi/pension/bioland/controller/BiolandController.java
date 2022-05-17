package com.ruoyi.pension.bioland.controller;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.pension.bioland.domain.po.BiolandDevice;
import com.ruoyi.pension.bioland.domain.po.SrcDataValue;
import com.ruoyi.pension.bioland.service.BiolandDeviceService;
import com.ruoyi.pension.bioland.service.SrcDataValueService;
import com.ruoyi.pension.bioland.utils.HdwDataCheckUtils;
import com.ruoyi.pension.bioland.utils.StringUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;

@RestController
@RequestMapping("/bioland")
@Tag(name = "爱奥乐相关接口API")
@Slf4j
public class BiolandController {
    @Autowired
    private SrcDataValueService srcDataValueService;
    @Autowired
    private BiolandDeviceService biolandDeviceService;
    /** 服务器IP地址 */
    @Value("${bioland.server.ip}")
    private String currServerIP;

    @Operation(summary = "爱奥乐数据上报")
    @Parameter(name = "data",description = "上报数据",required = true)
    @GetMapping("/data")
    public void receiveData(HttpServletRequest request,
                            HttpServletResponse response, @RequestParam("data") String data)
            throws Exception {

        log.error("Bioland: " + data);

        // 校验数据合法性
        if (!checkReceivedData(data)) return;
        SrcDataValue srcDataValue = new SrcDataValue(data);
        //先保存设备
        BiolandDevice biolandDevice = BiolandDevice.builder()
                .name(srcDataValue.getSerialNumber()) //默认名称为序列号
                .serialNumber(srcDataValue.getSerialNumber())
                .deviceType(srcDataValue.getDeviceType().getCode())
                .build();
        biolandDeviceService.saveIfNotPresent(biolandDevice);
        //再保存上报数据
        boolean result = srcDataValueService.saveDataAndSendNotice(srcDataValue);
        if (result) {
            StringBuilder rslt = new StringBuilder("+IP");
            rslt.append(getServerIp(request));
            rslt.append(getServerDate());
            rslt.append("OK");

            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-Type", "text/html; charset=utf-8");

            try(OutputStream outWriter = response.getOutputStream()) {
                byte[] towrite = rslt.toString().getBytes();
                response.setContentLength(towrite.length);
                outWriter.write(towrite);
                outWriter.flush();
            }
        }
    }

    /**
     * 校验上报数据的合法性
     *
     * @param data
     * @return
     */
    private boolean checkReceivedData(String data) {

        if ((data != null) && ((data.length() == 85) || (data.length() == 87))) {

            if ((data.length() == 85)
                    && (HdwDataCheckUtils.isSumCheckCode(data))) {
                return true;
            }

            if ((data.length() == 87)
                    && (HdwDataCheckUtils.isSumCheckCode(data))
                    && (HdwDataCheckUtils.isCrc8Code(data))) {
                return true;
            }
        }

        return false;
    }
    /**
     * 获取请求端ip地址
     *
     * @param request
     * @return
     */
    private String getServerIp(HttpServletRequest request) {

        String[] ips = StringUtil.toString(currServerIP).split("\\.");

        if ((null == ips) || (0 >= ips.length)) {
            return "";
        }

        String ports = StringUtils.leftPad(
                Integer.toHexString(request.getLocalPort()), 4, "0");

        StringBuilder ipsb = new StringBuilder();

        int sum = 0;

        for (int i = 0; i < ips.length; i++) {
            ipsb.append(StringUtils.leftPad(
                    Integer.toHexString(Integer.valueOf(ips[i]).intValue()), 2,
                    "0"));
            sum ^= Integer.valueOf(ips[i]).intValue();
        }

        sum ^= Integer.valueOf(ports.substring(2, 4), 16).intValue();
        ipsb.append(ports.substring(2, 4));
        sum ^= Integer.valueOf(ports.substring(0, 2), 16).intValue();
        ipsb.append(ports.substring(0, 2));
        ipsb.append(Integer.toHexString(sum));

        return ipsb.toString().toUpperCase();
    }

    /**
     * 获取服务器时间
     *
     * @return
     */
    private String getServerDate() {

        StringBuilder datasb = new StringBuilder();

        int sum = 0;

        Calendar cal = Calendar.getInstance();
        Integer year = Integer.valueOf(cal.get(1) % 100);
        datasb.append(StringUtils.leftPad(Integer.toHexString(year.intValue()),
                2, "0"));
        sum ^= year.intValue();

        Integer month = Integer.valueOf(cal.get(2) + 1);
        datasb.append(StringUtils.leftPad(
                Integer.toHexString(month.intValue()), 2, "0"));
        sum ^= month.intValue();

        Integer day = Integer.valueOf(cal.get(5));
        datasb.append(StringUtils.leftPad(Integer.toHexString(day.intValue()),
                2, "0"));
        sum ^= day.intValue();

        Integer hour = Integer.valueOf(cal.get(11));
        datasb.append(StringUtils.leftPad(Integer.toHexString(hour.intValue()),
                2, "0"));
        sum ^= hour.intValue();

        Integer minute = Integer.valueOf(cal.get(12));
        datasb.append(StringUtils.leftPad(
                Integer.toHexString(minute.intValue()), 2, "0"));
        sum ^= minute.intValue();

        datasb.append(StringUtils.leftPad(Integer.toHexString(sum), 2, "0"));

        return datasb.toString().toUpperCase();
    }
}
