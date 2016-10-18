package com.zaimella.snacks.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Created by mvelasco on 18/10/2016.
 */

public class SnacksUtil {

    public static String obtenerStackErrores(Exception e){

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        e.printStackTrace(printWriter);
        return writer.toString();
    }
}
