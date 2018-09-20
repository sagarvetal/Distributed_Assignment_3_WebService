package com.dcms.service;

import com.dcms.util.ErrorMessages;
import com.dcms.util.FieldConstants;
import com.dcms.util.LocationConstants;
import com.dcms.util.RecordTypeConstants;
import com.dcms.util.StatusConstants;

public class ValidationService {

	public static String validateStudentFields(final String firstName, final String lastName, final String coursesRegistered, final String status, final String statusDate) {
    	String fieldList = "";
    	if(!validateFirstName(firstName)) {
    		fieldList += fieldList.isEmpty() ? FieldConstants.FIRST_NAME : "," + FieldConstants.FIRST_NAME;
    	}

    	if(!validateLastName(lastName)) {
    		fieldList += fieldList.isEmpty() ? FieldConstants.LAST_NAME : "," + FieldConstants.LAST_NAME;
    	}

    	if(!validateCourses(coursesRegistered)) {
    		fieldList += fieldList.isEmpty() ? FieldConstants.COURSES : "," + FieldConstants.COURSES;
    	}

    	if(!validateStatus(status)) {
    		fieldList += fieldList.isEmpty() ? FieldConstants.STATUS : "," + FieldConstants.STATUS;
    	}

    	if(!validateStatusDate(statusDate)) {
    		fieldList += fieldList.isEmpty() ? FieldConstants.STATUS_DATE : "," + FieldConstants.STATUS_DATE;
    	}
    	
    	if(!fieldList.isEmpty()) {
    		fieldList = String.format(ErrorMessages.INVALID_VALUES, fieldList);
    	}
    	return fieldList;
    }

    public static String validateTeacherFields(final String firstName, final String lastName, final String address, final String phone, final String specialization, final String location) {
    	String fieldList = "";
    	
    	if(!validateFirstName(firstName)) {
    		fieldList += fieldList.isEmpty() ? FieldConstants.FIRST_NAME : "," + FieldConstants.FIRST_NAME;
    	}
    	
    	if(!validateLastName(lastName)) {
    		fieldList += fieldList.isEmpty() ? FieldConstants.LAST_NAME : "," + FieldConstants.LAST_NAME;
    	}
    	
    	if(!validateAddress(address)) {
    		fieldList += fieldList.isEmpty() ? FieldConstants.ADDRESS : "," + FieldConstants.ADDRESS;
    	}
    	
    	if(!validatePhone(phone)) {
    		fieldList += fieldList.isEmpty() ? FieldConstants.PHONE : "," + FieldConstants.PHONE;
    	}
    	
    	if(!validateSpecialization(specialization)) {
    		fieldList += fieldList.isEmpty() ? FieldConstants.SPECIALIZATION : "," + FieldConstants.SPECIALIZATION;
    	}
    	
    	if(!validateLocation(location)) {
    		fieldList += fieldList.isEmpty() ? FieldConstants.LOCATION : "," + FieldConstants.LOCATION;
    	}
    	
    	if(!fieldList.isEmpty()) {
    		fieldList = String.format(ErrorMessages.INVALID_VALUES, fieldList);
    	}
    	
    	return fieldList;
    }
    
    public static String validateEditFields(final String recordId, final String fieldName, final String newValue) {
    	String message = "";
    	if(recordId.startsWith(RecordTypeConstants.STUDENT_RECORD)) {
    		switch (fieldName.toLowerCase()) {
	            case "status":
	    			if(!validateStatus(newValue)) {
	    				message = String.format(ErrorMessages.INVALID_VALUES, FieldConstants.STATUS);
	    			} 
	                break;
	            case "courses registered":
	            	if(!validateCourses(newValue) || newValue.replace(",", "").trim().isEmpty() || newValue.trim().split(",").length == 0) {
	            		message = String.format(ErrorMessages.INVALID_VALUES, FieldConstants.COURSES);
	            	} 
	                break;
	            default:
	            	message = String.format(ErrorMessages.FIELD_NOT_FOUND, fieldName);
	        }
    	} else if(recordId.startsWith(RecordTypeConstants.TEACHER_RECORD)) {
    		switch (fieldName.toLowerCase()) {
	            case "address":
	    			if(!validateAddress(newValue)) {
	    				message = String.format(ErrorMessages.INVALID_VALUES, FieldConstants.ADDRESS);
	    			} 
	                break;
	            case "phone":
	            	if(!validatePhone(newValue)) {
	            		message = String.format(ErrorMessages.INVALID_VALUES, FieldConstants.PHONE);
	            	} 
	                break;
	            case "location":
	            	if(!validateLocation(newValue)) {
	            		message = String.format(ErrorMessages.INVALID_VALUES, FieldConstants.LOCATION);
	            	} 
	            	break;
	            default:
	            	message = String.format(ErrorMessages.FIELD_NOT_FOUND, fieldName);
	        }
    	}
    	return message;
    }
    
    private static boolean validateFirstName(final String firstName) {
    	boolean isValid = false;
    	if (firstName.matches("[a-zA-Z]+")) {
    		isValid = true;
    	}
    	return isValid;
    }
    
    private static boolean validateLastName(final String lastName) {
    	boolean isValid = false;
    	if (lastName.matches("[a-zA-Z]+")) {
    		isValid = true;
    	}
    	return isValid;
    }
    
    private static boolean validateAddress(final String address) {
    	boolean isValid = false;
    	if(null != address && !address.isEmpty()) {
			isValid = true;
		}
    	return isValid;
    }
    
    private static boolean validatePhone(final String phone) {
    	boolean isValid = false;
    	if (phone.matches("[0-9]+") && phone.length()==10) {
    		isValid = true;
    	}
    	return isValid;
    }
    
    private static boolean validateSpecialization(final String specialization) {
    	boolean isValid = false;
    	if (specialization.matches("[a-zA-Z]+")) {
    		isValid = true;
    	}
    	return isValid;
    }

    private static boolean validateLocation(final String location) {
    	switch (location.toUpperCase()) {
			case LocationConstants.MONTREAL:
			case LocationConstants.LAVAL:
			case LocationConstants.DOLLARD:
				return true;
			default:
				return false;
		}
    }
    
    private static boolean validateStatusDate(final String statusDate) {
    	boolean isValid = false;
    	if (statusDate.matches("\\d{2}-\\d{2}-\\d{4}")) {
    		isValid = true;
    	}
    	return isValid;
    }
    
    private static boolean validateStatus(final String status) {
    	switch (status.toUpperCase()) {
			case StatusConstants.ACTIVE:
			case StatusConstants.INACTIVE:
				return true;
			default:
				return false;
		}
    }
    
    private static boolean validateCourses(final String courses) {
    	boolean isValid = false;
    	if (courses.matches("^[A-Za-z0-9, _]*[A-Za-z0-9,][A-Za-z0-9, _]*$")) {
    		isValid = true;
    	}
    	return isValid;
    }
    
}
