package com.ruoyi.pension.common.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.exception.file.InvalidExtensionException;
import com.ruoyi.common.utils.file.FileUploadUtils;
import com.ruoyi.common.utils.file.FileUtils;
import com.ruoyi.pension.common.aspect.annotation.PensionDataScope;
import com.ruoyi.pension.common.domain.consts.PensionBusiness;
import com.ruoyi.pension.common.domain.po.BasePensionEntity;
import com.ruoyi.pension.common.domain.po.PensionPayment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.pension.common.mapper.PensionPaymentMapper;
import com.ruoyi.pension.owon.domain.po.SysDeptOwon;
import com.ruoyi.pension.owon.service.SysDeptOwonService;
import com.ruoyi.system.service.ISysDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
* @author Administrator
* @description 针对表【pension_payment】的数据库操作Service
* @createDate 2022-06-30 15:59:44
*/
@Service
public class PensionPaymentService extends ServiceImpl<PensionPaymentMapper, PensionPayment> implements IService<PensionPayment> {
    @Autowired
    private ISysDeptService sysDeptService;
    @Autowired
    private SysDeptOwonService sysDeptOwonService;
    @Autowired
    private Environment environment;
    @PensionDataScope(deptAlias = "a",ignoreUser = true)
    public List<PensionPayment> listByExample(PensionPayment pensionPayment){
        return this.baseMapper.listByExample(pensionPayment);
    }
    public PensionPayment getByDeptIdAndPayType(@NotNull Long deptId, @NotNull Integer payType){
        return this.baseMapper.getOneByDeptIdAndPayType(deptId,payType);
    }
    /**
     *获取当前机构商户配置信息,不存在则沿着父机构继续查找
     * @param deptId 部门id
     */
    public PensionPayment getOneOrAncestorByDeptIdAndPayType(@NotNull Long deptId, @NotNull Integer payType){
        Map<Long,SysDeptOwon> sysDeptMap = sysDeptOwonService.getAndAncestorsListByDeptId(deptId)
                .stream().collect(Collectors.toMap(SysDeptOwon::getDeptId, Function.identity()));
        Map<Long,List<PensionPayment>> paymentMap = super.list().stream()
                .filter(e -> Objects.equals(e.getStatus(),"0") && Objects.equals(e.getDelFlag(),"0") && Objects.equals(e.getPayType(), payType))
                .collect(Collectors.groupingBy(PensionPayment::getDeptId));
        List<PensionPayment> list = paymentMap.get(deptId);
        ArrayDeque<Long> stack = sysDeptOwonService.getAncestorsStack(sysDeptMap.get(deptId));
        while (list == null && !stack.isEmpty())
            list = paymentMap.get(deptId = stack.pop());

        if(list == null || list.isEmpty()) throw new RuntimeException("商户没有配置");
        PensionPayment pensionPayment = null;
        if(Arrays.stream(environment.getActiveProfiles())
                .anyMatch(e -> e.contains("dev"))){
            //测试环境
            pensionPayment =list.stream()
                    .filter(e -> e.getRemark() == null || e.getRemark().startsWith("dev"))
                    .peek(e -> {if(e.getUpdateTime() == null) e.setUpdateTime(e.getCreateTime());})
                    .max(Comparator.comparing(BasePensionEntity::getUpdateTime))
                    .orElse(null);
        }
        else {
            pensionPayment = list.stream()
                    .filter(e -> e.getRemark() == null || !e.getRemark().startsWith("dev"))
                    .peek(e -> {if(e.getUpdateTime() == null) e.setUpdateTime(e.getCreateTime());})
                    .max(Comparator.comparing(BasePensionEntity::getUpdateTime))
                    .orElse(null);
        }
        if(pensionPayment == null) throw new RuntimeException("商户没有配置");
        pensionPayment.setDeptName(sysDeptMap.get(deptId).getDeptName());
        return pensionPayment;
    }


    @Transactional
    public boolean add(PensionPayment pensionPayment, MultipartFile[] attachments) throws IOException, InvalidExtensionException {
        Integer payType = pensionPayment.getPayType();
        //微信只有证书模式
        if(payType == 2) pensionPayment.setCertModel(true);
        //证书模式则额外保存证书,并添加证书信息
        if(pensionPayment.getCertModel()) {
            //存储证书根路径
            String basePath = RuoYiConfig.getUploadPath() + PensionBusiness.MERCHANT_CERT_ROOT;
            //存储并保存路径信息,证书顺序: 支付宝公钥证书>支付宝根证书>应用公钥证书
            for(int i=0; i<attachments.length; ++i){
                if(attachments[i] == null || attachments[i].isEmpty()) continue;
                String fileName = FileUploadUtils.simpleUpload(basePath,attachments[i],new String[]{"crt","p12","pem"});
                String relatePath = PensionBusiness.MERCHANT_CERT_ROOT + fileName;
                //保存存储路径信息
                if(payType == 1) {//支付宝证书
                    if (i == 0) pensionPayment.setAlipayCertPath(relatePath);
                    else if (i == 1) pensionPayment.setAlipayRootCertPath(relatePath);
                    else if (i == 2) pensionPayment.setAppCertPath(relatePath);
                }
                else if(payType == 2){//微信证书
                    if (i == 0) pensionPayment.setWechatpayCertP12Path(relatePath);
                    else if (i == 1) pensionPayment.setWechatpayCertPemPath(relatePath);
                    else if (i == 2) pensionPayment.setWechatpayKeyPemPath(relatePath);
                }
            }
        }
        //存入数据库
        return save(pensionPayment);
    }
    public boolean updateEntity(PensionPayment pensionPayment,MultipartFile[] attachments) throws IOException, InvalidExtensionException {
        //证书模式且全部更换附件
        if(pensionPayment.getCertModel() && attachments.length == 3){
            String basePath = RuoYiConfig.getUploadPath() + PensionBusiness.MERCHANT_CERT_ROOT;
            FileUtils.deleteFile(basePath + pensionPayment.getAlipayCertPath());
            FileUtils.deleteFile(basePath + pensionPayment.getAlipayRootCertPath());
            FileUtils.deleteFile(basePath + pensionPayment.getAppCertPath());
            //存储并保存路径信息,证书顺序: 支付宝公钥证书>支付宝根证书>应用公钥证书
            for(int i=0; i<attachments.length; ++i){
                if(attachments[i] == null || attachments[i].isEmpty()) continue;
                String fileName = FileUploadUtils.simpleUpload(basePath,attachments[i],new String[]{"crt"});
                String relatePath = PensionBusiness.MERCHANT_CERT_ROOT + fileName;
                if(i == 0) pensionPayment.setAlipayCertPath(relatePath);
                else if(i == 1) pensionPayment.setAlipayRootCertPath(relatePath);
                else if(i == 2) pensionPayment.setAppCertPath(relatePath);
            }
        }
        return updateById(pensionPayment);
    }
    public boolean removeEntityBatchByIds(Collection<Integer> ids){
        String basePath = RuoYiConfig.getUploadPath() + PensionBusiness.MERCHANT_CERT_ROOT;

        //先删除证书
        list(new LambdaQueryWrapper<PensionPayment>()
                .select(PensionPayment::getId,PensionPayment::getAlipayCertPath,PensionPayment::getAlipayRootCertPath,PensionPayment::getAppCertPath)
                .in(PensionPayment::getId,ids))
                .forEach(e -> {
                    FileUtils.deleteFile(basePath + e.getAlipayCertPath());
                    FileUtils.deleteFile(basePath + e.getAlipayRootCertPath());
                    FileUtils.deleteFile(basePath + e.getAppCertPath());
                });
        //数据库删除,只逻辑删除
        return removeBatchByIds(ids);
    }

    public boolean changeStatus(PensionPayment pensionPayment){
        LambdaUpdateWrapper<PensionPayment> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper.set(PensionPayment::getStatus,pensionPayment.getStatus())
                .eq(PensionPayment::getId,pensionPayment.getId());
        return super.update(lambdaUpdateWrapper);
    }
}
