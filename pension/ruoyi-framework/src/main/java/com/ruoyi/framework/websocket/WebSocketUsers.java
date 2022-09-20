package com.ruoyi.framework.websocket;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.websocket.EncodeException;
import javax.websocket.Session;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.spring.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;

/**
 * websocket 客户端用户集
 * 
 * @author ruoyi
 */
public class WebSocketUsers
{
    /**
     * WebSocketUsers 日志控制器
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(WebSocketUsers.class);

    /**
     * 用户集
     */
    private static final Map<String, CopyOnWriteArraySet<Session>> USERS = new ConcurrentHashMap<>();
    private static final CopyOnWriteArraySet<Session> delFlag = new CopyOnWriteArraySet<>();//用于删除标记

    /**
     * 存储用户
     *
     * @param key 唯一键
     * @param session 用户信息
     */
    public static void put(String key, Session session)
    {
        USERS.putIfAbsent(key,new CopyOnWriteArraySet<>());
        USERS.computeIfPresent(key,(k,set) -> {
            set.add(session);
            return set;
        });
    }

    /**
     * 移除用户
     *
     * @param session 用户信息
     *
     * @return 移除结果
     */
    public static boolean remove(String key,Session session) {
        USERS.computeIfPresent(key,(k,set) -> {
            set.remove(session);
            if(set.isEmpty()) return delFlag;
            return set;
        });
        USERS.remove(key,delFlag);
        return true;
    }

    /**
     * 获取在线用户列表
     *
     * @return 返回用户集合
     */
    public static Map<String, CopyOnWriteArraySet<Session>> getUsers() {
        return USERS;
    }

    /**
     * 发送消息到指定账号
     * @param userId 账号
     * @param message 消息内容
     */
    public static void sendMessageToUsers(String userId, AjaxResult message){
        CopyOnWriteArraySet<Session> values = USERS.get(userId);
        if(values == null) return;
        for (Session value : values) {
            sendMessageToUser(value, message);
        }
    }

    /**
     * 发送消息到会话
     * @param session 会话
     * @param message 消息内容
     */
    public static void sendMessageToUser(Session session,AjaxResult message){
        if(session != null) {
            try{
                session.getBasicRemote().sendObject(message);
            }
            catch(IOException e){
                LOGGER.error("\n[发送消息异常]", e);
            } catch (EncodeException e) {
                LOGGER.info("\n[websocket消息转换字符串失败]",e);
            }
        }
        else {
            LOGGER.info("\n[你已离线]");
        }
    }

    /**
     * 群发消息文本消息
     *
     * @param message 消息内容
     */
    public static void sendMessageToUsersByText(String userId,String message) {
        CopyOnWriteArraySet<Session> values = USERS.get(userId);
        if(values == null) return;
        for (Session value : values) {
            sendMessageToUserByText(value, message);
        }
    }

    /**
     * 发送文本消息
     *
     * @param session 会话
     * @param message 消息内容
     */
    public static void sendMessageToUserByText(Session session, String message) {
        if (session != null)
        {
            try
            {
                session.getBasicRemote().sendText(message);
            }
            catch (IOException e)
            {
                LOGGER.error("\n[发送消息异常]", e);
            }
        }
        else
        {
            LOGGER.info("\n[你已离线]");
        }
    }
}
