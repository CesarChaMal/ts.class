package com.nurkiewicz.tsclass.parser.visitors

import com.google.common.collect.Lists
import com.nurkiewicz.tsclass.antlr.parser.TypeScriptBaseVisitor
import com.nurkiewicz.tsclass.antlr.parser.TypeScriptParser
import com.nurkiewicz.tsclass.parser.ast.Method
import com.nurkiewicz.tsclass.parser.ast.Parameter
import com.nurkiewicz.tsclass.parser.ast.Statement
import com.nurkiewicz.tsclass.parser.ast.Type
import java.util.*

internal class MemberFunctionDeclarationVisitor : TypeScriptBaseVisitor<Method>() {

    override fun visitMemberFunctionDeclaration(ctx: TypeScriptParser.MemberFunctionDeclarationContext): Method {
        val sig = ctx.memberFunctionImplementation().functionSignature()
        val methodName = sig.IDENT().getText()
        val typeCtx = sig.returnTypeAnnotation().returnType().type()
        return Method(methodName, typeOf(typeCtx), parameters(sig), parseBody(ctx))
    }

    private fun parameters(sig: TypeScriptParser.FunctionSignatureContext): List<Parameter> {
        val paramsCtx = sig.parameterList()
        return if (paramsCtx != null) {
            paramsCtx.accept(ParameterListVisitor())
        } else {
            emptyList()
        }
    }

    private fun typeOf(typeCtx: TypeScriptParser.TypeContext?): Type {
        return Type(if (typeCtx != null) typeCtx.typeName().getText() else "void")
    }

    private fun parseBody(ctx: TypeScriptParser.MemberFunctionDeclarationContext): List<Statement> {
        return ctx
                .memberFunctionImplementation()
                .functionBody()
                .sourceElement()
                .map({ se -> se.accept(SourceElementVisitor()) })
                .filter { it != null }
    }

    private class RequiredParameterListVisitor : TypeScriptBaseVisitor<List<Parameter>>() {

        override fun visitRequiredParameter(ctx: TypeScriptParser.RequiredParameterContext): List<Parameter> {
            val name = ctx.IDENT().getText()
            val typeName = Type(ctx.typeAnnotation().type().typeName().moduleOrTypeName().IDENT().getText())
            val parameter = Parameter(name, typeName)
            return Lists.newArrayList(parameter)
        }

        override fun defaultResult(): List<Parameter> {
            return ArrayList()
        }

        override fun aggregateResult(aggregate: List<Parameter>, nextResult: List<Parameter>?): List<Parameter> {
            return aggregate + (nextResult ?: emptyList())
        }
    }

    private class ParameterListVisitor : TypeScriptBaseVisitor<List<Parameter>>() {

        override fun visitRequiredParameterList(ctx: TypeScriptParser.RequiredParameterListContext): List<Parameter> {
            return ctx.accept(RequiredParameterListVisitor())
        }

    }
}