package com.clanconnect.api.validator;

import com.clanconnect.api.model.Credential;
import org.owasp.esapi.ESAPI;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class CredentialValidator implements Validator
{


    @Override
    public boolean supports(Class<?> clazz)
    {
        return Credential.class.equals(clazz);
    }


    @Override
    public void validate(Object object, Errors error)
    {

        // validate Credential bean attributes
        ValidationUtils.rejectIfEmpty(error, "username", "username can't be empty");
        ValidationUtils.rejectIfEmpty(error, "password", "password can't be empty");
        ValidationUtils.rejectIfEmpty(error, "uuid", "uuid can't be empty");

        if (error.hasErrors())
        {
            return;
        }

        Credential credential = (Credential) object;

        // validate the username
        if (!ESAPI.validator().isValidInput("username", credential.getUsername(), "SafeString", 100, false))
        {
            error.rejectValue("username", "username is not a valid string");
        }
        // validate the imei
        else if (!ESAPI.validator().isValidInput("uuid", credential.getUuid(), "GUID", 100, false))
        {
            error.rejectValue("uuid", "uuid is not valid");
        }
    }

}
