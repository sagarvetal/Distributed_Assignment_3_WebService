package com.dcms.model;

import java.io.Serializable;

import com.dcms.util.FieldConstants;

public class Teacher implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String recordId;
    private String firstName;
    private String lastName;
	private String address;
    private String phone;
    private String specialization;
    private String location;

    public Teacher(String recordId, String firstName, String lastName, String address, String phone, String specialization, String location) {
    	this.recordId = recordId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.phone = phone;
        this.location = location;
        this.specialization = specialization;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String toString() {
    	String record = FieldConstants.RECORD_ID + FieldConstants.FIELD_SEPARATOR_COLON + this.getRecordId();
    	record += FieldConstants.FIELD_SEPARATOR_PIPE + FieldConstants.FIRST_NAME + FieldConstants.FIELD_SEPARATOR_COLON + this.getFirstName();
    	record += FieldConstants.FIELD_SEPARATOR_PIPE + FieldConstants.LAST_NAME + FieldConstants.FIELD_SEPARATOR_COLON + this.getLastName();
    	record += FieldConstants.FIELD_SEPARATOR_PIPE + FieldConstants.ADDRESS + FieldConstants.FIELD_SEPARATOR_COLON + this.getAddress();
    	record += FieldConstants.FIELD_SEPARATOR_PIPE + FieldConstants.PHONE + FieldConstants.FIELD_SEPARATOR_COLON + this.getPhone();
    	record += FieldConstants.FIELD_SEPARATOR_PIPE + FieldConstants.SPECIALIZATION + FieldConstants.FIELD_SEPARATOR_COLON + this.getSpecialization();
    	record += FieldConstants.FIELD_SEPARATOR_PIPE + FieldConstants.LOCATION + FieldConstants.FIELD_SEPARATOR_COLON + this.getLocation();
    	return record;
    }

    public String getValues() {
		String record = this.getRecordId() + FieldConstants.FIELD_SEPARATOR_PIPE + this.getFirstName()
						+ FieldConstants.FIELD_SEPARATOR_PIPE + this.getLastName() + FieldConstants.FIELD_SEPARATOR_PIPE
						+ this.getAddress() + FieldConstants.FIELD_SEPARATOR_PIPE + this.getPhone() + FieldConstants.FIELD_SEPARATOR_PIPE
						+ this.getSpecialization() + FieldConstants.FIELD_SEPARATOR_PIPE + this.getLocation();
    	return record;
    }
}