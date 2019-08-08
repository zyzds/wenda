package cn.zyz.wenda.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class JedisAdapter implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);

    private JedisPool jedisPool;

    public Long sAdd(String key, String value) {
        //automatic resource management
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.sadd(key, value);
        } catch (Exception e) {
            logger.error("sAdd发生异常：" + e.getMessage());
        }
        return 0L;
    }

    public Long sCard(String key) {
        //automatic resource management
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.scard(key);
        } catch (Exception e) {
            logger.error("sCard发生异常：" + e.getMessage());
        }
        return 0L;
    }

    public Long sRem(String key, String value) {
        //automatic resource management
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.srem(key, value);
        } catch (Exception e) {
            logger.error("sRem发生异常：" + e.getMessage());
        }
        return 0L;
    }

    public boolean sIsMember(String key, String value) {
        //automatic resource management
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.sismember(key, value);
        } catch (Exception e) {
            logger.error("sIsMember发生异常：" + e.getMessage());
        }
        return false;
    }

    public Long lPush(String key, String value) {
        //automatic resource management
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lpush(key, value);
        } catch (Exception e) {
            logger.error("lPush发生异常：" + e.getMessage());
        }
        return 0L;
    }

    public List<String> lRange(String key, int start, int end) {
        //automatic resource management
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lrange(key, start, end);
        } catch (Exception e) {
            logger.error("lRange发生异常：" + e.getMessage());
        }
        return null;
    }

    public Long lRem(String key, int count, String value) {
        //automatic resource management
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.lrem(key, count, value);
        } catch (Exception e) {
            logger.error("lRem发生异常：" + e.getMessage());
        }
        return 0L;
    }

    public List<String> bRPop(int timeout, String key) {
        //automatic resource management
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.brpop(timeout, key);
        } catch (Exception e) {
            logger.error("bRPop发生异常：" + e.getMessage());
        }
        return new ArrayList<>();
    }

    public Transaction multi(Jedis jedis) {
        if (jedis != null) {
            try {
                return jedis.multi();
            } catch (Exception e) {
                logger.error("开启事务异常：" + e.getMessage());
            }
        }
        return null;
    }

    public List<Object> exec(Transaction tx) {
        if (tx != null) {
            try {
                return tx.exec();
            } catch (Exception e) {
                logger.error("事务执行异常：" + e.getMessage());
            } finally {
                tx.close();
            }
        }
        return new ArrayList<>();
    }

    public Double zScore(String key, String member) {
        //automatic resource management

        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zscore(key, member);
        } catch (Exception e) {
            logger.error("zScore发生异常：" + e.getMessage());
        }
        return null;
    }

    public Set<String> zRange(String key, int start, int end) {
        //automatic resource management
        try (Jedis jedis = jedisPool.getResource()) {
            if (jedis.exists(key)) {
                return jedis.zrange(key, start, end);
            }
        } catch (Exception e) {
            logger.error("zRange发生异常：" + e.getMessage());
        }
        return new HashSet<>();
    }

    public Long zCard(String key) {
        //automatic resource management
        try (Jedis jedis = jedisPool.getResource()) {
            return jedis.zcard(key);
        } catch (Exception e) {
            logger.error("zCard发生异常：" + e.getMessage());
        }
        return 0L;
    }

    public Jedis getJedis() {
        return jedisPool.getResource();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        jedisPool = new JedisPool("redis://localhost:6379/10");
    }
}
