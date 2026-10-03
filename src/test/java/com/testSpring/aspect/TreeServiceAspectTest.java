package com.testSpring.aspect;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
class TreeServiceAspectTest {


    // =============================================
    // ASPECT UNDER TEST
    // =============================================

    private TreeServiceAspect aspect;


    // =============================================
    // MOCKS
    // =============================================

    @Mock
    private JoinPoint joinPoint;

    @Mock
    private ProceedingJoinPoint proceedingJoinPoint;

    @Mock
    private Signature signature;


    // =============================================
    // CONSOLE OUTPUT
    // =============================================

    private ByteArrayOutputStream outputStream;

    private PrintStream originalOut;


    // =============================================
    // SETUP
    // =============================================

    @BeforeEach
    void setUp() {

        aspect =
                new TreeServiceAspect();


        /*
         * Store the original System.out.
         *
         * We temporarily redirect console output
         * so that we can verify the messages
         * printed by the Aspect.
         */

        originalOut =
                System.out;


        outputStream =
                new ByteArrayOutputStream();


        System.setOut(
                new PrintStream(outputStream)
        );
    }


    // =============================================
    // CLEANUP
    // =============================================

    @AfterEach
    void tearDown() {

        /*
         * Restore the original System.out after
         * every test.
         */

        System.setOut(originalOut);
    }


    // =============================================
    // BEFORE ADVICE
    // =============================================

    @Test
    void beforeServiceMethod_shouldPrintMethodAndArguments() {

        when(
                joinPoint.getSignature()
        )
        .thenReturn(signature);


        when(
                signature.getName()
        )
        .thenReturn("getOne");


        when(
                joinPoint.getArgs()
        )
        .thenReturn(
                new Object[] { 101 }
        );


        // Execute advice

        aspect.beforeServiceMethod(
                joinPoint
        );


        String output =
                outputStream.toString();


        // Verify output

        assertTrue(
                output.contains(
                        "AOP @Before"
                )
        );


        assertTrue(
                output.contains(
                        "Method: getOne"
                )
        );


        assertTrue(
                output.contains(
                        "Arguments: [101]"
                )
        );


        verify(
                joinPoint,
                times(1)
        )
        .getSignature();


        verify(
                joinPoint,
                times(1)
        )
        .getArgs();
    }


    // =============================================
    // AFTER ADVICE
    // =============================================

    @Test
    void afterServiceMethod_shouldPrintMethodName() {

        when(
                joinPoint.getSignature()
        )
        .thenReturn(signature);


        when(
                signature.getName()
        )
        .thenReturn("getAll");


        aspect.afterServiceMethod(
                joinPoint
        );


        String output =
                outputStream.toString();


        assertTrue(
                output.contains(
                        "AOP @After"
                )
        );


        assertTrue(
                output.contains(
                        "Method finished: getAll"
                )
        );


        verify(
                joinPoint,
                times(1)
        )
        .getSignature();
    }


    // =============================================
    // AFTER RETURNING ADVICE
    // =============================================

    @Test
    void afterReturning_shouldPrintReturnedValue() {

        when(
                joinPoint.getSignature()
        )
        .thenReturn(signature);


        when(
                signature.getName()
        )
        .thenReturn("getOne");


        String result =
                "Tree returned successfully";


        aspect.afterReturning(
                joinPoint,
                result
        );


        String output =
                outputStream.toString();


        assertTrue(
                output.contains(
                        "AOP @AfterReturning"
                )
        );


        assertTrue(
                output.contains(
                        "Method completed successfully: getOne"
                )
        );


        assertTrue(
                output.contains(
                        "Returned value: Tree returned successfully"
                )
        );


        verify(
                joinPoint,
                times(1)
        )
        .getSignature();
    }


    // =============================================
    // AFTER RETURNING - NULL RESULT
    // =============================================

    @Test
    void afterReturning_shouldHandleNullResult() {

        when(
                joinPoint.getSignature()
        )
        .thenReturn(signature);


        when(
                signature.getName()
        )
        .thenReturn("deleteTree");


        aspect.afterReturning(
                joinPoint,
                null
        );


        String output =
                outputStream.toString();


        assertTrue(
                output.contains(
                        "AOP @AfterReturning"
                )
        );


        assertTrue(
                output.contains(
                        "Method completed successfully: deleteTree"
                )
        );


        assertTrue(
                output.contains(
                        "Returned value: null"
                )
        );
    }


    // =============================================
    // AFTER THROWING ADVICE
    // =============================================

    @Test
    void afterThrowing_shouldPrintExceptionInformation() {

        when(
                joinPoint.getSignature()
        )
        .thenReturn(signature);


        when(
                signature.getName()
        )
        .thenReturn("getOne");


        RuntimeException exception =
                new RuntimeException(
                        "Tree not found"
                );


        aspect.afterThrowing(
                joinPoint,
                exception
        );


        String output =
                outputStream.toString();


        assertTrue(
                output.contains(
                        "AOP @AfterThrowing"
                )
        );


        assertTrue(
                output.contains(
                        "Exception in method: getOne"
                )
        );


        assertTrue(
                output.contains(
                        "Exception type: RuntimeException"
                )
        );


        assertTrue(
                output.contains(
                        "Exception message: Tree not found"
                )
        );


        verify(
                joinPoint,
                times(1)
        )
        .getSignature();
    }


    // =============================================
    // AROUND ADVICE - SUCCESS
    // =============================================

    @Test
    void measureExecutionTime_shouldReturnResult()
            throws Throwable {

        when(
                proceedingJoinPoint.getSignature()
        )
        .thenReturn(signature);


        when(
                signature.getName()
        )
        .thenReturn("getOne");


        String expectedResult =
                "TreeResult";


        when(
                proceedingJoinPoint.proceed()
        )
        .thenReturn(expectedResult);


        Object result =
                aspect.measureExecutionTime(
                        proceedingJoinPoint
                );


        // Verify actual return value

        assertEquals(
                expectedResult,
                result
        );


        String output =
                outputStream.toString();


        assertTrue(
                output.contains(
                        "AOP @Around"
                )
        );


        assertTrue(
                output.contains(
                        "Method: getOne"
                )
        );


        assertTrue(
                output.contains(
                        "Execution time:"
                )
        );


        assertTrue(
                output.contains(
                        "ms"
                )
        );


        verify(
                proceedingJoinPoint,
                times(1)
        )
        .proceed();
    }


    // =============================================
    // AROUND ADVICE - OBJECT RESULT
    // =============================================

    @Test
    void measureExecutionTime_shouldReturnSameObject()
            throws Throwable {

        when(
                proceedingJoinPoint.getSignature()
        )
        .thenReturn(signature);


        when(
                signature.getName()
        )
        .thenReturn("addTree");


        Object expectedResult =
                new Object();


        when(
                proceedingJoinPoint.proceed()
        )
        .thenReturn(expectedResult);


        Object actualResult =
                aspect.measureExecutionTime(
                        proceedingJoinPoint
                );


        assertSame(
                expectedResult,
                actualResult
        );


        String output =
                outputStream.toString();


        assertTrue(
                output.contains(
                        "Method: addTree"
                )
        );


        assertTrue(
                output.contains(
                        "Execution time:"
                )
        );


        verify(
                proceedingJoinPoint,
                times(1)
        )
        .proceed();
    }


    // =============================================
    // AROUND ADVICE - NULL RETURN VALUE
    // =============================================

    @Test
    void measureExecutionTime_shouldHandleNullResult()
            throws Throwable {

        when(
                proceedingJoinPoint.getSignature()
        )
        .thenReturn(signature);


        when(
                signature.getName()
        )
        .thenReturn("someMethod");


        when(
                proceedingJoinPoint.proceed()
        )
        .thenReturn(null);


        Object result =
                aspect.measureExecutionTime(
                        proceedingJoinPoint
                );


        assertNull(result);


        String output =
                outputStream.toString();


        assertTrue(
                output.contains(
                        "AOP @Around"
                )
        );


        assertTrue(
                output.contains(
                        "Method: someMethod"
                )
        );


        assertTrue(
                output.contains(
                        "Execution time:"
                )
        );


        verify(
                proceedingJoinPoint,
                times(1)
        )
        .proceed();
    }


    // =============================================
    // AROUND ADVICE - EXCEPTION
    // =============================================

    @Test
    void measureExecutionTime_shouldPropagateException()
            throws Throwable {

        when(
                proceedingJoinPoint.getSignature()
        )
        .thenReturn(signature);


        when(
                signature.getName()
        )
        .thenReturn("getOne");


        RuntimeException exception =
                new RuntimeException(
                        "Service failure"
                );


        when(
                proceedingJoinPoint.proceed()
        )
        .thenThrow(exception);


        RuntimeException thrown =
                assertThrows(
                        RuntimeException.class,
                        () ->
                                aspect.measureExecutionTime(
                                        proceedingJoinPoint
                                )
                );


        assertEquals(
                "Service failure",
                thrown.getMessage()
        );


        /*
         * This is particularly important for your
         * Aspect because measureExecutionTime()
         * contains a finally block.
         *
         * Therefore execution time should still be
         * printed even when proceed() throws an
         * exception.
         */

        String output =
                outputStream.toString();


        assertTrue(
                output.contains(
                        "AOP @Around"
                )
        );


        assertTrue(
                output.contains(
                        "Method: getOne"
                )
        );


        assertTrue(
                output.contains(
                        "Execution time:"
                )
        );


        verify(
                proceedingJoinPoint,
                times(1)
        )
        .proceed();
    }


    // =============================================
    // POINTCUT METHODS
    // =============================================
    //
    // Pointcut methods have empty bodies.
    //
    // Normally there is no business logic to test.
    // These calls are included only so JaCoCo can
    // mark the methods themselves as executed.
    // =============================================

    @Test
    void pointcutMethods_shouldExecuteWithoutError() {

        assertDoesNotThrow(
                () -> aspect.treeServiceMethods()
        );


        assertDoesNotThrow(
                () -> aspect.treeSoapServiceMethods()
        );


        assertDoesNotThrow(
                () -> aspect.allTreeServiceMethods()
        );
    }
}