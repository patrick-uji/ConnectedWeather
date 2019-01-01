package es.uji.connectedweather.servers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import es.uji.connectedweather.Utils;

public class AccuWeatherServer implements IWeatherServer
{
	
	private static final String BASE_URL = "http://dataservice.accuweather.com/";
	private static final String CITY_KEY_URL = BASE_URL + "locations/v1/cities/search?apikey=";
	private static final String CURRENT_WEATHER_URL = BASE_URL + "currentconditions/v1/";
	private static final String API_KEY = "MvwPACViq8icqwPNkBBAWSE9YeP4RmUe";
	
	private class CityInfo
	{
		private String key;
		private String name;
		private String country;
		private CityInfo(JSONObject city)
		{
			this.key = city.get("Key").toString();
			this.name = city.get("EnglishName").toString();
			this.country = Utils.readJSONObject(city, "Country", "EnglishName");
		}
	}

	@Override
	public Map<String, String> getCurrentWeather(String city)
	{
		HashMap<String, String> map = new HashMap<String, String>();
		try
		{
			CityInfo cityInfo = queryCity(city);
			String response = Utils.makeGETRequest(new URL(CURRENT_WEATHER_URL + cityInfo.key + "?apikey=" + API_KEY + "&details=true"));
			JSONObject data = (JSONObject)Utils.<JSONArray>parseJSON(response).get(0);
			JSONObject pastTemperature = Utils.readJSONObject(data, "TemperatureSummary", "Past24HourRange");
			JSONObject windData = (JSONObject)data.get("Wind");
			map.put("city", cityInfo.name);
			map.put("country", cityInfo.country);
			map.put("condition", data.get("WeatherText").toString());
			map.put("temperature", Utils.readJSONObject(data, "Temperature", "Metric", "Value").toString());
			map.put("humidity", data.get("RelativeHumidity").toString());
			map.put("wind", Utils.readJSONObject(windData, "Direction", "Degrees").toString());
			map.put("wind_degree", Utils.readJSONObject(windData, "Speed", "Metric", "Value").toString());
			map.put("visibility", Utils.readJSONObject(data, "Visibility", "Metric", "Value").toString());
			map.put("clouds", data.get("CloudCover").toString());
			map.put("pressure", Utils.readJSONObject(data, "Pressure", "Metric", "Value").toString());
			map.put("precipitation", Utils.readJSONObject(data, "PrecipitationSummary", "Precipitation", "Metric", "Value").toString());
			map.put("min_temp", Utils.readJSONObject(pastTemperature, "Minimum", "Metric", "Value").toString());
			map.put("max_temp", Utils.readJSONObject(pastTemperature, "Maximum", "Metric", "Value").toString());
		}
		catch (IOException | ParseException e)
		{
			e.printStackTrace();
		}
		return map;
	}
	
	private CityInfo queryCity(String city) throws MalformedURLException, IOException, ParseException
	{
		String response = Utils.makeGETRequest(new URL(CITY_KEY_URL + API_KEY + "&q=" + city));
		JSONArray citiesArray = Utils.parseJSON(response);
		return new CityInfo((JSONObject)citiesArray.get(0));
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
		return "AccuWeather";
	}
	
}
