package jcf.extproc.config.jobinstance;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

import jcf.extproc.config.ExtProcConstant;
import jcf.extproc.config.XmlConstant;
import jcf.extproc.exception.ExternalProcessException;
import jcf.extproc.process.JobInstanceInfo;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.basic.DateConverter;

public class JobInstanceXmlConfig {

	private XStream xstream;

	public JobInstanceXmlConfig() {
		xstream = new XStream(); 
		xstream.alias("JobInstance", JobInstanceInfo.class);
		xstream.registerConverter(new DateConverter(ExtProcConstant.DATE_FORMAT , new String[]{ExtProcConstant.DATE_FORMAT}));
	}
	
	public void write(JobInstanceInfo info, OutputStream outputStream) throws IOException {
		OutputStreamWriter writer = null;
		try {
			writer = new OutputStreamWriter(outputStream, XmlConstant.CHARSET);
			
		} catch (UnsupportedEncodingException e) {
			throw new ExternalProcessException("cannot set charset to " + XmlConstant.CHARSET, e);
		}

		writer.write(XmlConstant.XML_HEADER);
		xstream.toXML(info, writer);
	}
	
	public String toXml(JobInstanceInfo info) {
		return xstream.toXML(info);
	}
	
	public JobInstanceInfo read(InputStream inputStream) {
		InputStreamReader reader = null;
		try {
			reader = new InputStreamReader(inputStream, XmlConstant.CHARSET);
			
		} catch (UnsupportedEncodingException e) {
			throw new ExternalProcessException("error opening file for reading", e);
		}
		
		return (JobInstanceInfo) xstream.fromXML(reader);
	}
	
	public JobInstanceInfo fromXml(String xml) {
		return (JobInstanceInfo) xstream.fromXML(xml);
	}
}
