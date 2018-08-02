package com.campaigns.api.config;

import com.campaigns.api.utils.CustomException;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Logger;
import org.owasp.esapi.errors.IntrusionException;
import org.owasp.esapi.errors.ValidationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.security.access.AuthorizationServiceException;
import org.springframework.security.authentication.*;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.io.IOException;
import java.util.Date;
import java.util.Random;

@ControllerAdvice
public class GlobalControllerAdvice
{

    // initialize logger
    private final Logger logger = ESAPI.getLogger(this.getClass());

    /**
     * Handle Authentication Exceptions and return error and HttpStatus.UNAUTHORIZED
     */
    @ExceptionHandler({InternalAuthenticationServiceException.class, AuthorizationServiceException.class, BadCredentialsException.class, LockedException.class, DisabledException.class, AccountExpiredException.class, CredentialsExpiredException.class})
    public ResponseEntity<String> authorizationResponse(Exception exception)
    {
        String errorCode = generateErrorCode();
        logger.error(Logger.EVENT_FAILURE, "ERROR CODE: " + errorCode, exception);
        return new ResponseEntity<>("{ \"errorMessage\":\"Authentication Failure\",\"errorCode\":\"" + errorCode + "\"  }", HttpStatus.UNAUTHORIZED);

    }

    /**
     * Handle DuplicateKey Exceptions and return error and HttpStatus.CONFLICT
     */

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<String> duplicateKeyResponse(DuplicateKeyException exception)
    {

        String errorCode = generateErrorCode();
        logger.error(Logger.EVENT_FAILURE, "ERROR CODE: " + errorCode, exception);
        return new ResponseEntity<>("{ \"errorMessage\":\"Duplicate Key\",\"errorCode\":\"" + errorCode + "\"  }", HttpStatus.CONFLICT);

    }

    /**
     * Handle ServletRequestBinding Exceptions and return error and HttpStatus.BAD_REQUEST
     */

    @ExceptionHandler({ServletRequestBindingException.class, IntrusionException.class, ValidationException.class})
    public ResponseEntity<String> badRequestResponse(Exception exception)
    {

        String errorCode = generateErrorCode();
        logger.error(Logger.EVENT_FAILURE, "ERROR CODE: " + errorCode, exception);
        return new ResponseEntity<>("{ \"errorMessage\":\"Data not valid\",\"errorCode\":\"" + errorCode + "\"  }", HttpStatus.BAD_REQUEST);

    }

    /**
     * Handle HttpRequestMethodNotSupported Exception and return error and HttpStatus.METHOD_NOT_ALLOWED
     */

    @ExceptionHandler({HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<String> methodNotAllowedResponse(Exception exception)
    {

        String errorCode = generateErrorCode();
        logger.error(Logger.EVENT_FAILURE, "ERROR CODE: " + errorCode, exception);
        return new ResponseEntity<>("{ \"errorMessage\":\"Method Not Supported\",\"errorCode\":\"" + errorCode + "\"  }", HttpStatus.METHOD_NOT_ALLOWED);

    }

    /**
     * Handle IOException NoHandlerFoundException and return error and HttpStatus.NOT_FOUND
     */

    @ExceptionHandler({IOException.class, NoHandlerFoundException.class})
    public ResponseEntity<String> notFoundErrorResponse(Exception exception)
    {
        String errorCode = generateErrorCode();
        logger.error(Logger.EVENT_FAILURE, "ERROR CODE: " + errorCode, exception);
//        return new ResponseEntity<>("{ \"errorMessage\":\"No Handler Found\",\"errorCode\":\"" + errorCode + "\"  }", HttpStatus.NOT_FOUND);
        return ResponseEntity.ok("Welcome to Group Connect");
    }

    /**
     * Handle any other Exeptions and return error and HttpStatus.INTERNAL_SERVER_ERROR
     */

    @ExceptionHandler({MailAuthenticationException.class, Exception.class})
    public ResponseEntity<String> generalErrorResponse(Exception exception)
    {

        String errorCode = generateErrorCode();
        logger.error(Logger.EVENT_FAILURE, "ERROR CODE: " + errorCode, exception);
        return new ResponseEntity<>("{ \"errorMessage\":\"General Error\",\"errorCode\":\"" + errorCode + "\"  }", HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @ExceptionHandler({CustomException.class})
    public ResponseEntity<String> generalErrorResponse(CustomException exception)
    {
        String errorCode = generateErrorCode();
        logger.error(Logger.EVENT_FAILURE, "ERROR CODE: " + errorCode, exception);
        return new ResponseEntity<>(exception.toString(), HttpStatus.BAD_REQUEST);

    }

    /**
     * @return System generated error code to be used for investigation
     */

    private String generateErrorCode()
    {
        return new Random().nextInt(1000) + "" + new Date().getTime();
    }
}
