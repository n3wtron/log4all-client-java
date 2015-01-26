package net.log4all.client.exceptions;

/**
 * Created by igor on 03/06/14.
 */
public class Log4AllException extends Exception{
	private static final long serialVersionUID = 1L;

	public Log4AllException() {
    }

    public Log4AllException(String message) {
        super(message);
    }

    public Log4AllException(String message, Throwable cause) {
        super(message, cause);
    }

    public Log4AllException(Throwable cause) {
        super(cause);
    }
}
