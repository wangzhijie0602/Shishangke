package club._8b1t.service;

/**
 * Redis分布式锁服务接口
 */
public interface RedisLockService {
    
    /**
     * 尝试获取锁
     *
     * @param key 锁的键
     * @param timeoutSeconds 锁的超时时间（秒）
     * @return 是否成功获取锁
     */
    boolean tryLock(String key, int timeoutSeconds);
    
    /**
     * 释放锁
     *
     * @param key 锁的键
     * @return 是否成功释放锁
     */
    boolean unlock(String key);
    
    /**
     * 尝试获取锁并执行操作
     *
     * @param key 锁的键
     * @param timeoutSeconds 锁的超时时间（秒）
     * @param waitTimeSeconds 等待获取锁的时间（秒）
     * @param runnable 要执行的操作
     * @return 是否成功获取锁并执行操作
     */
    boolean tryLockAndRun(String key, int timeoutSeconds, int waitTimeSeconds, Runnable runnable);
} 