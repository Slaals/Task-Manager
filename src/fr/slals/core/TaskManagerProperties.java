package fr.slals.core;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class TaskManagerProperties extends Properties {

	private static final long serialVersionUID = 1L;
	
	private static final String CONF_PATH = System.getProperty("user.dir") + "\\src\\resources\\conf\\config.properties";
	
	public TaskManagerProperties() {
		super();
		
		InputStream input = null;
		try {
			input = new FileInputStream(CONF_PATH);
			
			load(input);
		} catch(IOException io) {
			io.printStackTrace();
		} finally {
			if(input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void save() {
		OutputStream output = null;
		try {
			output = new FileOutputStream(CONF_PATH);
			
			store(output, null);
		} catch(IOException io) {
			io.printStackTrace();
		} finally {
			if(output != null) {
				try {
					output.close();
				} catch(IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
