package com.tencent.devops.schedule.provider

/**
 * Worker Provider
 */
interface LockProvider {

    /**
     * 尝试获取锁
     * @param key 锁名称
     * @param expiration 过期时间
     */
    fun acquire(key: String, expiration: Long): String?

    /**
     * 释放锁
     * @param key 锁名称
     * @param token 锁token
     */
    fun release(key: String, token: String): Boolean

    /**
     * 刷新锁时间
     * @param key 锁名称
     * @param token 锁token
     * @param expiration 过期时间
     */
    fun refresh(key: String, token: String, expiration: Long): Boolean
}
