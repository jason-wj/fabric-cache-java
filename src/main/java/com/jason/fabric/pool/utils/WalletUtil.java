package com.jason.fabric.pool.utils;

import com.jason.fabric.pool.conf.Global;
import org.hyperledger.fabric.gateway.Identities;
import org.hyperledger.fabric.gateway.Identity;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.cert.CertificateException;

/**
 * 一些通用方法
 */
public final class WalletUtil {
    private WalletUtil(){}

    /**
     * 通过字符串将用户钱包加入
     * @param walletName
     * @param mspId
     * @param certificate
     * @param privateKey
     * @throws IOException
     * @throws CertificateException
     * @throws InvalidKeyException
     */
    public Identity putWallet(String walletName,String mspId,String certificate, String privateKey) throws IOException, CertificateException, InvalidKeyException {
        X509Credentials credentials = new X509Credentials(certificate, privateKey);
        Path walletDirectory = Paths.get(Global.getInstance().getWalletDirPath());
        Wallet wallet = Wallets.newFileSystemWallet(walletDirectory);
        Identity identity = Identities.newX509Identity(mspId, credentials.getCertificate(), credentials.getPrivateKey());
        wallet.put(walletName,identity);
        return identity;
    }

    /**
     * 通过路径将用户密钥等加入钱包
     * @param walletName
     * @param mspId
     * @param certificatePath
     * @param privateKeyPath
     * @throws IOException
     * @throws CertificateException
     * @throws InvalidKeyException
     */
    public Identity putWallet(String walletName,String mspId,Path certificatePath, Path privateKeyPath) throws IOException, CertificateException, InvalidKeyException {
        X509Credentials credentials = new X509Credentials(certificatePath, privateKeyPath);
        Path walletDirectory = Paths.get(Global.getInstance().getWalletDirPath());
        Wallet wallet = Wallets.newFileSystemWallet(walletDirectory);
        Identity identity = Identities.newX509Identity(mspId, credentials.getCertificate(), credentials.getPrivateKey());
        wallet.put(walletName, identity);
        return identity;
    }

    /**
     * 获取当前钱包用户
     * @param walletName 钱包名
     * @return
     * @throws IOException
     * @throws CertificateException
     * @throws InvalidKeyException
     */
    public Identity getWallet(String walletName) throws IOException, CertificateException, InvalidKeyException {
        Path walletDirectory = Paths.get(Global.getInstance().getWalletDirPath());
        Wallet wallet = Wallets.newFileSystemWallet(walletDirectory);
        return wallet.get(walletName);
    }

    public static WalletUtil getInstance(){
        return SingletonHolder.instance;
    }

    private static class SingletonHolder{
        private static final WalletUtil instance = new WalletUtil();  //静态初始化器，由JVM来保证线程安全
    }
}
