package com.clanconnect.api.model;

/**
 * Created by hassanayoub on 2/20/17.
 * <p>
 * Version class is a type for values like 1.0.0.
 */
public class Version implements Comparable<Version>
{

    private String version;

    public Version(String version)
    {
        if (version == null)
            throw new IllegalArgumentException("Version can not be null");
        if (!version.matches("[0-9]+(\\.[0-9]+)*"))
            throw new IllegalArgumentException("Invalid version format");
        this.version = version;
    }

    public static boolean isVersion(String version)
    {
        try
        {
            new Version(version);
            return true;
        }
        catch (Exception ignore)
        {
            return false;
        }
    }

    public final String get()
    {
        return this.version;
    }

    @Override
    public int compareTo(Version that)
    {
        if (that == null)
            return 1;
        String[] thisParts = this.get().split("\\.");
        String[] thatParts = that.get().split("\\.");
        int length = Math.max(thisParts.length, thatParts.length);
        for (int i = 0; i < length; i++)
        {
            int thisPart = i < thisParts.length ?
                    Integer.parseInt(thisParts[i]) : 0;
            int thatPart = i < thatParts.length ?
                    Integer.parseInt(thatParts[i]) : 0;
            if (thisPart < thatPart)
                return -1;
            if (thisPart > thatPart)
                return 1;
        }
        return 0;
    }

    @Override
    public boolean equals(Object that)
    {
        if (this == that)
            return true;
        if (that == null)
            return false;
        if (this.getClass() != that.getClass())
            return false;
        return this.compareTo((Version) that) == 0;
    }

}