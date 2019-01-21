package es.uji.connectedweather;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import es.uji.connectedweather.frames.MainFrame;
import es.uji.connectedweather.persistance.ICitiesPersistance;
import es.uji.connectedweather.servers.IWeatherServer;

public class MainFrameUnitTest
{

	//Class under test
	private MainFrame mainFrame;
	//Mocks for class under test
	private IWeatherServer mockServer;
	private ICitiesPersistance mockPersistance;
	//Another parameters for class under test
	private String city;
	private List<String> params;
	private List<String> favCities;
	
	@Before
	public void setConfiguration()
	{
		//Creating mocks
		mockServer = Mockito.mock(IWeatherServer.class);
		mockPersistance = Mockito.mock(ICitiesPersistance.class);
		//init variables
		city = "Madrid";
		params = new ArrayList<String>();
		favCities = new ArrayList<String>();
		//Create class under test
		mainFrame = new MainFrame();
		mainFrame.setServer(mockServer);
		mainFrame.setFavouritesPersistance(mockPersistance);
		mainFrame.setFavouritesList(favCities);
	}
	
	//Testing setServer
	
	@Test (expected=InvalidParameterException.class)
	public void setServer_null_IllegalArgumentException()
	{
		mainFrame.setServer(null);
		fail("Expected InvalidParameterException");
	}
	
	//Testing getCurrentWeather
	
	@Test
	public void getCurrentWeather_normal_ok()
	{
		//Init variables
		Map<String, String> dev = new HashMap<String, String>();
		dev.put("p", "p");
		params.add("p");
		//Set configuration
		Mockito.when(mockServer.getCurrentWeather(city)).thenReturn(dev);
		//Do test
		Map<String, String> result = mainFrame.getCurrentWeather(city, params);
		assertEquals(result, dev);
		Mockito.verify(mockServer).getCurrentWeather(city);
	}
	
	@Test
	public void getCurrentWeather_lessParams_ok()
	{
		//Init variables
		Map<String, String> dev = new HashMap<String, String>();
		dev.put("p", "p");
		dev.put("q", "q");
		Map<String, String> desired = new HashMap<String, String>();
		desired.put("p", "p");
		params.add("p");
		//Set configuration
		Mockito.when(mockServer.getCurrentWeather(city)).thenReturn(dev);
		//Do test
		Map<String, String> result = mainFrame.getCurrentWeather(city, params);
		assertEquals(result, desired);
		Mockito.verify(mockServer).getCurrentWeather(city);
	}
	
	@Test(expected=InvalidParameterException.class)
	public void getCurrentWeather_invalidCity_InvalidParameterException()
	{
		//Set configuration
		Mockito.when(mockServer.getCurrentWeather(city)).thenReturn(null);
		//Do test
		mainFrame.getCurrentWeather(city, params);
		fail("Expected InvalidParameterException");
	}
	
	@Test(expected=NullPointerException.class)
	public void getCurrentWeather_nullCity_nullPointerException()
	{
		city = null;
		params.add("temperature");
		mainFrame.getCurrentWeather(city, params);
		fail("Expected NullPointerException");
	}
	
	@Test(expected=InvalidParameterException.class)
	public void getCurrentWeather_invalidCity_invalidParameterException()
	{
		String city = "kjhdfjkd";
		params.add("temperature");
		mainFrame.getCurrentWeather(city, params);
		fail("Expected InvalidParameterException");
	}
	
	@Test(expected=NullPointerException.class)
	public void getCurrentWeather_nullParameters_nullPointerException()
	{
		params = null;
		mainFrame.getCurrentWeather(city, params);
		fail("Expected NullPointerException");
	}
	
	@Test
	public void getCurrentWeather_emptyParams_returnAll()
	{
		assertNotNull(mainFrame.getCurrentWeather(city, params));
	}
	
	//Testing mainFrame_Closing
	
	@Test
	public void mainFrameClosing_normal_ok()
	{
		//Init variables
		favCities.add(city);
		//Do test
		mainFrame.mainFrame_Closing(null);
		try
		{
			Mockito.verify(mockPersistance).save(favCities);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			fail("Exception thrown");
		}
	}
	
	@Test
	public void mainFrameClosing_emptyFav_ok()
	{
		//Do test
		mainFrame.mainFrame_Closing(null);
		try
		{
			Mockito.verify(mockPersistance).save(favCities);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			fail("Exception thrown");
		}
	}
	
	@Test (expected = NullPointerException.class)
	public void mainFrameClosing_nullPersistance_NullPointerException()
	{
		//Init variables
		favCities.add(city);
		//Set configuration
		mainFrame.setFavouritesPersistance(null);
		//Do test
		mainFrame.mainFrame_Closing(null);
		fail("Expected NullPointerException");
	}
	
	@Test
	public void mainFrameClosing_invalidFile_None()
	{
		//Init variables
		favCities.add(city);
		try
		{
			//Set configuration
			Mockito.doThrow(IOException.class).when(mockPersistance).save(favCities);
			//Do test
			mainFrame.mainFrame_Closing(null);
		}
		catch (Exception ex)
		{
			ex.printStackTrace();
			fail("No Exception expected");
		}
	}
	
	@Test (expected = InvalidParameterException.class)
	public void mainFrameClosing_nullFavourites_invalidParameterException()
	{
		//Set configuration
		mainFrame.setFavouritesList(null);
		//Do test
		mainFrame.mainFrame_Closing(null);
		fail("Expected InvalidParameterException");
	}
	
	//Init variables
	//Set configuration
	//Do test

}
