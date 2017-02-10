package com.example.lj.redwine.constant;

/**
 * Created by Administrator on 2016/11/17 0017.
 */
public class ConstantClass {
    private static final String http_prefix = "http://192.168.1.101:8080/redwine";//http后台访问前缀

    private static final String web_socket_prefix = "ws://192.168.1.101:8080/redwine/websocket/";//websocket后台访问前缀

    public static String getHttp_prefix() {
        return http_prefix;
    }
    public static String getWeb_socket_prefix() {
        return web_socket_prefix;
    }
}
