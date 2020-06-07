package com.niuke.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Service
public class JedisAdapter implements InitializingBean {
    public static final Logger logger = LoggerFactory.getLogger(JedisAdapter.class);
    private JedisPool pool;
    @Override
    public void afterPropertiesSet() throws Exception {
        pool = new JedisPool("redis://localhost:6379/2");
    }
    //使用redis的set数据结构，因为like/dislike数据构成非有序集合
    public long sadd(String key,String value){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sadd(key,value);
        }
        catch (Exception e){
            logger.error("发生异常"+e.getMessage());
        }
        finally {
            if (jedis != null){
                jedis.close();
            }
        }
        return 0;
    }
    //删除集合内的数据
    public long srem(String key,String value){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.srem(key,value);
        }
        catch (Exception e){
            logger.error("发生异常"+e.getMessage());
        }
        finally {
            if (jedis != null){
                jedis.close();
            }
        }
        return 0;
    }
    //计算集合内key的数量
    public long scard(String key){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.scard(key);
        }
        catch (Exception e){
            logger.error("发生异常"+e.getMessage());
        }
        finally {
            if (jedis != null){
                jedis.close();
            }
        }
        return 0;
    }
    //判断key，value是否在集合内
    public boolean sismember(String key,String value){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.sismember(key,value);
        }
        catch (Exception e){
            logger.error("发生异常"+e.getMessage());
        }
        finally {
            if (jedis != null){
                jedis.close();
            }
        }
        return false;
    }

    public long lpush(String key,String value){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lpush(key,value);
        }
        catch (Exception e){
            logger.error("发生异常"+e.getMessage());
        }
        finally {
            if (jedis != null){
                jedis.close();
            }
        }
        return 0;
    }
    public List<String> brpop(int timeout,String key){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.brpop(timeout,key);
        }
        catch (Exception e){
            logger.error("发生异常"+e.getMessage());
        }
        finally {
            if (jedis!=null){
                jedis.close();
            }
        }
        return null;
    }
    public Jedis getJedis(){
        return pool.getResource();
    }
    //开启事务
    public Transaction multi(Jedis jedis){
        try {
            return jedis.multi();
        }
        catch (Exception e){
            logger.error("发生异常"+e.getMessage());
        }
        finally {

        }
        return null;
    }
    public List<Object> exec(Transaction tx,Jedis jedis){
        try {
            //相当于事务的提交
            return tx.exec();
        }
        catch (Exception e){
            logger.error("发生异常"+e.getMessage());
        }
        finally {
            if (tx!=null){
                try {
                    tx.close();
                }
                catch (IOException ioe){

                }
            }
            if (jedis!=null){
                jedis.close();
            }
        }
        return null;
    }
    //将sorted_set集合内的元素取出来
    public Set<String> zrevrange(String key,int start,int end){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zrevrange(key,start,end);
        }
        catch (Exception e){
            logger.error("发生异常"+e.getMessage());
        }
        finally {
            if (jedis!=null){
                jedis.close();
            }
        }
        return null;
    }
    public long zcard(String key){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zcard(key);
        }
        catch (Exception e){
            logger.error("发生异常"+e.getMessage());
        }
        finally {
            if (jedis !=null){
                jedis.close();
            }
        }
        return 0;
    }
    public Double zscore(String key, String member){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.zscore(key,member);
        }
        catch (Exception e){
            logger.error("发生异常"+e.getMessage());
        }
        finally {
            if (jedis !=null){
                jedis.close();
            }
        }
        return null;
    }

    public List<String> lrang(String key,int start,int end){
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            return jedis.lrange(key,start,end);
        }catch (Exception e){
            logger.error("发生异常"+e.getMessage());
        }finally {
            if (jedis!=null){
                jedis.close();
            }
        }
        return null;
    }


}
