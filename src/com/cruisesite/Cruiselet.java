package com.cruisesite;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.corecruise.cruise.SessionObject;
import com.corecruise.cruise.SessionObjectWebParams;
import com.corecruise.cruise.services.interfaces.CruiseInterface;
import com.corecruise.cruise.services.interfaces.PluginInterface;
import com.cruisesite.utils.ValidateUser;


/**
 * Servlet implementation class Cruiselet
 */
@WebServlet({"/Cruiselet", "/test"})
public class Cruiselet extends HttpServlet implements CruiseInterface {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Cruiselet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 * http://localhost:8079/CuiseSite/Cruiselet?application=Hello&username=admin&password=admin&action=cDBCreateConnection&service=sampleService&value1=hello&Test=world
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		try {
			
			ValidateUser vu = new ValidateUser();
			vu.initializeValidation();
			
			SessionObjectWebParams so = new SessionObjectWebParams();
			so.go(this, vu, request.getParameterMap());
			response.getWriter().append(so.getResponseJSONPP());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

	@Override
	public boolean PreProcess(SessionObject cruise,PluginInterface plugIn) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean PostProcess(SessionObject cruise,PluginInterface plugIn) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void ProcessingError(SessionObject cruise) {
		System.out.println("Error occured");
		
	}

}
