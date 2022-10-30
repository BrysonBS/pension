package com.ruoyi.pension.common.domain.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MobileMessage {
    private String templateCode;
    private List<String> messages;
    private List<String> phones;
}
