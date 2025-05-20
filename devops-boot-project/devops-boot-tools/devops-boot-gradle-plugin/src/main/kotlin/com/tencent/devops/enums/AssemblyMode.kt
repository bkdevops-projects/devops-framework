package com.tencent.devops.enums

import java.util.Locale

enum class AssemblyMode {
    NONE,
    CONSUL,
    K8S,
    KUBERNETES,
    ;

    companion object {
        fun ofValueOrDefault(value: String): AssemblyMode {
            val upperCase = value.uppercase(Locale.getDefault())
            return entries.find { it.name == upperCase } ?: CONSUL
        }
    }
}
