package com.dcms.client;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import com.dcms.interfaces.CenterServer;
import com.dcms.service.ActivityLoggerService;
import com.dcms.util.ActionConstants;
import com.dcms.util.ErrorMessages;
import com.dcms.util.FieldConstants;
import com.dcms.util.FileConstants;
import com.dcms.util.LocationConstants;
import com.dcms.util.MessageTypeConstants;

public class ClientManager {

    private String managerId;
    private String serverLocation = "";
    private CenterServer server;
    private ActivityLoggerService activityLogger;
    private URL wsdlUrl;
    private Service service;

    public ClientManager(final String managerId) throws Exception {
        this.managerId = managerId;
        this.server = findServerInstance(managerId);
        this.activityLogger = new ActivityLoggerService(FileConstants.CLIENT_LOG_FILE_PATH + this.managerId + FileConstants.FILE_TYPE);
    }

    private CenterServer findServerInstance(final String managerId) throws Exception {
    	final QName qName = new QName("http://server.dcms.com/", "CenterServerImplService");
    	if(managerId.startsWith(LocationConstants.MONTREAL)){
        	serverLocation = LocationConstants.MONTREAL_DESC + " " + LocationConstants.SERVER;
        	wsdlUrl = new URL(LocationConstants.MONTREAL_WSDL_URL);
        } else if(managerId.startsWith(LocationConstants.LAVAL)){
        	serverLocation = LocationConstants.LAVAL_DESC + " " + LocationConstants.SERVER;
        	wsdlUrl = new URL(LocationConstants.LAVAL_WSDL_URL);
        } else if(managerId.startsWith(LocationConstants.DOLLARD)){
        	serverLocation = LocationConstants.DOLLARD_DESC + " " + LocationConstants.SERVER;
        	wsdlUrl = new URL(LocationConstants.DOLLARD_WSDL_URL);
        }
    	service = Service.create(wsdlUrl, qName);
    	return service.getPort(CenterServer.class);
    }
    
    public String getServerName() {
    	return serverLocation;
    }

    public void createTRecord(final String firstName, final String lastName, final String address, final String phone, final String specialization, final String location){
        try {
        	final String message = server.createTRecord(firstName, lastName, address, phone, specialization, location, managerId);
        	showMessage(message, ActionConstants.ADD_TR);
        } catch (Exception e) {
            activityLogger.log(MessageTypeConstants.ERROR, String.format(ErrorMessages.RECORD_CREATION_FAILED, e.getMessage()));
        }
    }

    public void createSRecord(final String firstName, final String lastName, final String coursesRegistered, final String status, final String statusDate){
        try {
        	final String message = server.createSRecord(firstName, lastName, coursesRegistered, status, statusDate, managerId);
        	showMessage(message, ActionConstants.ADD_SR);
        } catch (Exception e) {
            activityLogger.log(MessageTypeConstants.ERROR, String.format(ErrorMessages.RECORD_CREATION_FAILED, e.getMessage()));
        }
    }

    public void editRecord(final String recordID, final String fieldName, final String newValue) {
        try {
        	final String message = server.editRecord(recordID, fieldName, newValue, managerId);
        	showMessage(message, ActionConstants.EDIT);
        } catch (Exception e) {
            activityLogger.log(MessageTypeConstants.ERROR, String.format(ErrorMessages.RECORD_EDIT_FAILED, e.getMessage()));
        }
    }

    public void getRecordCounts() {
    	try {
    		final String message = server.getRecordCounts(managerId);
    		showMessage(message, ActionConstants.GET_COUNT);
    	} catch (Exception e) {
    		activityLogger.log(MessageTypeConstants.ERROR, String.format(ErrorMessages.RECORD_COUNT_FAILED, e.getMessage()));
    	}
    }

    public void displayRecord(final String recordId) {
        try {
        	final String message = server.displayRecord(recordId, managerId);
        	showMessage(message, ActionConstants.GET_RECORD);
        }  catch (Exception e) {
            activityLogger.log(MessageTypeConstants.ERROR, String.format(ErrorMessages.RECORD_NOT_FOUND, e.getMessage()));
        }
    }


    public void transferRecord(final String recordId , final String remoteCenterServerName) {
    	try {
    		final String message = server.transferRecord(managerId, recordId, remoteCenterServerName);
    		showMessage(message, ActionConstants.TRANSFER_RECORD);
    	}  catch (Exception e) {
    		activityLogger.log(MessageTypeConstants.ERROR, String.format(ErrorMessages.RECORD_TRANSFER_FAILED, e.getMessage()));
    	}
    }

    private void showMessage(final String message, final String action) {
    	if(Boolean.valueOf(message.split(FieldConstants.FIELD_SEPARATOR_ARROW)[0])) {
    		activityLogger.log(managerId, action, MessageTypeConstants.INFO + FieldConstants.FIELD_SEPARATOR_ARROW + message.split(FieldConstants.FIELD_SEPARATOR_ARROW)[1].trim());
    	}else {
    		activityLogger.log(managerId, action, MessageTypeConstants.ERROR + FieldConstants.FIELD_SEPARATOR_ARROW + message.split(FieldConstants.FIELD_SEPARATOR_ARROW)[1].trim());
    	}
    }
    
}