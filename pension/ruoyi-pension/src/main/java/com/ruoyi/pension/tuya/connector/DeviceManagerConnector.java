package com.ruoyi.pension.tuya.connector;

import com.google.gson.JsonObject;
import com.tuya.connector.api.annotations.GET;
import com.tuya.connector.api.annotations.QueryMap;
import com.tuya.connector.api.model.Result;

import java.util.Map;

public interface DeviceManagerConnector {
    @GET("/v1.3/iot-03/devices")
    Result<JsonObject> getDeviceList(@QueryMap Map<String,String> body);
}
