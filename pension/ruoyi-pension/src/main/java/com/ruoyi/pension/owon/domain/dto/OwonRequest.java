package com.ruoyi.pension.owon.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OwonRequest {
    private String agentId;
    private String mac;
    private String accessToken;
    private String sjson;
    private String ts;
}
