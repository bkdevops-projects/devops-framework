package com.tencent.devops.schedule.pojo.page

import kotlin.math.ceil

data class Page<out T>(
    val pageNumber: Int,
    val pageSize: Int,
    val totalRecords: Long,
    val totalPages: Long,
    val records: List<T>
) {
    constructor(pageNumber: Int, pageSize: Int, totalRecords: Long, records: List<T>) : this(
        pageNumber = pageNumber,
        pageSize = pageSize,
        totalRecords = totalRecords,
        totalPages = ceil(totalRecords * 1.0 / pageSize).toLong(),
        records = records
    )
}
