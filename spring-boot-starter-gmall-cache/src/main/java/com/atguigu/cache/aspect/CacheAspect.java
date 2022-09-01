package com.atguigu.cache.aspect;

import com.atguigu.cache.annotation.GmallCache;
import com.atguigu.cache.constant.SysRedisConst;
import com.atguigu.cache.service.CacheOpsService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

import java.lang.reflect.Type;

/**
 * @author Connor
 * @date 2022/9/1
 */
@Aspect
@Slf4j
public class CacheAspect {
    @Autowired
    private CacheOpsService cacheOpsService;
    private final ExpressionParser parser = new SpelExpressionParser();
    private final TemplateParserContext context = new TemplateParserContext();

    @Around("@annotation(com.atguigu.cache.annotation.GmallCache)")
    public Object around(ProceedingJoinPoint joinPoint) {
        String cacheKey = assembleCacheKey(joinPoint);
        Type genericReturnType = getGenericReturnType(joinPoint);
        boolean lock = false;
        String lockName = null;
        try {
            //1.前置通知
              //1.1查缓存
            Object cacheData = cacheOpsService.getCacheData(cacheKey, genericReturnType);
            if (cacheData != null) {
                log.info("缓存 - 存在");
                return cacheData;
            }
              //1.2查布隆(需要查布隆的查,不需要的不查)
            String bloomName = getBloomName(joinPoint);
            if (!StringUtils.isEmpty(bloomName)) {
                Object bloomValue = getBloomValue(joinPoint);
                if (!cacheOpsService.isBloomContains(bloomName, bloomValue)) {
                    log.info("布隆过滤器 - 不存在");
                    return null;
                }
            }
            //2.目标执行
            lockName = getLockName(joinPoint);
            lock = cacheOpsService.tryLock(lockName);
            if (lock) {
                log.info("布隆过滤器 - 存在 --回源中");
                Object proceed = joinPoint.proceed(joinPoint.getArgs());
                cacheOpsService.saveData(cacheKey, proceed);
                return proceed;
            } else {
                Thread.sleep(1000);
                return cacheOpsService.getCacheData(cacheKey, genericReturnType);
            }
            //3.返回通知
        } catch (Throwable e) {
            //4.异常通知
            throw new RuntimeException(e);
        } finally {
            //5.后置通知
            if (lock) {
                cacheOpsService.unlock(lockName);
            }
        }
    }

    /**
     * 获取要在布隆中查找的值
     *
     * @param joinPoint
     * @return
     */
    private Object getBloomValue(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = getMethodSignature(joinPoint);
        String valueExpression = signature.getMethod().getAnnotation(GmallCache.class).bloomValue();
        return evaluationExpression(valueExpression, joinPoint, Object.class);
    }

    /**
     * 获取布隆过滤器的名称
     *
     * @param joinPoint
     * @return
     */
    private String getBloomName(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = getMethodSignature(joinPoint);
        return signature.getMethod().getAnnotation(GmallCache.class).bloomName();
    }

    /**
     * 获取分布式锁的名称
     *
     * @param joinPoint
     * @return
     */
    private String getLockName(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = getMethodSignature(joinPoint);
        String lockName = signature.getMethod().getAnnotation(GmallCache.class).lockName();
        if (StringUtils.isEmpty(lockName)) {
            String methodName = signature.getMethod().getName();
            return SysRedisConst.LOCK_PREFIX + methodName;
        }
        return evaluationExpression(lockName, joinPoint, String.class);
    }

    /**
     * 取得目标执行后的返回值
     *
     * @param joinPoint
     * @return
     */
    private Type getGenericReturnType(ProceedingJoinPoint joinPoint) {
        return getMethodSignature(joinPoint).getMethod().getGenericReturnType();
    }

    /**
     * 获取MethodSignature签名对象
     *
     * @param joinPoint
     * @return
     */
    private MethodSignature getMethodSignature(ProceedingJoinPoint joinPoint) {
        Signature sig = joinPoint.getSignature();
        if (!(sig instanceof MethodSignature)) {
            throw new IllegalArgumentException();
        }
        return (MethodSignature) sig;
    }

    /**
     * 组装cacheKey
     *
     * @param joinPoint
     * @return
     */
    private String assembleCacheKey(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = getMethodSignature(joinPoint);
        GmallCache gmallCache = signature.getMethod().getDeclaredAnnotation(GmallCache.class);
        String cacheKey = gmallCache.cacheKey();
        return evaluationExpression(cacheKey, joinPoint, String.class);
    }

    /**
     * 根据注解中的spring表达式取值
     *
     * @param expressionStr
     * @param joinPoint
     * @param clazz
     * @param <T>
     * @return
     */
    private <T> T evaluationExpression(String expressionStr, ProceedingJoinPoint joinPoint, Class<T> clazz) {
        StandardEvaluationContext evaluationContext = new StandardEvaluationContext();
        evaluationContext.setVariable("params", joinPoint.getArgs());
        Expression expression = parser.parseExpression(expressionStr, context);
        return expression.getValue(evaluationContext, clazz);
    }
}
