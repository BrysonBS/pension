package com.ruoyi.pension.common.mapper;
import java.util.List;
import org.apache.ibatis.annotations.Param;

import com.ruoyi.pension.common.domain.po.PensionUpload;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
* @author Administrator
* @description 针对表【pension_upload】的数据库操作Mapper
* @createDate 2022-05-30 13:46:01
* @Entity com.ruoyi.pension.common.domain.po.PensionUpload
*/
public interface PensionUploadMapper extends BaseMapper<PensionUpload> {
    List<PensionUpload> getAllByRelateId(@Param("relateId") Integer relateId);
}




