package com.handy.common.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * date: 2021-01-15
 * description:
 */
public class IOUtils {

    public static void close(Closeable closeable){
        if(closeable != null){
            try {
                closeable.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
