package com.cruisesite.utils;

import java.util.HashMap;

import com.corecruise.cruise.SessionObject;
import com.corecruise.cruise.services.interfaces.ValidationInterface;



public class ValidateUser implements ValidationInterface {
    HashMap<String,String> validUsers = new HashMap<String,String>();
    boolean ok = false;
	public boolean isInitialized() {
		// TODO Auto-generated method stub
		return ok;
	}

	/**
	 * Simple validate via a hashmap of usernames and passwords.
	 * @return
	 */
	@Override
	public boolean initializeValidation() {
		validUsers.put("admin", "admin");
		validUsers.put("manager", "manager");
		validUsers.put("developer", "developer");
		validUsers.put("user", "user");
		
		ok = true;
		return ok;
	}

	@Override
	public boolean validateRequest(SessionObject obj) {
		boolean ret = false;
		if(validUsers.containsKey(obj.getApplication().getCredentials().getUsername())){
		   if(obj.getApplication().getCredentials().getPassword().equalsIgnoreCase(validUsers.get(obj.getApplication().getCredentials().getUsername()))){
			   ret = true;
		   }
		}
		return ret;
	}

	@Override
	public boolean validateRequest(SessionObject sobj, Object obj) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public boolean validateUserPass(String User, String Pass) {
		// TODO Auto-generated method stub
		return false;
	}





}
