package com.student.book_advisor.security.redis;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Date;

@Component
public class RedisUtil {

    private final JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");

    public void set(String key, String value, Date expirationDate) {
        Jedis jedis = null;
        try {
            Long timeout = (expirationDate.getTime() - new Date().getTime())/1000;
            jedis = pool.getResource();
            jedis.set(key, value);
            jedis.expireAt(key, timeout);
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            if(jedis != null) {
                jedis.close();
            }
        }
    }

    public boolean isMember(String key, String value) {
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            String cachedToken = jedis.get(key);
            if(cachedToken != null) {
                return cachedToken.equals(value);
            }
            else return false;
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            if(jedis != null) {
                jedis.close();
            }
        }
    }
}
