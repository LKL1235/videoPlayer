package com.example.demo.controller;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author websocket服务
 */
@ServerEndpoint(value = "/ws/{roomId}/{username}")
@Component
public class WebSocketServer {
    private static final Logger log = LoggerFactory.getLogger(WebSocketServer.class);
    public static final Map<String, Session> sessionMap = new ConcurrentHashMap<>();

    public static final Map<Integer, List<String>> roomMap = new ConcurrentHashMap<>();
    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session,@PathParam("roomId") Integer roomId, @PathParam("username") String username) {
        sessionMap.put(username, session);
        if(roomMap.containsKey(roomId)){
            roomMap.get(roomId).add(username);
        }else{
            ArrayList<String> users = new ArrayList<>();
            users.add(username);
            roomMap.put(roomId,users);
        }
        List<Session> sessionList=new ArrayList<>();
        roomMap.get(roomId).forEach(item->sessionList.add(sessionMap.get(item)));
        if (!sessionList.isEmpty()) {
            // 服务器端 再把消息组装一下，组装后的消息包含发送人和发送的文本内容
            // {"from": "zhang", "text": "hello"}
            JSONObject jsonObject = new JSONObject();
            jsonObject.set("url", "");  // from 是 zhang
            jsonObject.set("time", "");  // text 同上面的text
            jsonObject.set("isLive", false);
            jsonObject.set("tips", "用户" + username + "加入了");
            jsonObject.set("isPause",false);
            sessionList.forEach(item -> this.sendMessage(jsonObject.toString(), item));
        }
            log.info("有新用户加入，username={}, 当前在线人数为：{}", username, sessionMap.size());

    }
    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(Session session, @PathParam("username") String username) {
        sessionMap.remove(username);
        log.info("有一连接关闭，移除username={}的用户session, 当前在线人数为：{}", username, sessionMap.size());
    }
    /**
     * 收到客户端消息后调用的方法
     * 后台收到客户端发送过来的消息
     * onMessage 是一个消息的中转站
     * 接受 浏览器端 socket.send 发送过来的 json数据
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session,@PathParam("roomId") Integer roomId, @PathParam("username") String username) {
        log.info("服务端收到用户username={}的消息:{}", username, message);
        JSONObject obj = JSONUtil.parseObj(message);
        String url = obj.getStr("url"); // to表示发送给哪个用户，比如 admin
        String time = obj.getStr("time"); // 发送的消息文本  hello
        boolean isLive=(boolean)obj.get("isLive");
        String tips=obj.getStr("tips");
        boolean isPause=(boolean)obj.get("isPause");
        // {"to": "admin", "text": "聊天文本"}

        List<Session> sessionList=new ArrayList<>();
        roomMap.get(roomId).forEach(item->sessionList.add(sessionMap.get(item)));
        if (!sessionList.isEmpty()) {
            // 服务器端 再把消息组装一下，组装后的消息包含发送人和发送的文本内容
            // {"from": "zhang", "text": "hello"}
            JSONObject jsonObject = new JSONObject();
            jsonObject.set("url", url);  // from 是 zhang
            jsonObject.set("time", time);  // text 同上面的text
            jsonObject.set("isLive",isLive);
            jsonObject.set("tips",tips);
            jsonObject.set("isPause",isPause);
            sessionList.forEach(item->this.sendMessage(jsonObject.toString(), item));

            log.info("发送给用户username={}，消息：{}", sessionList, jsonObject.toString());
        } else {
            log.info("发送失败");
        }
    }
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }
    /**
     * 服务端发送消息给客户端
     */
    private void sendMessage(String message, Session toSession) {
        try {
            log.info("服务端给客户端[{}]发送消息{}", toSession.getId(), message);
            toSession.getBasicRemote().sendText(message);
        } catch (Exception e) {
            log.error("服务端发送消息给客户端失败", e);
        }
    }
    /**
     * 服务端发送消息给所有客户端
     */
    private void sendAllMessage(String message) {
        try {
            for (Session session : sessionMap.values()) {
                log.info("服务端给客户端[{}]发送消息{}", session.getId(), message);
                session.getBasicRemote().sendText(message);
            }
        } catch (Exception e) {
            log.error("服务端发送消息给客户端失败", e);
        }
    }
}
