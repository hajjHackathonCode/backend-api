/**
 *
 */
package com.clanconnect.utils;

import org.apache.log4j.FileAppender;
import org.apache.log4j.spi.ErrorCode;
import org.owasp.esapi.SafeFile;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomLogFileAppender extends FileAppender
{


    @Override
    public void activateOptions()
    {
        if (fileName != null)
        {
            try
            {
                SafeFile logFile = new SafeFile(fileName);
                if (logFile.exists())
                {
                    // backup the old log file
                    DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
                    String date = dateFormat.format(new Date());
                    logFile.renameTo(new SafeFile(fileName + "_" + date));
                }
                setFile(fileName, fileAppend, bufferedIO, bufferSize);
            }
            catch (Exception e)
            {
                errorHandler.error("Error", e, ErrorCode.FILE_OPEN_FAILURE);
            }
        }
    }
}
