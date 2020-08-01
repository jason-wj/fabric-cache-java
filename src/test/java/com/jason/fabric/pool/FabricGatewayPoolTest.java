package com.jason.fabric.pool;

import com.jason.fabric.pool.api.FabricConnection;
import org.apache.commons.pool2.ObjectPool;
import org.junit.Test;

import static org.junit.Assert.*;

public class FabricGatewayPoolTest {

    @Test
    public void testGatewayPool() throws Exception {
        ObjectPool<FabricConnection> fGWP = new FabricGatewayPool("test", "mychannel");
        FabricConnection contractConnect1 = fGWP.borrowObject();  //获取网络实例
        FabricConnection contractConnect2 = fGWP.borrowObject();
        assertNotEquals(contractConnect1, contractConnect2);
    }

    @Test public void testGatewayPoolException() {
        ObjectPool<FabricConnection>  fabricConnectionPool = new FabricGatewayPool(null, "mychannel");
        try {
            fabricConnectionPool.borrowObject();
        } catch (Exception e) {
            assertNotNull(e);
        }
    }

}