package com.dcms.client;

import java.util.HashSet;

import com.dcms.util.LocationConstants;
import com.dcms.util.StatusConstants;

public class MultiClientTest implements Runnable {

    private String managerID;

    public MultiClientTest(final String managerId) {
        this.managerID = managerId;
    }

    public static void main(String args[]) throws Exception {
        final HashSet<String> managers = new HashSet<>();
        managers.add("MTL0001");
        managers.add("MTL0002");
        managers.add("LVL0001");
        managers.add("LVL0002");
        managers.add("DDO0001");
        managers.add("DDO0002");
        managers.add("DDO0003");
        managers.add("MMM0001");

        for (final String managerId : managers) {
            final MultiClientTest clientThread = new MultiClientTest(managerId);
            final Thread t = new Thread(clientThread);
            t.start();
            System.out.println("Thread Started");
        }

        Thread.sleep(6000);
        final ClientManager clientManager = new ClientManager("MTL0001");
        System.out.println("******************** Final RESULT ********************");
        clientManager.getRecordCounts();
        System.out.println("****************************************************");
    }

    public void run() {
        try {
        	if (isValidManagerId(this.managerID)) {
        		
        		final ClientManager clientManager = new ClientManager(this.managerID);
        		
        		//Testing student record creation, edition and display functionality with proper data 
        		clientManager.createSRecord("Sagar", "Vetal", "French, English", StatusConstants.ACTIVE, "02-05-2018");
        		clientManager.displayRecord("SR00004");
        		clientManager.editRecord("SR00004", "status", "InActive");
        		clientManager.displayRecord("SR00004");
        		
        		//Testing teacher record creation, edition and display functionality with proper data 
        		clientManager.createTRecord("Himanshu", "Kohali", "Montreal", "0123456789", "french", LocationConstants.MONTREAL);
        		clientManager.displayRecord("TR00003");
        		clientManager.editRecord("TR00004", "address", "Alberta");
        		clientManager.displayRecord("TR00003");
        		
        		//Testing get record count and transfer record functionality
        		clientManager.getRecordCounts();
        		clientManager.transferRecord("SR00004", LocationConstants.LAVAL);
        		clientManager.getRecordCounts();
        		clientManager.transferRecord("SR00005", LocationConstants.DOLLARD);
        		clientManager.getRecordCounts();
        		
        		//Testing edit record functionality with improper data to test validations
        		clientManager.editRecord("TR00003", "location", "LON");
        		
        		//Testing get record count and transfer record functionality
        		clientManager.getRecordCounts();
        		clientManager.transferRecord("TR00003", LocationConstants.LAVAL);
        		clientManager.getRecordCounts();
        		clientManager.transferRecord("TR00004", LocationConstants.DOLLARD);
        		clientManager.getRecordCounts();
        	} else {
        		System.out.println("ERROR => Invalid Manager Id");
        	}
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            System.out.println("********************");
        }
    }
    
    private static boolean isValidManagerId(final String managerId) {
    	if(managerId == null || managerId.isEmpty()) {
    		return false;
    	}
    	if (!managerId.startsWith(LocationConstants.MONTREAL) &&
				!managerId.startsWith(LocationConstants.LAVAL) &&
				!managerId.startsWith(LocationConstants.DOLLARD)) {
    		return false;
    	}
    	if(managerId.substring(3, managerId.length()).length() != 4) {
    		return false;
    	}
    	if(!isInteger(managerId.substring(3, managerId.length()))) {
    		return false;
    	}
    	return true;
    }
    	
    private static boolean isInteger(final String str) {
    	try {
    		int num = Integer.parseInt(str);
    		return true;
    	} catch(NumberFormatException e) {
    		return false;
    	}
    }
}
