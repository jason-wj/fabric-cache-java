# fabric-cache-java
本项目是针对官方fabric-gateway-java的缓存化处理，加快链接建立、数据访问速度。参考了[FabricJavaPool](https://github.com/SamYuan1990/FabricJavaPool)，对其做了大量改动和调整，其中将原先作者的memcached缓存切换为redis，集中优化处理了gateway的读写优化。
具体内容后面有时间了再来详细描述

## 使用
maven中，需先加入：
```pom
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-pool2</artifactId>
    <version>2.8.0</version>
</dependency>

<dependency>
    <groupId>org.hyperledger.fabric</groupId>
    <artifactId>fabric-gateway-java</artifactId>
    <version>2.2.0</version>
</dependency>

<dependency>
    <groupId>redis.clients</groupId>
    <artifactId>jedis</artifactId>
    <version>3.3.0</version>
</dependency>
```