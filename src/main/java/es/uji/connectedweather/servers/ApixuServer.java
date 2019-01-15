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

public class ApixuServer implements IWeatherServer
{
	
	private static final String BASE_URL = "http://api.apixu.com/v1/";
	private static final String API_KEY = "0e11061e8f3a4dc2808184845180412";
	private static final String HISTORICAL_URL = BASE_URL + "history.json?key=" + API_KEY;
	private static final String CURRENT_WEATHER_URL = BASE_URL + "current.json?key=" + API_KEY;
	private static final String FORECAST_URL = BASE_URL + "forecast.json?days=5&key=" + API_KEY;
	
	@Override
	public Map<String, String> getCurrentWeather(String city)
	{
		if (city == null) throw new NullPointerException();
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
			return null;
		}
		return map;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Map<String, String>[] getNextWeekWeather(String city)
	{
		if (city == null) throw new NullPointerException();
		HashMap<String, String>[] maps = new HashMap[5];
		try
		{
			JSONObject weatherData;
			JSONObject forecastData;
			HashMap<String, String> map;
			String response = Utils.makeGETRequest(new URL(FORECAST_URL + "&q=" + city));
			JSONObject data = Utils.parseJSON(response);
			JSONObject locationData = (JSONObject)data.get("location");
			JSONArray forecasts = Utils.readJSONObject(data, "forecast", "forecastday");
			String cityName = locationData.get("name").toString();
			String country = locationData.get("country").toString();
			for (int currDayIndex = 0; currDayIndex < maps.length; currDayIndex++)
			{
				forecastData = (JSONObject)forecasts.get(currDayIndex);
				weatherData = (JSONObject)forecastData.get("day");
				map = new HashMap<String, String>();
				map.put("date", forecastData.get("date").toString());
				map.put("city", cityName);
				map.put("country", country);
				populateWeatherData(map, weatherData);
				map.put("wind", weatherData.get("maxwind_kph").toString());
				map.put("wind_degree", "N/A");
				map.put("pressure", "N/A");
				map.put("clouds", "N/A");
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
	
	private void populateWeatherData(Map<String, String> map, JSONObject weatherData)
	{
		map.put("temperature", weatherData.get("avgtemp_c").toString());
		map.put("condition", Utils.readJSONObject(weatherData, "condition", "text"));
		map.put("precipitation", weatherData.get("totalprecip_mm").toString());
		map.put("humidity", weatherData.get("avghumidity").toString());
		map.put("visibility", weatherData.get("avgvis_km").toString());
		map.put("min_temp", weatherData.get("mintemp_c").toString());
		map.put("max_temp", weatherData.get("maxtemp_c").toString());
	}

	@Override
	public Map<String, String> getHistoricalData(String city, LocalDate date)
	{
		if (city == null) throw new NullPointerException();
		if (date.getYear() >= 2015)
		{
			if (date.getYear() == LocalDate.now().getYear() &&
					date.getDayOfYear() == LocalDate.now().getDayOfYear()) {
				return null;
			}
			HashMap<String, String> map = new HashMap<String, String>();
			try
			{
				float windSum = 0;
				int cloudsSum = 0;
				JSONObject hourData;
				float pressureSum = 0;
				int windDegreeSum = 0;
				String response = Utils.makeGETRequest( new URL(HISTORICAL_URL + "&q=" + city + "&dt=" + date.toString()) );
				JSONObject data = Utils.parseJSON(response);
				JSONObject locationData = (JSONObject)data.get("location");
				JSONObject historicalData = (JSONObject)Utils.<JSONArray>readJSONObject(data, "forecast", "forecastday").get(0);
				JSONArray hoursData = (JSONArray)historicalData.get("hour");
				map.put("date", historicalData.get("date").toString());
				map.put("city", locationData.get("name").toString());
				map.put("country", locationData.get("country").toString());
				populateWeatherData(map, (JSONObject)historicalData.get("day"));
				for (int currHourIndex = 0; currHourIndex < hoursData.size(); currHourIndex++)
				{
					hourData = (JSONObject)hoursData.get(currHourIndex);
					windSum += (double)hourData.get("wind_kph");
					windDegreeSum += (long)hourData.get("wind_degree");
					pressureSum += (double)hourData.get("pressure_mb");
					cloudsSum += (long)hourData.get("cloud");
					
				}
				map.put( "wind", Float.toString(windSum / hoursData.size()) );
				map.put( "wind_degree", Float.toString(windDegreeSum / (float)hoursData.size()) );
				map.put( "pressure", Float.toString(pressureSum / hoursData.size()) );
				map.put( "clouds", Float.toString(cloudsSum / (float)hoursData.size()) );
			}
			catch (IOException | ParseException e)
			{
				e.printStackTrace();
				return null;
			}
			return map;
		}
		return null;
	}

	@Override
	public Map<String, String> getAlerts(String city)
	{
		return null; //Not supported...
	}

	@Override
	public String getName()
	{
		return "Apixu";
	}
	
}
