package com.dcms.interfaces;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

@WebService
@SOAPBinding(style = Style.RPC)
public interface CenterServer {

	String createTRecord(final String firstName, final String lastName, final String address, final String phone, final String specialization, final String location, final String managerId);
	String createSRecord(final String firstName, final String lastName, final String coursesRegistered, final String status, final String statusDate, final String managerId);
	String editRecord(final String recordId, final String fieldName, final String newValue, final String managerId);
	String getRecordCounts(final String managerId);
	String displayRecord(final String recordId, final String managerId);
	String transferRecord(final String managerId, final String recordId, final String remoteCenterServerName);
}
