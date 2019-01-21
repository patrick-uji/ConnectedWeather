package es.uji.connectedweather.servers;

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

public class ApixuServer implements IWeatherServer
{
	
	private static final String BASE_URL = "http://api.apixu.com/v1/";
	private static final String API_KEY = "0e11061e8f3a4dc2808184845180412";
	private static final String HISTORICAL_URL = BASE_URL + "history.json?key=" + API_KEY;
	private static final String CURRENT_WEATHER_URL = BASE_URL + "current.json?key=" + API_KEY;
	private static final String FORECAST_URL = BASE_URL + "forecast.json?days=5&key=" + API_KEY;
	
	private TemperatureUnit units;
	
	public ApixuServer()
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
			JSONObject locationData = (JSONObject)data.get("location");
			JSONObject weatherData = (JSONObject)data.get("current");
			map.put("date", LocalDate.now().toString());
			map.put("city", locationData.get("name").toString());
			map.put("country", locationData.get("country").toString());
			map.put("temperature", getTemperatureString(weatherData, "temp_c"));
			map.put("condition", Utils.readJSONObject(weatherData, "condition", "text"));
			map.put("wind", weatherData.get("wind_kph") + " km/h");
			map.put("wind_degree", weatherData.get("wind_degree").toString());
			map.put("pressure", weatherData.get("pressure_mb") + " mb");
			map.put("precipitation", weatherData.get("precip_mm") + " mm");
			map.put("humidity", weatherData.get("humidity").toString());
			map.put("clouds", weatherData.get("cloud").toString());
			map.put("visibility", weatherData.get("vis_km") + " km");
			map.put("min_temp", "N/A");
			map.put("max_temp", "N/A");
		}
		catch (ConnectException ex) //Connection timeout
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
	
	private String getTemperatureString(JSONObject weatherData, String key)
	{
		double temperature = (double)weatherData.get(key);
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
				map.put("wind", weatherData.get("maxwind_kph") + " km/h");
				map.put("wind_degree", "N/A");
				map.put("pressure", "N/A");
				map.put("clouds", "N/A");
				maps[currDayIndex] = map;
			}
		}
		catch (ConnectException ex) //Connection timeout
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
	
	private void populateWeatherData(Map<String, String> map, JSONObject weatherData)
	{
		map.put("temperature", getTemperatureString(weatherData, "avgtemp_c"));
		map.put("condition", Utils.readJSONObject(weatherData, "condition", "text"));
		map.put("precipitation", weatherData.get("totalprecip_mm") + " mm");
		map.put("humidity", weatherData.get("avghumidity").toString());
		map.put("visibility", weatherData.get("avgvis_km") + " km");
		map.put("min_temp", getTemperatureString(weatherData, "mintemp_c"));
		map.put("max_temp", getTemperatureString(weatherData, "maxtemp_c"));
	}

	@Override
	public Map<String, String> getHistoricalData(String city, LocalDate date)
	{
		if (city == null)
		{
			throw new NullPointerException();
		}
		if (date.getYear() >= 2015 && date.compareTo(LocalDate.now()) < 0)
		{
			HashMap<String, String> map = new HashMap<String, String>();
			try
			{
				int cloudsSum = 0;
				double windSum = 0;
				JSONObject hourData;
				int windDegreeSum = 0;
				double pressureSum = 0;
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
				map.put("wind", Utils.round(windSum / hoursData.size(), 1) + " km/h");
				map.put("wind_degree", Integer.toString( Math.round(windDegreeSum / (float)hoursData.size()) ));
				map.put("pressure", Utils.round(pressureSum / hoursData.size(), 1) + " mb");
				map.put("clouds", Integer.toString( Math.round(cloudsSum / (float)hoursData.size()) ));
			}
			catch (ConnectException ex) //Connection timeout
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
