package com.dcms.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

import com.dcms.interfaces.CenterServer;
import com.dcms.model.Student;
import com.dcms.model.Teacher;
import com.dcms.service.ActivityLoggerService;
import com.dcms.service.CounterService;
import com.dcms.service.ValidationService;
import com.dcms.util.ActionConstants;
import com.dcms.util.ErrorMessages;
import com.dcms.util.FieldConstants;
import com.dcms.util.FileConstants;
import com.dcms.util.LocationConstants;
import com.dcms.util.MessageTypeConstants;
import com.dcms.util.PortConstants;
import com.dcms.util.RecordTypeConstants;
import com.dcms.util.SuccessMessages;
import com.dcms.util.UdpServerMessages;

/*
 * @author Sagar Vetal
 * @Date 05/07/2018
 * @version 1
 * 
 * This is an implementation class of CenterServer interface.
 */

@WebService(endpointInterface = "com.dcms.interfaces.CenterServer")
@SOAPBinding(style = Style.RPC)
public class CenterServerImpl implements CenterServer {

	private String serverName;
	private List<String> locationList = Arrays.asList(LocationConstants.MONTREAL, LocationConstants.LAVAL, LocationConstants.DOLLARD);
    private HashMap<String, List<Object>> hmRecords;
    private ActivityLoggerService activityLogger;
    private ConcurrentHashMap<String, String> currentTransferringRecordIds;

    public CenterServerImpl(final String serverName) throws IOException {
        super();
        this.serverName = serverName;
        this.hmRecords = new HashMap<String, List<Object>>();
        this.activityLogger = new ActivityLoggerService(FileConstants.SERVER_LOG_FILE_PATH + serverName + "/" + FileConstants.ACTIVITY_LOG);
        this.currentTransferringRecordIds = new ConcurrentHashMap<String, String>();
    }

    public String getServerName() {
    	return serverName;
    }
    
    @WebMethod
    public String createTRecord(final String firstName, final String lastName, final String address, final String phone, final String specialization, final String location, final String managerId) {
        String message = "";
    	try {
            message = ValidationService.validateTeacherFields(firstName, lastName, address, phone, specialization, location);
            
            if(message.isEmpty()) {
            	final Optional<String> recordId = getIdCounter(RecordTypeConstants.TEACHER_RECORD); 
            	if(recordId.isPresent()) {
            		final Teacher teacher = new Teacher(recordId.get(), firstName, lastName, address, phone, specialization, location);
            		final String key = Character.toString(lastName.charAt(0)).toUpperCase();
            		message = saveRecord(key, teacher, managerId, recordId.get());
            	} else {
            		activityLogger.log(managerId, ActionConstants.ADD_TR, ErrorMessages.RECORD_ID_GENERATION_FAILED);
            		message = false + FieldConstants.FIELD_SEPARATOR_ARROW + ErrorMessages.RECORD_ID_GENERATION_FAILED;
            	}
            } else {
            	activityLogger.log(managerId, ActionConstants.ADD_TR, message);
            	message = false + FieldConstants.FIELD_SEPARATOR_ARROW + message;
            }
        } catch (Exception e) {
            activityLogger.log(managerId, ActionConstants.ADD_TR, ErrorMessages.RECORD_CREATION_FAILED, e.getMessage());
            message = false + FieldConstants.FIELD_SEPARATOR_ARROW + String.format(ErrorMessages.RECORD_CREATION_FAILED, e.getMessage());
        }
        return message;
    }

    @WebMethod
    public String createSRecord(final String firstName, final String lastName, final String coursesRegistered, final String status, final String statusDate, final String managerId) {
        String message = "";
    	try {
            message = ValidationService.validateStudentFields(firstName, lastName, coursesRegistered, status, statusDate);
            
            if(message.isEmpty()) {
        		final Optional<String> recordId = getIdCounter(RecordTypeConstants.STUDENT_RECORD); 
        		if(recordId.isPresent()) {
        			final Student student = new Student(recordId.get(), firstName, lastName, getCourses(coursesRegistered), status, statusDate);
        			final String key = Character.toString(lastName.charAt(0)).toUpperCase();
        			message = saveRecord(key, student, managerId, recordId.get());
        		} else {
        			activityLogger.log(managerId, ActionConstants.ADD_SR, ErrorMessages.RECORD_ID_GENERATION_FAILED);
        			message = false + FieldConstants.FIELD_SEPARATOR_ARROW + ErrorMessages.RECORD_ID_GENERATION_FAILED;
        		}
            } else {
            	activityLogger.log(managerId, ActionConstants.ADD_SR, message);
            	message = false + FieldConstants.FIELD_SEPARATOR_ARROW + message;
            }
        } catch (Exception e) {
            activityLogger.log(managerId, ActionConstants.ADD_SR, ErrorMessages.RECORD_CREATION_FAILED, e.getMessage());
            message = false + FieldConstants.FIELD_SEPARATOR_ARROW + String.format(ErrorMessages.RECORD_CREATION_FAILED, e.getMessage());
        }
        return message;
    }

    private Optional<String> getIdCounter(final String recordType) throws ClassNotFoundException {
    	Optional<String> recordId = Optional.empty();
    	switch(recordType) {
	    	case RecordTypeConstants.STUDENT_RECORD:
	    		recordId = Optional.of(RecordTypeConstants.STUDENT_RECORD + String.format("%05d", CounterService.getCounter(FileConstants.STUDENT_COUNTER_FILE_PATH)));
	    		break;
	    	case RecordTypeConstants.TEACHER_RECORD:
	    		recordId = Optional.of(RecordTypeConstants.TEACHER_RECORD + String.format("%05d", CounterService.getCounter(FileConstants.TEACHER_COUNTER_FILE_PATH)));
	    		break;
	    	default:
                break;	
    	}
    	return recordId;
    }
    
    private String saveRecord(final String key, final Object record, final String managerId, final String recordId) {
    	String message = "";
    	final String action = recordId.startsWith(RecordTypeConstants.STUDENT_RECORD) ? ActionConstants.ADD_SR : ActionConstants.ADD_TR;
    	try {
    		synchronized (hmRecords) {
    			if (hmRecords.get(key) != null) {
    				hmRecords.get(key).add(record);
    			} else {
    				final List<Object> records = new ArrayList<>();
    				records.add(record);
    				hmRecords.put(key, records);
    			}
			}
    		activityLogger.log(managerId, action, record);
    		message = true + FieldConstants.FIELD_SEPARATOR_ARROW + String.format(SuccessMessages.RECORD_CREATED, recordId);
    	} catch (Exception e) {
    		activityLogger.log(managerId, action, ErrorMessages.RECORD_CREATION_FAILED, e.getMessage());
            message = false + FieldConstants.FIELD_SEPARATOR_ARROW + String.format(ErrorMessages.RECORD_CREATION_FAILED, e.getMessage());
    	}
    	return message;
    }

    private HashSet<String> getCourses(final String coursesRegistered){
    	final HashSet<String> courses = new HashSet<>();
    	final String[] courseList = coursesRegistered.split(",");
    	for(final String course : courseList) {
    		if(!course.isEmpty()) {
    			courses.add(course.trim());
    		}
    	}
    	return courses;
    }
    
    @WebMethod
    public String editRecord(final String recordId, final String fieldName, final String newValue, final String managerId) {
    	String message = "";
    	try {
    		message = ValidationService.validateEditFields(recordId, fieldName, newValue);
    		
    		if(message.isEmpty()) {
    			synchronized (this) {
    				final Object record = searchRecord(recordId);
    				if(record != null) {
    					message = record instanceof Student ? 
    								editStudent(recordId, fieldName, newValue, (Student)record) : 
    								  editTeacher(recordId, fieldName, newValue, (Teacher)record);
    					activityLogger.log(managerId, ActionConstants.EDIT, message);
    					message = true + FieldConstants.FIELD_SEPARATOR_ARROW + message;
    				} else {
    					activityLogger.log(managerId, ActionConstants.EDIT, ErrorMessages.RECORD_NOT_FOUND, recordId);
    					message = false + FieldConstants.FIELD_SEPARATOR_ARROW + String.format(ErrorMessages.RECORD_NOT_FOUND, recordId);
    				}
    			}
    		} else {
    			activityLogger.log(managerId, ActionConstants.EDIT, message);
    			message = false + FieldConstants.FIELD_SEPARATOR_ARROW + message;
    		}
        } catch (Exception e) {
            activityLogger.log(managerId, ActionConstants.EDIT, ErrorMessages.RECORD_EDIT_FAILED, e.getMessage());
            message = false + FieldConstants.FIELD_SEPARATOR_ARROW + String.format(ErrorMessages.RECORD_EDIT_FAILED, e.getMessage());
        }
    	return message;
    }
    
    private String editStudent(final String recordId, final String fieldName, final String newValue, final Student student) {
    	String message = "";
    	switch (fieldName.toLowerCase()) {
            case "status":
				message += recordId + FieldConstants.FIELD_SEPARATOR_PIPE + FieldConstants.STATUS + FieldConstants.FIELD_SEPARATOR_PIPE + 
						   FieldConstants.FIELD_OLD_VALUE + FieldConstants.FIELD_SEPARATOR_COLON + student.getStatus() + FieldConstants.FIELD_SEPARATOR_PIPE +
						   FieldConstants.FIELD_NEW_VALUE + FieldConstants.FIELD_SEPARATOR_COLON + newValue;
				student.setStatus(newValue);
				student.setStatusDate(new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
                break;
            case "courses registered":
            	message += recordId + FieldConstants.FIELD_SEPARATOR_PIPE + FieldConstants.COURSES + FieldConstants.FIELD_SEPARATOR_PIPE + 
            			   FieldConstants.FIELD_OLD_VALUE + FieldConstants.FIELD_SEPARATOR_COLON + student.getCoursesRegistered() + FieldConstants.FIELD_SEPARATOR_PIPE +
            			   FieldConstants.FIELD_NEW_VALUE + FieldConstants.FIELD_SEPARATOR_COLON + newValue;
                student.setCoursesRegistered(getCourses(newValue));
                break;
            default:
            	message = String.format(ErrorMessages.FIELD_NOT_FOUND, fieldName);
                break;
        }
    	return message;
    }
    
    private String editTeacher(final String recordId, final String fieldName, final String newValue, final Teacher teacher) {
    	String message = "";
    	switch (fieldName.toLowerCase()) {
            case "address":
            	message += recordId + FieldConstants.FIELD_SEPARATOR_PIPE + FieldConstants.ADDRESS + FieldConstants.FIELD_SEPARATOR_PIPE + 
						   FieldConstants.FIELD_OLD_VALUE + FieldConstants.FIELD_SEPARATOR_COLON + teacher.getAddress() + FieldConstants.FIELD_SEPARATOR_PIPE +
						   FieldConstants.FIELD_NEW_VALUE + FieldConstants.FIELD_SEPARATOR_COLON + newValue;
            	teacher.setAddress(newValue);
                break;
            case "phone":
            	message += recordId + FieldConstants.FIELD_SEPARATOR_PIPE + FieldConstants.PHONE + FieldConstants.FIELD_SEPARATOR_PIPE + 
            			   FieldConstants.FIELD_OLD_VALUE + FieldConstants.FIELD_SEPARATOR_COLON + teacher.getPhone() + FieldConstants.FIELD_SEPARATOR_PIPE +
            			   FieldConstants.FIELD_NEW_VALUE + FieldConstants.FIELD_SEPARATOR_COLON + newValue;
            	teacher.setPhone(newValue);
                break;
            case "location":
            		message += recordId + FieldConstants.FIELD_SEPARATOR_PIPE + FieldConstants.LOCATION + FieldConstants.FIELD_SEPARATOR_PIPE + 
            				   FieldConstants.FIELD_OLD_VALUE + FieldConstants.FIELD_SEPARATOR_COLON + teacher.getLocation() + FieldConstants.FIELD_SEPARATOR_PIPE +
            				   FieldConstants.FIELD_NEW_VALUE + FieldConstants.FIELD_SEPARATOR_COLON + newValue;
            		teacher.setLocation(newValue);
                break;
            default:
            	message = String.format(ErrorMessages.FIELD_NOT_FOUND, fieldName);
            	break;
        }
    	return message;
    }

    @WebMethod
    public String getRecordCounts(final String managerId) {
    	String recordCount = null;
    	final ArrayList<String> otherRecordCounts = new ArrayList<>();
    	final CountDownLatch latch = new CountDownLatch(locationList.size()-1);
    	
        for (final String location : locationList) {
            if (location.equalsIgnoreCase(serverName)) {
                recordCount = serverName + ": " + this.getRecordCount();
            } else {
            	new Thread(() -> {
            		final String count = sendRecordCountRequest(location, managerId);
            		if(count != null)
            			otherRecordCounts.add(count);
        			latch.countDown();
            	}).start();
            }
        }
        try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        
        for(final String rdCount : otherRecordCounts) {
        	if(!recordCount.isEmpty()) {
        		recordCount += ", ";
        	} 
        	recordCount += rdCount.trim();
        }
		activityLogger.log(managerId, ActionConstants.GET_COUNT, recordCount);
		recordCount = true + FieldConstants.FIELD_SEPARATOR_ARROW + String.format(SuccessMessages.RECORD_COUNT, recordCount);
        return recordCount;
    }

    private String sendRecordCountRequest(final String location, final String managerId) {
    	String recordCount = null;
    	try {
			final InetAddress inetAddress = InetAddress.getByName("localhost");
			final int udpPort = PortConstants.getUdpPort(location);
			activityLogger.log(MessageTypeConstants.INFO, String.format(UdpServerMessages.UDP_REQUEST_SENT, ActionConstants.GET_COUNT, location));
			
			final DatagramSocket socket = new DatagramSocket();
			byte[] data = (serverName + FieldConstants.FIELD_SEPARATOR_ARROW + ActionConstants.GET_COUNT).toString().getBytes();
			final DatagramPacket packet = new DatagramPacket(data, data.length, inetAddress, udpPort);
			socket.send(packet);
			
			data = new byte[1000];
			socket.receive(new DatagramPacket(data, data.length));
			recordCount = location + ": " + new String(data);
			activityLogger.log(MessageTypeConstants.INFO, String.format(UdpServerMessages.UDP_RESPONSE_RECEiVED, ActionConstants.GET_COUNT, location));
			socket.close();
		} catch (Exception e) {
			activityLogger.log(MessageTypeConstants.ERROR, e.getMessage());
		}
    	return recordCount;
    }
    
    public synchronized Integer getRecordCount() {
    	return hmRecords.values().stream().mapToInt(List::size).sum();
    }
    
    @WebMethod
    public String displayRecord(final String recordId, final String managerId) {
    	String message = "";
        try {
            final Object record = this.searchRecord(recordId);
            if(record != null) {
            	activityLogger.log(managerId, ActionConstants.GET_RECORD, record.toString());
            	message = true + FieldConstants.FIELD_SEPARATOR_ARROW + record.toString();
            } else {
            	activityLogger.log(managerId, ActionConstants.GET_RECORD, ErrorMessages.RECORD_NOT_FOUND, recordId);
            	message = false + FieldConstants.FIELD_SEPARATOR_ARROW + String.format(ErrorMessages.RECORD_NOT_FOUND, recordId);
            }
        } catch (Exception e) {
        	activityLogger.log(managerId, ActionConstants.GET_RECORD, ErrorMessages.RECORD_NOT_FOUND, recordId);
        	message = false + FieldConstants.FIELD_SEPARATOR_ARROW + String.format(ErrorMessages.RECORD_NOT_FOUND, recordId);
        }
        return message;
    }

    private synchronized Object searchRecord(final String recordId) {
        for (final Map.Entry<String, List<Object>> entry : hmRecords.entrySet()) {
            final List<Object> recordList = entry.getValue();
            for (final Object record : recordList) {
				if(record instanceof Student) {
					if(recordId.equalsIgnoreCase(((Student) record).getRecordId())) {
						return record;
					}
				} else if(record instanceof Teacher) {
					if(recordId.equalsIgnoreCase(((Teacher) record).getRecordId())) {
						return record;
					}
				}
			}
        }
		return null;
    }

    @WebMethod
	public String transferRecord(final String managerId, final String recordId , final String remoteCenterServerName) {
		String message = "";
		try {
			if(!locationList.contains(remoteCenterServerName)) {
				activityLogger.log(managerId, ActionConstants.TRANSFER_RECORD, ErrorMessages.INVALID_REMOTE_SERVER, remoteCenterServerName, recordId);
	        	message = false + FieldConstants.FIELD_SEPARATOR_ARROW + String.format(ErrorMessages.INVALID_REMOTE_SERVER, remoteCenterServerName, recordId);
	        	return message;
			}
			
			if(serverName.equalsIgnoreCase(remoteCenterServerName)) {
				activityLogger.log(managerId, ActionConstants.TRANSFER_RECORD, ErrorMessages.SAME_REMOTE_SERVER, remoteCenterServerName, serverName, recordId);
	        	message = false + FieldConstants.FIELD_SEPARATOR_ARROW + String.format(ErrorMessages.SAME_REMOTE_SERVER, remoteCenterServerName, serverName, recordId);
	        	return message;
			}
			
			final Object record = this.searchRecord(recordId);
            if(record != null) {
            	if(currentTransferringRecordIds.containsKey(recordId)) {
            		message = String.format(ErrorMessages.RECORD_TRANSFER_IN_PROGRESS, recordId, currentTransferringRecordIds.get(recordId));
            		activityLogger.log(managerId, ActionConstants.TRANSFER_RECORD, message);
    	        	message = false + FieldConstants.FIELD_SEPARATOR_ARROW + message;
    	        	return message;
            	} else {
            		currentTransferringRecordIds.put(recordId, managerId);
            		final String recordValues = record instanceof Student ? ((Student)record).getValues() : ((Teacher)record).getValues();
            		final String requestData = serverName + FieldConstants.FIELD_SEPARATOR_ARROW + ActionConstants.TRANSFER_RECORD + 
            				FieldConstants.FIELD_SEPARATOR_ARROW + managerId + FieldConstants.FIELD_SEPARATOR_ARROW + recordValues;
            		final ArrayList<String> transferResults = new ArrayList<>();
            		final CountDownLatch latch = new CountDownLatch(1);
            		
            		new Thread(() -> {
            			final String result = sendTransferRecordRequest(remoteCenterServerName, managerId, requestData);
            			transferResults.add(result);
            			latch.countDown();	// Release await() in the thread.
            		}).start();
            		
            		try {
            			latch.await();
            		} catch (InterruptedException e) {
            			e.printStackTrace();
            		}
            		
            		message = transferResults.get(0);
            		
            		if(Boolean.valueOf(message.split(FieldConstants.FIELD_SEPARATOR_ARROW)[0])) {
            			deleteRecord(record);
            			currentTransferringRecordIds.remove(recordId);
            			activityLogger.log(managerId, ActionConstants.TRANSFER_RECORD, SuccessMessages.RECORD_TRANSFERRED, recordId);
            			message = true + FieldConstants.FIELD_SEPARATOR_ARROW + String.format(SuccessMessages.RECORD_TRANSFERRED, recordId);
            		} 
            	}
            	
            } else {
            	activityLogger.log(managerId, ActionConstants.TRANSFER_RECORD, ErrorMessages.RECORD_NOT_FOUND, recordId);
            	message = false + FieldConstants.FIELD_SEPARATOR_ARROW + String.format(ErrorMessages.RECORD_NOT_FOUND, recordId);
            }
		} catch (Exception e) {
        	activityLogger.log(managerId, ActionConstants.TRANSFER_RECORD, ErrorMessages.RECORD_TRANSFER_FAILED, e.getMessage());
        	message = false + FieldConstants.FIELD_SEPARATOR_ARROW + String.format(ErrorMessages.RECORD_TRANSFER_FAILED, e.getMessage());
        }
		return message;
	}
	
	private String sendTransferRecordRequest(final String location, final String managerId, final String data) {
		try {
			final InetAddress inetAddress = InetAddress.getByName("localhost");
			final int udpPort = PortConstants.getUdpPort(location);
			activityLogger.log(MessageTypeConstants.INFO, String.format(UdpServerMessages.UDP_REQUEST_SENT, ActionConstants.TRANSFER_RECORD, location));
			
			final DatagramSocket socket = new DatagramSocket();
			final DatagramPacket packet = new DatagramPacket(data.toString().getBytes(), data.toString().getBytes().length, inetAddress, udpPort);
			socket.send(packet);
			
			byte[] response = new byte[1000];
			socket.receive(new DatagramPacket(response, response.length));
			socket.close();

			activityLogger.log(MessageTypeConstants.INFO, String.format(UdpServerMessages.UDP_RESPONSE_RECEiVED, ActionConstants.TRANSFER_RECORD, location));
			return new String(response);
		} catch (IOException e) {
			activityLogger.log(MessageTypeConstants.ERROR, String.format(ErrorMessages.RECORD_TRANSFER_FAILED, e.getMessage()));
			return false + FieldConstants.FIELD_SEPARATOR_ARROW + String.format(ErrorMessages.RECORD_TRANSFER_FAILED, e.getMessage());
		}
	}

	private void deleteRecord(final Object record) {
        final String key = Character.toString(record instanceof Student ? ((Student)record).getLastName().charAt(0) : ((Teacher)record).getLastName().charAt(0));
        synchronized (hmRecords) {
        	final List<Object> recordList = this.hmRecords.get(key.toUpperCase());
        	if (recordList != null) {
        		if(recordList.size() > 0) {
        			recordList.remove(record);
        			if(recordList.size() == 0) {
        				this.hmRecords.remove(key);
        			}
        		}
        	}
		}
    }
	
	public String transferRecord(final String record, final String managerId) {
		final String[] recordDetails = record.split(FieldConstants.FIELD_SEPARATOR_PIPE);
		if(recordDetails[0].trim().startsWith(RecordTypeConstants.STUDENT_RECORD)) {
			final HashSet<String> courses = getCourses(recordDetails[3].replace("[", "").replace("]", "").trim());
			final Student student = new Student(recordDetails[0].trim(), recordDetails[1].trim(), recordDetails[2].trim(), courses, recordDetails[4].trim(), recordDetails[5].trim());
			return saveRecord(Character.toString(student.getLastName().charAt(0)).toUpperCase(), student, managerId, student.getRecordId());
		} else {
			final Teacher teacher = new Teacher(recordDetails[0].trim(), recordDetails[1].trim(), recordDetails[2].trim(), recordDetails[3].trim(), recordDetails[4].trim(), recordDetails[5].trim(), recordDetails[6].trim());
			return saveRecord(Character.toString(teacher.getLastName().charAt(0)).toUpperCase(), teacher, managerId, teacher.getRecordId());
		}
	}
}