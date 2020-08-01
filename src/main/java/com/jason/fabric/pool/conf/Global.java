package com.jason.fabric.pool.conf;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.jason.fabric.pool.api.FabricConnection;
import com.jason.fabric.pool.utils.OSinfoUtil;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

public class Global extends GenericObjectPoolConfig<FabricConnection> {
    private boolean useCache;       //是否使用缓存
    private String cacheURL;        //缓存服务器地址
    private String cachePort;      //缓存服务器端口号
    private String cachePwd;        //缓存数据库密码
    private String netConfigFile;   //fabric网络配置文件
    private String walletDirPath;   //存放钱包的路径
    private int cacheTimeout;       //创建连接超时等待
    private int cacheExpireTime;     //key有效时间



    private Global(){
        loadCacheConfig();
    }

    public static  Global getInstance(){
        return SingletonHolder.instance;
    }

    public String getCachePwd() {
        return cachePwd;
    }

    public boolean isUseCache() {
        return useCache;
    }

    public String getCacheURL() {
        return cacheURL;
    }

    public String getCachePort() {
        return cachePort;
    }

    public int getCacheTimeout() {
        return cacheTimeout;
    }

    public int getCacheExpireTime() {
        return cacheExpireTime;
    }

    public String getNetConfigFile() {
        return netConfigFile;
    }

    public String getWalletDirPath() {
        return walletDirPath;
    }

    /**
     * 加载缓存配置
     */
    public void loadCacheConfig() {
        String rootPath = Global.class.getResource("/").getPath();
        if(OSinfoUtil.isWindows()&&rootPath.startsWith("/")){
            rootPath = rootPath.replaceFirst("/","");
        }

        try {
            InputStream in = new FileInputStream(rootPath+"fabric-cache.properties");
            Properties properties = new Properties();
            properties.load(in);
            netConfigFile = rootPath+properties.getProperty("netConfigFile","connection.json");
            walletDirPath = properties.getProperty("walletDirPath",rootPath+"wallet");
            this.setMaxTotal(Integer.parseInt(properties.getProperty("maxTotal","100")));
            this.setMaxIdle(Integer.parseInt(properties.getProperty("maxIdle","100")));
            this.setMinIdle(Integer.parseInt(properties.getProperty("minIdle","5")));
            this.setMaxWaitMillis(Integer.parseInt(properties.getProperty("maxWaitMillis","1000")));
            useCache = Boolean.parseBoolean(properties.getProperty("UseCache","true"));
            cacheURL = properties.getProperty("cacheURL","127.0.0.1");
            cachePwd = properties.getProperty("cachePwd","123456");
            cachePort = properties.getProperty("cachePort","6379");
            cacheExpireTime = Integer.parseInt(properties.getProperty("cacheExpireTime","1000"));
            cacheTimeout = Integer.parseInt(properties.getProperty("cacheTimeout","1000"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class SingletonHolder{
        private static final Global instance = new Global();  //静态初始化器，由JVM来保证线程安全
    }
}



