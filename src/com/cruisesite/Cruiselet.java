package com.cruisesite;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.corecruise.cruise.SessionObject;
import com.corecruise.cruise.SessionObjectJSON;
import com.corecruise.cruise.services.interfaces.PluginClientInterface;
import com.corecruise.cruise.services.utils.Services;
import com.cruisesite.utils.ValidateUser;





/**
 * Servlet implementation class Cruiselet
 */
@WebServlet({"/Cruiselet"})
@MultipartConfig(fileSizeThreshold = 6291456, // 6 MB
				 maxFileSize = 10485760L, // 10 MB
				 maxRequestSize = 20971520L // 20 MB
)
public class Cruiselet extends HttpServlet implements PluginClientInterface {
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
					"		        \"parameters\" : {"+
					"		          \"pluginName\" : \"CruiseCorePlugin\","+
					"		          \"service\" : \"MissingCommand\","+
					"		          \"action\" : \"info\""+
					"		        }"+

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
			boolean testIncoming = false;
			Enumeration<?> headerNames = request.getHeaderNames();
			while (headerNames.hasMoreElements()) {
				String key = (String) headerNames.nextElement();
				String value = request.getHeader(key);
				System.out.println(key+":"+value);
				if(key.equalsIgnoreCase("content-type")) {
					if(value.equalsIgnoreCase("text/plain") || value.equalsIgnoreCase("application/json")) {
						testIncoming = true;
					}
				}
			}
			if(testIncoming) {
				StringBuffer jb = new StringBuffer();
				String line = null;
				try {
					BufferedReader reader = request.getReader();
					while ((line = reader.readLine()) != null) {
						jb.append(line);
					}
					sample = jb.toString();
				} catch (Exception e) { /*report an error*/ 
					e.printStackTrace();
				}
			}

			// Below is prep for CORS
			setAccessControlHeaders(response,request);
			// Set the three boilerplate commands
			ValidateUser vu = new ValidateUser();
			vu.initializeValidation();
			SessionObjectJSON so = new SessionObjectJSON();
            //Check for multipart
			if (request.getContentType() != null && request.getContentType().startsWith("multipart/")) {
				for (Part part : request.getParts()) {
					if (part != null && part.getSize() > 0) {
						String fileName = part.getSubmittedFileName();
						String contentType = part.getContentType();

						// allows only JPEG files to be uploaded
						if (!contentType.equalsIgnoreCase("image/jpeg")) {
							continue;
						}

						/*part.write(uploadFilePath + File.separator + fileName);

					writer.append("File successfully uploaded to " 
							+ uploadFolder.getAbsolutePath() 
							+ File.separator
							+ fileName
							+ "<br>\r\n");
						 */
					}
				}		
			}
			
			
			
			
			// SAMPLE: put the printwriter into the sessionobject state storage. This storage only last for the lifetime of the request.
			// we can then use it during post processing to write out the important bits.
			//    so.setRequestState("PrintWriter", response.getWriter());

			// Execute the Request
			so.go(this, vu, sample,false);

			// Return Response - Boiler plate Java/Servlet stuff
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
		
        resp.addHeader("x-access_token","Content-Type, content-type, Access-Control-Allow-Headers, Authorization, X-Requested-With");
        resp.addHeader("Access-Control-Max-Age", "1800");//30 min

	}

	@Override
	public boolean PreProcess(SessionObject sessionObject, Services service) {
		boolean ret = true;
		//ValidateUser vu = (ValidateUser) sessionObject.getValidator();
        ret = sessionObject.doPreProcess(service);
		return ret;
	}

	@Override
	public boolean PostProcess(SessionObject sessionObject, Services service) {
		boolean ret = true;
		//ValidateUser vu = (ValidateUser) sessionObject.getValidator();
        ret = sessionObject.doPostProcess(service);
		return ret;
	}

}
/*
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
		"		    }"+
		"		  }"+
		"		}";
*/
