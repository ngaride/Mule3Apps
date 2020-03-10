package com.mig.esb.util.schema;

import javax.xml.transform.Source;

import org.apache.log4j.Logger;
import org.mule.api.MuleEvent;
import org.mule.api.MuleMessage;
import org.mule.api.lifecycle.Initialisable;
import org.mule.api.lifecycle.InitialisationException;
import org.mule.api.transport.PropertyScope;
import org.mule.extension.validation.api.ValidationResult;
import org.mule.extension.validation.api.Validator;
import org.mule.module.xml.util.XMLUtils;
import org.mule.util.xmlsecurity.XMLSecureFactories;
import org.xml.sax.SAXParseException;

/**
 * SchemaValidator uses org.apache.xerces.jaxp.validation.XMLSchemaFactory as
 * SchemaFactory. It first loads XSD files included in XSD files specified in
 * schemaLocations property from classpath only.
 */
public class SchemaValidator implements Validator, Initialisable {

	private static final Logger logger = Logger.getLogger(SchemaValidator.class);

	private String basePath;
	private String schemaLocations = "";
	private Integer httpStatusOnException;
	private SchemaValidationFilter filter;

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public String getSchemaLocations() {
		return schemaLocations;
	}

	public void setSchemaLocations(String schemaLocations) {
		this.schemaLocations = schemaLocations;
	}

	public Integer getHttpStatusOnException() {
		return httpStatusOnException;
	}

	public void setHttpStatusOnException(Integer httpStatusOnException) {
		this.httpStatusOnException = httpStatusOnException;
	}

	@Override
	public ValidationResult validate(MuleEvent event) {
		logger.info("Validating XML data against schema(s) " + this.getSchemaLocations() + " ...");
		try {
			javax.xml.validation.Validator v = filter.createValidator();
			Source inboundSource = XMLUtils.toXmlSource(XMLSecureFactories.createDefault().getXMLInputFactory(), false, event.getMessage().getPayload());
			v.validate(inboundSource);
			logger.info("Validated XML data against schema(s) " + this.getSchemaLocations() + ".");
			return new SchemaValidationResult(null, false);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			if (httpStatusOnException != null) {
				setOutboundProperties(event.getMessage());
			}
			String errorMessage = (e instanceof SAXParseException ? "Error at line " + ((SAXParseException) e).getLineNumber() + ", column " + ((SAXParseException) e).getColumnNumber() + " - " : "")
					+ e.getLocalizedMessage();
			return new SchemaValidationResult(errorMessage, true);
		}
	}

	@Override
	public void initialise() throws InitialisationException {
		filter = new SchemaValidationFilter();
		filter.setBasePath(this.basePath);
		filter.setSchemaLocations(schemaLocations);
		logger.info("Loading " + schemaLocations);
		try {
			filter.initialise();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void setOutboundProperties(MuleMessage message) {
		message.setProperty("http.status", httpStatusOnException, PropertyScope.OUTBOUND);
	}

}
