package com.ruoyi.pension.owon.domain.result;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OwonResult {
    //"{\"reuslt\":true,\"description\":\"success\"}";
    private Boolean result;
    private String description;

    public static OwonResult success(){
        return new OwonResult(true,"success");
    }
    public static OwonResult failure(){
        return new OwonResult(false,"failure");
    }
}
