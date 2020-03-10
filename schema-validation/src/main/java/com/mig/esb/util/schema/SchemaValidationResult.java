package com.mig.esb.util.schema;

import org.mule.extension.validation.api.ValidationResult;

public class SchemaValidationResult implements ValidationResult {
	
	private String message;
	private boolean hasErrors = false;
	
	public SchemaValidationResult(String message, boolean hasErrors) {
		super();
		this.message = message;
		this.hasErrors = hasErrors;
	}
	
	@Override
	public String getMessage() {
		return message;
	}
	
	@Override
	public boolean isError() {
		return hasErrors;
	}
}