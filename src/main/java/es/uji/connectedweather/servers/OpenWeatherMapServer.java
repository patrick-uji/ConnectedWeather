package es.uji.connectedweather.servers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ConnectException;
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
		if (city == null)
		{
			throw new NullPointerException();
		}
		HashMap<String, String> map = new HashMap<String, String>();
		try
		{
			String response = Utils.makeGETRequest(new URL(CURRENT_WEATHER_URL + "&q=" + city));
			JSONObject data = Utils.parseJSON(response);
			JSONObject windData = (JSONObject)data.get("wind");
			JSONObject weatherData = (JSONObject)data.get("main");
			Object windDegree = safeFieldGet(windData, "deg"); //For some reason the "deg" field seems to be missing now...
			map.put("date", LocalDate.now().toString());
			populateWeatherData(map, data, weatherData, windData);
			map.put("pressure", Utils.round((long)weatherData.get("pressure"), 1) + " mb");
			map.put("visibility", Utils.round((long)data.get("visibility") / 1000F, 1) + " km"); //meters -> km
			map.put("wind_degree", windDegree != null ? windDegree.toString() : "N/A");
			map.put("country", Utils.readJSONObject(data, "sys", "country").toString());
			map.put("city", data.get("name").toString());
			map.put("precipitation", "N/A");
		}
		catch (ConnectException | FileNotFoundException ex) //Connection timeout | 404, city not found
		{
			return null;
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
		double windSpeed = unboxAndCastToDouble(windData.get("speed"));
		map.put("condition", conditionData.get("main").toString());
		map.put("temperature", getAndConvertFromKelvin(weatherData, "temp"));
		map.put("humidity", weatherData.get("humidity").toString());
		map.put("min_temp", getAndConvertFromKelvin(weatherData, "temp_min"));
		map.put("max_temp", getAndConvertFromKelvin(weatherData, "temp_max"));
		map.put( "wind", Utils.round(windSpeed * 3.6F, 1) + " km/h"); //m/s -> km/h
		map.put("clouds", Utils.readJSONObject(data, "clouds", "all").toString());
	}
	
	private double unboxAndCastToDouble(Object windSpeedPTR)
	{
		if (windSpeedPTR.getClass() == Double.class)
		{
			return (double)windSpeedPTR;
		}
		else
		{
			return (double)(long)windSpeedPTR; //Unbox and cast
		}
	}
	
	private String getAndConvertFromKelvin(JSONObject weatherData, String key)
	{
		double temperature = (double)weatherData.get(key) - 273.15;
		if (units == TemperatureUnit.FAHRENHEIT)
		{
			temperature = Utils.celsiusToFahrenheit(temperature);
		}
		return Utils.round(temperature, 1) + units.getSymbol();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Map<String, String>[] getWeatherForecast(String city)
	{
		if (city == null)
		{
			throw new NullPointerException();
		}
		HashMap<String, String>[] maps = new HashMap[5];
		try
		{
			JSONObject windData;
			Object windDegreePTR;
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
				windDegreePTR = safeFieldGet(windData, "deg"); //For some reason the "deg" field seems to be missing now...
				map.put("date", forecastData.get("dt_txt").toString().split(" ")[0]);
				populateWeatherData(map, forecastData, weatherData, windData);
				map.put("pressure", Utils.round((double)weatherData.get("pressure"), 1) + " mb");
				if (windDegreePTR != null)
				{
					map.put("wind_degree", Long.toString( Math.round(unboxAndCastToDouble(windDegreePTR)) ));
				}
				else
				{
					map.put("wind_degree", "N/A");
				}
				map.put("precipitation", "N/A");
				map.put("visibility", "N/A");
				map.put("country", country);
				map.put("city", cityName);
				maps[currDayIndex] = map;
			}
		}
		catch (ConnectException | FileNotFoundException ex) //Connection timeout | 404, city not found
		{
			return null;
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
