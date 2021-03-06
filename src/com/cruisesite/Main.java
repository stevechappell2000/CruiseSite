package com.cruisesite;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import com.corecruise.core.CoreCruise;
import com.corecruise.cruise.config.CruisePluginEnvironment;
import com.corecruise.cruise.services.interfaces.PluginInterface;
import com.corecruise.cruise.services.utils.Services;
import com.cruise.plugins.Action;
import com.cruise.plugins.ActionParameter;
import com.cruise.plugins.PlugInMetaData;

public class Main {
	static boolean continueRunning = true;
	static boolean continuePlugin = true;
	static boolean continueAction = true;
	static boolean continueParameters = true;
	static boolean serverRunning = false;
	static String slashCruiseServletName = "/Cruise";
	static int port = 8079;
	static String applicationName = "CruiseSite";
	static String serverName = null;
	static String targetURL = null;
	static String thisURL = null;
	static String portNumber = null;
	static Server server = null;
	static Scanner scanner = new Scanner(System.in);
	static ArrayList<String> plugins = null;
	static ExecutorService executor = null;
	static String ret = 
			"{"+
					"		  \"Application\" : {"+
					"		    \"parameters\" : {"+
					"		      \"name\" : \""+applicationName+"\","+
					"		      \"id\" : \"sampleid\""+
					"		    },"+
					"		    \"credentials\" : {"+
					"		      \"parameters\" : {"+
					"		        \"password\" : \"admin\","+
					"		        \"username\" : \"admin\""+
					"		      }"+
					"		    },"+
					"		    \"services\" : ["+
					"		         {\"parameters\" : {"+
					"                     \"pluginName\" : \"CruiseCorePlugin\","+
					"                     \"action\" : \"echo\""+
					"		              }"+
					"		         }"+
					"		    ]"+
					"		  }"+
					"		}";

	public static void main( String[] args ){   
		
        portNumber = null;
        serverName = null;
        targetURL = null;
        try {
        	///////////////////////////////////////
        	// Check properties file for startup params
        	CruisePluginEnvironment config = null;
        	String pluginName = "CruiseCorePlugin";
        	if(null == config)
        		config = CoreCruise.getCruiseConfig(pluginName);
        	if(null != config) {
        		if(config.getPluginProperties().containsKey("serverName")) {
        			serverName = config.getPluginProperties().getProperty("serverName");
        		}
        		if(config.getPluginProperties().containsKey("portNumber")) {
        			portNumber = config.getPluginProperties().getProperty("portNumber");
        		}
        		if(config.getPluginProperties().containsKey("registerWith")) {
        			targetURL = config.getPluginProperties().getProperty("registerWith");
        		}
        	}
            ////////////////////////////////////
        	// Check args, if found they override properties
        	if(null == args || args.length<1) {
        		if(null == portNumber)
        			portNumber = "8079";
        		if(null == serverName)
        			serverName = "CruiseGenericServer";
        		//targetURL = "http://localhost:8079/Cruise";
        	}else if(args.length == 1 && args[0].contains("|") == false) {
        		portNumber = args[0];
        		serverName = "CruiseGenericServer";
        		//targetURL = "http://localhost:"+portNumber+"/Cruise";
        	}else if(args.length > 0 && args[0].contains("|") == true) {
        		String[] s = args[0].split("\\|");
        		if(s.length==2) {
        			portNumber = s[0];
        			serverName = s[1];
        			//targetURL = "http://localhost:"+portNumber+"/Cruise";
        		}else if(s.length > 2) {
        			portNumber = s[0];
        			serverName = s[1];
        			targetURL  = s[2];
        		}
        	}
        	////////////////////////////////////
        }
        catch (ArrayIndexOutOfBoundsException e){
        	System.out.println("ArrayIndexOutOfBoundsException caught");
        }
        Runner r = new Runner();
        r.go(ret);
        if(null == portNumber ) {
        	if(validUser(r)) {
        		getPlugins(r);
        		//int cnt = 0;
        		//selectPlugin(r);
        		while(continueRunning) {
        			r.reset();
        			processActions(r);
        		}
        	}else {
        		System.out.print("Invalid login. Application Quitint");
        		System.exit(0);
        	}
        }else {
        	try {
        		System.out.println("PortNumber:"+portNumber);
        		port = new Integer(portNumber).intValue();
        		startServer(portNumber,"n");

        	}catch(Exception e) {
        		e.printStackTrace();
        	}
        }
	}
	private static void processActions(Runner r) {
		System.out.println("\n***************");
		System.out.println(" Select Action");
		System.out.println("***************");
		System.out.println("0:Exit");
		System.out.println("1:Execute Service");
		if(!serverRunning) {
			System.out.println("2:Start WebServer");
		}
		if(serverRunning) {
			System.out.println("3:Stop WebServer");
		}
		int selected = -1;
		String sSelected = scanner.nextLine();
		try {
			selected = new Integer(sSelected).intValue();
			if(selected == 0) {
				continueRunning = false;
			}else if(selected == 1) {
				continuePlugin = true;
				while(continuePlugin) {
					selectPlugin(r);
				}
			}else if(serverRunning == false && selected == 2) {
				startServer(null,null);
			}else if(serverRunning == true && selected == 3) {
				stopServer();
			}
		}catch(Exception err) {
			selected = -1;
		}
	}
	private static void selectPlugin(Runner r) {
		try {
			int cnt = 0;
			int selected = -1;
			System.out.println("\nAvailable Plugins:");
			System.out.println("0:Exit");

			for(String n: plugins) {
				System.out.println((cnt+1) +":"+n);
				++cnt;
			}
			System.out.print("Select Plugin By Number (0-"+cnt+"): ");
			String sSelected = scanner.nextLine();
			try {
				selected = new Integer(sSelected).intValue();
				if(selected == 0) {
					continuePlugin = false;
				}else if(selected > cnt || selected < 0) {
					continuePlugin = true;
				}else {
					continueAction = true;
					while(continueAction) {
						
						selectAction(r, plugins.get(selected-1));
					}
				}
			}catch(Exception err) {
				selected = -1;
			}

		}catch(Exception err) {

		}

	}
	private static void selectAction(Runner r, String pluginName) {
		System.out.println("\nSELECTED:"+pluginName);
		PluginInterface pi = CoreCruise.getLoadedClasses().get(pluginName);
		PlugInMetaData pmd = pi.getPlugInMetaData();
		int cnt = 0;
		int selected = -1;
		System.out.println("\nACTIONS\n0:Exit");
		for (Action a: pmd.getActions()) {
			System.out.println((cnt+1)+":"+a.getActionName());
			++cnt;
		}
		System.out.print("Select Action Number (0-"+cnt+"): ");
		String sSelected = scanner.nextLine();
		try {
			selected = new Integer(sSelected).intValue();
			if(selected == 0) {
				continueAction = false;
			}else if(selected > cnt || selected < 0) {
				continueAction = true;
			}else {
				continueParameters = true;
				while(continueParameters) {
					processParameters(r, pi, pmd.getActions().get(selected-1));
				}

			}
		}catch(Exception err) {
			err.printStackTrace();
			selected = -1;
		}

	}
	private static void processParameters(Runner r, PluginInterface pi, Action a) {
		Services service = new Services();
		service.Action(a.getActionName());
		ArrayList<ActionParameter> parms = a.getActionParams();
		int cnt = 0;
		int selected = -1;
		System.out.println("\nEnter Each required Parameter, optional can be skipped.");
		String sSelected = null;
		for (ActionParameter p: parms) {
			System.out.println(p.getParamName()+"(Required:"+p.getParamRequired()+"):");
			sSelected = scanner.nextLine();
			while(p.getParamRequired().equalsIgnoreCase("true") && sSelected.trim().length()<1) {
				sSelected = scanner.nextLine();
			}
			if(sSelected.trim().length()> 0 && sSelected.trim().equalsIgnoreCase("exit")) {
				continueParameters = false;
				break;
			}else if(sSelected.trim().length()> 0) {
				service.Parameter(p.getParamName(), sSelected);
			}
		}
		if(continueParameters) {
			boolean ret = r.goPlugin(pi, service);
			if(ret) {
				try {
					System.out.println(r.getResponse());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			System.out.println("Service Result:"+ret+" (Press return to continue)");
			sSelected = scanner.nextLine();
			r.reset();
		}
		continueParameters = false;
		/*System.out.print("Select Action Number (0-"+cnt+"): ");
		String sSelected = scanner.nextLine();
		try {
            selected = new Integer(sSelected).intValue();
            if(selected == 0) {
            	cont = false;
            }else if(selected > cnt-1 || selected < 0) {
            	cont = true;
            }else {
            	processParameters(pluginName, pmd.getActions().get(cnt-1));
            	cont = true;
            }
		}catch(Exception err) {
            selected = -1;
		}*/
	}
	private static boolean validUser(Runner r) {
		boolean ret = false;

		System.out.print("UserName: ");
		String name = scanner.nextLine();
		System.out.print("Password: ");
		String password = scanner.nextLine();
		System.out.println(name+"|"+password+"|");
		ret = r.validateUser(name, password);



		return ret;
	}
	private static String getPlugins(Runner r) {
		String ret = null;
		plugins = new ArrayList<String>();
		for(Class c :CoreCruise.getClasses()) {
			//System.out.println(c.getSimpleName());
			plugins.add(c.getSimpleName());
		}

		return ret;
	}
	private static void startServer(String inPort, String interAct) {
		String interactive = interAct;
		System.out.println(inPort+interAct);
        if(null == inPort ) {
			System.out.print("Enter portNumber ("+port+" is default):");
			String sSelected = scanner.nextLine();
			if(null == sSelected || sSelected.trim().length()<1) {
				
			}else {
				try {
					port = new Integer(sSelected).intValue();
				}catch(Exception err) {
					System.out.println("Bad port number entered.");
					return;
				}
			}
			System.out.print("Start as Interactive?(Y/N):");
			interactive = scanner.nextLine();
        }else {
        	interactive = "n";
        }
		if(interactive.trim().toUpperCase().startsWith("N")) {
			//executor = Executors.newSingleThreadExecutor();
			//executor.submit(() -> {
				server = new Server(port);
				thisURL = server.getURI().toString();
				if(thisURL.trim().endsWith("/")) {
					thisURL = thisURL.substring(0, thisURL.trim().length()-1);
				}
				System.out.println("SERVER URI:"+thisURL);
				// The ServletHandler is a dead simple way to create a context handler
				// that is backed by an instance of a Servlet.
				// This handler then needs to be registered with the Server object.
				ServletHandler handler = new ServletHandler();

				server.setHandler(handler);
				// Passing in the class for the Servlet allows jetty to instantiate an
				// instance of that Servlet and mount it on a given context path.

				// IMPORTANT:
				// This is a raw Servlet, not a Servlet that has been configured
				// through a web.xml @WebServlet annotation, or anything similar.
				ServletHolder x = handler.addServletWithMapping(Cruiselet.class, slashCruiseServletName);
				x.setInitParameter("gzip", "true");
				x.setInitOrder(1);
				//pin = mapper.readValue(plugInFo, PluginNames.class);


				try {
					server.start();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					executor.shutdown();
				}

				// The use of server.join() the will make the current thread join and
				// wait until the server is done executing.
				// See
				// http://docs.oracle.com/javase/7/docs/api/java/lang/Thread.html#join()
				try {
					System.out.println("Server is being joined");
					server.join();
					serverRunning = true;
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					executor.shutdown();
				}
			//});
			
			
	    				
		}else {
			try {
				executor = Executors.newSingleThreadExecutor();
				executor.submit(() -> {
					System.out.println("Server starting on port:"+port);
					server = new Server(port);
					ServletHandler handler = new ServletHandler();
					server.setHandler(handler);
					
					ServletHolder x = handler.addServletWithMapping(Cruiselet.class, slashCruiseServletName);
					x.setInitParameter("gzip", "true");
					x.setInitOrder(1);
					// Start things up!
					try {
						server.start();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					try {
						System.out.println("Server is being joined");
						serverRunning = true;
						server.join();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("Server started.");
				});
			}catch(Exception err) {
				err.printStackTrace();
			}
			serverRunning = true;
			System.out.println("Done!");
		}
	}
	private static void stopServer() {
		if(serverRunning) {
			try {
				if(null != server)
					server.stop();
				if(null != executor)
					executor.shutdownNow();
				System.out.println("Server has been shut down.");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		serverRunning = false;
		server = null;
	}
	public static String getApplicationName() {
		return applicationName;
	}
	public static void setApplicationName(String applicationName) {
		Main.applicationName = applicationName;
	}
	public static String getServerName() {
		return serverName;
	}
	public static void setServerName(String serverName) {
		Main.serverName = serverName;
	}
	public static String getTargetURL() {
		return targetURL;
	}
	public static void setTargetURL(String targetURL) {
		Main.targetURL = targetURL;
	}
	public static String getThisURL() {
		return thisURL;
	}
	public static void setThisURL(String thisURL) {
		Main.thisURL = thisURL;
	}
	public static String getPortNumber() {
		return portNumber;
	}
	public static void setPortNumber(String portNumber) {
		Main.portNumber = portNumber;
	}



}
