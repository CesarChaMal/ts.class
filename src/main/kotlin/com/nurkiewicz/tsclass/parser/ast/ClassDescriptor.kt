package com.nurkiewicz.tsclass.parser.ast

data class ClassDescriptor(
    val name: String,
    val fields: List<Field>,
    val methods: List<Method>) {

    fun findBestMatchingMethod(call: MethodCall): Method? =
            methods.find { it.matches(call) }

}

data class Field(val name: String, val type: Type)
