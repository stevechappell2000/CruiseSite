package com.cruisesite;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.corecruise.cruise.SessionObject;
import com.corecruise.cruise.SessionObjectJSON;
import com.corecruise.cruise.services.interfaces.CruiseInterface;
import com.corecruise.cruise.services.interfaces.PluginInterface;
import com.cruisesite.utils.ValidateUser;



/**
 * Servlet implementation class Cruiselet
 */
@WebServlet({"/Cruiselet", "/test"})
public class Cruiselet extends HttpServlet implements CruiseInterface {
	private static final long serialVersionUID = 1L;
	String sample = 
			"{"+
					"		  \"Application\" : {"+
					"		    \"parameters\" : {"+
					"		      \"name\" : \"sampleapp\","+
					"		      \"id\" : \"sampleid\""+
					"		    },"+
					"		    \"credentials\" : {"+
					"		      \"parameters\" : {"+
					"		        \"password\" : \"admin\","+
					"		        \"username\" : \"admin\""+
					"		      }"+
					"		    },"+
					"		    \"services\" : {"+
					/*"		      \"SampleService\" : {"+
					"		        \"parameters\" : {"+
					"		          \"pluginName\" : \"CruiseDatabase\","+
					"		          \"service\" : \"GetPlugIn_Info\","+
					"		          \"action\" : \"info\""+
					"		        }"+
					"		      },"+*/
					"		      \"DatabaseConnection\" : {"+
					"		        \"parameters\" : {"+
					"		          \"pluginName\" : \"CruiseDatabase\","+
					"		          \"service\" : \"AddConnection\","+
					"		          \"action\" : \"cDBCreatePool\","+
					"		          \"PoolName\" : \"MyPool\","+
					"		          \"DriverClassName\" : \"org.mariadb.jdbc.Driver\","+
					"		          \"maximumPoolSize\" : \"25\","+
					"		          \"minimumIdle\" : \"5\","+
					"		          \"jdbcUrl\" : \"jdbc:mysql://localhost:3306/cruisecore?useSSL=false\","+
					"		          \"schema\" : \"cruisecore\","+
					"		          \"username\" : \"root\","+
					"		          \"password\" : \"admin\""+
					"		        }"+
					"		      },"+
					
					"		      \"TableUpdate\" : {"+
					"		        \"parameters\" : {"+
					"		          \"pluginName\" : \"CruiseDatabase\","+
					"		          \"service\" : \"UpdateRecord\","+
					"		          \"action\" : \"update\","+
					"		          \"PoolName\" : \"MyPool\","+
					"		          \"fromlist\" : \"cru_components\","+
					"		          \"COMPONENTINDEX\" : \"0000\","+
					"		          \"COMPONENTNAME\" : \"Thank you disillusionment\","+
					"		          \"TableName\" : \"cru_components\""+
					"		        }"+
					"		      },"+
					
					"		      \"TableSelect\" : {"+
					"		        \"parameters\" : {"+
					"		          \"pluginName\" : \"CruiseDatabase\","+
					"		          \"service\" : \"selectAll\","+
					"		          \"action\" : \"select\","+
					"		          \"PoolName\" : \"MyPool\","+
					"		          \"fromlist\" : \"cru_components\","+
					"		          \"TableName\" : \"cru_components\""+
					"		        }"+
					"		      }"+
					/*","+
					"		      \"DatabaseInformation\" : {"+
					"		        \"parameters\" : {"+
					"		          \"pluginName\" : \"CruiseDatabase\","+
					"		          \"service\" : \"Pool Information\","+
					"		          \"action\" : \"cDBGetPoolInfo\","+
					"		          \"PoolName\" : \"MyPool\""+
					"		        }"+
					"		      }"+*/

					"		    }"+
					"		  }"+
					"		}";
	/*
	jdbcConfig.setDriverClassName(service.getParameter("DriverClassName").getValue());
	jdbcConfig.setMaximumPoolSize(service.getParameter("maximumPoolSize").IntValue());
	jdbcConfig.setMinimumIdle(service.getParameter("minimumIdle").IntValue());
	jdbcConfig.setJdbcUrl(service.getParameter("jdbcUrl").getValue());
	jdbcConfig.setUsername(service.getParameter("username").getValue());
	jdbcConfig.setPassword(service.getParameter("password").getValue());
	 */
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
			setAccessControlHeaders(response,request);
			ValidateUser vu = new ValidateUser();
			vu.initializeValidation();
			SessionObjectJSON so = new SessionObjectJSON();
			//so.getResponseJSON();
			//put the printwriter into the sessionobject state storage. This storage only last for the lifetime of the request.
			//we can then use it during post processing to write out the important bits.

			//so.setRequestState("PrintWriter", response.getWriter());

			so.go(this, vu, sample,false);
			PrintWriter pw = response.getWriter();
			pw.write(so.getResponseJSONPP());
			pw.flush();
			pw.close();


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
	public boolean PreProcess(SessionObject so,PluginInterface plugIn) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean PostProcess(SessionObject so,PluginInterface plugIn) {

		return true;
	}

	@Override
	public void ProcessingError(SessionObject so) {
		System.out.println("Error occured");

	}
	@Override
	protected void doOptions(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		setAccessControlHeaders(resp,req);
		resp.setStatus(HttpServletResponse.SC_OK);
	}

	private void setAccessControlHeaders(HttpServletResponse resp,HttpServletRequest req) {
		resp.setHeader("Access-Control-Allow-Origin", req.getHeader("Origin"));
		resp.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS");
	}

}
