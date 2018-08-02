/**
 * 
 */
package com.campaigns.api.controller;

import com.campaigns.api.model.MobileError;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MobileErrorController {
	
	// initialize logger
    private final Logger logger = ESAPI.getLogger(this.getClass());
    
	@RequestMapping(value = { "/api/error" }, method = RequestMethod.POST)
	public ResponseEntity<?> logError(@RequestBody MobileError mobileError) throws Exception{
		
		//get the username
		String username = SecurityContextHolder.getContext().getAuthentication().getName();
        mobileError.setUsername(username);
        //validate the input
        ESAPI.validator().getValidInput("errorName", mobileError.getName(), "SafeString", 500, true);
        ESAPI.validator().getValidInput("errorMessage", mobileError.getMessage(), "SafeString", 10000, false);
        ESAPI.validator().getValidInput("errorPlatform", mobileError.getPlatform(), "SafeString", 500, true);
        ESAPI.validator().getValidInput("errorType", mobileError.getType(), "SafeString", 100, false);
        //log the error
        logger.error(Logger.EVENT_FAILURE, "MOBILE DETAILS: ", new Exception(mobileError.toString()));
        return new ResponseEntity<>("{\"response\":\"The error has been logged successfully\"}", HttpStatus.OK);
	}

}
