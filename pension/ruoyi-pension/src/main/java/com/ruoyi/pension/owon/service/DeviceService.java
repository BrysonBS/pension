package com.ruoyi.pension.owon.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.pension.common.aspect.annotation.PensionDataScope;
import com.ruoyi.pension.common.domain.enums.Platform;
import com.ruoyi.pension.owon.domain.po.Device;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.pension.owon.domain.po.DeviceEp;
import com.ruoyi.pension.owon.domain.po.DevicePhone;
import com.ruoyi.pension.owon.domain.po.Gateway;
import com.ruoyi.pension.owon.mapper.DeviceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
* @author Administrator
* @description 针对表【owon_device】的数据库操作Service
* @createDate 2022-04-18 09:27:16
*/
@Service
public class DeviceService extends ServiceImpl<DeviceMapper, Device> implements IService<Device> {
    @Autowired
    private DevicePhoneService devicePhoneService;
    @Autowired
    private RedisCache redisCache;
    @Autowired
    private SysDeptOwonService deptOwonService;
    @Autowired
    private GatewayService gatewayService;

    @PensionDataScope(deptAlias = "a",ignoreUser = true)
    public List<Device> getListByExample(Device device){
        return this.baseMapper.listByExample(device);
    }
    @Transactional
    public void updateNameAndLinkStatusByList(List<DeviceEp> list){
        list.forEach(ep ->{
            this.baseMapper.updateNameAndLinkStatusByIeeeAndEp(ep.getName(),ep.getLinkStatus(),ep.getIeee(),ep.getEp());
        });
    }

    public int saveOrUpdateByIeeeAndEpAndName(DeviceEp deviceEp){
        LambdaQueryWrapper<Device> queryWrapper = new LambdaQueryWrapper<>();
        //ieee : hash index
        queryWrapper.eq(Device::getIeee,deviceEp.getIeee());
                //.eq(deviceEp.getEp() != null,Device::getEp,deviceEp.getEp());
        Device device = this.baseMapper.selectOne(queryWrapper);
        if(device == null){
            //新增时先更新deptId;
            Optional.ofNullable(gatewayService.getOneByGwCode(deviceEp.getGwCode()))
                    .ifPresent(gateway -> deviceEp.setDeptId(gateway.getDeptId()));
            //然后新增
            return insertByDeviceEp(deviceEp);
        }
        if(device.getDeptName() != null && deviceEp.getName() != null){
            if(!device.getDeptName().equals(deviceEp.getName())){
                //需更新名称
                return updateNameAndLinkStatusAndNetState(deviceEp);
            }
        }
        return 1;
    }

    @Transactional
    public int insertByDeviceEp(DeviceEp deviceEp){
        return this.baseMapper.insertByDeviceEp(deviceEp);
    }
    @Transactional
    public int updateNameAndLinkStatusAndNetState(DeviceEp deviceEp){
        return this.baseMapper.updateNameAndLinkStatusAndNetState(deviceEp);
    }


    /**
     * 根据设备ieee和ep查找设备和设备归属部门名称
     * @param ieee
     * @param ep
     * @return
     */
    public Device getOneAndDeptNameByIeeeAndEp(String ieee,Integer ep){
        return this.baseMapper.getOneAndDeptNameByIeeeAndEp(ieee,ep);
    }

    /**
     * 更新设备和关联电话
     * @param device
     * @return
     */
    @Transactional
    public boolean updateDeviceAndPhoneAndCategoriesId(Device device){
        LambdaUpdateWrapper<Device> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .set(device.getName() != null,Device::getName,device.getName())
                .set(device.getSerialNo() != null,Device::getSerialNo,device.getSerialNo())
                .set(device.getDeptId() != null,Device::getDeptId,device.getDeptId())
                .set(device.getCid() != null,Device::getCategoriesId,device.getCid())
                .eq(Device::getId,device.getId());
        this.update(updateWrapper);
        DevicePhone[] phones = device.getPhones();
        //设置电话归属平台
        for(DevicePhone p : phones) p.setSource(Platform.OWON);
        Integer[] phonesId = device.getPhonesId();
        if(phonesId != null && phonesId.length > 0) //删除
            devicePhoneService.removeByIds(List.of(phonesId));
        if(phones!= null && phones.length > 0) //更新
            devicePhoneService.saveOrUpdateBatch(List.of(phones));
        return true;
    }

    /**
     * 根据设备ieee和ep查找手机号列表
     * @param ieee
     * @param ep
     */
    List<String> getPhonesByIeeeAndEp(String ieee,Integer ep){
        return this.baseMapper.getPhonesByIeeeAndEp(ieee,ep);
    }

    @Transactional
    public boolean removeDeviceAndPhoneByIds(Collection<? extends Serializable> idList) {
        return removeByIds(idList) && devicePhoneService.removeByDeviceIdsAndSource(idList, Platform.OWON);
    }

    public Device getOneById(Integer id){
        return this.baseMapper.getOneById(id);
    }
    public List<Device> getListByDeptId(long deptId){
        List<Long> deptIds = deptOwonService.getListDeptAndChildrenByDeptId(deptId);
        return this.baseMapper.getListByDeptIdsAndCids(deptIds,null);
    }
    public List<Device> getSleepListByDeptId(long deptId){
        List<Long> deptIds = deptOwonService.getListDeptAndChildrenByDeptId(deptId);
        return this.baseMapper.getListByDeptIdsAndCids(deptIds,List.of(1));
    }

    /**
     * 获取指定ieee和设备类型(cid)的设备数量
     * @param ieee
     * @param cids
     * @return
     */
    public int getCountByIeeeAndCids(String ieee,Collection<Integer> cids){
        return this.baseMapper.getCountByIeeeAndCids(ieee,cids);
    }
}
