package com.jason.fabric.pool.api;

public interface FabricConnection {

    String query(String chainCode, String fcn, String... arguments) throws Exception;

    String invoke(String chainCode, String fcn, String... arguments) throws Exception;

}
