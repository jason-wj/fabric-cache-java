package com.jason.fabric.pool.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.jason.fabric.pool.utils.RedisUtil;
import com.jason.fabric.pool.utils.StringUtil;

public class FabricContractConnectCacheProxyImpl implements InvocationHandler {

    private final Object obj;
    private final String userName;
    private final String channelName;
    private static final String METHOD_QUERY = "query";
    private static final String METHOD_INVOKE = "invoke";

    public FabricContractConnectCacheProxyImpl(Object obj, String userName, String channelName) {
        this.channelName = channelName;
        this.userName = userName;
        this.obj = obj;
    }

    /**
     * 生成缓存key
     * @return
     */
    public String genericKey(String userName,String channelName,String chainCodeName){
        return userName.concat(channelName).concat(chainCodeName);
    }

    public String genericField(Object[] args) {
        String field = "";
        field = field.concat(args[0].toString());
        field = field.concat(args[1].toString());
        if (args.length > 2) {
            String[] list = (String[]) args[2];
            for (Object l : list) {
                field = field.concat(l.toString());
            }
        }
        return field;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        switch (method.getName()){
            case METHOD_QUERY:
                String field = genericField(args);
                String chainCodeName = (String) args[0];  //args[0]为chainCode名称
                if(StringUtil.isBlank(chainCodeName)){
                    throw new Exception("chaincode name is error!");
                }
                String key = genericKey(userName,channelName,chainCodeName);
                String r = RedisUtil.hget(key,field);
                if(!StringUtil.isBlank(r)){
                    return r;
                }
                //若不存在，则加入缓存
                result = method.invoke(obj, args);
                if(result==null){
                    result="";
                }
                RedisUtil.hset(key,field, (String) result);
                break;
            case METHOD_INVOKE:
                result = method.invoke(obj, args);
                String invokChainCodeName = (String) args[0];  //args[0]为chainCode名称
                if(StringUtil.isBlank(invokChainCodeName)){
                    throw new Exception("chaincode name is error!");
                }
                RedisUtil.del(genericKey(userName,channelName,invokChainCodeName));
                break;
            default:
                result = method.invoke(obj, args);
        }
        return result;
    }
}