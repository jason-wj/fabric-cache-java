package com.jason.fabric.pool.utils;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.jason.fabric.pool.conf.Global;
import org.apache.log4j.Logger;

import redis.clients.jedis.*;
import redis.clients.jedis.params.SetParams;
import redis.clients.jedis.params.ZAddParams;
import redis.clients.jedis.params.ZIncrByParams;

/**
 * @ClassName RedisUtil
 * @Description TODO
 * @Author george
 * @Date 2020/2/14 19:28
 * @Param
 * @Version 1.0
 **/
public class RedisUtil {
    private static final Logger log = Logger.getLogger(RedisUtil.class);

    public static JedisPool jedisPool;
    public static int maxTotal;
    public static int maxIdle;
    public static long maxWaitMillis;


    static{
            maxTotal = Global.getInstance().getMaxTotal();
            maxIdle = Global.getInstance().getMaxIdle();
            maxWaitMillis = Global.getInstance().getMaxWaitMillis();
            JedisPoolConfig jedisPoolConfig = initPoolConfig();
            jedisPool = new JedisPool(jedisPoolConfig, Global.getInstance().getCacheURL(), Integer.parseInt(Global.getInstance().getCachePort()),Global.getInstance().getCacheTimeout(),Global.getInstance().getCachePwd(),0,false);;    //配置Jedis的配置，端口，服务器地址
        }

    /**
     * 初始化Jedis
     * @return
     */
    private static JedisPoolConfig initPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        // 最大分配的对象数
        jedisPoolConfig.setMaxTotal(maxTotal);
        // jedis实例
        jedisPoolConfig.setMaxIdle(maxIdle);
        // 最大等待时间
        jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
        jedisPoolConfig.setTestOnBorrow(true);
        // 是否进行有效性检查
        jedisPoolConfig.setTestOnReturn(true);
        return jedisPoolConfig;
    }

    /************************************************************* key 操作 开始 *************************************************************/
    /**
     * 根据key删除缓存
     *
     * @return 被删除key的数量
     * @Author chengpunan
     * @Description //TODO george
     * @Date 15:06 2020/2/12DataType
     * @Param key
     **/
    public static boolean del(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.del(key);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            assert jedis != null;
            jedis.close();
        }
    }

    public static Long del(byte[] key) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.del(key);
        jedis.close();
        return index;
    }

    public static Long del(byte[]... key) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.del(key);
        jedis.close();
        return index;
    }

    public static long xdel(String key, StreamEntryID... ids) {
        Jedis jedis = jedisPool.getResource();
        long index = jedis.xdel(key, ids);
        jedis.close();
        return index;
    }

    /**
     * 序列化给定 key
     *
     * @return 如果 key 不存在，那么返回 nil 。
     * 否则，返回序列化之后的值。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 15:11 2020/2/12
     * @Param key
     **/
    public static byte[] dump(String key) {
        Jedis jedis = jedisPool.getResource();
        byte[] index = jedis.dump(key);
        jedis.close();
        return index;
    }

    public static byte[] dump(byte[] key) {
        Jedis jedis = jedisPool.getResource();
        byte[] index = jedis.dump(key);
        jedis.close();
        return index;
    }

    /**
     * 检查给定 key 是否存在。
     *
     * @return 若 key 存在返回 true ，否则返回 false 。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 15:13 2020/2/12
     * @Param key
     **/
    public static boolean exists(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            if (jedis == null) {
                return false;
            } else {
                return jedis.exists(key);
            }
        } catch (Exception e) {
            log.error("Redis缓存判断key是否存在 出错！", e);
            return false;
        } finally {
            assert jedis != null;
            jedis.close();
        }
    }

    public static Long exists(String... key) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.exists(key);
        jedis.close();
        return index;
    }

    public static boolean exists(byte[] key) {
        Jedis jedis = jedisPool.getResource();
        boolean index = jedis.exists(key);
        jedis.close();
        return index;
    }

    public static Long exists(byte[]... key) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.exists(key);
        jedis.close();
        return index;
    }

    /**
     * 设置 key 的过期时间
     *
     * @return 设置成功返回 1 。
     * 当 key 不存在或者不能为 key 设置过期时间时(比如在低于 2.1.3 版本的 Redis 中你尝试更新 key 的过期时间)返回 0 。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 15:15 2020/2/12
     * @Param key
     * @Param seconds 单位以秒计
     **/
    public static Long expire(String key, int seconds) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.expire(key, seconds);
        jedis.close();
        return index;
    }

    public static Long expire(byte[] key, int seconds) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.expire(key, seconds);
        jedis.close();
        return index;
    }

    /**
     * 用于以 UNIX 时间戳(unix timestamp)格式设置 key 的过期时间
     * 。key 过期后将不再可用。
     *
     * @return 设置成功返回 1 。
     * 当 key 不存在或者不能为 key 设置过期时间时(比如在低于 2.1.3 版本的 Redis 中你尝试更新 key 的过期时间)返回 0 。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 15:18 2020/2/12
     * @Param key
     * @Param unixTime  UNIX 时间戳
     **/
    public static Long expireAt(String key, int unixTime) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.expireAt(key, unixTime);
        jedis.close();
        return index;
    }

    public static Long expireAt(byte[] key, int unixTime) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.expireAt(key, unixTime);
        jedis.close();
        return index;
    }

    /**
     * 设置 key 的过期时间
     *
     * @return 设置成功，返回 1, key 不存在或设置失败，返回 0
     * @Author chengpunan
     * @Description //TODO george
     * @Date 15:19 2020/2/12
     * @Param key
     * @Param milliseconds 毫秒
     **/
    public static Long pexpire(String key, int milliseconds) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.pexpire(key, milliseconds);
        jedis.close();
        return index;
    }

    public static Long pexpire(byte[] key, int milliseconds) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.pexpire(key, milliseconds);
        jedis.close();
        return index;
    }

    /**
     * 命令用于设置 key 的过期时间，以毫秒计。key 过期后将不再可用。
     *
     * @return 设置成功返回 1 。
     * 当 key 不存在或者不能为 key 设置过期时间时(比如在低于 2.1.3 版本的 Redis 中你尝试更新 key 的过期时间)返回 0 。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 15:22 2020/2/12
     * @Param millisecondsUnixTime
     **/
    public static Long pexpireAt(String key, int millisecondsUnixTime) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.pexpireAt(key, millisecondsUnixTime);
        jedis.close();
        return index;
    }

    public static Long pexpireAt(byte[] key, int millisecondsUnixTime) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.pexpireAt(key, millisecondsUnixTime);
        jedis.close();
        return index;
    }

    /**
     * 命令用于查找所有符合给定模式 pattern 的 key 。。
     *
     * @return 符合给定模式的 key 列表 (Array)。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 15:24 2020/2/12
     * @Param 赛选所有key为 key* 的值
     **/
    public static Set<String> keys(String key) {
        Jedis jedis = jedisPool.getResource();
        Set<String> set = jedis.keys(key);
        jedis.close();
        return set;
    }

    public static Set<byte[]> keys(byte[] key) {
        Jedis jedis = jedisPool.getResource();
        Set<byte[]> set = jedis.keys(key);
        jedis.close();
        return set;
    }

    public static Set<String> getAllkeys() {
        Jedis jedis = jedisPool.getResource();
        Set<String> set = jedis.keys("*");
        jedis.close();
        return set;
    }

    /**
     * 命令用于移除给定 key 的过期时间，使得 key 永不过期。
     *
     * @return 当过期时间移除成功时，返回 1 。
     * 如果 key 不存在或 key 没有设置过期时间，返回 0 。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 15:43 2020/2/12
     * @Param
     **/
    public static Long persist(String key) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.persist(key);
        jedis.close();
        return index;
    }

    public static Long persist(byte[] key) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.persist(key);
        jedis.close();
        return index;
    }

    /**
     * 以毫秒为单位返回 key 的剩余过期时间。
     *
     * @return 当 key 不存在时，返回 -2 。
     * 当 key 存在但没有设置剩余生存时间时，返回 -1 。
     * 否则，以毫秒为单位，返回 key 的剩余生存时间。
     * 注意 当 key 不存在，或者 key 没有设置剩余生存时间时，命令都返回 -1 。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 15:45 2020/2/12
     * @Param
     **/
    public static Long pttl(String key) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.pttl(key);
        jedis.close();
        return index;
    }

    public static Long pttl(byte[] key) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.pttl(key);
        jedis.close();
        return index;
    }

    /**
     * 以秒为单位返回 key 的剩余过期时间。
     *
     * @return 当 key 不存在时，返回 -2 。
     * 当 key 存在但没有设置剩余生存时间时，返回 -1 。
     * 否则，以秒为单位，返回 key 的剩余生存时间。
     * 注意：在 Redis 2.8 以前，当 key 不存在，或者 key 没有设置剩余生存时间时，命令都返回 -1 。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 15:46 2020/2/12
     * @Param
     **/
    public static Long ttl(String key) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.ttl(key);
        jedis.close();
        return index;
    }

    public static Long ttl(byte[] key) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.ttl(key);
        jedis.close();
        return index;
    }

    /**
     * 返回 key 所储存的值的类型。
     *
     * @return 返回 key 的数据类型，
     * 数据类型有：none (key不存在) string (字符串) list (列表) set (集合) zset (有序集) hash (哈希表)
     * @Author chengpunan
     * @Description //TODO george
     * @Date 15:47 2020/2/12
     * @Param
     **/
    public static String type(String key) {
        Jedis jedis = jedisPool.getResource();
        String keyName = jedis.type(key);
        jedis.close();
        return keyName;
    }

    public static String type(byte[] key) {
        Jedis jedis = jedisPool.getResource();
        String keyName = jedis.type(key);
        jedis.close();
        return keyName;
    }
    /************************************************************* key 操作 结束 *************************************************************/
    /************************************************************* String 操作 开始 *************************************************************/
    /**
     * 向缓存中设置字符串内容
     *
     * @param key   key
     * @param value value
     * @return
     * @throws Exception
     */
    public static boolean set(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            if (jedis != null) {
                jedis.set(key, value);
            }
            return true;
        } catch (Exception e) {
            log.error("Redis缓存设置key值 出错！", e);
            return false;
        } finally {
            jedis.close();
        }
    }

    public static void set(String key, String value, SetParams setParams) {
        Jedis jedis = jedisPool.getResource();
        jedis.set(key, value, setParams);
        jedis.close();
    }

    public static void set(byte[] key, byte[] value) {
        Jedis jedis = jedisPool.getResource();
        jedis.set(key, value);
        jedis.close();
    }

    public static void set(byte[] key, byte[] value, SetParams setParams) {
        Jedis jedis = jedisPool.getResource();
        jedis.set(key, value, setParams);
        jedis.close();
    }

    /**
     * 获取指定 key 的值。如果 key 不存在，返回 nil 。如果key 储存的值不是字符串类型，返回一个错误。
     *
     * @return 返回 key 的值，如果 key 不存在时，返回 nil。 如果 key 不是字符串类型，那么返回一个错误。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 15:48 2020/2/12
     * @Param
     **/
    public static byte[] get(byte[] key) {
        Jedis jedis = jedisPool.getResource();
        byte[] value = jedis.get(key);
        jedis.close();
        return value;
    }

    /**
     * 获取存储在指定 key 中字符串的子字符串。
     * 字符串的截取范围由 start 和 end 两个偏移量决定(包括 start 和 end 在内)。
     *
     * @return 截取得到的子字符串。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 15:50 2020/2/12
     * @Param
     **/
    public static String getrange(String key, int start, int end) {
        Jedis jedis = jedisPool.getResource();
        String value = jedis.getrange(key, start, end);
        jedis.close();
        return value;
    }

    public static byte[] getrange(byte[] key, int start, int end) {
        Jedis jedis = jedisPool.getResource();
        byte[] value = jedis.getrange(key, start, end);
        jedis.close();
        return value;
    }

    /**
     * 用于设置指定 key 的值，并返回 key 的旧值。
     *
     * @return 返回给定 key 的旧值。 当 key 没有旧值时，即 key 不存在时，返回 nil 。
     * 当 key 存在但不是字符串类型时，返回一个错误。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 15:52 2020/2/12
     * @Param
     **/
    public static String getSet(String key, String value) {
        Jedis jedis = jedisPool.getResource();
        String oldValue = jedis.getSet(key, value);
        jedis.close();
        return oldValue;
    }

    public static byte[] getSet(byte[] key, byte[] value) {
        Jedis jedis = jedisPool.getResource();
        byte[] oldValue = jedis.getSet(key, value);
        jedis.close();
        return oldValue;
    }

    /**
     * 对 key 所储存的字符串值，获取指定偏移量上的位(bit)。
     *
     * @return 字符串值指定偏移量上的位(bit)。
     * 当偏移量 OFFSET 比字符串值的长度大，或者 key 不存在时，返回 0 。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 15:54 2020/2/12
     * @Param
     **/
    public static boolean getbit(String key, int OFFSET) {
        Jedis jedis = jedisPool.getResource();
        boolean index = jedis.getbit(key, OFFSET);
        jedis.close();
        return index;
    }

    public static boolean getbit(byte[] key, int OFFSET) {
        Jedis jedis = jedisPool.getResource();
        boolean index = jedis.getbit(key, OFFSET);
        jedis.close();
        return index;
    }

    /**
     * 返回所有(一个或多个)给定 key 的值。 如果给定的 key 里面，有某个 key 不存在，那么这个 key 返回特殊值 nil 。
     *
     * @return 一个包含所有给定 key 的值的列表。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 15:55 2020/2/12
     * @Param
     **/
    public static List<String> mget(String... key) {
        Jedis jedis = jedisPool.getResource();
        List<String> stringList = jedis.mget(key);
        jedis.close();
        return stringList;
    }

    public static List<byte[]> mget(byte[]... key) {
        Jedis jedis = jedisPool.getResource();
        List<byte[]> stringList = jedis.mget(key);
        jedis.close();
        return stringList;
    }

    /**
     * 用于对 key 所储存的字符串值，设置或清除指定偏移量上的位(bit)。
     *
     * @return 指定偏移量原来储存的位。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 15:57 2020/2/12
     * @Param
     **/
    public static boolean setbit(String key, Long OFFSET, String value) {
        Jedis jedis = jedisPool.getResource();
        boolean index = jedis.setbit(key, OFFSET, value);
        jedis.close();
        return index;
    }

    public static boolean setbit(byte[] key, Long OFFSET, byte[] value) {
        Jedis jedis = jedisPool.getResource();
        boolean index = jedis.setbit(key, OFFSET, value);
        jedis.close();
        return index;
    }

    public static boolean setbit(String key, Long OFFSET, boolean value) {
        Jedis jedis = jedisPool.getResource();
        boolean index = jedis.setbit(key, OFFSET, value);
        jedis.close();
        return index;
    }

    public static boolean setbit(byte[] key, Long OFFSET, boolean value) {
        Jedis jedis = jedisPool.getResource();
        boolean index = jedis.setbit(key, OFFSET, value);
        jedis.close();
        return index;
    }

    /**
     * 指定的 key 设置值及其过期时间。如果 key 已经存在， SETEX 命令将会替换旧的值。
     *
     * @return 设置成功时返回 OK 。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 15:59 2020/2/12
     * @Param
     **/
    public static String setex(String key, int TIMEOUT, String value) {
        Jedis jedis = jedisPool.getResource();
        String index = jedis.setex(key, TIMEOUT, value);
        jedis.close();
        return index;
    }

    public static String setex(byte[] key, int TIMEOUT, byte[] value) {
        Jedis jedis = jedisPool.getResource();
        String index = jedis.setex(key, TIMEOUT, value);
        jedis.close();
        return index;
    }

    /**
     * 在指定的 key 不存在时，为 key 设置指定的值。
     *
     * @return 设置成功，返回 1 。 设置失败，返回 0 。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 16:00 2020/2/12
     * @Param
     **/
    public static Long setnx(String key, String value) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.setnx(key, value);
        jedis.close();
        return index;
    }

    public static Long setnx(byte[] key, byte[] value) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.setnx(key, value);
        jedis.close();
        return index;
    }

    /**
     * 获取指定 key 所储存的字符串值的长度
     *
     * @return 字符串值的长度。 当 key 不存在时，返回 0。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 16:01 2020/2/12
     * @Param
     **/
    public static Long strlen(String key) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.strlen(key);
        jedis.close();
        return index;
    }

    public static Long strlen(byte[] key) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.strlen(key);
        jedis.close();
        return index;
    }

    /**
     * 将 key 中储存的数字加上指定的增量值。
     *
     * @return 加上指定的增量值之后， key 的值。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 16:03 2020/2/12
     * @Param
     **/
    public static boolean incrBy(String key, int value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.incrBy(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            jedis.close();
        }
    }

    public static Long incrBy(byte[] key, int amount) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.incrBy(key, amount);
        jedis.close();
        return index;
    }

    /**
     * decrby(key, integer)：名称为key的string减少integer
     */
    public static boolean decrBy(String key, int value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.decrBy(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            jedis.close();
        }
    }

    public static Long decrBy(byte[] key, int amount) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.decrBy(key, amount);
        jedis.close();
        return index;
    }

    /**
     * 用于为指定的 key 追加值。
     *
     * @return 追加指定值之后， key 中字符串的长度。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 16:04 2020/2/12
     * @Param
     **/
    public static Long append(String key, String value) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.append(key, value);
        jedis.close();
        return index;
    }

    public static Long append(byte[] key, byte[] value) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.append(key, value);
        jedis.close();
        return index;
    }
    /************************************************************* String 操作 结束 *************************************************************/
    /************************************************************* Hash 操作 开始 *************************************************************/
    /**
     * 用于删除哈希表 key 中的一个或多个指定字段，不存在的字段将被忽略。
     *
     * @return 被成功删除字段的数量，不包括被忽略的字段。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 16:06 2020/2/12
     * @Param
     **/
    public static boolean delMapKey(String key, String mapKey) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.hdel(key, mapKey);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            jedis.close();
        }
    }

    public static Long hdel(String key, String... fields) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.hdel(key, fields);
        jedis.close();
        return index;
    }

    public static Long hdel(byte[] key, byte[]... fields) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.hdel(key, fields);
        jedis.close();
        return index;
    }

    /**
     * 用于查看哈希表的指定字段是否存在。
     *
     * @return 如果哈希表含有给定字段，返回 true 。
     * 如果哈希表不含有给定字段，或 key 不存在，返回 false 。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 16:10 2020/2/12
     * @Param
     **/
    public static boolean hexists(String key, String mapKey) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.hexists(key, mapKey);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            //shardedJedisPool.jedis.close();
        }
    }

    public static boolean hexists(byte[] key, byte[] value) {
        Jedis jedis = jedisPool.getResource();
        boolean index = jedis.hexists(key, value);
        jedis.close();
        return index;
    }

    /**
     * 于返回哈希表中指定字段的值。
     *
     * @return 返回给定字段的值。如果给定的字段或 key 不存在时，返回 nil 。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 16:10 2020/2/12
     * @Param
     **/
    public static String hget(String key, String value) {
        Jedis jedis = null;
        String index=null;
        try{
            jedis = jedisPool.getResource();
            index = jedis.hget(key, value);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
        return index;
    }

    public static byte[] hget(byte[] key, byte[] value) {
        Jedis jedis = jedisPool.getResource();
        byte[] index = jedis.hget(key, value);
        jedis.close();
        return index;
    }

    /**
     * 返回哈希表中，所有的字段和值。
     *
     * @return 以列表形式返回哈希表的字段及字段值。 若 key 不存在，返回空列表。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 16:12 2020/2/12
     * @Param
     **/
    public static Map<String, String> hgetall(String key) {
        Jedis jedis = jedisPool.getResource();
        Map<String, String> index = jedis.hgetAll(key);
        jedis.close();
        return index;
    }

    public static Map<byte[], byte[]> hgetall(byte[] key) {
        Jedis jedis = jedisPool.getResource();
        Map<byte[], byte[]> index = jedis.hgetAll(key);
        jedis.close();
        return index;
    }

    /**
     * 为哈希表中的字段值加上指定增量值。 增量也可以为负数，相当于对指定字段进行减法操作。
     *
     * @return 执行 HINCRBY 命令之后，哈希表中字段的值。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 16:15 2020/2/12
     * @Param
     **/
    public static Long hincrby(String key, String field, int value) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.hincrBy(key, field, value);
        jedis.close();
        return index;
    }

    public Double hincrByFloat(String key, String field, double value) {
        Jedis jedis = jedisPool.getResource();
        Double index = jedis.hincrByFloat(key, field, value);
        jedis.close();
        return index;
    }

    /**
     * 用于获取哈希表中的所有域（field）。
     *
     * @return 包含哈希表中所有域（field）列表。
     * 当 key 不存在时，返回一个空列表。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 16:17 2020/2/12
     * @Param
     **/
    public static Set<String> hkeys(String key) {
        Jedis jedis = jedisPool.getResource();
        Set<String> index = jedis.hkeys(key);
        jedis.close();
        return index;
    }

    public static Set<byte[]> hkeys(byte[] key) {
        Jedis jedis = jedisPool.getResource();
        Set<byte[]> index = jedis.hkeys(key);
        jedis.close();
        return index;
    }

    /**
     * 获取哈希表中字段的数量。
     *
     * @return 哈希表中字段的数量。 当 key 不存在时，返回 0 。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 16:18 2020/2/12
     * @Param
     **/
    public static Long hlen(String key) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.hlen(key);
        jedis.close();
        return index;
    }

    /**
     * 命令用于返回哈希表中，一个或多个给定字段的值。
     *
     * @return 一个包含多个给定字段关联值的表，表值的排列顺序和指定字段的请求顺序一样。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 16:19 2020/2/12
     * @Param
     **/
    public static List<String> hmget(String key, String... fields) {
        Jedis jedis = jedisPool.getResource();
        List<String> index = jedis.hmget(key, fields);
        jedis.close();
        return index;
    }

    /**
     * 用于同时将多个 field-value (字段-值)对设置到哈希表中。
     * 此命令会覆盖哈希表中已存在的字段。
     * 如果哈希表不存在，会创建一个空哈希表，并执行 HMSET 操作。
     *
     * @return 如果命令执行成功，返回 OK 。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 16:20 2020/2/12
     * @Param
     **/
    public static String hmset(String key, Map<String, String> map) {
        Jedis jedis = jedisPool.getResource();
        String index = jedis.hmset(key, map);
        jedis.close();
        return index;
    }

    /**
     * 命令用于为哈希表中的字段赋值 。
     * 如果哈希表不存在，一个新的哈希表被创建并进行 HSET 操作。
     * 如果字段已经存在于哈希表中，旧值将被覆盖。
     *
     * @return 如果字段是哈希表中的一个新建字段，并且值设置成功，返回 1 。
     * 如果哈希表中域字段已经存在且旧值已被新值覆盖，返回 0 。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 16:22 2020/2/12
     * @Param
     **/
    public static Long hset(String key, Map<String, String> map) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.hset(key, map);
        jedis.close();
        return index;
    }

    public static Long hset(String key, String field, String value) {
        Jedis jedis = null;
        Long index=null;
        try{
            jedis = jedisPool.getResource();
            index = jedis.hset(key, field, value);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(jedis!=null){
                jedis.close();
            }
        }
        return index;
    }

    public static Long hset(byte[] key, byte[] field, byte[] value) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.hset(key, field, value);
        jedis.close();
        return index;
    }

    public static Long hset(byte[] key, Map<byte[], byte[]> field) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.hset(key, field);
        jedis.close();
        return index;
    }

    /**
     * 返回哈希表所有域(field)的值。
     *
     * @return 一个包含哈希表中所有域(field)值的列表。 当 key 不存在时，返回一个空表。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 16:26 2020/2/12
     * @Param
     **/
    public static List<String> hvals(String key) {
        Jedis jedis = jedisPool.getResource();
        List<String> index = jedis.hvals(key);
        jedis.close();
        return index;
    }
    /************************************************************* Hash 操作 结束 *************************************************************/
    /************************************************************* List 操作 开始 *************************************************************/
    /**
     * 移出并获取列表的第一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。
     *
     * @return 如果列表为空，返回一个 nil 。
     * 否则，返回一个含有两个元素的列表，第一个元素是被弹出元素所属的 key ，第二个元素是被弹出元素的值。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 16:48 2020/2/12
     * @Param
     **/
    public static List<String> blpop(String... key) {
        Jedis jedis = jedisPool.getResource();
        List<String> index = jedis.blpop(key);
        jedis.close();
        return index;
    }

    public static List<String> blpop(int timeout, String... key) {
        Jedis jedis = jedisPool.getResource();
        List<String> index = jedis.blpop(timeout, key);
        jedis.close();
        return index;
    }

    public static List<String> blpop(int timeout, String key) {
        Jedis jedis = jedisPool.getResource();
        List<String> index = jedis.blpop(timeout, key);
        jedis.close();
        return index;
    }

    /**
     * 移出并获取列表的第一个元素， 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。
     *
     * @return 如果列表为空，返回一个 nil 。
     * 否则，返回一个含有两个元素的列表，第一个元素是被弹出元素所属的 key ，第二个元素是被弹出元素的值。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 16:48 2020/2/12
     * @Param
     **/
    public static List<String> brpop(String... key) {
        Jedis jedis = jedisPool.getResource();
        List<String> index = jedis.brpop(key);
        jedis.close();
        return index;
    }

    public static List<String> brpop(int timeout, String... key) {
        Jedis jedis = jedisPool.getResource();
        List<String> index = jedis.brpop(timeout, key);
        jedis.close();
        return index;
    }

    public static List<String> brpop(int timeout, String key) {
        Jedis jedis = jedisPool.getResource();
        List<String> index = jedis.brpop(timeout, key);
        jedis.close();
        return index;
    }

    /**
     * 列表中取出最后一个元素，并插入到另外一个列表的头部；
     * 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。
     *
     * @return 假如在指定时间内没有任何元素被弹出，则返回一个 nil 和等待时长。
     * 反之，返回一个含有两个元素的列表，第一个元素是被弹出元素的值，第二个元素是等待时长。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 16:48 2020/2/12
     * @Param
     **/
    public static String brpoplpush(String source, String desc, int timeout) {
        Jedis jedis = jedisPool.getResource();
        String index = jedis.brpoplpush(source, desc, timeout);
        jedis.close();
        return index;
    }

    /**
     * 通过索引获取列表中的元素。
     * 你也可以使用负数下标，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。
     *
     * @return 列表中下标为指定索引值的元素。
     * 如果指定索引值不在列表的区间范围内，返回 nil 。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 16:58 2020/2/12
     * @Param
     **/
    public static String lindex(String key, Long indexs) {
        Jedis jedis = jedisPool.getResource();
        String index = jedis.lindex(key, indexs);
        jedis.close();
        return index;
    }

    /**
     * 用于在列表的元素前或者后插入元素。
     * 当指定元素不存在于列表中时，不执行任何操作。
     * 当列表不存在时，被视为空列表，不执行任何操作。
     * 如果 key 不是列表类型，返回一个错误。
     *
     * @return 如果命令执行成功，返回插入操作完成之后，列表的长度。
     * 如果没有找到指定元素 ，返回 -1 。
     * 如果 key 不存在或为空列表，返回 0 。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 17:01 2020/2/12
     * @Param
     **/
    public static Long linsert(String key, ListPosition where, String pivot, String value) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.linsert(key, where, pivot, value);
        jedis.close();
        return index;
    }

    /**
     * 用于返回列表的长度。 如果列表 key 不存在，则 key 被解释为一个空列表，返回 0 。
     * 如果 key 不是列表类型，返回一个错误。
     *
     * @return 列表的长度。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 17:03 2020/2/12
     * @Param
     **/
    public static Long llen(String key) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.llen(key);
        jedis.close();
        return index;
    }

    /**
     * 移出并获取列表的第一个元素
     *
     * @return 列表的第一个元素。 当列表 key 不存在时，返回 nil 。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 17:03 2020/2/12
     * @Param
     **/
    public static String lpop(String key) {
        Jedis jedis = jedisPool.getResource();
        String index = jedis.lpop(key);
        jedis.close();
        return index;
    }

    /**
     * 将一个或多个值插入到列表头部
     *
     * @return 执行 LPUSH 命令后，列表的长度。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 17:03 2020/2/12
     * @Param
     **/
    public static Long lpush(String key, String... value) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.lpush(key, value);
        jedis.close();
        return index;
    }

    /**
     * 将一个值插入到已存在的列表头部
     *
     * @return LPUSHX 命令执行之后，列表的长度。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 17:03 2020/2/12
     * @Param
     **/
    public static Long lpushx(String key, String... value) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.lpushx(key, value);
        jedis.close();
        return index;
    }

    public static List<String> getListString(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            if (jedis != null) {
                return jedis.lrange(key, 0, -1);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            jedis.close();
        }
    }

    /**
     * 根据参数 COUNT 的值，移除列表中与参数 VALUE 相等的元素。
     * count > 0 : 从表头开始向表尾搜索，移除与 VALUE 相等的元素，数量为 COUNT
     * count < 0 : 从表尾开始向表头搜索，移除与 VALUE 相等的元素，数量为 COUNT 的绝对值。
     * count = 0 : 移除表中所有与 VALUE 相等的值。
     *
     * @return 一个列表，包含指定区间内的元素。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 17:03 2020/2/12
     * @Param
     **/
    public static Long lrem(String key, long count, String value) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.lrem(key, count, value);
        jedis.close();
        return index;
    }

    /**
     * 通过索引来设置元素的值。
     * 当索引参数超出范围，或对一个空列表进行 LSET 时，返回一个错误。
     *
     * @return 操作成功返回 ok ，否则返回错误信息。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 17:22 2020/2/12
     * @Param
     **/
    public static String lset(String key, long count, String value) {
        Jedis jedis = jedisPool.getResource();
        String index = jedis.lset(key, count, value);
        jedis.close();
        return index;
    }

    /**
     * 对一个列表进行修剪(trim)，就是说，让列表只保留指定区间内的元素，不在指定区间之内的元素都将被删除。
     * 下标 0 表示列表的第一个元素，以 1 表示列表的第二个元素，以此类推。
     * 你也可以使用负数下标，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。
     *
     * @return
     * @Author chengpunan
     * @Description //TODO george
     * @Date 17:23 2020/2/12
     * @Param
     **/
    public static String ltrim(String key, long start, int end) {
        Jedis jedis = jedisPool.getResource();
        String index = jedis.ltrim(key, start, end);
        jedis.close();
        return index;
    }

    /**
     * 用于移除列表的最后一个元素，返回值为移除的元素。
     *
     * @return 被移除的元素。
     * 当列表不存在时，返回 nil 。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 17:24 2020/2/12
     * @Param
     **/
    public static String rpop(String key) {
        Jedis jedis = jedisPool.getResource();
        String index = jedis.rpop(key);
        jedis.close();
        return index;
    }

    /**
     * 用于移除列表的最后一个元素，并将该元素添加到另一个列表并返回。
     *
     * @return 被弹出的元素。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 17:25 2020/2/12
     * @Param
     **/
    public static String rpoplpush(String srcKey, String dstKey) {
        Jedis jedis = jedisPool.getResource();
        String index = jedis.rpoplpush(srcKey, dstKey);
        jedis.close();
        return index;
    }

    /**
     * 用于将一个或多个值插入到列表的尾部(最右边)。
     * 如果列表不存在，一个空列表会被创建并执行 RPUSH 操作。 当列表存在但不是列表类型时，返回一个错误。
     * 注意：在 Redis 2.4 版本以前的 RPUSH 命令，都只接受单个 value 值。
     *
     * @return 执行 RPUSH 操作后，列表的长度。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 17:27 2020/2/12
     * @Param
     **/
    public static Long rpush(String key, String... value) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.rpush(key, value);
        jedis.close();
        return index;
    }

    /**
     * 用于将一个值插入到已存在的列表尾部(最右边)。
     * 如果列表不存在，操作无效。
     *
     * @return 执行 Rpushx 操作后，列表的长度。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 17:28 2020/2/12
     * @Param
     **/
    public static Long rpushx(String key, String... value) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.rpushx(key, value);
        jedis.close();
        return index;
    }
    /************************************************************* List 操作 结束 *************************************************************/
    /************************************************************* Set 操作 开始 *************************************************************/
    /**
     * 将一个或多个成员元素加入到集合中，已经存在于集合的成员元素将被忽略。
     * 假如集合 key 不存在，则创建一个只包含添加的元素作成员的集合。
     * 当集合 key 不是集合类型时，返回一个错误。
     * 注意：在 Redis2.4 版本以前， SADD 只接受单个成员值。
     *
     * @return 被添加到集合中的新元素的数量，不包括被忽略的元素。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 17:29 2020/2/12
     * @Param
     **/
    public static boolean sadd(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.sadd(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            jedis.close();
        }
    }

    /**
     * 返回集合中元素的数量。
     *
     * @return 集合的数量。 当集合 key 不存在时，返回 0 。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 17:30 2020/2/12
     * @Param
     **/
    public static Long scard(String key) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.scard(key);
        jedis.close();
        return index;
    }

    /**
     * sdiff:返回所有给定key与第一个key的差集。（用法：sdiff set集合1 set集合2）
     *
     * @param key1
     * @param key2
     * @return
     */
    public static Set<String> sdiff(String key1, String key2) {
        Jedis jedis = null;
        Set<String> diffList = null;
        try {
            jedis = jedisPool.getResource();
            diffList = jedis.sdiff(key1, key2);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        return diffList;
    }

    /**
     * 返回给定集合之间的差集。不存在的集合 key 将视为空集。
     * 差集的结果来自前面的 FIRST_KEY ,而不是后面的 OTHER_KEY1，也不是整个 FIRST_KEY OTHER_KEY1..OTHER_KEYN 的差集。
     *
     * @return 包含差集成员的列表。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 17:31 2020/2/12
     * @Param
     **/
    public static Set<String> sdiff(String... key) {
        Jedis jedis = jedisPool.getResource();
        Set<String> index = jedis.sdiff(key);
        jedis.close();
        return index;
    }

    /**
     * 给定集合之间的差集存储在指定的集合中。
     * 如果指定的集合 key 已存在，则会被覆盖。
     *
     * @return 结果集中的元素数量。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 17:32 2020/2/12
     * @Param
     **/
    public static Long sdiffstore(String key, String... value) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.sdiffstore(key, value);
        jedis.close();
        return index;
    }

    /**
     * 返回给定所有给定集合的交集。
     * 不存在的集合 key 被视为空集。
     * 当给定集合当中有一个空集时，结果也为空集(根据集合运算定律)。
     *
     * @return 交集成员的列表。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 17:33 2020/2/12
     * @Param
     **/
    public static Set<String> sinter(String... key) {
        Jedis jedis = jedisPool.getResource();
        Set<String> index = jedis.sinter(key);
        jedis.close();
        return index;
    }

    /**
     * 将给定集合之间的交集存储在指定的集合中。
     * 如果指定的集合已经存在，则将其覆盖。
     *
     * @return 返回存储交集的集合的元素数量。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 17:35 2020/2/12
     * @Param
     **/
    public static Long sinterstore(String key, String... value) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.sinterstore(key, value);
        jedis.close();
        return index;
    }

    /**
     * 判断成员元素是否是集合的成员。
     *
     * @return 如果成员元素是集合的成员，返回 1 。
     * 如果成员元素不是集合的成员，或 key 不存在，返回 0 。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 17:36 2020/2/12
     * @Param
     **/
    public static boolean sismember(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.sismember(key, value);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            jedis.close();
        }
    }

    /**
     * smembers(key) ：返回名称为key的set的所有元素
     *
     * @param key
     * @return
     */
    public static Set<String> smembers(String key) {
        Jedis jedis = null;
        Set<String> list = null;
        try {
            jedis = jedisPool.getResource();
            list = jedis.smembers(key);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jedis.close();
        }
        return list;
    }

    /**
     * 将指定成员 member 元素从 source 集合移动到 destination 集合。
     * SMOVE 是原子性操作。
     * 如果 source 集合不存在或不包含指定的 member 元素，则 SMOVE 命令不执行任何操作，仅返回 0 。否则， member 元素从 source 集合中被移除，并添加到 destination 集合中去。
     * 当 destination 集合已经包含 member 元素时， SMOVE 命令只是简单地将 source 集合中的 member 元素删除。
     * 当 source 或 destination 不是集合类型时，返回一个错误。
     *
     * @return 如果成员元素被成功移除，返回 1 。 如果成员元素不是 source 集合的成员，并且没有任何操作对 destination 集合执行，那么返回 0 。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 17:38 2020/2/12
     * @Param
     **/
    public static Long smove(String srckey, String dstkey, String member) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.smove(srckey, dstkey, member);
        jedis.close();
        return index;
    }

    /**
     * 用于移除集合中的指定 key 的一个或多个随机元素，移除后会返回移除的元素。
     * 该命令类似 Srandmember 命令，但 SPOP 将随机元素从集合中移除并返回，而 Srandmember 则仅仅返回随机元素，而不对集合进行任何改动。
     *
     * @return 被移除的随机元素。 当集合不存在或是空集时，返回 nil 。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 17:40 2020/2/12
     * @Param
     **/
    public static String spop(String key) {
        Jedis jedis = jedisPool.getResource();
        String index = jedis.spop(key);
        jedis.close();
        return index;
    }

    public static Set<String> spop(String key, long count) {
        Jedis jedis = jedisPool.getResource();
        Set<String> index = jedis.spop(key, count);
        jedis.close();
        return index;
    }


    /**
     * 用于返回集合中的一个随机元素。
     *
     * @return 只提供集合 key 参数时，返回一个元素；如果集合为空，返回 nil 。 如果提供了 count 参数，那么返回一个数组；如果集合为空，返回空数组。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 17:42 2020/2/12
     * @Param
     **/
    public static String srandmember(String key) {
        Jedis jedis = jedisPool.getResource();
        String index = jedis.srandmember(key);
        jedis.close();
        return index;
    }

    public static List<String> srandmember(String key, int count) {
        Jedis jedis = jedisPool.getResource();
        List<String> index = jedis.srandmember(key, count);
        jedis.close();
        return index;
    }

    /**
     * 用于移除集合中的一个或多个成员元素，不存在的成员元素会被忽略。
     * 当 key 不是集合类型，返回一个错误。
     *
     * @return 被成功移除的元素的数量，不包括被忽略的元素。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 17:44 2020/2/12
     * @Param
     **/
    public static boolean srem(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.srem(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            jedis.close();
        }
    }

    /**
     * 返回给定集合的并集。不存在的集合 key 被视为空集。
     *
     * @return 并集成员的列表
     * @Author chengpunan
     * @Description //TODO george
     * @Date 17:47 2020/2/12
     * @Param
     **/
    public static Set<String> sunion(String... keys) {
        Jedis jedis = jedisPool.getResource();
        Set<String> index = jedis.sunion(keys);
        jedis.close();
        return index;
    }

    /**
     * 将给定集合的并集存储在指定的集合 destination 中。如果 destination 已经存在，则将其覆盖。
     *
     * @return 结果集中的元素数量。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 17:48 2020/2/12
     * @Param
     **/
    public static Long sunionstore(String dstkey, String... keys) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.sunionstore(dstkey, keys);
        jedis.close();
        return index;
    }

    /**
     * 用于迭代集合中键的元素。
     *
     * @return 数组列表。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 17:49 2020/2/12
     * @Param
     **/
    public static ScanResult<String> sscan(String key, String cursor) {
        Jedis jedis = jedisPool.getResource();
        ScanResult<String> index = jedis.sscan(key, cursor);
        jedis.close();
        return index;
    }

    public static ScanResult<String> sscan(String key, String cursor, ScanParams params) {
        Jedis jedis = jedisPool.getResource();
        ScanResult<String> index = jedis.sscan(key, cursor, params);
        jedis.close();
        return index;
    }

    /**
     * 将一个或多个成员元素及其分数值加入到有序集当中。
     * 如果某个成员已经是有序集的成员，那么更新这个成员的分数值，并通过重新插入这个成员元素，来保证该成员在正确的位置上。
     * 分数值可以是整数值或双精度浮点数。
     * 如果有序集合 key 不存在，则创建一个空的有序集并执行 ZADD 操作。
     * 当 key 存在但不是有序集类型时，返回一个错误。
     * 注意： 在 Redis 2.4 版本以前， ZADD 每次只能添加一个元素。
     *
     * @return 被成功添加的新成员的数量，不包括那些被更新的、已经存在的成员。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 18:22 2020/2/12
     * @Param
     **/
    public static boolean zadd(String key, double score, String member) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.zadd(key, score, member);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            jedis.close();
        }
    }

    public static Long zadd(String key, double score, String member, ZAddParams params) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.zadd(key, score, member, params);
        jedis.close();
        return index;
    }

    public static Long zadd(String key, Map<String, Double> scoreMembers) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.zadd(key, scoreMembers);
        jedis.close();
        return index;
    }

    public static Long zadd(String key, Map<String, Double> scoreMembers, ZAddParams params) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.zadd(key, scoreMembers, params);
        jedis.close();
        return index;
    }

    /**
     * 用于计算集合中元素的数量。
     *
     * @return 当 key 存在且是有序集类型时，返回有序集的基数。 当 key 不存在时，返回 0 。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 18:24 2020/2/12
     * @Param
     **/
    public static long zcard(String key) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.zcard(key);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            jedis.close();
        }
    }

    /**
     * 用于计算有序集合中指定分数区间的成员数量。
     *
     * @return 分数值在 min 和 max 之间的成员的数量。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 18:25 2020/2/12
     * @Param
     **/
    public static long zcount(String key, double min, double max) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.zcount(key, min, max);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        } finally {
            jedis.close();
        }
    }

    public static Long zcount(String key, String min, String max) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.zcount(key, min, max);
        jedis.close();
        return index;
    }

    /**
     * 对有序集合中指定成员的分数加上增量 increment
     * 可以通过传递一个负数值 increment ，让分数减去相应的值，比如 ZINCRBY key -5 member ，就是让 member 的 score 值减去 5 。
     * 当 key 不存在，或分数不是 key 的成员时， ZINCRBY key increment member 等同于 ZADD key increment member 。
     * 当 key 不是有序集类型时，返回一个错误。
     * 分数值可以是整数值或双精度浮点数。
     *
     * @return member 成员的新分数值，以字符串形式表示。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 18:26 2020/2/12
     * @Param
     **/
    public static Double zincrby(String key, double increment, String member) {
        Jedis jedis = jedisPool.getResource();
        Double index = jedis.zincrby(key, increment, member);
        jedis.close();
        return index;
    }

    public static Double zincrby(String key, double increment, String member, ZIncrByParams params) {
        Jedis jedis = jedisPool.getResource();
        Double index = jedis.zincrby(key, increment, member, params);
        jedis.close();
        return index;
    }

    /**
     * 计算给定的一个或多个有序集的交集，其中给定 key 的数量必须以 numkeys 参数指定，并将该交集(结果集)储存到 destination 。
     * 默认情况下，结果集中某个成员的分数值是所有给定集下该成员分数值之和。
     *
     * @return 保存到目标结果集的的成员数量。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 18:28 2020/2/12
     * @Param
     **/
    public static Long zinterstore(String dstkey, String... sets) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.zinterstore(dstkey, sets);
        jedis.close();
        return index;
    }

    public static Long zinterstore(String dstkey, ZParams params, String... sets) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.zinterstore(dstkey, params, sets);
        jedis.close();
        return index;
    }

    /**
     * 在计算有序集合中指定字典区间内成员数量。
     *
     * @return 指定区间内的成员数量。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 18:32 2020/2/12
     * @Param
     **/
    public static Long zlexcount(String key, String min, String max) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.zlexcount(key, min, max);
        jedis.close();
        return index;
    }

    /**
     * 返回有序集中，指定区间内的成员。
     * 其中成员的位置按分数值递增(从小到大)来排序。
     * 具有相同分数值的成员按字典序(lexicographical order )来排列。
     * 如果你需要成员按
     * 值递减(从大到小)来排列，请使用 ZREVRANGE 命令。
     * 下标参数 start 和 stop 都以 0 为底，也就是说，以 0 表示有序集第一个成员，以 1 表示有序集第二个成员，以此类推。
     * 你也可以使用负数下标，以 -1 表示最后一个成员， -2 表示倒数第二个成员，以此类推。
     *
     * @return 指定区间内，带有分数值(可选)的有序集成员的列表。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 18:33 2020/2/12
     * @Param
     **/
    public static Set<String> zrange(String key, long start, long stop) {
        Jedis jedis = jedisPool.getResource();
        Set<String> index = jedis.zrange(key, start, stop);
        jedis.close();
        return index;
    }

    /**
     * 字典区间返回有序集合的成员。
     *
     * @return 指定区间内的元素列表。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 18:34 2020/2/12
     * @Param
     **/
    public static Set<String> zrangeByLex(String key, String min, String max) {
        Jedis jedis = jedisPool.getResource();
        Set<String> index = jedis.zrangeByLex(key, min, max);
        jedis.close();
        return index;
    }

    public Set<String> zrangeByLex(String key, String min, String max, int offset, int count) {
        Jedis jedis = jedisPool.getResource();
        Set<String> index = jedis.zrangeByLex(key, min, max, offset, count);
        jedis.close();
        return index;
    }

    /**
     * 返回集合中score在给定区间的元素
     */
    public static Set<String> zrangeByScore(String key, double min, double max) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            return jedis.zrangeByScore(key, min, max);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            jedis.close();
        }
    }

    public static Set<String> zrangeByScore(String key, String min, String max) {
        Jedis jedis = jedisPool.getResource();
        Set<String> index = jedis.zrangeByScore(key, min, max);
        jedis.close();
        return index;
    }

    public static Set<String> zrangeByScore(String key, double min, double max, int offset, int count) {
        Jedis jedis = jedisPool.getResource();
        Set<String> index = jedis.zrangeByScore(key, min, max, offset, count);
        jedis.close();
        return index;
    }

    public static Set<String> zrangeByScore(String key, String min, String max, int offset, int count) {
        Jedis jedis = jedisPool.getResource();
        Set<String> index = jedis.zrangeByScore(key, min, max, offset, count);
        jedis.close();
        return index;
    }

    /**
     * 有序集中指定成员的排名。其中有序集成员按分数值递增(从小到大)顺序排列。
     *
     * @return 如果成员是有序集 key 的成员，返回 member 的排名。 如果成员不是有序集 key 的成员，返回 nil 。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 18:39 2020/2/12
     * @Param
     **/
    public static Long zrank(String key, String member) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.zrank(key, member);
        jedis.close();
        return index;
    }

    /**
     * 用于移除有序集中的一个或多个成员，不存在的成员将被忽略。
     * 当 key 存在但不是有序集类型时，返回一个错误。
     * 注意： 在 Redis 2.4 版本以前， ZREM 每次只能删除一个元素。
     *
     * @return 被成功移除的成员的数量，不包括被忽略的成员。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 18:40 2020/2/12
     * @Param
     **/
    public static boolean zrem(String key, String... members) {
        Jedis jedis = null;
        try {
            jedis = jedisPool.getResource();
            jedis.zrem(key, members);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            jedis.close();
        }
    }

    /**
     * 用于移除有序集合中给定的字典区间的所有成员。
     *
     * @return 被成功移除的成员的数量，不包括被忽略的成员。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 18:41 2020/2/12
     * @Param
     **/
    public static Long zremrangeByLex(String key, String min, String max) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.zremrangeByLex(key, min, max);
        jedis.close();
        return index;
    }

    /**
     * 用于移除有序集中，指定排名(rank)区间内的所有成员。
     *
     * @return 被移除成员的数量。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 18:42 2020/2/12
     * @Param
     **/
    public static Long zremrangeByRank(String key, long start, long stop) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.zremrangeByRank(key, start, stop);
        jedis.close();
        return index;
    }

    /**
     * 用于移除有序集中，指定分数（score）区间内的所有成员。
     *
     * @return 被移除成员的数量。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 18:43 2020/2/12
     * @Param
     **/
    public static Long zremrangeByScore(String key, double min, double max) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.zremrangeByScore(key, min, max);
        jedis.close();
        return index;
    }

    public static Long zremrangeByScore(String key, String min, String max) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.zremrangeByScore(key, min, max);
        jedis.close();
        return index;
    }

    /**
     * 返回有序集中，指定区间内的成员。
     * 其中成员的位置按分数值递减(从大到小)来排列。
     * 具有相同分数值的成员按字典序的逆序(reverse lexicographical order)排列。
     * 除了成员按分数值递减的次序排列这一点外， ZREVRANGE 命令的其他方面和 ZRANGE 命令一样。
     *
     * @return 指定区间内，带有分数值(可选)的有序集成员的列表。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 18:45 2020/2/12
     * @Param
     **/
    public static Set<String> zrevrange(String key, long start, long stop) {
        Jedis jedis = jedisPool.getResource();
        Set<String> index = jedis.zrevrange(key, start, stop);
        jedis.close();
        return index;
    }

    /**
     * 有序集中指定分数区间内的所有的成员。有序集成员按分数值递减(从大到小)的次序排列。
     * 具有相同分数值的成员按字典序的逆序(reverse lexicographical order )排列。
     * 除了成员按分数值递减的次序排列这一点外， ZREVRANGEBYSCORE 命令的其他方面和 ZRANGEBYSCORE 命令一样。
     *
     * @return 指定区间内，带有分数值(可选)的有序集成员的列表。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 18:47 2020/2/12
     * @Param
     **/
    public static Set<String> zrevrangeByScore(String key, double max, double min) {
        Jedis jedis = jedisPool.getResource();
        Set<String> index = jedis.zrevrangeByScore(key, max, min);
        jedis.close();
        return index;
    }

    public static Set<String> zrevrangeByScore(String key, String max, String min) {
        Jedis jedis = jedisPool.getResource();
        Set<String> index = jedis.zrevrangeByScore(key, max, min);
        jedis.close();
        return index;
    }

    public static Set<String> zrevrangeByScore(String key, double max, double min, int offset, int count) {
        Jedis jedis = jedisPool.getResource();
        Set<String> index = jedis.zrevrangeByScore(key, max, min, offset, count);
        jedis.close();
        return index;
    }

    /**
     * 返回有序集中成员的排名。其中有序集成员按分数值递减(从大到小)排序。
     * 排名以 0 为底，也就是说， 分数值最大的成员排名为 0 。
     * 使用 ZRANK 命令可以获得成员按分数值递增(从小到大)排列的排名。
     *
     * @return 如果成员是有序集 key 的成员，返回成员的排名。 如果成员不是有序集 key 的成员，返回 nil 。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 18:51 2020/2/12
     * @Param
     **/
    public static Long zrevrank(String key, String member) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.zrevrank(key, member);
        jedis.close();
        return index;
    }


    /**
     * 计算给定的一个或多个有序集的并集，其中给定 key 的数量必须以 numkeys 参数指定，并将该并集(结果集)储存到 destination 。
     * 默认情况下，结果集中某个成员的分数值是所有给定集下该成员分数值之和 。
     *
     * @return 保存到 destination 的结果集的成员数量。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 18:54 2020/2/12
     * @Param
     **/
    public static Long zunionstore(String dstkey, String... sets) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.zunionstore(dstkey, sets);
        jedis.close();
        return index;
    }

    public static Long zunionstore(String dstkey, ZParams params, String... sets) {
        Jedis jedis = jedisPool.getResource();
        Long index = jedis.zunionstore(dstkey, params, sets);
        jedis.close();
        return index;
    }

    /**
     * 用于迭代有序集合中的元素（包括元素成员和元素分值）
     *
     * @return 返回的每个元素都是一个有序集合元素，一个有序集合元素由一个成员（member）和一个分值（score）组成。
     * @Author chengpunan
     * @Description //TODO george
     * @Date 18:55 2020/2/12
     * @Param
     **/
    public static ScanResult<Tuple> zscan(String key, String cursor) {
        Jedis jedis = jedisPool.getResource();
        ScanResult<Tuple> index = jedis.zscan(key, cursor);
        jedis.close();
        return index;
    }

    public static ScanResult<Tuple> zscan(String key, String cursor, ScanParams params) {
        Jedis jedis = jedisPool.getResource();
        ScanResult<Tuple> index = jedis.zscan(key, cursor, params);
        jedis.close();
        return index;
    }
    /************************************************************* Set 操作 结束 *************************************************************/
}