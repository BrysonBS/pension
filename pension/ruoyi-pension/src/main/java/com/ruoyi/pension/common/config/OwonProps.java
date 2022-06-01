package com.ruoyi.pension.common.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Component
@ConfigurationProperties(prefix = "owon")
public class OwonProps {

    @JsonIgnore
    private String server_china;
    private String server_other;
    private String uri_accessToken_get;
    private String uri_accessToken_refresh;
    private String uri_sendGwData;


    private String agentId;
    private String agentKey;
    private String accessToken;
    private String refreshToken;
    private long ts;
    //private String username;
    //private String password;
    private String mac = "3C6A2CFFFED0FF3D";


    public String getRequestJson() throws JsonProcessingException {
        return JsonMapper.builder().build()
                .createObjectNode()
                .put("agentId",this.agentId)
                .put("agentKey",this.agentKey)
                .put("ts",this.ts)
                .toString();
    }
    public String getRefreshRequestJson(){
        return JsonMapper.builder().build()
                .createObjectNode()
                .put("agentId",this.agentId)
                .put("refreshToken",this.refreshToken)
                .put("ts",this.ts)
                .toString();
    }
}
