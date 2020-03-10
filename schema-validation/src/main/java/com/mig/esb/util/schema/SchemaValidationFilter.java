package com.mig.esb.util.schema;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.log4j.Logger;
import org.apache.xerces.dom.DOMInputImpl;
import org.mule.util.IOUtils;
import org.mule.util.StringUtils;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.SAXException;


public class SchemaValidationFilter {

	private static final Logger logger = Logger.getLogger(SchemaValidationFilter.class);

	private String basePath;
	private String schemaLocations = "";
	private Schema schema;

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

	public void initialise() throws Exception {
		if (org.apache.commons.lang3.StringUtils.isBlank(this.schemaLocations)) {
			throw new Exception("schemaLocations should be specified.");
		}

		String[] split = StringUtils.splitAndTrim(this.getSchemaLocations(), ",");
		Source[] schemas = new Source[split.length];
		for (int i = 0; i < split.length; i++) {
			String loc = split[i];
			InputStream schemaStream;
			try {
				schemaStream = IOUtils.getResourceAsStream(loc, getClass());
			} catch (IOException e) {
				throw new Exception("Error in loading shema file " + loc + ".", e);
			}
			schemas[i] = new StreamSource(schemaStream, IOUtils.getResourceAsUrl(split[i], getClass()).toString());
		}

		SchemaFactory schemaFactory = new org.apache.xerces.jaxp.validation.XMLSchemaFactory();
		logger.info("Schema factory implementation: " + schemaFactory);

		schemaFactory.setResourceResolver(new ClasspathResourceResolver());

		try {
			schema = schemaFactory.newSchema(schemas);
		} catch (SAXException e) {
			throw new Exception("Exception in creation Schema.", e);
		}
	}

	public Validator createValidator() throws SAXException {
		return this.schema.newValidator();
	}

	private class ClasspathResourceResolver implements LSResourceResolver {

		@Override
		public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {

			if (basePath != null) {
				systemId = basePath + File.separator + systemId;
			}

			try {
				logger.info("Loading " + systemId);
				InputStream resource = IOUtils.getResourceAsStream(systemId, getClass());
				return new DOMInputImpl(publicId, systemId, baseURI, resource, null);
			} catch (IOException e) {
				logger.error("Cannot load " + systemId + ". Please check if the file exists.", e);
				e.printStackTrace();
				return null;
			}
		}

	}
}
