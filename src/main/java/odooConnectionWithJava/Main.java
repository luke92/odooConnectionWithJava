package odooConnectionWithJava;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class Main {

	public static void main(String[] args) {
		try {
			
			final String url = "https://demo.odoo.com";
			final String db = "database";
			final String username = "username";
			final String password = "password";
			final String tableCustomers = "res.partner";
			
			
			final XmlRpcClient client = new XmlRpcClient();
			
			int uid = login(client, url, db, username, password);			
			System.out.println("Login : " + uid);
			
			Boolean canReadDataInResPartner = canReadData(url, db, uid, password, tableCustomers);
			System.out.println("Can Read data in Customers: " + canReadDataInResPartner);
			
			int numRecords = numRecords(url, db, uid, password, tableCustomers);
			System.out.println("Quantity number of records in Customers : " + numRecords);
			
			//List<String> userData = getUserData(url, db, uid, password, tableCustomers);
			//System.out.println("User Data " + userData);
		}		
		catch (Exception ex) {
			System.out.println(ex);
		}
		System.out.println("Press any key to continue");
	}
	
	public static ArrayList<String> emptyList(){
		return new ArrayList<String>();
	}
	
	public static List<Object> asList(Object ...objects){
		return  Arrays.asList(objects);
	}
	
	public static HashMap<String, String> emptyMap(){
		return new  HashMap<String, String>();
	}
	
	public static XmlRpcClientConfigImpl GetCommonConfig(XmlRpcClient client, String url) throws MalformedURLException, XmlRpcException {
		XmlRpcClientConfigImpl common_config = new XmlRpcClientConfigImpl();
		common_config.setServerURL(new URL(String.format("%s/xmlrpc/2/common", url)));
		Map<String, String> serverInfo = (Map<String, String>) client.execute(common_config, "version", emptyList());
		return common_config;
	}
	
	public static int login(XmlRpcClient client, String url, String db, String username, String password) throws MalformedURLException, XmlRpcException {
		XmlRpcClientConfigImpl common_config = GetCommonConfig(client, url);
		
		int uid = (int)client.execute(
			    common_config, "authenticate", asList(
			        db, username, password, emptyMap()));
		
		return uid;
	}
	
	public static XmlRpcClient getModels(String url) throws MalformedURLException {
		XmlRpcClient models = new XmlRpcClient() {
			{
				setConfig(new XmlRpcClientConfigImpl() {
					{
						setServerURL(new URL(String.format("%s/xmlrpc/2/object", url)));
					}
				});
			}
		};
		
		return models;
	}
	
	public static Boolean canReadData(String url, String db, int uid, String password, String table) throws XmlRpcException, MalformedURLException {
		XmlRpcClient models = getModels(url);
		
		Boolean readData = (Boolean) models.execute("execute_kw", asList(db, uid, password, table, "check_access_rights",
      		asList("read"), new HashMap() {
      			{
      				put("raise_exception", false);
      			}
      		} ));
		
		return readData;
	}
	
	public static int numRecords(String url, String db, int uid, String password, String table) throws MalformedURLException, XmlRpcException {
		XmlRpcClient models = getModels(url);
		int numRecords = (Integer)models.execute("execute_kw", asList(
			    db, uid, password,
			    table, "search_count",
			    asList(asList(
			        asList("is_company", "=", true),
			        asList("customer", "=", true)))
			));
		
		return numRecords;
	}
	
	public static List<String> getUserData(String url, String db, int uid, String password, String table) throws MalformedURLException, XmlRpcException{
		XmlRpcClient models = getModels(url);
		List<Object> userData = asList((Object[])models.execute("execute_kw", asList(
			    db, uid, password,
			    table, "search",
			    asList(asList(
			        asList("is_company", "=", true),
			        asList("customer", "=", true))),
			    new HashMap() {{ put("offset", 2); put("limit", 5); }}
			)));
		String[] strings = userData.stream().toArray(String[]::new);
		List al = Arrays.asList(strings);
		return al;
	}
	
}
