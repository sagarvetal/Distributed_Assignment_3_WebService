package com.dcms.service;

import java.io.*;

/**
 * This class reads the counter from counter file of respective record type
 * and increment the counter by 1.
 */

public class CounterService implements Serializable {

	private static final long serialVersionUID = 1L;

    
	public static synchronized Integer getCounter(final String filePath) throws ClassNotFoundException {
		Integer count = 0;
    	try {
	        final FileInputStream inputStream = new FileInputStream(filePath);
	        final BufferedInputStream brInputStream = new BufferedInputStream(inputStream);
	        final ObjectInputStream input = new ObjectInputStream(brInputStream);
	        count = (Integer) input.readObject();
	        input.close();
	        brInputStream.close();
	        inputStream.close();
    	} catch (FileNotFoundException e) {
            //e.printStackTrace();
        } catch (IOException e) {
            //e.printStackTrace();
        }
    	writeCounter(filePath, ++count);
        return count;
    }
    
    public static synchronized void writeCounter(final String filePath, final Integer count) {
    	try {
        	final FileOutputStream outputStream = new FileOutputStream(filePath, false);
            final BufferedOutputStream brOutputStream = new BufferedOutputStream(outputStream);
            final ObjectOutputStream output = new ObjectOutputStream (brOutputStream);
            output.writeObject(count);
            output.close();
            brOutputStream.close();
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
