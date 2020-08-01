package com.jason.fabric.pool;

import java.lang.reflect.Proxy;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.jason.fabric.pool.impl.FabricContractConnectCacheProxyImpl;
import com.jason.fabric.pool.conf.Global;
import com.jason.fabric.pool.api.FabricConnection;
import com.jason.fabric.pool.impl.FabricContractConnectImpl;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;

public class FabricGatewayPool extends GenericObjectPool<FabricConnection> {
    public FabricGatewayPool(String userName, String channel) {
        super(new ContractPoolFactory(userName, channel), Global.getInstance());
    }

    private static class ContractPoolFactory extends BasePooledObjectFactory<FabricConnection> {
        private final String userName;
        private final String channel;

        ContractPoolFactory(String userName, String channel){
            this.userName = userName;
            this.channel = channel;
        }

        @Override
        public FabricConnection create() throws Exception {
            Path walletDirectory = Paths.get(Global.getInstance().getWalletDirPath());
            Wallet wallet = Wallets.newFileSystemWallet(walletDirectory);
            Path netConfigPath = Paths.get(Global.getInstance().getNetConfigFile());
            Gateway.Builder builder = Gateway.createBuilder().identity(wallet, userName).networkConfig(netConfigPath);
            Gateway gateway = builder.connect();
            FabricContractConnectImpl fCCI = new FabricContractConnectImpl(gateway.getNetwork(channel));
            if (Global.getInstance().isUseCache()) {
                FabricContractConnectCacheProxyImpl proxy = new FabricContractConnectCacheProxyImpl(fCCI, userName, channel);
                return (FabricConnection) Proxy.newProxyInstance(FabricContractConnectImpl.class.getClassLoader(), new Class[]{FabricConnection.class}, proxy);
            } else {
                return fCCI;
            }
        }

        @Override
        public PooledObject<FabricConnection> wrap(FabricConnection obj) {
            return new DefaultPooledObject<>(obj);
        }

    }
}
