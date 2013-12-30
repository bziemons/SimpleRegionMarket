
package com.thezorro266.bukkit.srm.exceptions;

public class ThisShouldNeverHappenException extends RuntimeException {

	private static final long serialVersionUID = -5134174393398186655L;

	public ThisShouldNeverHappenException() {
		super();
	}

	public ThisShouldNeverHappenException(String message) {
		super(message);
	}
}
