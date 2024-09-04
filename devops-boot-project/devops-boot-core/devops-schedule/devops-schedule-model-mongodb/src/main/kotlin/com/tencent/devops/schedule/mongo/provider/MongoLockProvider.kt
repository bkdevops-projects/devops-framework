package com.tencent.devops.schedule.mongo.provider

import com.mongodb.client.result.UpdateResult
import com.tencent.devops.schedule.mongo.model.TLockInfo
import com.tencent.devops.schedule.provider.LockProvider
import org.slf4j.LoggerFactory
import org.springframework.dao.DuplicateKeyException
import org.springframework.data.mongodb.core.FindAndModifyOptions
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.data.mongodb.core.query.and
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.data.mongodb.core.query.where
import java.util.UUID

/**
 * 基于mongodb实现的lock
 */
class MongoLockProvider(
    private val mongoTemplate: MongoTemplate,
) : LockProvider {
    override fun acquire(key: String, expiration: Long): String? {
        val query = Query.query(where(TLockInfo::id).isEqualTo(key))
        val token = generateToken()
        val update = Update()
            .setOnInsert(TLockInfo::id.name, key)
            .setOnInsert(TLockInfo::expireAt.name, System.currentTimeMillis() + expiration)
            .setOnInsert(TLockInfo::token.name, token)

        val options = FindAndModifyOptions().upsert(true)
            .returnNew(true)
        try {
            val lock = mongoTemplate.findAndModify(
                query,
                update,
                options,
                TLockInfo::class.java,
            )!!
            val locked = lock.token == token

            // 如果已过期
            if (!locked && lock.expireAt < System.currentTimeMillis()) {
                val deleted = mongoTemplate.remove(
                    Query.query(
                        where(TLockInfo::id).isEqualTo(key)
                            .and(TLockInfo::token).isEqualTo(lock.token)
                            .and(TLockInfo::expireAt).`is`(lock.expireAt),
                    ),
                    TLockInfo::class.java,
                )
                if (deleted.deletedCount >= 1) {
                    // 成功释放锁， 再次尝试获取锁
                    return acquire(key, expiration)
                }
            }
            return if (locked) {
                logger.trace("Acquired lock for key {} with token {}", key, token)
                return token
            } else {
                null
            }
        } catch (ignore: DuplicateKeyException) {
        }
        return null
    }

    override fun release(key: String, token: String): Boolean {
        val query = Query.query(
            where(TLockInfo::id).isEqualTo(key)
                .and(TLockInfo::token).isEqualTo(token),
        )
        val deleted = mongoTemplate.remove(query, TLockInfo::class.java)
        val released = deleted.deletedCount == 1L
        if (released) {
            logger.trace("Remove query successfully affected 1 record for key {} with token {}", key, token)
        } else if (deleted.deletedCount > 0) {
            logger.error("Unexpected result from release for key {} with token {}, released {}", key, token, deleted)
        } else {
            logger.error("Remove query did not affect any records for key {} with token {}", key, token)
        }

        return released
    }

    override fun refresh(key: String, token: String, expiration: Long): Boolean {
        val query = Query.query(
            where(TLockInfo::id).isEqualTo(key)
                .and(TLockInfo::token).isEqualTo(token),
        )
        val update = Update.update(TLockInfo::expireAt.name, System.currentTimeMillis() + expiration)
        val updated: UpdateResult = mongoTemplate.updateFirst(query, update, TLockInfo::class.java)
        val refreshed = updated.modifiedCount == 1L
        if (refreshed) {
            logger.trace("Refresh query successfully affected 1 record for key {} with token {}", key, token)
        } else if (updated.modifiedCount > 0) {
            logger.error("Unexpected result from refresh for key {} with token {}, released {}", key, token, updated)
        } else {
            logger.warn(
                "Refresh query did not affect any records for key {} with token {}. " +
                    "This is possible when refresh interval fires for the final time " +
                    "after the lock has been released",
                key,
                token,
            )
        }

        return refreshed
    }

    companion object {
        private val logger = LoggerFactory.getLogger(MongoLockProvider::class.java)

        private fun generateToken(): String {
            return UUID.randomUUID().toString().replace("-", "")
        }
    }
}
