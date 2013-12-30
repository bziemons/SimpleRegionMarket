
package com.thezorro266.bukkit.srm.exceptions;

public class ContentLoadException extends Exception {

	private static final long serialVersionUID = 7261993650328186824L;

	public ContentLoadException(String message) {
		super(message);
	}

	public ContentLoadException(String message, Throwable cause) {
		super(message, cause);
	}
}
