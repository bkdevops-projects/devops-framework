package com.tencent.devops.schedule.utils

import com.tencent.devops.schedule.pojo.page.BasePageRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable

fun <T> Page<T>.toPage(): com.tencent.devops.schedule.pojo.page.Page<T> {
    return com.tencent.devops.schedule.pojo.page.Page(
        pageNumber = number,
        pageSize = size,
        totalRecords = totalElements,
        totalPages = totalPages.toLong(),
        records = content
    )
}

fun BasePageRequest.ofPageable(): Pageable {
    val number = if (pageNumber <= 0) 1 else pageNumber
    val size = if (pageSize <= 0) 20 else pageSize
    return PageRequest.of(number - 1, size)
}

