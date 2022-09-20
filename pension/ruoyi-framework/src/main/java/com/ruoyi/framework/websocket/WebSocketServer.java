package com.ruoyi.framework.websocket;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.function.BiFunction;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.spring.SpringUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * websocket 消息处理
 * 
 * @author ruoyi
 */
@Component
//多实例,每个链接创建一个对象
@ServerEndpoint(value = "/websocket/message/{token}",encoders = { ServerEncoder.class })
public class WebSocketServer {
    /**
     * WebSocketServer 日志控制器
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketServer.class);

    /**
     * 默认最多允许同时在线人数100
     */
    public static int socketMaxOnlineCount = 3;

    private static Semaphore socketSemaphore = new Semaphore(socketMaxOnlineCount);

    private LoginUser loginUser;
    private Session session;
    private String token;


    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("token") String token) throws IOException {
        this.session = session;
        this.loginUser = getLoginUser(token);
        this.token = token;
        boolean semaphoreFlag = false;
        // 尝试获取信号量
        semaphoreFlag = SemaphoreUtils.tryAcquire(socketSemaphore);
        if (!semaphoreFlag || loginUser == null) {
            if(semaphoreFlag){
                LOGGER.info("\n websocket用户认证失败");
                WebSocketUsers.sendMessageToUser(session, AjaxResult.error("用户认证失败"));
            }
            else {
                // 未获取到信号量
                LOGGER.error("\n 当前在线人数超过限制数- {}", socketMaxOnlineCount);
                WebSocketUsers.sendMessageToUser(session,AjaxResult.error("当前在线人数超过限制数：" + socketMaxOnlineCount));
            }
            session.close();
        }
        else {
            // 添加用户
            //session.setMaxIdleTimeout(300000);//300s
            WebSocketUsers.put(loginUser.getUserId()+"",session);

            LOGGER.info("\n 建立连接 - {}", session);
            //LOGGER.info("\n 当前连接数 - {}",socketMaxOnlineCount - socketSemaphore.availablePermits());
            LOGGER.info("\n 当前人数 - {}", WebSocketUsers.getUsers().size());
            WebSocketUsers.sendMessageToUser(session,AjaxResult.success("连接成功"));
        }
    }

    /**
     * 连接关闭时处理
     */
    @OnClose
    public void onClose() {
        try {
            LOGGER.info("\n 关闭连接 - {}", session);
            // 移除用户
            if(loginUser != null)
                WebSocketUsers.remove(loginUser.getUserId()+"",session);
        }finally {
            SemaphoreUtils.release(socketSemaphore);//释放信号量
        }
    }

    /**
     * 抛出异常时处理
     */
    @OnError
    public void onError(Throwable exception) throws Exception {
        if (session.isOpen())
        {
            // 关闭连接
            session.close();
        }
        //String sessionId = session.getId();
        LOGGER.info("\n 连接异常 - {}", loginUser.getUsername()+" : "+loginUser.getUserId());
        LOGGER.info("\n 异常信息 - {}", exception);
        // 移出用户
        //WebSocketUsers.remove(sessionId);
        WebSocketUsers.remove(loginUser.getUserId()+"",session);
        // 获取到信号量则需释放
        SemaphoreUtils.release(socketSemaphore);
    }

    /**
     * 服务器接收到客户端消息时调用的方法
     */
    @OnMessage
    public void onMessage(String message) {
        LoginUser user = getLoginUser(this.token);
        if(user == null)
            WebSocketUsers.sendMessageToUser(session,AjaxResult.error("登录过期"));
        else {
            ObjectMapper objectMapper = SpringUtils.getBean(ObjectMapper.class);
            JsonNode jsonNode = null;
            try {
                jsonNode = objectMapper.readTree(message);
            } catch (JsonProcessingException e) {
                LOGGER.error("前端传入websocket数据无法转为json",e);
                e.printStackTrace();
            }
            String operate = jsonNode.at("/operate").asText();
            //心跳
            if("0".equals(operate)){
                WebSocketUsers.sendMessageToUser(session,AjaxResult.success().put(AjaxResult.OPERATE_TAG,"0"));
                return;
            }

            //其他
            BiFunction<LoginUser,JsonNode,AjaxResult> messageHandler = SpringUtils.getBean(operate);
            WebSocketUsers.sendMessageToUser(session,messageHandler.apply(user,jsonNode));

        }
    }


    private LoginUser getLoginUser(String token) {
        Environment environment = SpringUtils.getBean(Environment.class);
        String secret = environment.getProperty("token.secret");
        if (StringUtils.isNotEmpty(token) && !"undefined".equalsIgnoreCase(token)){
            try {
                Claims claims = Jwts.parser()
                        .setSigningKey(secret)
                        .parseClaimsJws(token)
                        .getBody();
                // 解析对应的权限以及用户信息
                String uuid = (String) claims.get(Constants.LOGIN_USER_KEY);
                String userKey = Constants.LOGIN_TOKEN_KEY + uuid;
                RedisCache redisCache = SpringUtils.getBean(RedisCache.class);
                LoginUser user = redisCache.getCacheObject(userKey);
                return user;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return null;
    }
}
