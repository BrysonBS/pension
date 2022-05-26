package com.ruoyi.framework.config.properties;

import com.ruoyi.common.annotation.DataSource;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "aliyun")
public class AliyunProps {
    private String ossEndpoint;
    private String accessKey;
    private String secret;
    private String bucket;
}
