package com.dcms.service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.util.Date;

import com.dcms.util.FieldConstants;

/**
 * Provide Logging Functionality
 * It will helpful when we want to recover the data during server crash
 */
public class ActivityLoggerService {

    private FileWriter fileWriter = null;
    private BufferedWriter bufferedWriter = null;
    private PrintWriter printWriter = null;

    public ActivityLoggerService(final String activityLoggerFile) throws IOException {
        fileWriter = new FileWriter(activityLoggerFile, true);
        bufferedWriter = new BufferedWriter(fileWriter);
        printWriter = new PrintWriter(bufferedWriter);
    }

    public synchronized void log(final String managerId, final String action, final Object obj) {
        try {
        	final String dataLog = DateFormat.getDateTimeInstance().format(new Date()) + FieldConstants.FIELD_SEPARATOR_ARROW + 
								   managerId + FieldConstants.FIELD_SEPARATOR_ARROW + 
								   action + FieldConstants.FIELD_SEPARATOR_PIPE + 
								   getAttributes(obj);
    		printWriter.println(dataLog);
    		System.out.println(dataLog);
			bufferedWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public synchronized void log(final String managerId, final String action, final String message, final Object... args) {
    	try {
    		final String dataLog = String.format(DateFormat.getDateTimeInstance().format(new Date()) + FieldConstants.FIELD_SEPARATOR_ARROW + 
								   managerId + FieldConstants.FIELD_SEPARATOR_ARROW + 
								   action + FieldConstants.FIELD_SEPARATOR_PIPE + message, args);
			printWriter.println(dataLog);
			System.out.println(dataLog);
			bufferedWriter.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    public synchronized void log(final String messageType, final String message) {
    	try {
    		final String dataLog = DateFormat.getDateTimeInstance().format(new Date()) + FieldConstants.FIELD_SEPARATOR_ARROW + 
								   messageType + FieldConstants.FIELD_SEPARATOR_COLON + 
								   message;
    		printWriter.println(dataLog);
    		System.out.println(dataLog);
    		bufferedWriter.flush();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    }

    private String getAttributes(final Object obj){
        String record = "";
        final Class<?> c = obj.getClass();
        final Field[] attributes = c.getDeclaredFields();
        for(final Field attribute : attributes ){
            try {
                record += attribute.getName().toString() + FieldConstants.FIELD_SEPARATOR_COLON + attribute.get(obj);
                record += FieldConstants.FIELD_SEPARATOR_PIPE;
            } catch (IllegalArgumentException e1) {
                return obj.toString();
            } catch (IllegalAccessException e1) {
                return obj.toString();
            }
        }
        return record;
    }

}