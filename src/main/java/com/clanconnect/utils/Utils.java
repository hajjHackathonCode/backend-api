package com.clanconnect.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by hassanayoub on 9/4/16.
 * <p>
 * Utility class
 */

public class Utils
{



    /**
     * @param request
     * @return
     */

    public static String getClientIPAddressFromRequest(HttpServletRequest request)
    {
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null)
        {
            ipAddress = request.getRemoteAddr();
        }

        return ipAddress;
    }

    /**
     * @param serializable
     * @return
     * @throws IOException
     */
    public static String transferSerializableToJsonString(Serializable serializable) throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        return mapper.writeValueAsString(serializable);
    }

    /**
     * @param string
     * @param serializableClass
     * @return
     * @throws IOException
     */

    public static <T extends Serializable> T transferJsonStringToSerializable(String string, Class<T> serializableClass)
            throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(string, serializableClass);
    }

    /**
     * @param string
     * @return
     */

    public static String transferStringToBase64String(String string)
    {

        return Base64.getEncoder().encodeToString(string.getBytes());

    }

    /**
     * @param string
     * @return
     */
    public static String transferBase64StringToString(String string)
    {

        return new String(Base64.getDecoder().decode(string));

    }

    public static boolean isArrayEmptyOrNull(Object[] array)
    {
        if (array == null || array.length == 0)
            return true;
        else
        {
            return Arrays.stream(array).filter(s -> s == null || s.toString().trim().equals("")).count() == array.length;
        }
    }

    @SuppressWarnings("Duplicates")
    public static Date attachCurrentTime(Date date)
    {
        Locale currentLocale = LocaleContextHolder.getLocale() != null ? LocaleContextHolder.getLocale() : Locale.getDefault();
        Calendar calendar = Calendar.getInstance(currentLocale);
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, Calendar.getInstance(currentLocale).get(Calendar.HOUR_OF_DAY));
        calendar.set(Calendar.MINUTE, Calendar.getInstance(currentLocale).get(Calendar.MINUTE));
        return calendar.getTime();
    }

    @SuppressWarnings("Duplicates")
    public static Date attachTime(Date date, int hour, int minute)
    {
        Calendar calendar = LocaleContextHolder.getLocale() != null ? Calendar.getInstance(LocaleContextHolder.getLocale()) : Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        return calendar.getTime();
    }


    public static Date parseDate(String dateStr, int hour, int minute)
    {
        Date date = parseDate(dateStr);
        Calendar calendar = LocaleContextHolder.getLocale() != null ? Calendar.getInstance(LocaleContextHolder.getLocale()) : Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        return calendar.getTime();
    }

    public static boolean isDate(String dateStr)
    {
        return parseDate(dateStr) != null;
    }

    public static Date parseDate(String dateStr)
    {
        try
        {
            Locale currentLocale = LocaleContextHolder.getLocale() != null ? LocaleContextHolder.getLocale() : Locale.getDefault();
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", currentLocale);
            Date date = dateFormat.parse(dateStr);
            Calendar calendar = Calendar.getInstance(currentLocale);
            calendar.setTime(date);
            calendar.set(Calendar.HOUR_OF_DAY, Calendar.getInstance(currentLocale).get(Calendar.HOUR_OF_DAY));
            calendar.set(Calendar.MINUTE, Calendar.getInstance(currentLocale).get(Calendar.MINUTE));
            return calendar.getTime();

        }
        catch (Exception ex)
        {
            return null;
        }
    }

    public static int compareCalendars(Calendar calendar, Calendar targetCalendar)
    {
        Calendar c = calendar;
        Calendar t = targetCalendar;
        if (c.get(Calendar.YEAR) <= t.get(Calendar.YEAR))
        {
            if (c.get(Calendar.MONTH) <= t.get(Calendar.MONTH))
            {
                return Integer.compare(c.get(Calendar.DAY_OF_MONTH), t.get(Calendar.DAY_OF_MONTH));
            }
        }

        return 1;
    }


}