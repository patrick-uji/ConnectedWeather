package es.uji.connectedweather.servers;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import es.uji.connectedweather.TemperatureUnit;
import es.uji.connectedweather.Utils;

public class OpenWeatherMapServer implements IWeatherServer
{

	private static final String API_KEY = "a7d5eb730320dc49334b09cf08f7f915";
	private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";
	private static final String FORECAST_URL = BASE_URL + "forecast?appid=" + API_KEY;
	private static final String CURRENT_WEATHER_URL = BASE_URL + "weather?appid=" + API_KEY;
	@SuppressWarnings("unused")
	private static final String HISTORICAL_URL = BASE_URL + "history/city?appid=" + API_KEY;
	
	private TemperatureUnit units;
	
	public OpenWeatherMapServer()
	{
		this.units = TemperatureUnit.CELSIUS;
	}
	
	@Override
	public void setTemperatureUnits(TemperatureUnit units)
	{
		this.units = units;
	}
	
	@Override
	public Map<String, String> getCurrentWeather(String city)
	{
		if (city == null) throw new NullPointerException();
		HashMap<String, String> map = new HashMap<String, String>();
		try
		{
			String response = Utils.makeGETRequest(new URL(CURRENT_WEATHER_URL + "&q=" + city));
			JSONObject data = Utils.parseJSON(response);
			JSONObject windData = (JSONObject)data.get("wind");
			JSONObject weatherData = (JSONObject)data.get("main");
			Long windDegree = safeFieldGet(windData, "deg"); //For some reason the "deg" field seems to be missing now...
			map.put("date", LocalDate.now().toString());
			populateWeatherData(map, data, weatherData, windData);
			map.put( "pressure", Float.toString(Utils.round((long)weatherData.get("pressure"), 1)) );
			map.put( "visibility", Float.toString(Utils.round((long)data.get("visibility") / 1000F, 1)) ); //meters -> km
			map.put("wind_degree", windDegree != null ? windDegree.toString() : "N/A");
			map.put("country", Utils.readJSONObject(data, "sys", "country").toString());
			map.put("city", data.get("name").toString());
			map.put("precipitation", "N/A");
		}
		catch (IOException | ParseException ex)
		{
			ex.printStackTrace();
			return null;
		}
		return map;
	}
	
	@SuppressWarnings("unchecked")
	private <T> T safeFieldGet(JSONObject jsonObject, String key)
	{
		Object field = jsonObject.get(key);
		return field != null ? (T)field : null;
	}
	
	private void populateWeatherData(Map<String, String> map, JSONObject data, JSONObject weatherData, JSONObject windData)
	{
		JSONObject conditionData = (JSONObject)((JSONArray)data.get("weather")).get(0);
		map.put("condition", conditionData.get("main").toString());
		map.put("temperature", getAndConvertFromKelvin(weatherData, "temp"));
		map.put("humidity", weatherData.get("humidity").toString());
		map.put("min_temp", getAndConvertFromKelvin(weatherData, "temp_min"));
		map.put("max_temp", getAndConvertFromKelvin(weatherData, "temp_max"));
		map.put( "wind", Double.toString(Utils.round((double)windData.get("speed") * 3.6F, 1)) ); //m/s -> km/s
		map.put("clouds", Utils.readJSONObject(data, "clouds", "all").toString());
	}
	
	private String getAndConvertFromKelvin(JSONObject weatherData, String key)
	{
		double temperature = (double)weatherData.get(key) - 273.15;
		if (units == TemperatureUnit.FAHRENHEIT)
		{
			temperature = Utils.celsiusToFahrenheit(temperature);
		}
		return Double.toString(Utils.round(temperature, 1));
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Map<String, String>[] getWeatherForecast(String city)
	{
		if (city == null) throw new NullPointerException();
		HashMap<String, String>[] maps = new HashMap[5];
		try
		{
			Double windDegree;
			JSONObject windData;
			JSONObject weatherData;
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
				weatherData = (JSONObject)forecastData.get("main");
				windData = (JSONObject)forecastData.get("wind");
				windDegree = safeFieldGet(windData, "deg"); //For some reason the "deg" field seems to be missing now...
				map.put("date", forecastData.get("dt_txt").toString().split(" ")[0]);
				populateWeatherData(map, forecastData, weatherData, windData);
				map.put( "pressure", Double.toString(Utils.round((double)weatherData.get("pressure"), 1)) );
				map.put("wind_degree", windDegree != null ? windDegree.toString() : "N/A");
				map.put("precipitation", "N/A");
				map.put("visibility", "N/A");
				map.put("country", country);
				map.put("city", cityName);
				maps[currDayIndex] = map;
			}
		}
		catch (IOException | ParseException ex)
		{
			ex.printStackTrace();
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
