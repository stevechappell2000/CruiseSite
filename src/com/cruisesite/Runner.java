package com.cruisesite;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import com.corecruise.core.CoreCruise;
import com.corecruise.cruise.SessionObject;
import com.corecruise.cruise.SessionObjectJSON;
import com.corecruise.cruise.services.interfaces.PluginClientInterface;
import com.corecruise.cruise.services.interfaces.PluginInterface;
import com.corecruise.cruise.services.utils.Services;
import com.corecruise.cruise.testharness.ValidateUser;


public class Runner implements PluginClientInterface{
	ValidateUser vu = null;
	SessionObjectJSON sessionObject = null;
    public Runner() {
    	if(null == vu) {
    		//Boiler plate
    		//create validation object
			vu = new ValidateUser();
			//do validation
			vu.initializeValidation();
			//create a sessionObject
			sessionObject = new SessionObjectJSON();
			
    	}
    }
    public boolean goFromFile(String filename) {
    	boolean ret = false;
    	try {
    		//read the file (JSON) actions
    		String x = new String(Files.readAllBytes(Paths.get(filename)));
    		//using the SessionObject, execute the JSON actions
			ret = sessionObject.go(this, vu, x,false);
			
			try {
				System.out.println(sessionObject.getResponseJSONPP());
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
    }
    public boolean go(String jsonString) {
    	boolean ret = false;
    	try {
    		//using the SessionObject, execute the JSON actions
			ret = sessionObject.go(this, vu, jsonString ,false);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
    }
    public boolean goPlugin(PluginInterface pi, Services service) {
    	return pi.executePlugin(sessionObject, service);
    }
    public void reset() {
    	sessionObject.resetResponse();
    }
    public String getResponse() throws Exception {
    	return sessionObject.getResponseJSONPP();
    }
	public boolean PreProcess(SessionObject sessionObject, Services service) {
		// TODO Auto-generated method stub
		return true;
	}

	public boolean PostProcess(SessionObject sessionObject, Services service) {
		// TODO Auto-generated method stub
		return true;
	}

	public void ProcessingError(SessionObject sessionObject) {
		// TODO Auto-generated method stub
		
	}
	public SessionObjectJSON getSessionObject() {
		return sessionObject;
	}
	public void setSessonObject(SessionObjectJSON so) {
		this.sessionObject = so;
	}
	public boolean validateUser(String user, String pass) {
		boolean ret = false;
		ret = vu.validateUserPass(user, pass);
		return ret;
	}
	public ArrayList<String> getPlugIns(){
		ArrayList<String> ret = null;
		//so.getCruiseObject().PostProcess(sessionObject, service)
		
		
		return ret;
		
	}

}
