package es.uji.connectedweather;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JComboBox;
import com.github.lgooddatepicker.components.DatePicker;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JTabbedPane;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.BorderLayout;
import javax.swing.JTable;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.AbstractListModel;
public class MainFrameDesign extends JFrame
{
	private static final long serialVersionUID = 2869286737217332104L;
	private JComboBox<String> serversComboBox;
	private JPanel contentPanel;
	private MainFrame mainFrame;
	private DatePicker datePicker;
	private JPanel[] servicePanels;
	private JTable alertDataTable;
	private JTextField citySearchBox;
	private JTable historicalDataTable;
	private JLabel temperaturUnitsLabel;
	private JList<String> parameterList;
	private JTable[] forecastDataTables;
	private JTable currentWeatherDataTable;
	private JList<String> favouriteCitiesList;
	private JComboBox<String> serviceComboBox;
	private JComboBox<String> temperatureUnitsBox;
	
	public MainFrameDesign(MainFrame mainFrame)
	{
		this();
		this.mainFrame = mainFrame;
	}
	
	public JComboBox<String> getServersComboBox()
	{
		return serversComboBox;
	}
	
	public JTextField getCitySearchBox()
	{
		return citySearchBox;
	}
	
	public JList<String> getFavouriteCitiesList()
	{
		return favouriteCitiesList;
	}
	
	public JComboBox<String> getServiceComboBox()
	{
		return serviceComboBox;
	}
	
	public JPanel getServicePanel(int index)
	{
		return servicePanels[index];
	}
	
	public JTable getCurrentWeatherDataTable()
	{
		return currentWeatherDataTable;
	}
	
	public JTable getForecastDataTable(int index)
	{
		return forecastDataTables[index];
	}
	
	public DatePicker getDatePicker()
	{
		return datePicker;
	}
	
	public JTable getHistoricalDataTable()
	{
		return null;
	}
	
	public JTable getAlertDataTable()
	{
		return alertDataTable;
	}
	
	public JList<String> getParameterList()
	{
		return parameterList;
	}
	
	/**
	 * Create the frame.
	 */
	public MainFrameDesign()
	{
		this.forecastDataTables = new JTable[5];
		setResizable(false);
		setTitle("ConnectedWeather");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 330, 346);
		contentPanel = new JPanel();
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPanel);
		contentPanel.setLayout(null);
		
		serversComboBox = new JComboBox<String>();
		serversComboBox.setBounds(10, 10, 83, 20);
		contentPanel.add(serversComboBox);
		
		citySearchBox = new JTextField();
		citySearchBox.setBounds(105, 10, 135, 20);
		contentPanel.add(citySearchBox);
		citySearchBox.setColumns(10);
		
		JButton searchCityButton = new JButton("Search");
		searchCityButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		searchCityButton.setBounds(250, 9, 65, 23);
		contentPanel.add(searchCityButton);
		
		favouriteCitiesList = new JList<String>();
		favouriteCitiesList.setModel(new DefaultListModel<String>());
		favouriteCitiesList.setBorder(new LineBorder(new Color(0, 0, 0)));
		favouriteCitiesList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent arg0) {
			}
		});
		favouriteCitiesList.setBounds(10, 40, 83, 128);
		contentPanel.add(favouriteCitiesList);
		
		JLabel serviceLabel = new JLabel("Service:");
		serviceLabel.setBounds(150, 41, 46, 14);
		contentPanel.add(serviceLabel);
		
		serviceComboBox = new JComboBox<String>();
		serviceComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				mainFrame.serviceComboBox_SelectionChanged(arg0);
			}
		});
		serviceComboBox.setModel(new DefaultComboBoxModel<String>(new String[] {"Current Weather", "Weather Forecast", "Historical Data", "Weather Alarms"}));
		serviceComboBox.setBounds(195, 38, 120, 20);
		contentPanel.add(serviceComboBox);

		JPanel currentWeatherPanel = new JPanel();
		currentWeatherPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		currentWeatherPanel.setBounds(105, 70, 210, 115);
		contentPanel.add(currentWeatherPanel);
		currentWeatherPanel.setLayout(new BorderLayout(0, 0));
		
		currentWeatherDataTable = new JTable();
		currentWeatherDataTable.setModel(createMapTableModel());
		currentWeatherPanel.add(currentWeatherDataTable);
		
		JPanel weatherForecastPanel = new JPanel();
		weatherForecastPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		weatherForecastPanel.setBounds(105, 70, 210, 115);
		weatherForecastPanel.setVisible(false);
		contentPanel.add(weatherForecastPanel);
		weatherForecastPanel.setLayout(new BorderLayout(0, 0));

		JTable newTable;
		int currForecastIndex = 0;
		int nextForecastIndex = 1;
		JTabbedPane forecastDayTabs = new JTabbedPane(JTabbedPane.TOP);
		while (currForecastIndex < forecastDataTables.length)
		{
			newTable = new JTable();
			forecastDayTabs.add("Day " + nextForecastIndex, newTable);
			forecastDataTables[currForecastIndex] = newTable;
			newTable.setModel(createMapTableModel());
			currForecastIndex = nextForecastIndex;
			nextForecastIndex++;
		}
		weatherForecastPanel.add(forecastDayTabs);
		
		JPanel historicalDataPanel = new JPanel();
		historicalDataPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		historicalDataPanel.setBounds(105, 70, 210, 115);
		historicalDataPanel.setVisible(false);
		contentPanel.add(historicalDataPanel);
		historicalDataPanel.setLayout(new BorderLayout(0, 0));
		
		datePicker = new DatePicker();
		historicalDataPanel.add(datePicker, BorderLayout.NORTH);
		
		historicalDataTable = new JTable();
		historicalDataTable.setModel(createMapTableModel());	
		historicalDataPanel.add(historicalDataTable, BorderLayout.CENTER);
		
		JPanel weatherAlertsPanel = new JPanel();
		weatherAlertsPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		weatherAlertsPanel.setBounds(105, 70, 210, 115);
		weatherAlertsPanel.setVisible(false);
		contentPanel.add(weatherAlertsPanel);
		weatherAlertsPanel.setLayout(new BorderLayout(0, 0));
		
		alertDataTable = new JTable();
		alertDataTable.setModel(createMapTableModel());
		weatherAlertsPanel.add(alertDataTable, BorderLayout.CENTER);
		
		parameterList = new JList<String>();
		DefaultListModel<String> parameterListModel = new DefaultListModel<>();
		parameterListModel.addElement("date");
		parameterListModel.addElement("temp");
		parameterList.setModel(parameterListModel);
		parameterList.setBorder(new LineBorder(new Color(0, 0, 0)));
		parameterList.setBounds(105, 218, 120, 20);
		contentPanel.add(parameterList);
		
		temperatureUnitsBox = new JComboBox<String>();
		temperatureUnitsBox.setModel(new DefaultComboBoxModel<String>(new String[] {"C\u00BA", "F\u00BA"}));
		temperatureUnitsBox.setBounds(125, 246, 37, 20);
		contentPanel.add(temperatureUnitsBox);
		
		temperaturUnitsLabel = new JLabel("Temperature units:");
		temperaturUnitsLabel.setBounds(25, 249, 97, 14);
		contentPanel.add(temperaturUnitsLabel);
		
		this.servicePanels = new JPanel[] {
				currentWeatherPanel, weatherForecastPanel, historicalDataPanel, weatherAlertsPanel
		};
	}
	
	private DefaultTableModel createMapTableModel()
	{
		return new DefaultTableModel(
				new Object[][] { },
				new String[] {"Data", "Value"}
		);
	}
}
