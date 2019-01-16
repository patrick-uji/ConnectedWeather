package es.uji.connectedweather.frames;

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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.Font;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;

public class MainFrameDesign extends JFrame
{
	
	private static final long serialVersionUID = 2869286737217332104L;
	private JPanel contentPanel;
	private MainFrame mainFrame;
	private DatePicker datePicker;
	private JTable alertDataTable;
	private JPanel[] servicePanels;
	private JButton searchCityButton;
	private JTextField citySearchBox;
	private JTable historicalDataTable;
	private JButton openSettingsButton;
	private JTabbedPane forecastDayTabs;
	private JTable[] forecastDataTables;
	private JButton saveWeatherDataButton;
	private JTable currentWeatherDataTable;
	private JButton addFavouriteCityButton;
	private JButton editFavouriteCityButton;
	private JTextField editFavouriteCityBox;
	private JList<String> favouriteCitiesList;
	private JComboBox<String> serversComboBox;
	private JComboBox<String> serviceComboBox;
	private JButton removeFavouriteCityButton;
	
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
	
	public JButton getSearchCityButton()
	{
		return searchCityButton;
	}
	
	public JButton getAddFavouriteCityButton()
	{
		return addFavouriteCityButton;
	}
	
	public JList<String> getFavouriteCitiesList()
	{
		return favouriteCitiesList;
	}
	
	public JTextField getEditFavouriteCityBox()
	{
		return editFavouriteCityBox;
	}
	
	public JButton getEditFavouriteCityButton()
	{
		return editFavouriteCityButton;
	}
	
	public JButton getRemoveFavouriteCityButton()
	{
		return removeFavouriteCityButton;
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
	
	public JTabbedPane getForecastDayTabs()
	{
		return forecastDayTabs;
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
		return historicalDataTable;
	}
	
	public JTable getAlertDataTable()
	{
		return alertDataTable;
	}
	
	public JButton getSaveWeatherDataButton()
	{
		return saveWeatherDataButton;
	}
	
	/**
	 * Create the frame.
	 */
	public MainFrameDesign()
	{
		addWindowFocusListener(new WindowFocusListener() {
			public void windowGainedFocus(WindowEvent e) {
				mainFrame.mainFrame_GainedFocus(e);
			}
			public void windowLostFocus(WindowEvent e) {
			}
		});
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				mainFrame.mainFrame_Closing(e);
			}
		});
		this.forecastDataTables = new JTable[5];
		setResizable(false);
		setTitle("EI1048 - ConnectedWeather");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 460, 395);
		contentPanel = new JPanel();
		contentPanel.setBackground(new Color(153, 204, 255));
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPanel);
		contentPanel.setLayout(null);
		
		serversComboBox = new JComboBox<String>();
		serversComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainFrame.serversComboBox_SelectionChanged(e);
			}
		});
		serversComboBox.setBounds(10, 10, 115, 20);
		contentPanel.add(serversComboBox);
		
		citySearchBox = new JTextField();
		citySearchBox.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e)
			{
				mainFrame.citySearchBox_TextChanged(e);
			}
			@Override
			public void insertUpdate(DocumentEvent e)
			{
				mainFrame.citySearchBox_TextChanged(e);
			}
			@Override
			public void changedUpdate(DocumentEvent e)
			{
				mainFrame.citySearchBox_TextChanged(e);
			}
		});
		citySearchBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				searchCityButton.doClick();
			}
		});
		citySearchBox.setBounds(135, 11, 164, 20);
		contentPanel.add(citySearchBox);
		citySearchBox.setColumns(10);
		
		searchCityButton = new JButton("Search");
		searchCityButton.setEnabled(false);
		searchCityButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainFrame.searchCityButton_Click(e);
			}
		});
		searchCityButton.setBounds(309, 10, 65, 23);
		contentPanel.add(searchCityButton);
		
		favouriteCitiesList = new JList<String>();
		favouriteCitiesList.setBackground(new Color(230, 230, 250));
		favouriteCitiesList.setModel(new DefaultListModel<String>());
		favouriteCitiesList.setBorder(new LineBorder(new Color(0, 0, 0)));
		favouriteCitiesList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				mainFrame.favouriteCitiesList_ValueChanged(e);
			}
		});
		
		addFavouriteCityButton = new JButton("Fav");
		addFavouriteCityButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainFrame.addFavouriteCityButton_Click(e);
			}
		});
		addFavouriteCityButton.setEnabled(false);
		addFavouriteCityButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		addFavouriteCityButton.setBounds(384, 10, 60, 23);
		contentPanel.add(addFavouriteCityButton);
		
		JLabel serviceLabel = new JLabel("Service:");
		serviceLabel.setBounds(279, 42, 46, 14);
		contentPanel.add(serviceLabel);
		
		serviceComboBox = new JComboBox<String>();
		serviceComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainFrame.serviceComboBox_SelectionChanged(e);
			}
		});
		serviceComboBox.setModel(new DefaultComboBoxModel<String>(new String[] {"Current Weather", "Weather Forecast", "Historical Data", "Weather Alarms"}));
		serviceComboBox.setBounds(324, 39, 120, 20);
		contentPanel.add(serviceComboBox);
		favouriteCitiesList.setBounds(10, 40, 115, 240);
		contentPanel.add(favouriteCitiesList);
		
		editFavouriteCityBox = new JTextField();
		editFavouriteCityBox.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(DocumentEvent e)
			{
				mainFrame.editFavouriteCityBox_TextChanged(e);
			}
			@Override
			public void insertUpdate(DocumentEvent e)
			{
				mainFrame.editFavouriteCityBox_TextChanged(e);
			}
			@Override
			public void changedUpdate(DocumentEvent e)
			{
				mainFrame.editFavouriteCityBox_TextChanged(e);
			}
		});
		editFavouriteCityBox.setEnabled(false);
		editFavouriteCityBox.setColumns(10);
		editFavouriteCityBox.setBounds(10, 288, 115, 20);
		contentPanel.add(editFavouriteCityBox);
		
		editFavouriteCityButton = new JButton("Rename");
		editFavouriteCityButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				mainFrame.editFavouriteCityButton_Click(e);
			}
		});
		editFavouriteCityButton.setEnabled(false);
		editFavouriteCityButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		editFavouriteCityButton.setBounds(10, 313, 115, 20);
		contentPanel.add(editFavouriteCityButton);
		
		removeFavouriteCityButton = new JButton("Remove");
		removeFavouriteCityButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainFrame.removeFavouriteCityButton_Click(e);
			}
		});
		removeFavouriteCityButton.setEnabled(false);
		removeFavouriteCityButton.setFont(new Font("Tahoma", Font.BOLD, 11));
		removeFavouriteCityButton.setBounds(10, 338, 115, 20);
		contentPanel.add(removeFavouriteCityButton);

		JPanel currentWeatherPanel = new JPanel();
		currentWeatherPanel.setBackground(new Color(230, 230, 250));
		currentWeatherPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		currentWeatherPanel.setBounds(135, 70, 309, 255);
		contentPanel.add(currentWeatherPanel);
		currentWeatherPanel.setLayout(new BorderLayout(0, 0));
		
		currentWeatherDataTable = new JTable();
		currentWeatherDataTable.setModel(createMapTableModel());
		currentWeatherPanel.add(currentWeatherDataTable);
		
		JPanel weatherForecastPanel = new JPanel();
		weatherForecastPanel.setBackground(new Color(230, 230, 250));
		weatherForecastPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		weatherForecastPanel.setBounds(135, 70, 309, 255);
		weatherForecastPanel.setVisible(false);
		contentPanel.add(weatherForecastPanel);
		weatherForecastPanel.setLayout(new BorderLayout(0, 0));

		JTable newTable;
		int currForecastIndex = 0;
		int nextForecastIndex = 1;
		forecastDayTabs = new JTabbedPane(JTabbedPane.TOP);
		forecastDayTabs.setBackground(new Color(230, 230, 250));
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
		historicalDataPanel.setBackground(new Color(230, 230, 250));
		historicalDataPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		historicalDataPanel.setBounds(135, 70, 309, 255);
		historicalDataPanel.setVisible(false);
		contentPanel.add(historicalDataPanel);
		historicalDataPanel.setLayout(new BorderLayout(0, 0));
		
		datePicker = new DatePicker();
		historicalDataPanel.add(datePicker, BorderLayout.NORTH);
		
		historicalDataTable = new JTable();
		historicalDataTable.setModel(createMapTableModel());	
		historicalDataPanel.add(historicalDataTable, BorderLayout.CENTER);
		
		JPanel weatherAlertsPanel = new JPanel();
		weatherAlertsPanel.setBackground(new Color(230, 230, 250));
		weatherAlertsPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
		weatherAlertsPanel.setBounds(135, 70, 309, 255);
		weatherAlertsPanel.setVisible(false);
		contentPanel.add(weatherAlertsPanel);
		weatherAlertsPanel.setLayout(new BorderLayout(0, 0));
		
		alertDataTable = new JTable();
		alertDataTable.setModel(createMapTableModel());
		weatherAlertsPanel.add(alertDataTable, BorderLayout.CENTER);
		
		openSettingsButton = new JButton("Open Settings...");
		openSettingsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainFrame.openSettingsButton_Click(e);
			}
		});
		openSettingsButton.setBounds(135, 335, 139, 23);
		contentPanel.add(openSettingsButton);
		
		saveWeatherDataButton = new JButton("Save");
		saveWeatherDataButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				mainFrame.saveWeatherDataButton_Click(e);
			}
		});
		saveWeatherDataButton.setEnabled(false);
		saveWeatherDataButton.setBounds(364, 336, 80, 23);
		contentPanel.add(saveWeatherDataButton);
		
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
