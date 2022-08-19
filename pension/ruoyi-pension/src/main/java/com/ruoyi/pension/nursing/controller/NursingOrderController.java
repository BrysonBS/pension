package com.ruoyi.pension.nursing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.pension.common.api.AlipayManager;
import com.ruoyi.pension.common.api.OrderNumberManager;
import com.ruoyi.pension.common.api.QRCodeUtil;
import com.ruoyi.pension.common.api.WeChatPayManager;
import com.ruoyi.pension.common.domain.bo.AlipayInfo;
import com.ruoyi.pension.common.domain.bo.WeChatPayInfo;
import com.ruoyi.pension.nursing.domain.po.NursingOrder;
import com.ruoyi.pension.nursing.domain.po.NursingOrderItems;
import com.ruoyi.pension.nursing.domain.po.NursingServicePrice;
import com.ruoyi.pension.nursing.domain.po.NursingWorker;
import com.ruoyi.pension.nursing.service.NursingOrderService;
import com.ruoyi.pension.nursing.service.NursingServicePriceService;
import com.ruoyi.pension.nursing.service.NursingWorkerService;
import com.wechat.pay.contrib.apache.httpclient.exception.HttpCodeException;
import com.wechat.pay.contrib.apache.httpclient.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/nursing/order")
public class NursingOrderController extends BaseController {
    @Autowired
    private NursingOrderService nursingOrderService;
    @Autowired
    private NursingServicePriceService nursingServicePriceService;
    @Autowired
    private NursingWorkerService nursingWorkerService;
    @Autowired
    private OrderNumberManager orderNumberManager;
    @Autowired
    private AlipayManager alipayManager;
    @Autowired
    private WeChatPayManager weChatPayManager;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private Environment environment;

    @PreAuthorize("@ss.hasPermi('nursing:order:add')")
    @Log(title = "护理管理/订单中心/订单列表", businessType = BusinessType.INSERT)
    @PostMapping
    public void add(@RequestBody @Valid NursingOrder nursingOrder, HttpServletResponse response) throws GeneralSecurityException, NotFoundException, IOException, HttpCodeException {
        if(nursingOrder.getId() != null)
            throw new RuntimeException("订单已存在,无法新增!");
        //校验时间
        if(nursingOrder.getBeginTime().isAfter(LocalDateTime.now().plusMinutes(5)))
            throw new RuntimeException("护理时间必须大于当前时间");
        if(nursingOrder.getServiceItemsIds() == null || nursingOrder.getServiceItemsIds().isEmpty())
            throw new RuntimeException("请选择护理项目");

        //账号和机构id
        Long deptId = getDeptId();
        Long userId = getUserId();
        //计算价格
        List<Integer> serviceIds = nursingOrder.getServiceItemsIds();

        //获取价格列表
        List<NursingServicePrice> priceList = nursingServicePriceService.getListOrAncestorByDeptId(deptId);
        //价格集合
        Map<Integer,NursingServicePrice> priceMap = priceList.stream()
                .collect(Collectors.toMap(NursingServicePrice::getId, Function.identity()));
        //原价总金额
        BigDecimal totalAmount = serviceIds.stream()
                .map(e -> priceMap.get(e).getPrice())
                .reduce(BigDecimal.ZERO,BigDecimal::add);
        //折后价总金额
        BigDecimal payAmount = serviceIds.stream()
                .map(e -> priceMap.get(e).getDiscountPrice())
                .reduce(BigDecimal.ZERO,BigDecimal::add);
        //服务项目列表集合
        List<NursingOrderItems> nursingOrderItems = serviceIds.stream()
                .map(e -> {
                    NursingServicePrice nursingServicePrice = priceMap.get(e);
                    return NursingOrderItems.builder()
                            .dictLabel(nursingServicePrice.getDictLabel())
                            .dictValue(nursingServicePrice.getDictValue())
                            .price(nursingServicePrice.getPrice())
                            .discount(nursingServicePrice.getDiscount())
                            .discountPrice(nursingServicePrice.getDiscountPrice())
                            .build();
                })
                .toList();

        //单号
        String orderNumber = switch(nursingOrder.getPayType()){
            case 1 -> orderNumberManager.getAlipayOrderNumber(deptId);
            case 2 -> orderNumberManager.getWeChatPayOrderNumber(deptId);
            default -> throw new RuntimeException("请选择付款方式!");
        };
        //订单信息
        NursingOrder order = NursingOrder.builder()
                .deptId(deptId)
                .userId(userId)
                .orderSn(orderNumber)
                .totalAmount(totalAmount)
                .payAmount(payAmount)
                .payType(nursingOrder.getPayType())
                .sourceType(nursingOrder.getSourceType())
                .status(0)//未支付
                //服务项目价格列表
                .nursingOrderItems(nursingOrderItems)
                .remark(nursingOrder.getRemark())
                //地址信息
                .province(nursingOrder.getProvince())
                .city(nursingOrder.getCity())
                .district(nursingOrder.getDistrict())
                .address(nursingOrder.getAddress())
                .detailAddress(nursingOrder.getDetailAddress())
                .lat(nursingOrder.getLat())
                .lng(nursingOrder.getLng())

                .beginTime(nursingOrder.getBeginTime())
                .personId(nursingOrder.getPersonId())
                .build();
        order.setCreateBy(getUsername());
        order.setCreateTime(LocalDateTime.now());
        //保存订单并返回支付二维码
        if(order.getSourceType() == 0) //PC端支付
            saveOrderAndresponseToQRCode(response,order);
    }
    @PostMapping("/pay")
    public void payQRCode(@RequestBody NursingOrder nursingOrder,HttpServletResponse response) throws IOException {
        if(nursingOrder.getId() == null) throw new RuntimeException("订单有误!");
        NursingOrder order = nursingOrderService.getById(nursingOrder.getId());
        if(order == null) throw new RuntimeException("找不到该订单!");
        String code = order.getQrCode();
        if(code == null) throw new RuntimeException("订单有误!");
        BigDecimal payAmount = order.getPayAmount().setScale(2, RoundingMode.HALF_UP);;
        String outTradeNo = order.getOrderSn();
        String logo = environment.getProperty(
                switch(order.getPayType()){
                    case 1 -> "alipay.logo";
                    case 2 -> "wechatpay.logo";
                    default -> throw new RuntimeException("订单支付方式有误!");
                }
        );
        //禁止图片缓存
        response.setHeader("Pragma","no-cache");
        response.setHeader("Cache-Control","no-cache");
        response.setDateHeader("Expires",0);
        //根据图片类型来修改相应的contentType
        response.setContentType("image/png");

        //自定义响应头,添加金额和订单号信息
        response.addHeader("pay-amount",payAmount.stripTrailingZeros().toPlainString());
        response.addHeader("pay-order",outTradeNo);
        QRCodeUtil.createCodeAndLogoToOutputStream(code,logo , response.getOutputStream());
    }
    @PreAuthorize("@ss.hasPermi('nursing:order:refund')")
    @Log(title = "护理管理/订单中心/订单列表:退款", businessType = BusinessType.OTHER)
    @PostMapping("/refund")
    public AjaxResult refund(@RequestBody NursingOrder order) throws GeneralSecurityException, NotFoundException, IOException, HttpCodeException {
        NursingOrder selectOrder = nursingOrderService.getByOrderSn(order.getOrderSn());
        if(selectOrder == null) return AjaxResult.error("订单不存在");
        //已全额退款或者退款金额未0则直接返回
        if((selectOrder.getRefundAmount() !=null && selectOrder.getRefundAmount().equals(selectOrder.getPayAmount())) ||
                BigDecimal.ZERO.equals(order.getRefundAmount()))
            return AjaxResult.success();
        //未设置则全部退款
        if(order.getRefundAmount() == null){
            order.setRefundAmount(selectOrder.getRefundAmount() == null ? selectOrder.getPayAmount() :
                    selectOrder.getPayAmount().subtract(selectOrder.getRefundAmount()));
        }
        return toAjax(
                switch (order.getPayType()){
                    //支付宝退款
                    case 1: yield alipayManager.refundOutTradeNo(
                            selectOrder.getDeptId(),
                            selectOrder.getUserId(),
                            selectOrder.getOrderSn(),
                            order.getRefundAmount());
                    case 2: yield weChatPayManager.refundOutTradeNo(
                            selectOrder.getDeptId(),
                            selectOrder.getUserId(),
                            selectOrder.getOrderSn(),
                            selectOrder.getPayAmount(),
                            order.getRefundAmount());
                    default: throw new RuntimeException("退款方式不支持");
                });
    }

    @PreAuthorize("@ss.hasPermi('nursing:order:delivery')")
    @Log(title = "护理管理/订单中心/订单列表:派单", businessType = BusinessType.OTHER)
    @PatchMapping("/delivery")
    public AjaxResult delivery(@RequestBody NursingOrder order){
        return toAjax(nursingOrderService.updateDelivery(order));
    }
    @PreAuthorize("@ss.hasPermi('nursing:order:receive')")
    @Log(title = "护理管理/订单中心/订单列表:接单", businessType = BusinessType.OTHER)
    @PatchMapping("/receive")
    public AjaxResult receive(@RequestBody NursingOrder order){
        order.setUserId(getUserId());
        return toAjax(nursingOrderService.updateReceive(order));
    }
    @PreAuthorize("@ss.hasPermi('nursing:order:complete')")
    @Log(title = "护理管理/订单中心/订单列表:结单", businessType = BusinessType.OTHER)
    @PatchMapping("/complete")
    public AjaxResult complete(@RequestBody NursingOrder order){
        if(order.getId() == null || order.getDeptId() == null || order.getWorkerId() == null)
            throw new RuntimeException("订单有误");
        return toAjax(nursingOrderService.updateComplete(order));
    }
    @PreAuthorize("@ss.hasPermi('nursing:order:cancel')")
    @Log(title = "护理管理/订单中心/订单列表:取消订单", businessType = BusinessType.OTHER)
    @PatchMapping("/cancel")
    public AjaxResult cancel(@RequestBody NursingOrder order) throws GeneralSecurityException, NotFoundException, IOException, HttpCodeException {
        NursingOrder selectOrder = nursingOrderService.getById(order.getId());
        if(selectOrder == null) throw new RuntimeException("订单不存在");
        if(selectOrder.getStatus() != 0 && selectOrder.getStatus() != 1)
            throw new RuntimeException("当前订单无法取消");
        //已支付订单,先退款
        if(selectOrder.getStatus() == 1 && (int)(refund(order).get(AjaxResult.CODE_TAG)) == HttpStatus.ERROR)
            return AjaxResult.error("取消失败");
        //关闭订单
        return toAjax(nursingOrderService.cancelOrder(order.getId()));
    }

    @PreAuthorize("@ss.hasPermi('nursing:order:remove')")
    @Log(title = "护理管理/订单中心/订单列表:删除并退款", businessType = BusinessType.DELETE)
    @DeleteMapping("/deleteAndRefund/batch")
    public AjaxResult deleteAndRefund(@RequestParam("id") List<Integer> ids) throws GeneralSecurityException, NotFoundException, IOException, HttpCodeException {
        Map<Integer,List<NursingOrder>> map =
                nursingOrderService.listByIds(ids)
                        .stream()
                        .peek(e -> e.setRefundAmount(null))
                        .collect(Collectors.groupingBy(e ->(e.getStatus() == 0 || e.getStatus() == 4) ? 0 : 1 ));
        //未支付订单直接删除
        boolean result = true;
        if(map.get(0) != null)
                result = nursingOrderService.removeByIds(map.get(0)
                        .stream()
                        .map(NursingOrder::getId)
                        .toList());
        //已支付订单先退款再删除
        for(NursingOrder order : map.get(1))
            if((int)(refund(order).get(AjaxResult.CODE_TAG)) == HttpStatus.SUCCESS)
                nursingOrderService.removeById(order.getId());
            else result = false;

        return result ? AjaxResult.success("全部删除成功") : AjaxResult.error("存在部分订单删除失败");
    }
    @PreAuthorize("@ss.hasPermi('nursing:order:remove')")
    @Log(title = "护理管理/订单中心/订单列表", businessType = BusinessType.DELETE)
    @DeleteMapping("/batch")
    public AjaxResult delete(@RequestParam("id") List<Integer> ids) throws GeneralSecurityException, NotFoundException, IOException, HttpCodeException {
        return toAjax(nursingOrderService.removeByIds(ids));
    }

    @GetMapping("/list")
    public TableDataInfo list(NursingOrder nursingOrder){
        startPage();
        List<NursingOrder> list = nursingOrderService.getListByExample(nursingOrder);
        return getDataTable(list);
    }

    @GetMapping("/listOptions")
    public TableDataInfo listOptions(NursingOrder nursingOrder){
        //进行中的订单
        nursingOrder.setStatusList(List.of(2,3));
        //忽略提交过护理记录的订单
        nursingOrder.setRecordId(-1);
        //非管理员只能查看自己正在接的订单
        if(getLoginUser().getUser().getRoles().stream().noneMatch(e -> e.getRoleKey().contains("admin"))){
            nursingOrder.setWorkerIds(
                    Stream.concat(Stream.of(-1),
                                    nursingWorkerService.getListByExample(NursingWorker.builder()
                                                    .userId(getUserId())
                                                    .statusList(List.of("0","2"))
                                                    .build())
                                            .stream()
                                            .map(NursingWorker::getId))
                            .toList()
            );
        }
        startPage();
        List<NursingOrder> list = nursingOrderService.getListByExample(nursingOrder);
        return getDataTable(list);
    }

    //支付
    private void saveOrderAndresponseToQRCode(HttpServletResponse response,NursingOrder order) throws IOException, GeneralSecurityException, NotFoundException, HttpCodeException {
        String code = null;
        BigDecimal totalAmount = order.getPayAmount().setScale(2, RoundingMode.HALF_UP);;
        String outTradeNo = order.getOrderSn();
        String logo = null;
        //支付宝支付
        if(order.getPayType() == 1) {
            AlipayInfo alipayInfo = AlipayInfo.builder()
                    .userId(order.getUserId())
                    .deptId(order.getDeptId())
                    .totalAmount(totalAmount)
                    .tradeNo(outTradeNo)
                    .subject(null)
                    .build();

            ObjectNode objectNode = alipayManager.paymentPreCreate(alipayInfo);
            code = objectNode.get("qr_code").asText();
            //totalAmount = objectNode.get("totalAmount").asText();
            //outTradeNo = objectNode.get("outTradeNo").asText();
            logo = environment.getProperty("alipay.logo");
        }
        //微信支付
        else if(order.getPayType() == 2){
            WeChatPayInfo info = WeChatPayInfo.builder()
                    .ip(getLoginUser().getIpaddr())
                    .userId(getUserId())
                    .deptId(getDeptId())
                    .totalAmount(totalAmount)
                    .tradeNo(outTradeNo)
                    .subject(null)
                    .buildNative();
            //获取支付code_url
            code = objectMapper.readTree(weChatPayManager.paymentPay(info))
                    .get("code_url").asText();
            logo = environment.getProperty("wechatpay.logo");
        }

        //保存支付二维码
        order.setQrCode(code);
        //保存订单
        if(!nursingOrderService.saveCascade(order)) throw new RuntimeException("订单保存失败!");

        //禁止图片缓存
        response.setHeader("Pragma","no-cache");
        response.setHeader("Cache-Control","no-cache");
        response.setDateHeader("Expires",0);
        //根据图片类型来修改相应的contentType
        response.setContentType("image/png");

        //自定义响应头,添加金额和订单号信息
        response.addHeader("pay-amount",totalAmount.stripTrailingZeros().toPlainString());
        response.addHeader("pay-order",outTradeNo);
        QRCodeUtil.createCodeAndLogoToOutputStream(code,logo , response.getOutputStream());
    }
}
