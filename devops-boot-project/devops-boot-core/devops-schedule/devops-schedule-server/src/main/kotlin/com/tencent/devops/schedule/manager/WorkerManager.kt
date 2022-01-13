package com.tencent.devops.schedule.manager

import com.tencent.devops.schedule.enums.DiscoveryTypeEnum
import com.tencent.devops.schedule.pojo.page.Page
import com.tencent.devops.schedule.pojo.trigger.HeartBeatParam
import com.tencent.devops.schedule.pojo.worker.WorkerGroup
import com.tencent.devops.schedule.pojo.worker.WorkerGroupCreateRequest
import com.tencent.devops.schedule.pojo.worker.WorkerGroupName
import com.tencent.devops.schedule.pojo.worker.WorkerGroupQueryParam
import com.tencent.devops.schedule.pojo.worker.WorkerInfo
import java.time.LocalDateTime

/**
 * 执行器管理器接口
 */
interface WorkerManager {
    /**
     * 列表查询工作组
     * @param param 查询参数
     */
    fun listGroupPage(param: WorkerGroupQueryParam): Page<WorkerGroup>

    /**
     * 查询工作组名称
     */
    fun listGroupName(): List<WorkerGroupName>

    /**
     * 根据id查找工作组
     * @param id 工作组id
     */
    fun findGroupById(id: String): WorkerGroup?

    /**
     * 创建工作组
     * @param request 创建请求
     * @return 主键id
     */
    fun createGroup(request: WorkerGroupCreateRequest): String

    /**
     * 根据discovery type查找工作组
     * @param type 地址发现类型
     */
    fun listGroupByDiscoverType(type: DiscoveryTypeEnum): List<WorkerGroup>

    /**
     * 移除dead worker
     * @param timeout 超时时间
     * @param from 起始时间
     */
    fun deleteDeadWorker(timeout: Int, from: LocalDateTime)

    /**
     * 列表查询 worker
     * @param timeout 超时时间
     * @param from 起始时间
     */
    fun listWorkerByUpdateTime(timeout: Int, from: LocalDateTime): List<WorkerInfo>

    /**
     * 更新group信息
     * @param group group信息
     */
    fun updateGroup(group: WorkerGroup)

    /**
     * 处理worker心跳
     */
    fun workerHeartbeat(param: HeartBeatParam)

    /**
     * 删除worker group
     * @param id group id
     */
    fun deleteWorkerGroup(id: String)
}
