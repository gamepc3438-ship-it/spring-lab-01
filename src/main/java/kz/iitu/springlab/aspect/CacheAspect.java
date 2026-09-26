package kz.iitu.springlab.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
public class CacheAspect {

    private final Map<String, Object> cache = new ConcurrentHashMap<>();

    @Around("@annotation(kz.iitu.springlab.cache.SimpleCache)")
    public Object cache(ProceedingJoinPoint pjp) throws Throwable {

        String key = pjp.getSignature().toShortString()
                + Arrays.toString(pjp.getArgs());

        if (cache.containsKey(key)) {
            System.out.println("[CACHE] HIT -> " + key);
            return cache.get(key);
        }

        System.out.println("[CACHE] MISS -> " + key);

        Object result = pjp.proceed();

        cache.put(key, result);

        return result;
    }
}