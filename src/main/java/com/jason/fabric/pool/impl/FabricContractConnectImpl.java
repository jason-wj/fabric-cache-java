package com.jason.fabric.pool.impl;

import java.nio.charset.StandardCharsets;

import com.jason.fabric.pool.api.FabricConnection;
import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.Network;


public class FabricContractConnectImpl implements FabricConnection {

    private Network network;

    public FabricContractConnectImpl(Network network) {
        this.network = network;
    }

    @Override
    public String query(String chainCode, String fcn, String... arguments) throws Exception {
        Contract contract = network.getContract(chainCode);
        byte[] queryAllResult = contract.evaluateTransaction(fcn, arguments);
        return new String(queryAllResult, StandardCharsets.UTF_8);
    }

    @Override
    public String invoke(String chainCode, String fcn, String... arguments) throws Exception {
        Contract contract = network.getContract(chainCode);
        byte[] invokeAllResult = contract.submitTransaction(fcn, arguments);
        return new String(invokeAllResult, StandardCharsets.UTF_8);
    }
}
