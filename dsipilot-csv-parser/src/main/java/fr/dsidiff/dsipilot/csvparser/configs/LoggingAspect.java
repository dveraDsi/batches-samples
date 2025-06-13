package fr.dsidiff.dsipilot.csvparser.configs;

import fr.dsidiff.dsipilot.csvparser.data.LogRepository;
import fr.dsidiff.dsipilot.csvparser.models.LogEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.time.LocalDateTime;
import java.util.UUID;


@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {
    private final LogRepository logRepository;

    @Pointcut("within(@fr.dsidiff.* *)")
    public void applicationMethods() {
    }

    @Pointcut("within(@org.springframework.stereotype.* *)")
    public void withinSpringStereotypes() {
    }

    @Around("applicationMethods() || withinSpringStereotypes()")
    public Object profileServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch watch = new StopWatch();
        watch.start();

        // do some logging before method execution
        //log.info("### " + joinPoint.getSignature().toShortString() + "| Args => " + Arrays.asList(joinPoint.getArgs()));
        Object retVal = joinPoint.proceed();
        // and some logging after method execution
        watch.stop();
        //log.info("### " + joinPoint.getSignature().toShortString() + "| Args => " + Arrays.asList(joinPoint.getArgs()) + ": completed in " + "\t\t\t" + watch.getTotalTimeMillis() + " ms" );
        log.info("### {}| duration => {} ms", joinPoint.getSignature().toShortString(), watch.getTotalTimeMillis());
        String signatureName = joinPoint.getSignature().getName();
        String declaringTypeName = joinPoint.getSignature().getDeclaringTypeName();
        LogEntity logEntity = new LogEntity();
        logEntity.setLogId(UUID.randomUUID().toString());
        logEntity.setDeclaringTypeName(declaringTypeName);
        logEntity.setSignatureName(signatureName);
        logEntity.setType("SUCCESS");
        logEntity.setDuration(watch.getTotalTimeMillis());
        logEntity.setCreatedAt(LocalDateTime.now());
        logRepository.save(logEntity);
        return retVal;
    }


    /**
     * Advice that logs methods throwing exceptions.
     *
     * @param joinPoint join point for advice
     * @param e exception
     */
    @AfterThrowing(
            pointcut = "applicationMethods() && withinSpringStereotypes()",
            throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        String signatureName = joinPoint.getSignature().getName();
        String declaringTypeName = joinPoint.getSignature().getDeclaringTypeName();
        String cause = e.getCause() != null ? e.getCause().getMessage() : "NULL";
        LogEntity logEntity = new LogEntity();
        logEntity.setLogId(UUID.randomUUID().toString());
        logEntity.setDeclaringTypeName(declaringTypeName);
        logEntity.setSignatureName(signatureName);
        logEntity.setType("ERROR");
        logEntity.setMessage(cause);
        logEntity.setCreatedAt(LocalDateTime.now());
        logRepository.save(logEntity);

        log.error("Exception in {}.{}() with cause = {}",
                declaringTypeName,
                signatureName,
                cause);
    }
}
