package com.ruoyi.pension.owon.controller.device;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.pension.owon.domain.po.Gateway;
import com.ruoyi.pension.owon.domain.po.SysDeptOwon;
import com.ruoyi.pension.owon.service.GatewayService;
import com.ruoyi.pension.owon.service.SysDeptOwonService;
import com.ruoyi.system.service.ISysDeptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/device/gateway")
public class GatewayController extends BaseController{
    @Autowired
    private SysDeptOwonService sysDeptOwonService;
    @Autowired
    private GatewayService gatewayService;
    /** 新增网关 */
    @PostMapping()
    public AjaxResult postGateway(@Validated @RequestBody Gateway gateway){
        if(gateway.getDeptId() == null) gateway.setDeptId(100L);
        gateway.setCreateBy(getUsername());
        gateway.setCreateTime(LocalDateTime.now());
        //return AjaxResult.success();
        return toAjax(gatewayService.save(gateway));
    }
    /** 删除网关 */
    @DeleteMapping()
    public AjaxResult deleteGateway(Gateway gateway){
        return toAjax(gatewayService.removeById(gateway.getId()));
    }
    /** 批量删除 */
    @DeleteMapping("/batch")
    public AjaxResult deleteGateways(Integer[] id){
        return toAjax(gatewayService.removeByIds(List.of(id)));
    }
    /** 修改网关 */
    @PutMapping()
    public AjaxResult putGateway(@RequestBody Gateway gateway){
        return toAjax(gatewayService.updateById(gateway));
    }
    /** 查询网关 */
    @GetMapping()
    public AjaxResult getGateway(Gateway gateway){
       Gateway gatewayResult = gatewayService.getById(gateway.getId());
       if(gatewayResult == null) return AjaxResult.error();
       else return AjaxResult.success()
                .put(AjaxResult.DATA_TAG,gatewayResult);
    }
    /** 根据id查询网关 */
    @GetMapping("/{id}")
    public AjaxResult getGateway(@PathVariable("id") Integer id){
        Gateway gatewayResult = gatewayService.getById(id);
        if(gatewayResult == null) return AjaxResult.error();
        else return AjaxResult.success()
                .put(AjaxResult.DATA_TAG,gatewayResult);
    }



    @GetMapping("/list")
    public TableDataInfo list(Optional<Long> deptId, Gateway gateway){
        //不存在,从用户信息中获取
        List<Long> deptIds = sysDeptOwonService.getListByDeptId(
                deptId.orElse(getLoginUser().getDeptId())
        );

        LambdaQueryWrapper<Gateway> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.select(
                Gateway::getId,Gateway::getDeptId,Gateway::getGwName,
                Gateway::getGwCode,Gateway::getOrderNum,Gateway::getLeader,
                Gateway::getPhone, Gateway::getStatus,Gateway::getCreateBy,
                Gateway::getCreateTime
                )
                .like(gateway.getGwName() != null,Gateway::getGwName,gateway.getGwName())
                .like(gateway.getGwCode() != null,Gateway::getGwCode,gateway.getGwCode())
                .in(Gateway::getDeptId,deptIds)
                .eq(gateway.getStatus() != null,Gateway::getStatus,gateway.getStatus());
        startPage();
        List<Gateway> list = gatewayService.list(lambdaQueryWrapper);
        return getDataTable(list);
    }

    @PostMapping("/export")
    public void export(HttpServletResponse response, Gateway gateway) {
        //List<SysUser> list = userService.selectUserList(user);
        //ExcelUtil<SysUser> util = new ExcelUtil<SysUser>(SysUser.class);
        //util.exportExcel(response, list, "用户数据");
    }
    @PatchMapping("/changeStatus")
    public AjaxResult changeStatus(@RequestBody Gateway gateway){
        LambdaUpdateWrapper<Gateway> lambdaUpdateWrapper = new LambdaUpdateWrapper<>();
        lambdaUpdateWrapper
                .set(gateway.getId() != null ,Gateway::getStatus,gateway.getStatus())
                .eq(gateway.getId() != null,Gateway::getId,gateway.getId());
        return toAjax(gatewayService.update(lambdaUpdateWrapper));
    }
}
