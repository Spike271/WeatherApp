import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class BackEnd
{
	@SuppressWarnings("unchecked")
	public static JSONObject getWeatherData(String locationName)
	{
		JSONArray locationData = getLocationData(locationName);
		JSONObject location = (JSONObject) locationData.get(0);
		
		double latitude = (double) location.get("latitude");
		double longitude = (double) location.get("longitude");
		
		String urlString = "https://api.open-meteo.com/v1/forecast?latitude=" + latitude + "&longitude=" + longitude
				+ "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&timezone=Asia%2FTokyo";
		
		try
		{
			HttpURLConnection conn = fetchApiResponse(urlString);
			if (conn.getResponseCode() != 200)
			{
				System.out.println("Cannot connect to the API");
				return null;
			}
			
			StringBuilder resultJson = new StringBuilder();
			Scanner scanner = new Scanner(conn.getInputStream());
			if (scanner.hasNext())
			{
				resultJson.append(scanner.nextLine());
			}
			
			scanner.close();
			conn.disconnect();
			
			JSONParser parser = new JSONParser();
			JSONObject resultJsonObject = (JSONObject) parser.parse(String.valueOf(resultJson));
			JSONObject hourly = (JSONObject) resultJsonObject.get("hourly");
			JSONArray time = (JSONArray) hourly.get("time");
			int index = findIndexOfCurrentTime(time);
			
			JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
			double tempearture = (double) temperatureData.get(index);
			
			JSONArray weatherCode = (JSONArray) hourly.get("weather_code");
			String weatherCondition = convertWeatherCode((long) weatherCode.get(index));
			
			JSONArray relativeHumidity = (JSONArray) hourly.get("relative_humidity_2m");
			long humidity = (long) relativeHumidity.get(index);
			
			JSONArray windSpeedData = (JSONArray) hourly.get("wind_speed_10m");
			double windSpeed = (double) windSpeedData.get(index);
			
			JSONObject weatherData = new JSONObject();
			weatherData.put("temperature", tempearture);
			weatherData.put("weather_condition", weatherCondition);
			weatherData.put("humidity", humidity);
			weatherData.put("windspeed", windSpeed);
			return weatherData;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		return null;
	}
	
	private static JSONArray getLocationData(String locationName)
	{
		locationName = locationName.replaceAll(" ", "+");
		String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" + locationName
				+ "&count=10&language=en&format=json";
		
		try
		{
			HttpURLConnection conn = fetchApiResponse(urlString);
			if (conn.getResponseCode() != 200)
			{
				System.out.println("Cannot find the data\nPlease try again later");
			}
			else
			{
				StringBuilder resultJSON = new StringBuilder();
				Scanner scanner = new Scanner(conn.getInputStream());
				while (scanner.hasNext())
				{
					resultJSON.append(scanner.nextLine());
				}
				scanner.close();
				conn.disconnect();
				
				JSONParser parser = new JSONParser();
				JSONObject resultJsonObject = (JSONObject) parser.parse(String.valueOf(resultJSON));
				
				JSONArray locationData = (JSONArray) resultJsonObject.get("results");
				return locationData;
			}
		}
		catch (Exception e)
		{
			System.out.println("Unable to fetch the Api");
		}
		return null;
	}
	
	private static HttpURLConnection fetchApiResponse(String urlString)
	{
		try
		{
			URI url = URI.create(urlString);
			HttpURLConnection con = (HttpURLConnection) url.toURL().openConnection();
			con.setRequestMethod("GET");
			con.connect();
			return con;
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	private static int findIndexOfCurrentTime(JSONArray timeList)
	{
		String currentTime = getCurrentTime();
		
		for (int i = 0; i < timeList.size(); i++)
		{
			String time = (String) timeList.get(i);
			if (time.equalsIgnoreCase(currentTime))
			{
				return i;
			}
		}
		return 0;
	}
	
	private static String getCurrentTime()
	{
		LocalDateTime currentDateTime = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:00");
		String formattedDateTime = currentDateTime.format(formatter);
		return formattedDateTime;
	}
	
	private static String convertWeatherCode(long weatherCode)
	{
		String weatherCondition = "";
		
		if (weatherCode == 0L)
			weatherCondition = "Clear";
		
		else if (weatherCode >= 0L && weatherCode <= 3L)
			weatherCondition = "Cloudy";
		
		else if ((weatherCode >= 51L && weatherCode <= 67L) || (weatherCode >= 80L && weatherCode <= 99L))
			weatherCondition = "Rain";
		
		else if (weatherCode >= 71L && weatherCode <= 77L)
			weatherCondition = "Snow";
		
		return weatherCondition;
	}
	
	public static boolean checkInternet()
	{
		boolean check = false;
		try
		{
			InetAddress address = InetAddress.getByName("google.com");
			if (address.isReachable(5000))
				check = true;
		}
		catch (UnknownHostException e)
		{}
		catch (IOException e)
		{}
		return check;
	}
	
}