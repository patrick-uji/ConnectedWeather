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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import es.uji.connectedweather.Utils;
import es.uji.connectedweather.adapters.SwingListModelAsList;
import es.uji.connectedweather.persistance.FileFavouriteCityPersistance;
import es.uji.connectedweather.persistance.IFavouriteCityPersistance;
import es.uji.connectedweather.servers.AccuWeatherServer;
import es.uji.connectedweather.servers.ApixuServer;
import es.uji.connectedweather.servers.IWeatherServer;
import es.uji.connectedweather.servers.OpenWeatherMapServer;

public class MainFrame
{
	
	private static final int TIMEOUT_SECONDS = 5;
	private static final Color DARK_GREEN = new Color(0, 192, 0);
	private static final Color DARK_ORANGE = new Color(255, 160, 0);
	private IFavouriteCityPersistance favouriteCitiesPersistance;
	private Map<String, String>[] loadedMaps; //Using an array to avoid multiple variables for each service
	private List<String> favouriteCitiesList;
	private IWeatherServer usingServer;
	private List<String> parameterList;
	private IWeatherServer[] servers;
	private MainFrameDesign design;
	private int lastPanelIndex;
	
	@SuppressWarnings("unchecked")
	public MainFrame()
	{
		JComboBox<String> serversComboBox;
		this.design = new MainFrameDesign(this);
		this.loadedMaps = new Map[1 + 5 + 1 + 1];
		this.favouriteCitiesPersistance = new FileFavouriteCityPersistance();
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
				favouriteCitiesList = new SwingListModelAsList<String>(design.getFavouriteCitiesList());
				parameterList = new SwingListModelAsList<String>(design.getParameterList());
				try
				{
					favouriteCitiesPersistance.loadFavouriteCities(favouriteCitiesList);
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
	
	public void serversComboBox_SelectionChanged(ActionEvent e)
	{
		usingServer = this.servers[design.getServersComboBox().getSelectedIndex()];
	}
	
	public void citySearchBox_TextChanged(DocumentEvent e)
	{
		boolean enable = !design.getCitySearchBox().getText().equals("");
		design.getSearchCityButton().setEnabled(enable);
		enableColoredButton(design.getAddFavouriteCityButton(), DARK_GREEN, enable);
	}
	
	private void enableColoredButton(JButton button, Color foreColor, boolean enabled)
	{
		button.setEnabled(enabled);
		button.setForeground(enabled ? foreColor : Color.GRAY);
	}
	
	public void searchCityButton_Click(ActionEvent e)
	{
		Map<String, String> map;
		boolean enableSaveButton;
		String city = design.getCitySearchBox().getText();
		switch (lastPanelIndex)
		{
			case 0:
				map = getCurrentWeather(city, parameterList);
				refreshTable(design.getCurrentWeatherDataTable(), map);
				enableSaveButton = map != null;
				loadedMaps[0] = map;
				break;
			case 1:
				Map<String, String>[] maps = usingServer.getWeatherForecast(city);
				for (int currDayIndex = 0; currDayIndex < maps.length; currDayIndex++)
				{
					refreshTable(design.getForecastDataTable(currDayIndex), maps[currDayIndex]);
					loadedMaps[1 + currDayIndex] = maps[currDayIndex];
				}
				enableSaveButton = maps != null;
				break;
			case 2:
				LocalDate selectedDate = design.getDatePicker().getDate();
				map = tryLoadLocalHistoricalData(city, selectedDate);
				if (map == null && Utils.showAudioConfirmDialog("No historical data exists locally.\n\nDo you wish to try and query the selected server for it?", "OPTION: Query server?",
						 										JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION)
				{
					map = usingServer.getHistoricalData(city, selectedDate);
					if (map == null)
					{
						Utils.showAudioMessageDialog("The selected server does not support this functionality or an invalid date was supplied.",
													 "INFO: Unsupported functionality or invalid date", JOptionPane.WARNING_MESSAGE);
					}
				}
				refreshTable(design.getHistoricalDataTable(), map);
				enableSaveButton = map != null;
				loadedMaps[1 + 5] = map;
				break;
			default: //case 3
				map = usingServer.getAlerts(city);
				if (map == null)
				{
					Utils.showAudioMessageDialog("The selected server does not support this functionality.",
												 "INFO: Unsupported functionality", JOptionPane.WARNING_MESSAGE);
				}
				refreshTable(design.getAlertDataTable(), map);
				enableSaveButton = map != null;
				loadedMaps[1 + 5 + 1] = map;
				break;
		}
		design.getSaveWeatherDataButton().setEnabled(enableSaveButton);
	}
	
	private Map<String, String> executeTaskAndHandleTimeout(Callable<Map<String, String>> task)
	{
		/*
		try
		{
			//return Utils.timeoutTask(task, TIMEOUT_SECONDS, TimeUnit.SECONDS, false);
		}
		catch (TimeoutException ex)
		{
			if (Utils.showAudioConfirmDialog("The server is taking to long to respond.\n\nWould you like to switch servers?", "INFO: Server busy", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION)
			{
				//task.
			}
			return null;
		}
		catch (InterruptedException | ExecutionException ex)
		{
			ex.printStackTrace();
			return null;
		}
		*/
		return null;
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
	
	private void refreshTable(JTable table, Map<String, String> map)
	{
		DefaultTableModel tableModel = (DefaultTableModel)table.getModel();
		if (map != null)
		{
			int currRowIndex = 0;
			tableModel.setRowCount(map.size());
			for (String currKey : map.keySet())
			{
				tableModel.setValueAt(currKey, currRowIndex, 0);
				tableModel.setValueAt(map.get(currKey), currRowIndex, 1);
				currRowIndex++;
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
		favouriteCitiesList.add(citySearchBox.getText());
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
			case 2: return loadedMaps[1 + 5];
			default: return loadedMaps[1 + 5 + 1];
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
		//TODO: Cambiar el servicio y simular click busqueda?
	}
	
	public void editFavouriteCityBox_TextChanged(DocumentEvent e)
	{
		boolean enabled = !design.getEditFavouriteCityBox().getText().equals("");
		design.getEditFavouriteCityButton().setEnabled(enabled);
	}
	
	public void editFavouriteCityButton_Click(ActionEvent e)
	{
		String newCity = design.getEditFavouriteCityBox().getText();
		favouriteCitiesList.set(design.getFavouriteCitiesList().getSelectedIndex(), newCity);
		design.getCitySearchBox().setText(newCity);
	}
	
	public void removeFavouriteCityButton_Click(ActionEvent e)
	{
		favouriteCitiesList.remove(design.getFavouriteCitiesList().getSelectedIndex());
	}
	
	public void openSettingsButton_Click(ActionEvent e)
	{
		
	}
	
	public void saveWeatherDataButton_Click(ActionEvent e)
	{
		Map<String, String> map = getServiceLoadedMap();
		String cityFolderPath = "history" + File.separator + map.get("city");
		File cityFolder = new File(cityFolderPath);
		File weatherFile = new File(cityFolderPath + File.separator + map.get("date") + ".txt");
		cityFolder.mkdirs();
		try (FileWriter weatherDataWriter = new FileWriter(weatherFile))
		{
			for (String currKey : map.keySet())
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
	
	public void setServer(IWeatherServer server) {
		if (server == null) throw new InvalidParameterException();
		usingServer = server;
	}
	
	public Map<String, String> getCurrentWeather(String city, List<String> params) {
		if (city == null || params == null)
		{
			throw new NullPointerException();
		}
		Map<String, String> weatherData = usingServer.getCurrentWeather(city);
		if (weatherData == null)
		{
			throw new InvalidParameterException();
		}
		if (params.size() <= 0)
		{
			return weatherData;
		}
		Map<String, String> reducedMap = new HashMap<String, String>();
		for (String param : params)
		{
			if (weatherData.containsKey(param))
			{
				reducedMap.put(param, weatherData.get(param));
			}
			else
			{
				throw new InvalidParameterException();
			}
		}
		return reducedMap;
	}
	
	public void mainFrame_Closing(WindowEvent e)
	{
		try
		{
			favouriteCitiesPersistance.saveFavouriteCities(favouriteCitiesList);
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}
	}
	
}
