package com.ruoyi.pension.common.message;

import com.ruoyi.common.core.domain.model.LoginUser;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PensionMessage {
    @NotNull
    private LoginUser user;
    private String operate;
    private String type;
    private String message;
}
