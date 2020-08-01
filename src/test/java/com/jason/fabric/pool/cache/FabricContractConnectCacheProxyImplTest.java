package com.jason.fabric.pool.cache;

import java.lang.reflect.Method;

import com.jason.fabric.pool.impl.FabricContractConnectCacheProxyImpl;
import org.junit.Assert;
import org.junit.Test;


public class FabricContractConnectCacheProxyImplTest {

    static String noQuery() {
        return "a";
    }

    static String query(String a, String b) {
        return "a";
    }

    /**
     * 生成缓存的key
     * @throws Throwable
     */
    @Test
    public void genericKey() throws Throwable{
        FabricContractConnectCacheProxyImpl test = new FabricContractConnectCacheProxyImpl("test", "test", "test");
        String rs = test.genericKey("user", "mychannel","hospital_record");
        Assert.assertEquals("usermychannelhospital_record", rs);
    }

    @Test
    public void invokeForQuery() throws Throwable {
        FabricContractConnectCacheProxyImpl test = new FabricContractConnectCacheProxyImpl("test", "Jason", "mychannel");
        Method method = FabricContractConnectCacheProxyImplTest.class.getDeclaredMethod("query", String.class, String.class);
        String rs = (String) test.invoke(null, method, new Object[]{"1", "2"});
        Assert.assertEquals("a", rs);
    }

    @Test
    public void invokeNonQuery() throws Throwable {
        FabricContractConnectCacheProxyImpl test = new FabricContractConnectCacheProxyImpl("test", "test", "test");
        Method method = FabricContractConnectCacheProxyImplTest.class.getDeclaredMethod("noQuery");
        String rs = (String) test.invoke(null, method, null);
        Assert.assertEquals("a", rs);
    }
}