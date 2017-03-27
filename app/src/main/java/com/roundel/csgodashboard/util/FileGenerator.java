package com.roundel.csgodashboard.util;

import java.io.File;
import java.io.IOException;
import java.util.Random;

/**
 * Created by Krzysiek on 2017-02-22.
 */
public class FileGenerator
{
    private static final String TAG = FileGenerator.class.getSimpleName();
    private static final char[] hexArray = "0123456789ABCDEF".toCharArray();
    private static final int DEFAULT_FILE_LENGTH = 24;

    public static String bytesToHex(byte[] bytes)
    {
        char[] hexChars = new char[bytes.length * 2];
        for(int j = 0; j < bytes.length; j++)
        {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private static String generateRandomName(String prefix, String suffix, int length)
    {
        if(length > 128)
            throw new IllegalArgumentException("Length cannot be grater than 128");
        if(length < 0)
            throw new IllegalArgumentException("Length cannot be smaller than 0");

        byte[] bytes = new byte[length / 2];
        new Random().nextBytes(bytes);
        return prefix + bytesToHex(bytes).toLowerCase() + suffix;
    }

    public static File createRandomFile(String prefix, String suffix, int length, File directory) throws IOException
    {
        File file;
        do
        {
            String name = generateRandomName(prefix, suffix, length);
            file = new File(directory + File.separator + name);
        }
        while(file.exists());

        file.getParentFile().mkdirs();
        file.createNewFile();

        return file;
    }

    public static File createRandomFile(String prefix, String suffix, File directory) throws IOException
    {
        return createRandomFile(prefix, suffix, DEFAULT_FILE_LENGTH, directory);
    }
}
