package com.tencent.devops.enums

enum class AssemblyMode {
    NONE,
    CONSUL,
    K8S,
    KUBERNETES,
    ;

    companion object {
        fun ofValueOrDefault(value: String): AssemblyMode {
            val upperCase = value.toUpperCase()
            return values().find { it.name == upperCase } ?: CONSUL
        }
    }
}
