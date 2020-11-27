package ge.bestline.dhl.utils;

import java.io.Serializable;

public class ConfigurationException extends Exception implements Serializable {
    private static final long serialVersionUID = 1L;

    public ConfigurationException() {
        super();
    }

    public ConfigurationException(String message, Throwable throwable) {
        super(message, throwable);
    }

	public ConfigurationException(String message) {
		super(message);
	}

	public ConfigurationException(Throwable throwable) {
		super(throwable);
	}
}
