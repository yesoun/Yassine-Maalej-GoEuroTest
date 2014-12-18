/*
 * Author : Yassine Maalej
 * Email & Phone : maalej.yessine@gmail.com  & +21621196229
 * About : GoEuro JavaDeveloper Test : Consume JSON Stream depending on args entry and create corresponding comma separated values file 
 * Last Update : 18/12/2014
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.MalformedURLException;
import java.net.URL;

import	org.json.*;

public class GoEuroTest {
	final String sGoEuroApiUrl="http://api.goeuro.com/api/v2/position/suggest/en/";

	/**
	 * method used to read, parse json objects and will create a JSONArray which will be given into parameter in createcsvfile method
	 * @param sParameter : used as the STRING (args[0]) given when running the program with command. it will be added to the GoEuro's api URL.
	 * @return JSONArray containing all the json objects.
	 */
	public JSONArray  parseGoEuroJsonApi(String sParameter) 
	{
		// used to hold the full url of the API endpoint.
		String sAPIEndPoint= sGoEuroApiUrl+sParameter;
		System.out.println(sAPIEndPoint);
		URL sAPIEndPointUrl = null;
		try 
		{
			sAPIEndPointUrl = new URL(sAPIEndPoint);
		}
		catch (MalformedURLException e) 
		{
			e.printStackTrace();
		}
		
		//inputStream is used to read from web page of the url containing json objects.
		InputStream inputStream = null;
		try
		{
			inputStream = sAPIEndPointUrl.openStream();
		} 
		catch (IOException e ) 
		{
			e.printStackTrace();
		}
		
		
		// bufferRearder is used to read from inputStream.
		BufferedReader bufferRearder = new BufferedReader(new InputStreamReader(inputStream));
		
		// stringBuilder is used to hold the contents(json text) of all the lines in bufferReader.
		StringBuilder stringBuilder = new StringBuilder();
		String line;
		try
		{
			while ((line = bufferRearder.readLine()) != null) 
			{
				stringBuilder.append(line);
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		System.out.println(stringBuilder);
		//jsonArray is used to contain an ordered sequence of objects conform to JSON syntax rules and could be accessed by index.
		JSONArray jsonArray = null ;
		try 
		{
			jsonArray = new JSONArray(stringBuilder.toString().trim());
		}
		catch (JSONException e) 
		{
			e.printStackTrace();
		}
		    
		return jsonArray;		    

	}

	
	/**
	 * method to create a comma separated values file. 	It takes in parameter two variables
	 * @param jsonArray : contains json objects retrieved from the api endpoint and returned by parseGoEuroJsonApi(). 
	 * @param sParameter : used as the STRING (args[0]) given when running the program with command. it will serve as the name of the created csv file.
	 */
	
	public void createCSVFile(JSONArray jsonArray, String sParameter)
	{
		
		// the jsonArray is empty there is no need to create an empty csv file
		if (jsonArray.length()==0)
		return;
		
		// if no existing file with sParameter name , then create it in the same directory of the executed jar
		// the name of the file is the same one given in parameter when running
		File csvFile = new File("./"+sParameter+".txt");
		if(!csvFile.exists())
		try {
				csvFile.createNewFile();
			}
		catch (IOException e)
			{
				e.printStackTrace();
			}
		
		FileWriter fileWriter=null;
		BufferedWriter bufferWriter=null;
		try {
				fileWriter = new FileWriter(csvFile.getAbsoluteFile());
				bufferWriter = new BufferedWriter(fileWriter);
				for (int i = 0; i < jsonArray.length(); i++)
				{
					JSONObject json = jsonArray.getJSONObject(i);
					
					// I didn't use getInt,getLong or getString independently of the type just get(object)
					Object id=json.get("_id") ;
					Object name=json.get("name") ;
					Object type=json.get("type") ;
					// geo_poistion is a JSON object with keys latitude and longitude
					JSONObject geoPosition = json.getJSONObject("geo_position");
					Object latitude=geoPosition.get("latitude") ;
					Object longitude=geoPosition.get("longitude") ;
					// writing in csv file of requested details of ith json object in jsonArray. (O(n) complexity code) 
					bufferWriter.write(id+","+name+","+type+","+latitude+","+longitude);
					System.out.println(id+","+name+","+type+","+latitude+","+longitude);
					//csv with quotes
					//bufferWriter.write("\""+id+"\""+","+"\""+name+"\""+","+"\""+type+"\""+","+"\""+latitude+"\""+","+"\""+longitude+"\"");
					bufferWriter.newLine();
				}
			} 
		catch (IOException | JSONException e) 
			{
				e.printStackTrace();
			}
		finally 
		{
			try
			{
				bufferWriter.close();		   
	     	}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	
	public static void main(String [] args) 
	{
		// if the command line has no parameter or more than one parameter then break
		if (args.length!=1)
		return;
		
		String sParameter = args[0];
		GoEuroTest goeurotest= new GoEuroTest();
		try 
		{
			JSONArray jsonArray= goeurotest.parseGoEuroJsonApi(sParameter);
			goeurotest.createCSVFile(jsonArray, sParameter);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		System.out.println("end program");
		
	}
	
}
