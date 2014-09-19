package org.magnum.dataup;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.NOT_FOUND)
public class VideoNotFoundException extends Exception {

	public VideoNotFoundException(String reason) {
		super(reason);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
