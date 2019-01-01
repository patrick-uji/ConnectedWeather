package es.uji.connectedweather.servers;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import es.uji.connectedweather.Utils;

public class OpenWeatherMapServer implements IWeatherServer
{

	private static final String API_KEY = "a7d5eb730320dc49334b09cf08f7f915";
	private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";
	private static final String CURRENT_WEATHER_URL = BASE_URL + "/weather?appid=" + API_KEY;
	
	@Override
	public Map<String, String> getCurrentWeather(String city)
	{
		HashMap<String, String> map = new HashMap<String, String>();
		try
		{
			String response = Utils.makeGETRequest(new URL(CURRENT_WEATHER_URL + "&q=" + city));
			JSONObject data = Utils.parseJSON(response);
			JSONObject windData = (JSONObject)data.get("wind");
			JSONObject weatherData = (JSONObject)data.get("main");
			JSONObject conditionData = (JSONObject)((JSONArray)data.get("weather")).get(0);
			map.put("condition", conditionData.get("main").toString());
			map.put("temperature", weatherData.get("temp").toString());
			map.put("pressure", weatherData.get("pressure").toString());
			map.put("humidity", weatherData.get("humidity").toString());
			map.put("min_temp", weatherData.get("temp_min").toString());
			map.put("max_temp", weatherData.get("temp_max").toString());
			map.put("visibility", data.get("visibility").toString());
			map.put("wind", windData.get("speed").toString());
			map.put("wind_degree", windData.get("deg").toString());
			map.put("clouds", Utils.readJSONObject(data, "clouds", "all").toString());
			map.put("country", Utils.readJSONObject(data, "sys", "country").toString());
			map.put("city", data.get("name").toString());
			map.put("precipitation", "N/A");
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
		return "OpenWeatherMap";
	}
	
}
