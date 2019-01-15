package es.uji.connectedweather;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListModel;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import es.uji.connectedweather.adapters.SwingListModelAsList;
import es.uji.connectedweather.servers.AccuWeatherServer;
import es.uji.connectedweather.servers.ApixuServer;
import es.uji.connectedweather.servers.IWeatherServer;
import es.uji.connectedweather.servers.OpenWeatherMapServer;

public class MainFrame
{
	
	private static final Color DARK_GREEN = new Color(0, 192, 0);
	private static final Color DARK_ORANGE = new Color(255, 160, 0);
	private IWeatherServer usingServer;
	private List<String> parameterList;
	private IWeatherServer[] servers;
	private MainFrameDesign design;
	private int lastPanelIndex;
	
	public MainFrame()
	{
		JComboBox<String> serversComboBox;
		this.design = new MainFrameDesign(this);
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
				parameterList = new SwingListModelAsList<String>(design.getParameterList());
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
		String city = design.getCitySearchBox().getText();
		switch (lastPanelIndex)
		{
			case 0:
				refreshTable(design.getCurrentWeatherDataTable(), getCurrentWeather(city, parameterList));
				break;
			case 1:
				Map<String, String>[] maps = usingServer.getNextWeekWeather(city);
				for (int currDayIndex = 0; currDayIndex < maps.length; currDayIndex++)
				{
					refreshTable(design.getForecastDataTable(currDayIndex), maps[currDayIndex]);
				}
				break;
			case 2:
				//TODO: Check if valid map was returned
				refreshTable( design.getHistoricalDataTable(), usingServer.getHistoricalData(city, design.getDatePicker().getDate()) );
				break;
			case 3:
				//TODO: Check if valid map was returned
				refreshTable( design.getAlertDataTable(), usingServer.getAlerts(city));
				break;
		}
	}
	
	private void refreshTable(JTable table, Map<String, String> map)
	{
		int currRowIndex = 0;
		DefaultTableModel tableModel = (DefaultTableModel)table.getModel();
		tableModel.setRowCount(map.size());
		for (String currKey : map.keySet())
		{
			tableModel.setValueAt(currKey, currRowIndex, 0);
			tableModel.setValueAt(map.get(currKey), currRowIndex, 1);
			currRowIndex++;
		}
	}
	
	public void addFavouriteCityButton_Click(ActionEvent e)
	{
		DefaultListModel<String> listModel = (DefaultListModel<String>)design.getFavouriteCitiesList().getModel();
		JTextField citySearchBox = design.getCitySearchBox();
		listModel.addElement(citySearchBox.getText());
		citySearchBox.setText("");
	}
	
	public void serviceComboBox_SelectionChanged(ActionEvent e)
	{
		design.getServicePanel(lastPanelIndex).setVisible(false);
		lastPanelIndex = design.getServiceComboBox().getSelectedIndex();
		design.getServicePanel(lastPanelIndex).setVisible(true);
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
		}
		editFavouriteCityBox.setEnabled(enable);
		enableColoredButton(design.getEditFavouriteCityButton(), DARK_ORANGE, enable);
		enableColoredButton(design.getRemoveFavouriteCityButton(), Color.RED, enable);
		//TODO: Cambiar el servicio y simular click busqueda?
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
		if (city == null || params == null) throw new NullPointerException();
		Map<String, String> weatherData = usingServer.getCurrentWeather(city);
		if (weatherData == null) throw new InvalidParameterException();
		if (params.size() <= 0) return weatherData;
		Map<String, String> dev = new HashMap<String, String>();
		for (String param:params) {
			if (weatherData.containsKey(param)) {
				dev.put(param, weatherData.get(param));
			}else {
				throw new InvalidParameterException();
			}
		}
		return dev;
	}
	
}
