package es.uji.connectedweather.servers;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import es.uji.connectedweather.Utils;

public class ApixuServer implements IWeatherServer
{
	
	private static final String BASE_URL = "http://api.apixu.com/v1";
	private static final String API_KEY = "0e11061e8f3a4dc2808184845180412";
	private static final String CURRENT_WEATHER_URL = BASE_URL + "/current.json?key=" + API_KEY;
	
	@Override
	public Map<String, String> getCurrentWeather(String city)
	{
		HashMap<String, String> map = new HashMap<String, String>();
		try
		{
			String response = Utils.makeGETRequest(new URL(CURRENT_WEATHER_URL + "&q=" + city));
			JSONObject data = Utils.parseJSON(response);
			JSONObject locationData = (JSONObject)data.get("location");
			JSONObject weatherData = (JSONObject)data.get("current");
			map.put("city", locationData.get("name").toString());
			map.put("country", locationData.get("country").toString());
			map.put("temperature", weatherData.get("temp_c").toString());
			map.put("condition", Utils.readJSONObject(weatherData, "condition", "text"));
			map.put("wind", weatherData.get("wind_kph").toString());
			map.put("wind_degree", weatherData.get("wind_degree").toString());
			map.put("pressure", weatherData.get("pressure_mb").toString());
			map.put("precipitation", weatherData.get("precip_mm").toString());
			map.put("humidity", weatherData.get("humidity").toString());
			map.put("clouds", weatherData.get("cloud").toString());
			map.put("visibility", weatherData.get("vis_km").toString());
			map.put("min_temp", "N/A");
			map.put("max_temp", "N/A");
		}
		catch (IOException | ParseException e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	@Override
	public Map<String, String>[] getNextWeekWeather(String city)
	{
		return null;
	}

	@Override
	public Map<String, String> getHistoricalData(String city)
	{
		return null;
	}

	@Override
	public Map<String, String> getAlarms(String city)
	{
		return null;
	}

	@Override
	public String getName()
	{
		return "Apixu";
	}
	
}
