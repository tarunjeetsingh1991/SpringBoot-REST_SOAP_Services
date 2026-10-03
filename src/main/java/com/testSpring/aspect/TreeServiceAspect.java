package com.testSpring.aspect;

import java.util.Arrays;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;


@Aspect
@Component
public class TreeServiceAspect {


    // =============================================
    // POINTCUT - REST SERVICE
    // =============================================
    //
    // This pointcut matches every method inside:
    //
    // com.testSpring.service.TreeService
    //
    // First *  = any return type
    // *(..)    = any method with any number of arguments

    @Pointcut(
        "execution(* com.testSpring.service.TreeService.*(..))"
    )
    public void treeServiceMethods() {
    }


    // =============================================
    // POINTCUT - SOAP SERVICE
    // =============================================
    //
    // This pointcut matches every method inside:
    //
    // com.testSpring.service.TreeSoapService

    @Pointcut(
        "execution(* com.testSpring.service.TreeSoapService.*(..))"
    )
    public void treeSoapServiceMethods() {
    }


    // =============================================
    // COMBINED POINTCUT
    // =============================================
    //
    // This combines REST and SOAP service pointcuts.
    //
    // Therefore, the advice can run for methods in:
    //
    // TreeService
    //
    // AND
    //
    // TreeSoapService

    @Pointcut(
        "treeServiceMethods() || treeSoapServiceMethods()"
    )
    public void allTreeServiceMethods() {
    }


    // =============================================
    // BEFORE ADVICE
    // =============================================
    //
    // Runs BEFORE the actual service method.
    //
    // JoinPoint gives us information about:
    //
    // method name
    // arguments
    // target object
    // method signature

    @Before("allTreeServiceMethods()")
    public void beforeServiceMethod(
            JoinPoint joinPoint) {

        String methodName =
                joinPoint.getSignature().getName();

        Object[] arguments =
                joinPoint.getArgs();


        System.out.println(
                "\n========================================"
        );

        System.out.println(
                "AOP @Before"
        );

        System.out.println(
                "Method: " + methodName
        );

        System.out.println(
                "Arguments: "
                + Arrays.toString(arguments)
        );

        System.out.println(
                "========================================"
        );
    }


    // =============================================
    // AFTER ADVICE
    // =============================================
    //
    // Runs AFTER the method finishes.
    //
    // Important:
    //
    // @After runs whether the method:
    //
    // 1. completes successfully
    //
    // OR
    //
    // 2. throws an exception

    @After("allTreeServiceMethods()")
    public void afterServiceMethod(
            JoinPoint joinPoint) {

        String methodName =
                joinPoint.getSignature().getName();


        System.out.println(
                "\nAOP @After"
        );

        System.out.println(
                "Method finished: " + methodName
        );
    }


    // =============================================
    // AFTER RETURNING ADVICE
    // =============================================
    //
    // Runs ONLY when the service method completes
    // successfully.
    //
    // It will NOT run when an exception occurs.
    //
    // returning = "result"
    //
    // connects the service method's return value
    // with the result parameter below.

    @AfterReturning(
        pointcut = "allTreeServiceMethods()",
        returning = "result"
    )
    public void afterReturning(
            JoinPoint joinPoint,
            Object result) {

        String methodName =
                joinPoint.getSignature().getName();


        System.out.println(
                "\nAOP @AfterReturning"
        );

        System.out.println(
                "Method completed successfully: "
                + methodName
        );

        System.out.println(
                "Returned value: " + result
        );
    }


    // =============================================
    // AFTER THROWING ADVICE
    // =============================================
    //
    // Runs ONLY if the service method throws
    // an exception.
    //
    // We are NOT handling the exception here.
    //
    // We are only logging it.
    //
    // REST exceptions will still go to:
    //
    // GlobalExceptionHandler
    //
    // SOAP exceptions will still go to:
    //
    // SoapExceptionResolver

    @AfterThrowing(
        pointcut = "allTreeServiceMethods()",
        throwing = "exception"
    )
    public void afterThrowing(
            JoinPoint joinPoint,
            Throwable exception) {

        String methodName =
                joinPoint.getSignature().getName();


        System.out.println(
                "\nAOP @AfterThrowing"
        );

        System.out.println(
                "Exception in method: "
                + methodName
        );

        System.out.println(
                "Exception type: "
                + exception.getClass().getSimpleName()
        );

        System.out.println(
                "Exception message: "
                + exception.getMessage()
        );
    }


    // =============================================
    // AROUND ADVICE
    // =============================================
    //
    // @Around is the most powerful advice.
    //
    // It runs:
    //
    // BEFORE
    //    ↓
    // actual method
    //    ↓
    // AFTER
    //
    // ProceedingJoinPoint allows us to call:
    //
    // proceed()
    //
    // which executes the actual service method.
    //
    // Here we use @Around to calculate execution
    // time.

    @Around("allTreeServiceMethods()")
    public Object measureExecutionTime(
            ProceedingJoinPoint joinPoint)
            throws Throwable {

        String methodName =
                joinPoint.getSignature().getName();


        long startTime =
                System.nanoTime();


        try {

            // Execute the actual service method
            Object result =
                    joinPoint.proceed();


            return result;

        } finally {

            long endTime =
                    System.nanoTime();


            long executionTime =
                    endTime - startTime;


            double executionTimeMs =
                    executionTime / 1_000_000.0;


            System.out.println(
                    "\nAOP @Around"
            );

            System.out.println(
                    "Method: " + methodName
            );

            System.out.println(
                    "Execution time: "
                    + executionTimeMs
                    + " ms"
            );
        }
    }
}