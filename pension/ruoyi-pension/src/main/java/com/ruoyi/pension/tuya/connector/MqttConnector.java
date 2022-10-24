package com.ruoyi.pension.tuya.connector;

import com.google.gson.JsonObject;
import com.tuya.connector.api.annotations.Body;
import com.tuya.connector.api.annotations.GET;
import com.tuya.connector.api.annotations.POST;
import com.tuya.connector.api.annotations.Path;
import com.tuya.connector.api.model.Result;
import java.util.Map;

public interface MqttConnector{
    @POST("/v1.0/open-hub/access/config")
    Result<JsonObject> getMqttConfig(@Body Map<String,String> body);
    @GET("/v1.0/users/{uid}/devices/{deviceId}/webrtc-configs")
    Result<JsonObject> getWebRTCInfo(@Path("uid") String uid,@Path("deviceId") String deviceId);
}
