package com.wyk.student.util;

import com.wyk.exception.CustomizeException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Optional;

@Slf4j
@Component
public class SpELUtil {

    private static final SpelExpressionParser parser = new SpelExpressionParser();

    public String parseSpEL(String value,String defaultVal, ProceedingJoinPoint joinPoint) {
        if (!StringUtils.hasText(value)) {
            throw new CustomizeException("SpEL表达式为空", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        EvaluationContext context = getContext(joinPoint,defaultVal);
        try {
            return parser.parseExpression(value).getValue(context, String.class);
        } catch (Exception e) {
            throw new CustomizeException("SpEL表达式解析失败", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    public Object parseBloomSpEL(String value,String defaultVal, ProceedingJoinPoint joinPoint) {
        if (!StringUtils.hasText(value)) {
            throw new CustomizeException("SpEL表达式为空", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        EvaluationContext context = getContext(joinPoint,defaultVal);
        try {
            return parser.parseExpression(value).getValue(context);
        } catch (Exception e) {
            throw new CustomizeException("SpEL表达式解析失败", HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
    private EvaluationContext getContext(ProceedingJoinPoint joinPoint, String defaultVal) {
        String[] parameterNames = Optional.of(joinPoint)
                .map(ProceedingJoinPoint::getSignature)
                .filter(MethodSignature.class::isInstance)
                .map(MethodSignature.class::cast)
                .map(MethodSignature::getParameterNames)
                .orElseThrow(() -> new CustomizeException("获取方法名失败", HttpStatus.INTERNAL_SERVER_ERROR.value()));
        Object[] parameterArgs = joinPoint.getArgs();
        EvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < parameterNames.length; i++) {
            context.setVariable(parameterNames[i],
                    parameterArgs[i] != null ? parameterArgs[i] : String.format("%s:%s",parameterNames[i],defaultVal));
        }
        return context;
    }

}
