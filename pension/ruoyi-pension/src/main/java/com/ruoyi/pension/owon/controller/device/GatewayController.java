package com.ruoyi.pension.owon.controller.device;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.pension.common.aspect.annotation.OperationInfo;
import com.ruoyi.pension.owon.domain.po.Gateway;
import com.ruoyi.pension.owon.service.GatewayService;
import com.ruoyi.pension.owon.service.SysDeptOwonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/device/gateway")
public class GatewayController extends BaseController{
    @Autowired
    private GatewayService gatewayService;
    /** 新增网关 */
    @PreAuthorize("@ss.hasPermi('device:gateway:add')")
    @Log(title = "设备管理/网关", businessType = BusinessType.UPDATE)
    @OperationInfo(OperationInfo.Info.CREATED)
    @PostMapping()
    public AjaxResult postGateway(@Validated @RequestBody Gateway gateway){
        return toAjax(gatewayService.save(gateway));
    }
    /** 删除网关 */
    @PreAuthorize("@ss.hasPermi('device:gateway:remove')")
    @Log(title = "设备管理/网关", businessType = BusinessType.DELETE)
    @DeleteMapping()
    public AjaxResult deleteGateway(Gateway gateway){
        return toAjax(gatewayService.removeById(gateway.getId()));
    }
    /** 批量删除 */
    @PreAuthorize("@ss.hasPermi('device:gateway:remove')")
    @Log(title = "设备管理/网关", businessType = BusinessType.DELETE)
    @DeleteMapping("/batch")
    public AjaxResult deleteGateways(Integer[] id){
        return toAjax(gatewayService.removeByIds(List.of(id)));
    }
    /** 修改网关 */
    @PreAuthorize("@ss.hasPermi('device:gateway:edit')")
    @Log(title = "设备管理/网关", businessType = BusinessType.DELETE)
    @OperationInfo(OperationInfo.Info.UPDATED)
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
    public TableDataInfo list(Gateway gateway){
        startPage();
        List<Gateway> list = gatewayService.getListByExample(gateway);
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
