package fr.slals.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Properties;

public class TaskManagerProperties extends Properties {

	private static final long serialVersionUID = 1L;
	
	private static final String CONF_PATH = System.getProperty("user.dir") + "\\src\\resources\\conf\\config.properties";
	
	public TaskManagerProperties() {
		super();
		
		// Create conf file if he doesn't exist
		File file = new File(CONF_PATH);
		if(!file.exists()) {
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(file));
				
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
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
	
	public ArrayList<String> getExts(String img) {
		ArrayList<String> res = new ArrayList<String>();
		String[] exts = getProperty("EXT").split(";");
		for(int i = 0; i < exts.length; i++) {
			String value = getProperty("EXT_" + exts[i]);
			if(value != null && value.equals(img)) {
				res.add(exts[i]);
			}
		}
		return res;
	}
	
	public void setExt(String ext) {
		if(!extExistsInList(ext)) {
			setProperty("EXT", getProperty("EXT") + ";" + ext.toUpperCase());
		}
	}
	
	public void deleteExt(String ext) {
		ext = ext.toUpperCase();
		remove("EXT_" + ext);
		save();
	}
	
	private boolean extExistsInList(String ext) {
		ext = ext.toUpperCase();
		String[] exts = getProperty("EXT").split(";");
		for(String propExt : exts) {
			if(ext.equals(propExt)) {
				return true;
			} 
		}
		
		return false;
	}
	
	public boolean extExists(String ext) {
		ext = ext.toUpperCase();
		if(getProperty("EXT_" + ext) != null) {
			return true;
		}
		
		return false;
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
