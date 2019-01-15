package es.uji.connectedweather.servers;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
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
	private static final String FORECAST_URL = BASE_URL + "forecast?appid=" + API_KEY;
	private static final String CURRENT_WEATHER_URL = BASE_URL + "weather?appid=" + API_KEY;
	@SuppressWarnings("unused")
	private static final String HISTORICAL_URL = BASE_URL + "history/city?appid=" + API_KEY;
	
	@Override
	public Map<String, String> getCurrentWeather(String city)
	{
		if (city == null) throw new NullPointerException();
		HashMap<String, String> map = new HashMap<String, String>();
		try
		{
			String response = Utils.makeGETRequest(new URL(CURRENT_WEATHER_URL + "&q=" + city));
			JSONObject data = Utils.parseJSON(response);
			populateWeatherData(map, data);
			map.put("visibility", data.get("visibility").toString());
			map.put("country", Utils.readJSONObject(data, "sys", "country").toString());
			map.put("city", data.get("name").toString());
			map.put("precipitation", "N/A");
		}
		catch (IOException | ParseException e)
		{
			e.printStackTrace();
			return null;
		}
		return map;
	}
	
	private void populateWeatherData(Map<String, String> map, JSONObject data)
	{
		JSONObject windData = (JSONObject)data.get("wind");
		JSONObject weatherData = (JSONObject)data.get("main");
		JSONObject conditionData = (JSONObject)((JSONArray)data.get("weather")).get(0);
		map.put("condition", conditionData.get("main").toString());
		map.put("temperature", weatherData.get("temp").toString());
		map.put("pressure", weatherData.get("pressure").toString());
		map.put("humidity", weatherData.get("humidity").toString());
		map.put("min_temp", weatherData.get("temp_min").toString());
		map.put("max_temp", weatherData.get("temp_max").toString());
		map.put("wind", windData.get("speed").toString());
		map.put("wind_degree", windData.get("deg").toString()); //Comprobar <------------- :(
		map.put("clouds", Utils.readJSONObject(data, "clouds", "all").toString());
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, String>[] getNextWeekWeather(String city)
	{
		if (city == null) throw new NullPointerException();
		HashMap<String, String>[] maps = new HashMap[5];
		try
		{
			JSONObject forecastData;
			HashMap<String, String> map;
			String response = Utils.makeGETRequest(new URL(FORECAST_URL + "&q=" + city));
			JSONObject data = Utils.parseJSON(response);
			JSONArray forecasts = (JSONArray)data.get("list");
			JSONObject cityData = (JSONObject)data.get("city");
			String cityName = cityData.get("name").toString();
			String country = cityData.get("country").toString();
			for (int currDayIndex = 0; currDayIndex < maps.length; currDayIndex++)
			{
				map = new HashMap<String, String>();
				forecastData = (JSONObject)forecasts.get(currDayIndex * 8);
				map.put("date", forecastData.get("dt_txt").toString().split(" ")[0]);
				populateWeatherData(map, forecastData);
				map.put("precipitation", "N/A");
				map.put("visibility", "N/A");
				map.put("country", country);
				map.put("city", cityName);
				maps[currDayIndex] = map;
			}
		}
		catch (IOException | ParseException e)
		{
			e.printStackTrace();
			return null;
		}
		return maps;
	}
	
	@Override
	public Map<String, String> getHistoricalData(String city, LocalDate date)
	{
		return null; //Not supported... (Requires subscription)
	}

	@Override
	public Map<String, String> getAlerts(String city)
	{
		return null; //Alerts by cities not supported & limited info
	}

	@Override
	public String getName()
	{
		return "OpenWeatherMap";
	}
	
}
