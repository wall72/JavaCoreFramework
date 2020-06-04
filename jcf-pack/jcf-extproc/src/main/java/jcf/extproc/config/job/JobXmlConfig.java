package jcf.extproc.config.job;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import jcf.extproc.config.JobConfig;
import jcf.extproc.config.XmlConstant;
import jcf.extproc.exception.ExternalProcessException;
import jcf.extproc.process.AntProcess;
import jcf.extproc.process.CommandLineProcess;

import com.thoughtworks.xstream.XStream;

public class JobXmlConfig {

	private XStream xstream;

	public JobXmlConfig() {
		xstream = new XStream(); 
		xstream.alias("Job", JobConfig.class);

		/*
		 * deprecated field, for compatibility.
		 */
		xstream.omitField(CommandLineProcess.class, "paused");
		xstream.omitField(AntProcess.class, "paused");
	}
	
	
	public JobConfig read(InputStream inputStream) {
		InputStreamReader reader = null;
		try {
			reader = new InputStreamReader(inputStream, XmlConstant.CHARSET);
			
		} catch (UnsupportedEncodingException e) {
			throw new ExternalProcessException("error opening file for reading", e);
		}
		
		return (JobConfig) xstream.fromXML(reader);
	}

	public void write(JobConfig info, OutputStream outputStream) throws IOException {
		OutputStreamWriter writer = null;
		try {
			writer = new OutputStreamWriter(outputStream, XmlConstant.CHARSET);
			
		} catch (UnsupportedEncodingException e) {
			throw new ExternalProcessException("cannot set charset to " + XmlConstant.CHARSET, e);
		}
		writer.write(XmlConstant.XML_HEADER);
		xstream.toXML(info, writer);
	}
	
}
