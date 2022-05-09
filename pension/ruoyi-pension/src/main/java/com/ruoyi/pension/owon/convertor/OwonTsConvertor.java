package com.ruoyi.pension.owon.convertor;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class OwonTsConvertor {
    public static long UTC0_SECOND = ZonedDateTime
            .of(2000,1,1,0,0,0,0, ZoneId.of("UTC"))
            .toEpochSecond();
    public static long getNowTs(){
        return (ZonedDateTime.now(ZoneId.of("UTC")).toEpochSecond()
                - UTC0_SECOND);
    }

}
