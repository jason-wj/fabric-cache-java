package com.jason.fabric.pool.impl;

import com.jason.fabric.pool.FabricGatewayPool;
import com.jason.fabric.pool.api.FabricConnection;
import org.apache.commons.pool2.ObjectPool;
import org.junit.Assert;
import org.junit.Test;

public class FabricContractConnectImplTest {

    @Test
    public void query() throws Exception {
        ObjectPool<FabricConnection> fGWP = new FabricGatewayPool("test", "mychannel");
        FabricConnection contractConnect = fGWP.borrowObject();  //获取网络实例
        String result = contractConnect.query("hospital_recordInfo", "QueryHistoryRecord","110121");
        Assert.assertEquals(result,"{\"recordInfos\":[{\"identity\":\"110114\",\"sickName\":\"zhangsan\",\"drugName\":[\"110114\",\"110115\"]}]}");
    }

    @Test
    public void invoke() throws Exception {
        ObjectPool<FabricConnection> fGWP = new FabricGatewayPool("test", "mychannel");
        FabricConnection contractConnect = fGWP.borrowObject();  //获取网络实例
        String result = contractConnect.invoke("hospital_recordInfo","Save","110121","zhangsan","[\"110114\",\"110115\"]","110114");
        Assert.assertEquals("", result);
    }
}