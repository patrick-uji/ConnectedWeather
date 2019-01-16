package es.uji.connectedweather.servers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
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
	private static final String FORECAST_URL = BASE_URL + "forecasts/v1/daily/5day/";
	private static final String HISTORICAL_URL = BASE_URL + "currentconditions/v1/";
	private static final String API_KEY = "MvwPACViq8icqwPNkBBAWSE9YeP4RmUe";
	private static final String ALERTS_URL = BASE_URL + "alerts/v1/";
	
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
		if (city == null) throw new NullPointerException();
		HashMap<String, String> map = new HashMap<String, String>();
		try
		{
			CityInfo cityInfo = queryCity(city);
			String response = Utils.makeGETRequest(new URL(CURRENT_WEATHER_URL + cityInfo.key + "?details=true&apikey=" + API_KEY));
			JSONObject data = (JSONObject)Utils.<JSONArray>parseJSON(response).get(0);
			map.put("city", cityInfo.name);
			map.put("country", cityInfo.country);
			populateData(map, data);
		}
		catch (IOException | ParseException ex)
		{
			ex.printStackTrace();
			return null;
		}
		return map;
	}
	
	private CityInfo queryCity(String city) throws MalformedURLException, IOException, ParseException
	{
		String response = Utils.makeGETRequest(new URL(CITY_KEY_URL + API_KEY + "&q=" + city));
		JSONArray citiesArray = Utils.parseJSON(response);
		return new CityInfo((JSONObject)citiesArray.get(0));
	}

	private void populateData(Map<String, String> map, JSONObject data)
	{
		JSONObject pastTemperature = Utils.readJSONObject(data, "TemperatureSummary", "Past24HourRange");
		JSONObject windData = (JSONObject)data.get("Wind");
		map.put("condition", data.get("WeatherText").toString());
		map.put("temperature", Utils.readJSONObject(data, "Temperature", "Metric", "Value").toString());
		map.put("humidity", data.get("RelativeHumidity").toString());
		map.put("wind", Utils.readJSONObject(windData, "Speed", "Metric", "Value").toString());
		map.put("wind_degree", Utils.readJSONObject(windData, "Direction", "Degrees").toString());
		map.put("visibility", Utils.readJSONObject(data, "Visibility", "Metric", "Value").toString());
		map.put("clouds", data.get("CloudCover").toString());
		map.put("pressure", Utils.readJSONObject(data, "Pressure", "Metric", "Value").toString());
		map.put("precipitation", Utils.readJSONObject(data, "PrecipitationSummary", "Precipitation", "Metric", "Value").toString());
		map.put("min_temp", Utils.readJSONObject(pastTemperature, "Minimum", "Metric", "Value").toString());
		map.put("max_temp", Utils.readJSONObject(pastTemperature, "Maximum", "Metric", "Value").toString());
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Map<String, String>[] getNextWeekWeather(String city)
	{
		if (city == null) throw new NullPointerException();
		HashMap<String, String>[] maps = new HashMap[5];
		try
		{
			JSONObject windData;
			JSONObject weatherData;
			JSONObject forecastData;
			JSONObject pastTemperature;
			HashMap<String, String> map;
			CityInfo cityInfo = queryCity(city);
			String response = Utils.makeGETRequest(new URL(FORECAST_URL + cityInfo.key + "?details=true&apikey=" + API_KEY));
			JSONObject data = (JSONObject)Utils.parseJSON(response);
			JSONArray forecasts = (JSONArray)data.get("DailyForecasts");
			for (int currDayIndex = 0; currDayIndex < maps.length; currDayIndex++)
			{
				forecastData = (JSONObject)forecasts.get(currDayIndex);
				pastTemperature = (JSONObject)forecastData.get("Temperature");
				weatherData = (JSONObject)forecastData.get("Day");
				windData = (JSONObject)weatherData.get("Wind");
				map = new HashMap<String, String>();
				map.put("date", forecastData.get("Date").toString().split("T")[0]);
				map.put("city", cityInfo.name);
				map.put("country", cityInfo.country);
				map.put("condition", weatherData.get("IconPhrase").toString());
				map.put("temperature", "N/A"); //Calculate AVG? [(Max + Min) / 2]
				map.put("humidity", "N/A");
				map.put("wind", Utils.readJSONObject(windData, "Speed", "Value").toString());
				map.put("wind_degree", Utils.readJSONObject(windData, "Direction", "Degrees").toString()); //Mph
				map.put("visibility", "N/A");
				map.put("clouds", weatherData.get("CloudCover").toString());
				map.put("pressure", "N/A");
				map.put("precipitation", Utils.readJSONObject(weatherData, "TotalLiquid", "Value").toString()); //Inches
				map.put("min_temp", Utils.readJSONObject(pastTemperature, "Minimum", "Value").toString()); //F�
				map.put("max_temp", Utils.readJSONObject(pastTemperature, "Maximum", "Value").toString()); //F�
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
		if (city == null) throw new NullPointerException();
		if ( date.equals(LocalDate.now().minusDays(1)) )
		{
			HashMap<String, String> map = new HashMap<String, String>();
			try
			{
				CityInfo cityInfo = queryCity(city);
				String response = Utils.makeGETRequest(new URL(HISTORICAL_URL + cityInfo.key + "/historical/24?details=true&apikey=" + API_KEY));
				JSONArray data = Utils.parseJSON(response); 
				JSONObject historicalData = (JSONObject)data.get(data.size() - 1);
				map.put("date", historicalData.get("LocalObservationDateTime").toString().split("T")[0]);
				populateData(map, historicalData);
			}
			catch (IOException | ParseException ex)
			{
				ex.printStackTrace();
				return null;
			}
			return map;
		}
		return null;
	}

	@Override
	public Map<String, String> getAlerts(String city)
	{
		if (city == null) throw new NullPointerException();
		HashMap<String, String> map = new HashMap<String, String>();
		try
		{
			CityInfo cityInfo = queryCity(city);
			String response = Utils.makeGETRequest(new URL(ALERTS_URL + cityInfo.key + "?details=true&apikey=" + API_KEY));
			JSONObject headlineData = (JSONObject)Utils.<JSONObject>parseJSON(response).get("Headline");
			map.put("date", headlineData.get("EffectiveDate").toString().split("T")[0]);
			map.put("severity", headlineData.get("Severity").toString());
			map.put("message", headlineData.get("Text").toString());
			map.put("category", headlineData.get("Category").toString());
			map.put("end_date", headlineData.get("EndDate").toString().split("T")[0]);
		}
		catch (IOException | ParseException ex)
		{
			ex.printStackTrace();
			return null;
		}
		return map;
	}

	@Override
	public String getName()
	{
		return "AccuWeather";
	}
	
}
