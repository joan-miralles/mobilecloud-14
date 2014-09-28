package org.magnum.mobilecloud.video;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.BAD_REQUEST)
public class VideoServiceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public VideoServiceException(String message) {
		super(message);
	}
	
}
