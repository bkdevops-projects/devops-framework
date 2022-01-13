package com.tencent.devops.schedule.provider

import com.tencent.devops.schedule.pojo.page.Page
import com.tencent.devops.schedule.pojo.worker.WorkerGroup
import com.tencent.devops.schedule.pojo.worker.WorkerGroupName
import com.tencent.devops.schedule.pojo.worker.WorkerGroupQueryParam
import com.tencent.devops.schedule.pojo.worker.WorkerInfo
import java.time.LocalDateTime

/**
 * Worker Provider
 */
interface WorkerProvider {
    /**
     * 根据名称查找worker group
     * @param name 组名
     * @return 执行器组
     */
    fun findGroupByName(name: String): WorkerGroup?

    /**
     * 根据名称查找worker group
     * @param id 工作组id
     * @return 工作组
     */
    fun findGroupById(id: String): WorkerGroup?

    /**
     * 创建工作组
     * @param workerGroup 工作组信息
     * @return 主键id
     */
    fun addGroup(workerGroup: WorkerGroup): String

    /**
     * 分页查询工作组
     * @param param 查询参数
     */
    fun listGroupPage(param: WorkerGroupQueryParam): Page<WorkerGroup>

    /**
     * 查询工作组名称
     */
    fun listGroupName(): List<WorkerGroupName>

    /**
     * 根据discovery type查找工作组
     * @param type 地址发现类型
     */
    fun listGroupByDiscoveryType(type: Int): List<WorkerGroup>

    /**
     * 删除长时间未更新的worker
     * @param time 时间点
     * @return 删除数量
     */
    fun deleteWorkerByUpdateTimeLessThan(time: LocalDateTime): Long

    /**
     * 列表查询dead worker
     * @param time 时间点
     */
    fun listWorkerByUpdateTimeGreaterThan(time: LocalDateTime): List<WorkerInfo>

    /**
     * 更新group信息
     * @param group group信息
     */
    fun updateGroup(group: WorkerGroup)

    /**
     * 更新worker信息，worker不存在则插入
     * @param group group名称
     * @param address address名称
     */
    fun upsertWorker(group: String, address: String)

    /**
     * 删除worker信息
     * @param group group名称
     * @param address address名称
     */
    fun deleteWorker(group: String, address: String)

    /**
     * 删除worker group
     * @param id group id
     */
    fun deleteWorkerGroup(id: String)
}
