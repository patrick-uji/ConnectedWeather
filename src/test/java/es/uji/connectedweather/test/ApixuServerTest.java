package es.uji.connectedweather.test;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;
import org.mockito.Mockito;
import es.uji.connectedweather.servers.IWeatherServer;
import static org.junit.Assert.*;

public class ApixuServerTest
{
	
	private IWeatherServer server;
	
	public ApixuServerTest()
	{
		Map<String, String> mockDictionary = new HashMap<String, String>();
		mockDictionary.put("city", "Madrid");
		mockDictionary.put("country", "Spain");
		mockDictionary.put("temperature", "10");
		mockDictionary.put("condition", "Clear");
		mockDictionary.put("wind", "0");
		mockDictionary.put("wind_degree", "0");
		mockDictionary.put("pressure", "1024");
		mockDictionary.put("precipitation", "0");
		mockDictionary.put("humidity", "82");
		mockDictionary.put("visibility", "10");
		this.server = Mockito.mock(IWeatherServer.class);
		Mockito.when(this.server.getCurrentWeather("Madrid"))
		       .thenReturn(mockDictionary);
	}
	
	@Test
	public void getCurrentWeather_ciudadReal_diccionario()
	{
		assertNotNull(server.getCurrentWeather("Madrid"));
	}
	
	@Test
	public void getCurrentWeather_ciudadInvalida_diccionarioNulo()
	{
		assertNull(server.getCurrentWeather("asdgdssdf"));
	}
	
	@Test(expected = NullPointerException.class)
	public void getCurrentWeather_ciudadNula_excepcion()
	{
		server.getCurrentWeather(null);
	}
	
}
