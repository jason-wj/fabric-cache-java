package com.jason.fabric.pool;

import com.jason.fabric.pool.utils.WalletUtil;
import org.hyperledger.fabric.gateway.Identity;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.cert.CertificateException;

public class WalletUtilTest {

    private static final String certificate = "-----BEGIN CERTIFICATE-----\n" +
            "MIICKjCCAdCgAwIBAgIQGxTHkM4m6HxKBTParJ9fBjAKBggqhkjOPQQDAjBzMQsw\n" +
            "CQYDVQQGEwJVUzETMBEGA1UECBMKQ2FsaWZvcm5pYTEWMBQGA1UEBxMNU2FuIEZy\n" +
            "YW5jaXNjbzEZMBcGA1UEChMQb3JnMS5leGFtcGxlLmNvbTEcMBoGA1UEAxMTY2Eu\n" +
            "b3JnMS5leGFtcGxlLmNvbTAeFw0yMDA3MzEwMTM4MDBaFw0zMDA3MjkwMTM4MDBa\n" +
            "MGwxCzAJBgNVBAYTAlVTMRMwEQYDVQQIEwpDYWxpZm9ybmlhMRYwFAYDVQQHEw1T\n" +
            "YW4gRnJhbmNpc2NvMQ8wDQYDVQQLEwZjbGllbnQxHzAdBgNVBAMMFlVzZXIxQG9y\n" +
            "ZzEuZXhhbXBsZS5jb20wWTATBgcqhkjOPQIBBggqhkjOPQMBBwNCAAQCK9Mz133o\n" +
            "gPsIdlD/Rsi38kkjCsjEGhGmvTy/YaZEvXveK4Fw5aqxy17//4s99T2zTeZMIjkD\n" +
            "FfhK5KKU4GLno00wSzAOBgNVHQ8BAf8EBAMCB4AwDAYDVR0TAQH/BAIwADArBgNV\n" +
            "HSMEJDAigCAcnUlSZZsufUAHoCliLN5YGYqDU1ciO2u+W8TRer5GEjAKBggqhkjO\n" +
            "PQQDAgNIADBFAiEAtvE6FCVt0ZUrE0orYS9HyvYArZwx5ld/4jtvpqD/h1cCIHiM\n" +
            "MLwyvQVY2cwcammJR2kdDmSoZiRGjqujoURdnReZ\n" +
            "-----END CERTIFICATE-----\n";
    private static final String privateKey = "-----BEGIN PRIVATE KEY-----\n" +
            "MIGHAgEAMBMGByqGSM49AgEGCCqGSM49AwEHBG0wawIBAQQgkHma/KCEYtrU1MNB\n" +
            "4PDAsSHkIIMzDyEJpp/FlQ3y1fehRANCAAQCK9Mz133ogPsIdlD/Rsi38kkjCsjE\n" +
            "GhGmvTy/YaZEvXveK4Fw5aqxy17//4s99T2zTeZMIjkDFfhK5KKU4GLn\n" +
            "-----END PRIVATE KEY-----\n";

    /**
     * 测试钱包存入和取出
     * @throws CertificateException
     * @throws InvalidKeyException
     * @throws IOException
     */
    @Test
    public void wallet_put_and_get() throws CertificateException, InvalidKeyException, IOException {
        Identity identity1 = WalletUtil.getInstance().putWallet("test","Org1MSP",certificate,privateKey);
        Identity identity2 = WalletUtil.getInstance().getWallet("test");
        Assert.assertEquals(identity1, identity2);

    }
}