import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class BackEnd
{
	public static Map<String, Object> getWeatherData(String locationName)
	{
		Location location = getCoordinates(locationName);
		
		double latitude = location.latitude;
		double longitude = location.longitude;

        if (latitude == 0.0 && longitude == 0.0) return new HashMap<>();
		
		String urlString = """
                https://api.open-meteo.com/v1/forecast?\
                latitude=%.2f\
                &longitude=%.2f\
                &hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&timezone=Asia%%2FKolkata\
                """.formatted(latitude, longitude);
		try
		{
            WeatherResponse response = getWeather(urlString);
            if (response == null) return new HashMap<>();
			
			Hourly hourly = response.hourly;
			List<String> time = hourly.time;
			int index = findIndexOfCurrentTime(time);

            return getStringObjectMap(hourly, index);
		}
		catch (Exception _) {}
		
		return new HashMap<>();
	}

    private static Map<String, Object> getStringObjectMap(Hourly hourly, int index)
    {
        List<Double> temperatureData = hourly.temperature2m;
        double temperature = temperatureData.get(index);

        List<Integer> weatherCode = hourly.weatherCode;
        String weatherCondition = convertWeatherCode((long) weatherCode.get(index));

        List<Integer> relativeHumidity = hourly.relativeHumidity2m;
        long humidity = (long) relativeHumidity.get(index);

        List<Double> windSpeedData = hourly.windSpeed10m;
        double windSpeed = windSpeedData.get(index);

        Map<String, Object> weatherData = new HashMap<>();
        weatherData.put("temperature", temperature);
        weatherData.put("weather_condition", weatherCondition);
        weatherData.put("humidity", humidity);
        weatherData.put("windspeed", windSpeed);
        return weatherData;
    }

    static Location getCoordinates(String locationName)
	{
		locationName = locationName.replaceAll(" ", "+");
		String urlString = """
                https://geocoding-api.open-meteo.com/v1/search?name=%s&count=1&language=en&format=json\
                """.formatted(locationName);

        GeoResponse data = getLocation(urlString)
                .orElse(new GeoResponse(new ArrayList<>()));

        return data.results.getFirst();
	}

    static WeatherResponse getWeather(String urlString)
    {
        Gson gson = new Gson();
        HttpResponse<String> response = callApi(urlString);

        if (response == null) return null;
        return gson.fromJson(response.body(), WeatherResponse.class);
    }
	
	private static Optional<GeoResponse> getLocation(String urlString)
	{
        Gson gson = new Gson();
        HttpResponse<String> response = callApi(urlString);

        if (response == null) return Optional.empty();
        return Optional.of(gson.fromJson(response.body(), GeoResponse.class));
	}

    private static HttpResponse<String> callApi(String urlString)
    {
        try
        {
            HttpRequest getRequest = HttpRequest.newBuilder()
                    .uri(new URI(urlString))
                    .GET()
                    .build();

            try (HttpClient httpClient = HttpClient.newHttpClient())
            {
                return httpClient.send(getRequest, BodyHandlers.ofString());
            }
        }
        catch (Exception e) {
            System.err.println("Something went wrong!\n" + e);
        }
        return null;
    }
	
	private static int findIndexOfCurrentTime(List<String> timeList)
	{
		String currentTime = getCurrentTime();

		for (int i = 0; i < timeList.size(); i++)
		{
			String time = timeList.get(i);
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
        return currentDateTime.format(formatter);
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
		catch (IOException _) {}
		return check;
	}

    record GeoResponse(List<Location> results) {}

    record Location(double latitude, double longitude) {}

    record WeatherResponse(Hourly hourly) {}

    // Hourly data record
    record Hourly(
            List<String> time,
            @SerializedName("temperature_2m") List<Double> temperature2m,
            @SerializedName("relative_humidity_2m") List<Integer> relativeHumidity2m,
            @SerializedName("weather_code") List<Integer> weatherCode,
            @SerializedName("wind_speed_10m") List<Double> windSpeed10m
    ) {}
}