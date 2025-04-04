package club._8b1t.service.impl;

import club._8b1t.service.RedisLockService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Redis分布式锁服务实现类
 */
@Service
@Slf4j
public class RedisLockServiceImpl implements RedisLockService {
    
    @Resource
    private RedissonClient redissonClient;
    
    @Override
    public boolean tryLock(String key, int timeoutSeconds) {
        try {
            RLock lock = redissonClient.getLock(wrapLockKey(key));
            return lock.tryLock(0, timeoutSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("获取分布式锁异常，key: {}", key, e);
            return false;
        }
    }
    
    @Override
    public boolean unlock(String key) {
        try {
            RLock lock = redissonClient.getLock(wrapLockKey(key));
            // 仅当锁被当前线程持有时才解锁
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("释放分布式锁异常，key: {}", key, e);
            return false;
        }
    }
    
    @Override
    public boolean tryLockAndRun(String key, int timeoutSeconds, int waitTimeSeconds, Runnable runnable) {
        RLock lock = redissonClient.getLock(wrapLockKey(key));
        boolean locked = false;
        try {
            locked = lock.tryLock(waitTimeSeconds, timeoutSeconds, TimeUnit.SECONDS);
            if (locked) {
                runnable.run();
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("执行加锁操作异常，key: {}", key, e);
            return false;
        } finally {
            if (locked && lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }
    
    /**
     * 包装锁的键，添加统一前缀
     */
    private String wrapLockKey(String key) {
        return "lock:" + key;
    }
} 