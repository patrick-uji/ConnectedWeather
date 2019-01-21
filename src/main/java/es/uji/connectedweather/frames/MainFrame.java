package es.uji.connectedweather.frames;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import es.uji.connectedweather.TemperatureUnit;
import es.uji.connectedweather.Utils;
import es.uji.connectedweather.adapters.SwingListModelAsList;
import es.uji.connectedweather.persistance.CitiesFilePersistance;
import es.uji.connectedweather.persistance.ICitiesPersistance;
import es.uji.connectedweather.servers.AccuWeatherServer;
import es.uji.connectedweather.servers.ApixuServer;
import es.uji.connectedweather.servers.IWeatherServer;
import es.uji.connectedweather.servers.OpenWeatherMapServer;

public class MainFrame
{
	
	public static final int FORECAST_DAYS = 5;
	private static final String[] WEATHERDATA_KEYS = { //Using this vector to iterate over the keys in that order
		"date", "city", "country",
		"temperature", "min_temp", "max_temp",
		"condition", "pressure", "humidity",
		"precipitation", "wind", "wind_degree",
		"visibility", "clouds"
	};
	private static final int QUERY_TIMEOUT = 5;
	private static final Color DARK_GREEN = new Color(0, 192, 0);
	private static final Color DARK_ORANGE = new Color(255, 160, 0);
	private ICitiesPersistance favouritesPersistance;
	private Map<String, String>[] loadedMaps; //Using an array to avoid multiple variables for each service
	private SettingsDialog settingsDialog;
	private List<String> favouritesList;
	private IWeatherServer usingServer;
	private List<String> parameterList;
	private IWeatherServer[] servers;
	private MainFrameDesign design;
	private Thread queryThread;
	private int lastPanelIndex;
	
	@SuppressWarnings("unchecked")
	public MainFrame()
	{
		JComboBox<String> serversComboBox;
		this.design = new MainFrameDesign(this);
		this.parameterList = new ArrayList<String>();
		this.settingsDialog = new SettingsDialog(this);
		this.loadedMaps = new Map[1 + FORECAST_DAYS + 1 + 1];
		this.favouritesPersistance = new CitiesFilePersistance("favourites.txt");
		this.servers = new IWeatherServer[] {
			new OpenWeatherMapServer(),
			new AccuWeatherServer(),
			new ApixuServer()
		};
		this.design.getDatePicker().setDateToToday();
		serversComboBox = design.getServersComboBox();
		for (IWeatherServer currWeatherServer : servers)
		{
			serversComboBox.addItem(currWeatherServer.getName());
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run()
			{
				favouritesList = new SwingListModelAsList<String>(design.getFavouriteCitiesList());
				try
				{
					favouritesPersistance.load(favouritesList);
				}
				catch (FileNotFoundException ex) { }
				catch (IOException ex)
				{
					ex.printStackTrace();
				}
			}
		});
		this.usingServer = this.servers[0];
	}
	
	
	public ICitiesPersistance getFavouritesPersistance()
	{
		return favouritesPersistance;
	}

	public void setFavouritesPersistance(ICitiesPersistance favouritesPersistance)
	{
		this.favouritesPersistance = favouritesPersistance;
	}
	
	public List<String> getFavouritesList()
	{
		return favouritesList;
	}

	public void setFavouritesList(List<String> favouritesList)
	{
		this.favouritesList = favouritesList;
	}
	
	public IWeatherServer getUsingServer()
	{
		return usingServer;
	}

	public void setUsingServer(IWeatherServer usingServer)
	{
		this.usingServer = usingServer;
	}
	
	public List<String> getParameterList()
	{
		return parameterList;
	}
	
	public MainFrameDesign getDesign()
	{
		return design;
	}

	public void serversComboBox_SelectionChanged(ActionEvent e)
	{
		usingServer = this.servers[design.getServersComboBox().getSelectedIndex()];
	}
	
	public void citySearchBox_TextChanged(DocumentEvent e)
	{
		boolean enable = !design.getCitySearchBox().getText().equals("");
		enableColoredButton(design.getAddFavouriteCityButton(), DARK_GREEN, enable);
		design.getQueryCityButton().setEnabled(enable);
	}
	
	private void enableColoredButton(JButton button, Color foreColor, boolean enabled)
	{
		button.setForeground(enabled ? foreColor : Color.GRAY);
		button.setEnabled(enabled);
	}
	
	public void queryCityButton_Click(ActionEvent e)
	{
		LocalDate selectedDate = design.getDatePicker().getDate();
		JTextField citySearchBox = design.getCitySearchBox();
		String city = citySearchBox.getText();
		queryThread = new Thread(() -> queryCityService(city, selectedDate));
		design.getServersComboBox().setEnabled(false);
		design.getQueryCityButton().setEnabled(false);
		citySearchBox.setEnabled(false);
		queryThread.start();
	}
	
	private void queryCityService(String city, LocalDate selectedDate)
	{
		Map<String, String> map;
		boolean querySuccessful;
		switch (lastPanelIndex)
		{
			case 0:
				map = executeTimedQuery(() -> getCurrentWeather(city, parameterList));
				querySuccessful = map != null;
				if (querySuccessful)
				{
					loadedMaps[0] = map;
				}
				break;
			case 1:
				Map<String, String>[] maps = executeTimedQuery(() -> usingServer.getWeatherForecast(city));
				querySuccessful = maps != null;
				if (querySuccessful)
				{
					for (int currDayIndex = 0; currDayIndex < maps.length; currDayIndex++)
					{
						loadedMaps[1 + currDayIndex] = maps[currDayIndex];
					}
				}
				else
				{
					showCityNotFoundMessage();
				}
				break;
			case 2:
				map = tryLoadLocalHistoricalData(city, selectedDate);
				if (map == null && Utils.showAudioConfirmDialog("No historical data exists locally.\n\nDo you wish to try and query the selected server for it?", "OPTION: Query server?",
						 										JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
				{
					map = executeTimedQuery(() -> usingServer.getHistoricalData(city, selectedDate));
					if (map == null)
					{
						Utils.showAudioMessageDialog("The selected server does not support this functionality or an invalid city/date was supplied.",
													 "INFO: Unsupported functionality or invalid city/date", JOptionPane.WARNING_MESSAGE);
					}
				}
				querySuccessful = map != null;
				if (querySuccessful)
				{
					loadedMaps[1 + FORECAST_DAYS] = map;
				}
				break;
			default: //case 3
				map = executeTimedQuery(() -> usingServer.getAlerts(city));
				querySuccessful = map != null;
				if (querySuccessful)
				{
					loadedMaps[1 + FORECAST_DAYS + 1] = map;
				}
				else
				{
					Utils.showAudioMessageDialog("The selected server does not support this functionality or an invalid city was supplied.",
												 "INFO: Unsupported functionality or invalid city", JOptionPane.WARNING_MESSAGE);
				}
				break;
		}
		SwingUtilities.invokeLater(() -> handleQueryDone(querySuccessful));
	}
	
	private <T> T executeTimedQuery(Callable<T> task)
	{
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Future<T> future = executor.submit(task);
		try
		{
			try
			{
				return future.get(QUERY_TIMEOUT, TimeUnit.SECONDS);
			}
			catch (TimeoutException ex)
			{
				if (Utils.showAudioConfirmDialog("The server is taking too long to respond...\n\nWould you like to switch servers?", "INFO: Server busy",
												 JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.NO_OPTION)
				{
					return future.get(); //Let's wait until the query is actually done...
				}
				future.cancel(true);
			}
		}
		catch (InterruptedException | ExecutionException ex)
		{
			if (ex.getCause().getClass() == InvalidParameterException.class)
			{
				showCityNotFoundMessage();
			}
			else
			{
				ex.printStackTrace();
			}
		}
		return null;
	}
	
	private void showCityNotFoundMessage()
	{
		Utils.showAudioMessageDialog("The server could not find the supplied city.", "ERROR: City not found", JOptionPane.WARNING_MESSAGE);
	}

	private Map<String, String> tryLoadLocalHistoricalData(String city, LocalDate date)
	{
		File historicalDataFile = new File("history" + File.separator + city + File.separator + date.toString() + ".txt");
		try (Scanner historicalDataReader = new Scanner(historicalDataFile))
		{
			String[] splitLine;
			HashMap<String, String> map = new HashMap<String, String>();
			while (historicalDataReader.hasNextLine())
			{
				splitLine = historicalDataReader.nextLine().split("=");
				map.put(splitLine[0], splitLine[1]);
			}
			return map;
		}
		catch (FileNotFoundException ex)
		{
			return null;
		}
	}
	
	private void handleQueryDone(boolean querySucessful)
	{
		if (querySucessful)
		{
			switch (lastPanelIndex)
			{
				case 0:
					refreshTable(design.getCurrentWeatherDataTable(), filterMap(loadedMaps[0], parameterList));
					break;
				case 1:
					for (int currDayIndex = 0; currDayIndex < FORECAST_DAYS; currDayIndex++)
					{
						refreshTable(design.getForecastDataTable(currDayIndex), filterMap(loadedMaps[1 + currDayIndex], parameterList));
					}
					break;
				case 2:
					refreshTable(design.getHistoricalDataTable(), filterMap(loadedMaps[1 + FORECAST_DAYS], parameterList));
					break;
				default: //case 3
					refreshTable(design.getAlertDataTable(), loadedMaps[1 + FORECAST_DAYS + 1]);
					break;
			}
			design.getSaveWeatherDataButton().setEnabled(true);
		}
		design.getServersComboBox().setEnabled(true);
		design.getQueryCityButton().setEnabled(true);
		design.getCitySearchBox().setEnabled(true);
	}
	
	private void refreshTable(JTable table, Map<String, String> map)
	{
		DefaultTableModel tableModel = (DefaultTableModel)table.getModel();
		if (map != null)
		{
			String currValue;
			int currRowIndex = 0;
			tableModel.setRowCount(map.size());
			for (String currKey : WEATHERDATA_KEYS)
			{
				currValue = map.get(currKey);
				if (currValue != null)
				{
					tableModel.setValueAt(currKey, currRowIndex, 0);
					tableModel.setValueAt(map.get(currKey), currRowIndex, 1);
					currRowIndex++;
				}
			}
		}
		else
		{
			tableModel.setRowCount(0);
		}
	}
	
	public void addFavouriteCityButton_Click(ActionEvent e)
	{
		JTextField citySearchBox = design.getCitySearchBox();
		favouritesList.add(citySearchBox.getText());
		citySearchBox.setText("");
	}
	
	public void serviceComboBox_SelectionChanged(ActionEvent e)
	{
		design.getServicePanel(lastPanelIndex).setVisible(false);
		lastPanelIndex = design.getServiceComboBox().getSelectedIndex();
		design.getServicePanel(lastPanelIndex).setVisible(true);
		design.getSaveWeatherDataButton().setEnabled(getServiceLoadedMap() != null);
	}
	
	private Map<String, String> getServiceLoadedMap()
	{
		switch (lastPanelIndex)
		{
			case 0: return loadedMaps[0];
			case 1: return loadedMaps[1 + design.getForecastDayTabs().getSelectedIndex()];
			case 2: return loadedMaps[1 + FORECAST_DAYS];
			default: return loadedMaps[1 + FORECAST_DAYS + 1];
		}
	}
	
	public void favouriteCitiesList_ValueChanged(ListSelectionEvent e)
	{
		JList<String> favouriteCitiesList = design.getFavouriteCitiesList();
		JTextField editFavouriteCityBox = design.getEditFavouriteCityBox();
		boolean enable = favouriteCitiesList.getModel().getSize() != 0;
		if (enable)
		{
			String selectedCity = favouriteCitiesList.getSelectedValue();
			design.getCitySearchBox().setText(selectedCity);
			editFavouriteCityBox.setText(selectedCity);
		}
		else
		{
			editFavouriteCityBox.setText("");
			design.getCitySearchBox().setText("");
		}
		editFavouriteCityBox.setEnabled(enable);
		enableColoredButton(design.getEditFavouriteCityButton(), DARK_ORANGE, enable);
		enableColoredButton(design.getRemoveFavouriteCityButton(), Color.RED, enable);
	}
	
	public void editFavouriteCityBox_TextChanged(DocumentEvent e)
	{
		boolean enabled = !design.getEditFavouriteCityBox().getText().equals("");
		design.getEditFavouriteCityButton().setEnabled(enabled);
	}
	
	public void editFavouriteCityButton_Click(ActionEvent e)
	{
		String newCity = design.getEditFavouriteCityBox().getText();
		favouritesList.set(design.getFavouriteCitiesList().getSelectedIndex(), newCity);
		design.getCitySearchBox().setText(newCity);
	}
	
	public void removeFavouriteCityButton_Click(ActionEvent e)
	{
		favouritesList.remove(design.getFavouriteCitiesList().getSelectedIndex());
	}
	
	public void openSettingsButton_Click(ActionEvent e)
	{
		settingsDialog.show();
	}
	
	public void settingsDialog_OK()
	{
		SettingsDialogDesign settingsDialogDesign = settingsDialog.getDesign();
		updateParameterList(settingsDialogDesign);
		setNewUnits(settingsDialogDesign);
		refreshServiceTables();
	}
	
	private void updateParameterList(SettingsDialogDesign settingsDialogDesign)
	{
		parameterList.clear();
		for (JCheckBox currParamBox : settingsDialogDesign.getParamCheckBoxes())
		{
			if (currParamBox.isSelected())
			{
				parameterList.add(currParamBox.getText());
			}
		}
	}
	
	private void setNewUnits(SettingsDialogDesign settingsDialogDesign)
	{
		int selectedUnitIndex = settingsDialogDesign.getTemperatureUnitsBox().getSelectedIndex();
		TemperatureUnit newUnits = selectedUnitIndex == 0 ? TemperatureUnit.CELSIUS : TemperatureUnit.FAHRENHEIT;
		for (IWeatherServer currServer : servers)
		{
			currServer.setTemperatureUnits(newUnits);
		}
	}
	
	private void refreshServiceTables()
	{
		safeTableRefresh(design.getCurrentWeatherDataTable(), loadedMaps[0]);
		for (int currDayIndex = 0; currDayIndex < FORECAST_DAYS; currDayIndex++)
		{
			safeTableRefresh(design.getForecastDataTable(currDayIndex), loadedMaps[1 + currDayIndex]);
		}
		safeTableRefresh(design.getHistoricalDataTable(), loadedMaps[1 + FORECAST_DAYS]);
	}
	
	private void safeTableRefresh(JTable table, Map<String, String> loadedMap)
	{
		if (loadedMap != null)
		{
			refreshTable(table, filterMap(loadedMap, parameterList));
		}
	}
	
	public void saveWeatherDataButton_Click(ActionEvent e)
	{
		Map<String, String> map = getServiceLoadedMap();
		String cityFolderPath = "history" + File.separator + map.get("city");
		File weatherFile = new File(cityFolderPath + File.separator + map.get("date") + ".txt");
		File cityFolder = new File(cityFolderPath);
		cityFolder.mkdirs();
		try (FileWriter weatherDataWriter = new FileWriter(weatherFile))
		{
			for (String currKey : WEATHERDATA_KEYS)
			{
				weatherDataWriter.write(currKey + "=" + map.get(currKey) + "\r\n"); //\r\n to properly display it on Window's notepad
			}
			Utils.showAudioMessageDialog("Weather data has been successfully saved!", "INFO: Data successfully saved!", JOptionPane.INFORMATION_MESSAGE);
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}
	
	public void show()
	{
		if (design != null)
		{
			design.setVisible(true);
		}
	}
	
	public void setServer(IWeatherServer server)
	{
		if (server == null)
		{
			throw new InvalidParameterException();
		}
		usingServer = server;
	}
	
	public Map<String, String> getCurrentWeather(String city, List<String> params)
	{
		if (city == null || params == null)
		{
			throw new NullPointerException();
		}
		Map<String, String> weatherData = usingServer.getCurrentWeather(city);
		if (weatherData == null)
		{
			throw new InvalidParameterException();
		}
		return filterMap(weatherData, params);
	}
	
	private Map<String, String> filterMap(Map<String, String> map, List<String> params)
	{
		if (params.size() <= 0)
		{
			return map;
		}
		Map<String, String> filteredMap = new HashMap<String, String>();
		for (String param : params)
		{
			if (map.containsKey(param))
			{
				filteredMap.put(param, map.get(param));
			}
			else
			{
				throw new InvalidParameterException();
			}
		}
		return filteredMap;
	}
	
	public void mainFrame_GainedFocus(WindowEvent e)
	{
		if (!design.isEnabled())
		{
			settingsDialog.getDesign().requestFocus();
		}
	}
	
	public void mainFrame_Closing(WindowEvent e)
	{
		if (favouritesList == null)
		{
			throw new InvalidParameterException();
		}
		try
		{
			favouritesPersistance.save(favouritesList);
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}
	
}
