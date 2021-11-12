package com.promineotech.jeep.errorhandler;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import lombok.extern.slf4j.Slf4j;



@RestControllerAdvice
@Slf4j
public class GlobalErrorHandler {
	
	private enum LogStatus{
		STACK_TRACE, MESSAGE_ONLY
	};
	
	@ExceptionHandler(NoSuchElementException.class)
	@ResponseStatus(code = HttpStatus.NOT_FOUND)
	public Map<String, Object> handelNoSuchElementException(NoSuchElementException e, WebRequest webrequest) {
		return createExceptionMessage(e, HttpStatus.NOT_FOUND, webrequest, LogStatus.MESSAGE_ONLY);
	}
	
	@ExceptionHandler(ConstraintViolationException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	
	public Map<String, Object> handleConstraintViolationException(ConstraintViolationException e, WebRequest webrequest){
		return createExceptionMessage(e, HttpStatus.BAD_REQUEST, webrequest, LogStatus.MESSAGE_ONLY);
	}
	
	
	@ExceptionHandler(Exception.class)
	@ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
	public Map<String, Object> handleException(Exception e, WebRequest webrequest){
		return createExceptionMessage(e, HttpStatus.INTERNAL_SERVER_ERROR, webrequest, LogStatus.STACK_TRACE);
	}
	
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	public Map<String, Object> handeMethodArgumentTypeMismatchException( MethodArgumentTypeMismatchException e, WebRequest webrequest) {
		return createExceptionMessage(e, HttpStatus.BAD_REQUEST, webrequest, LogStatus.MESSAGE_ONLY);
	}



	private Map<String, Object> createExceptionMessage(Exception e, HttpStatus status, WebRequest webrequest, LogStatus logstatus) {
		Map<String, Object> error = new HashMap<>();
		String timeStamp = ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME);
		if(webrequest instanceof ServletWebRequest) {
			error.put("uri", (((ServletWebRequest) webrequest).getRequest().getRequestURI()));
		}
		error.put("message", e.toString());
		error.put("Status Code", status.value());
		;
		error.put("timestamp", timeStamp);
		error.put("reason", status.getReasonPhrase());
		
		if (logstatus == LogStatus.MESSAGE_ONLY) {
			log.error("Exception: {}", e.toString());
				
			}
		else {
			log.error("Exception: ", e);
		}
		return error;
	};
	
	
}
