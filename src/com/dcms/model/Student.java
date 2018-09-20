package com.dcms.model;

import java.io.Serializable;
import java.util.HashSet;

import com.dcms.util.FieldConstants;

public class Student implements Serializable {

    private static final long serialVersionUID = 1L;
	
    private String recordId;
    private String firstName;
    private String lastName;
	private HashSet<String> coursesRegistered;
    private String status;
    private String statusDate; 
    //private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Student(final String recordId, final String firstName, final String lastName, final HashSet<String> courseRegistered, final String status, final String statusDate) {
    	this.recordId = recordId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.coursesRegistered = courseRegistered;
        this.status = status;
        this.statusDate = statusDate;
    }
    
    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public HashSet<String> getCoursesRegistered() {
        return this.coursesRegistered;
    }

    public void setCoursesRegistered(HashSet<String> coursesRegistered) {
        this.coursesRegistered = coursesRegistered;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusDate() {
        return this.statusDate;
    }

    public void setStatusDate(String statusDate) {
        this.statusDate = statusDate;
    }

    public String toString() {
    	String record = FieldConstants.RECORD_ID + FieldConstants.FIELD_SEPARATOR_COLON + this.getRecordId();
    	record += FieldConstants.FIELD_SEPARATOR_PIPE +  FieldConstants.FIRST_NAME + FieldConstants.FIELD_SEPARATOR_COLON + this.getFirstName();
    	record += FieldConstants.FIELD_SEPARATOR_PIPE + FieldConstants.LAST_NAME + FieldConstants.FIELD_SEPARATOR_COLON + this.getLastName();
    	record += FieldConstants.FIELD_SEPARATOR_PIPE + FieldConstants.COURSES + FieldConstants.FIELD_SEPARATOR_COLON + this.getCoursesRegistered();
    	record += FieldConstants.FIELD_SEPARATOR_PIPE + FieldConstants.STATUS + FieldConstants.FIELD_SEPARATOR_COLON + this.getStatus();
    	record += FieldConstants.FIELD_SEPARATOR_PIPE + FieldConstants.STATUS_DATE + FieldConstants.FIELD_SEPARATOR_COLON + this.getStatusDate();
    	return record;
    }

    public String getValues() {
		String record = this.getRecordId() + FieldConstants.FIELD_SEPARATOR_PIPE + this.getFirstName()
						+ FieldConstants.FIELD_SEPARATOR_PIPE + this.getLastName() + FieldConstants.FIELD_SEPARATOR_PIPE
						+ this.getCoursesRegistered() + FieldConstants.FIELD_SEPARATOR_PIPE + this.getStatus()
						+ FieldConstants.FIELD_SEPARATOR_PIPE + this.getStatusDate();
    	return record;
    }

}