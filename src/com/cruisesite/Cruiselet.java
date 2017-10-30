package com.cruisesite;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.corecruise.cruise.SessionObject;
import com.corecruise.cruise.SessionObjectWebJSON;
import com.corecruise.cruise.SessionObjectWebParams;
import com.corecruise.cruise.services.interfaces.CruiseInterface;
import com.corecruise.cruise.services.interfaces.PluginInterface;
import com.cruisesite.utils.ValidateUser;
import com.zaxxer.hikari.HikariConfig;


/**
 * Servlet implementation class Cruiselet
 */
@WebServlet({"/Cruiselet", "/test"})
public class Cruiselet extends HttpServlet implements CruiseInterface {
	private static final long serialVersionUID = 1L;
	private String sample = 
			"{\"application\" :"
		    +"{"
			+  "\"name\":\"SampleApplication\","
			+  "\"id\":\"unkown\","
			+" \"parameters\":["
			+"    {\"key\":\"AppParam1\", \"value\":\"AppParamsomeValue1\"},"
			+"    {\"key\":\"AppParam2\", \"value\":\"AppParamsomeValue2\"}"
			+"]},"
			+" \"credentials\":"
			+"   { \"username\":\"admin\","
			+ "    \"password\" : \"admin\","
			+"     \"parameters\":["
			+"        {\"key\":\"CredParam1\", \"value\":\"CredParamsomeValue1\"},"
			+"        {\"key\":\"CredParam2\", \"value\":\"CredParamsomeValue2\"}"
			+"]},"
			+ "\"services\":["
			+"    {\"action\":\"cDBCreateConnection\", "
			+"     \"service\":\"testDatabase\", "
			+"     \"pluginName\":\"CruiseDatabase\", "
			+"     \"parameters\":["
			+"        {\"key\":\"PoolName\", \"value\":\"CuiseDBPool\"},"
			+"        {\"key\":\"DriverClassName\", \"value\":\"com.mysql.jdbc.Driver\"},"
			+"        {\"key\":\"maximumPoolSize\", \"value\":\"25\"},"
			+"        {\"key\":\"minimumIdle\", \"value\":\"5\"},"
			+"        {\"key\":\"jdbcUrl\", \"value\":\"jdbc:mysql://localhost:3306/cruisecore?useSSL=false\"},"
			+"        {\"key\":\"username\", \"value\":\"root\"},"
			+"        {\"key\":\"password\", \"value\":\"admin\"}"
			+"       ]"
			+"    }"
			+" ]"
			+""
			+"}";
	/*
	 * ds.setDriverClassName("com.mysql.jdbc.Driver");
        HikariConfig jdbcConfig = new HikariConfig();
        jdbcConfig.setPoolName(service.getParameter("PoolName").getValue());
        jdbcConfig.setMaximumPoolSize(service.getParameter("maximumPoolSize").getIntValue());
        jdbcConfig.setMinimumIdle(service.getParameter("minimumIdle").getIntValue());
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
			
			ValidateUser vu = new ValidateUser();
			vu.initializeValidation();
			
			SessionObjectWebJSON so = new SessionObjectWebJSON();
			so.go(this, vu, sample,false);
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
