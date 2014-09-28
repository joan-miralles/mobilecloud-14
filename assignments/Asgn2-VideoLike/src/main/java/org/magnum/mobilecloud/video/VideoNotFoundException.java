package org.magnum.mobilecloud.video;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND)
public class VideoNotFoundException extends VideoServiceException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8651185075820416399L;

	public VideoNotFoundException(String message) {
		super(message);
	}

}
