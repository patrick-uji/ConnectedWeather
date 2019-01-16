package es.uji.connectedweather.servers;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

public class OpenWeatherMapServerTest {

	private IWeatherServer server;
	private String city;
	
	@Before
	public void setConfiguration() {
		server = new OpenWeatherMapServer();
		city = "Madrid";
	}
	
	//Testing getCurrentWeather 
	
	@Test
	public void getCurrentWeather_normal_ok()
	{
		assertNotNull(server.getCurrentWeather(city));
	}
	
	@Test
	public void getCurrentWeather_invalidCity_diccionarioNulo()
	{
		city = "asdgdssdf";
		assertNull(server.getCurrentWeather(city));
	}
	
	@Test(expected = NullPointerException.class)
	public void getCurrentWeather_nullCity_nullPointerException()
	{
		city = null;
		server.getCurrentWeather(city);
		fail("Expected NullPointerException");
	}
	
	//Testing getNextWeekWeather
	
	@Test
	public void getNextWeekWeather_normal_ok()
	{
		assertNotNull(server.getWeatherForecast(city));
	}
	
	@Test
	public void getNextWeekWeather_invalidCity_diccionarioNulo()
	{
		city = "asdgdssdf";
		assertNull(server.getWeatherForecast(city));
	}
	
	@Test(expected = NullPointerException.class)
	public void getNextWeekWeather_nullCity_nullPointerException()
	{
		city = null;
		server.getWeatherForecast(city);
		fail("Expected NullPointerException");
	}
	

}