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
import javax.swing.JTabbedPane;
public class MainFrameDesign extends JFrame
{
	private static final long serialVersionUID = 2869286737217332104L;
	private JComboBox<String> serversComboBox;
	private JPanel contentPanel;
	@SuppressWarnings("unused")
	private MainFrame mainFrame;
	private DatePicker datePicker;
	private JTextField textField;
	private JList favouriteCitiesList;
	private JPanel weatherForecastPanel;
	private JPanel historicalDataPanel;
	private JPanel weatherAlertsPanel;
	private JTabbedPane tabbedPane;
	
	public MainFrameDesign(MainFrame mainFrame)
	{
		this();
		this.mainFrame = mainFrame;
	}
	
	public JComboBox<String> getServersComboBox()
	{
		return serversComboBox;
	}
	
	/**
	 * Create the frame.
	 */
	public MainFrameDesign()
	{
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
		
		textField = new JTextField();
		textField.setBounds(105, 10, 135, 20);
		contentPanel.add(textField);
		textField.setColumns(10);
		
		JButton searchCityButton = new JButton("Search");
		searchCityButton.setBounds(250, 9, 65, 23);
		contentPanel.add(searchCityButton);
		
		favouriteCitiesList = new JList();
		favouriteCitiesList.setBounds(10, 40, 83, 128);
		contentPanel.add(favouriteCitiesList);
		
		JLabel serviceLabel = new JLabel("Service:");
		serviceLabel.setBounds(150, 41, 46, 14);
		contentPanel.add(serviceLabel);
		
		JComboBox<String> comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"Current Weather", "Weather Forecast", "Historical Data", "Weather Alarms"}));
		comboBox.setBounds(195, 38, 120, 20);
		contentPanel.add(comboBox);
		
		weatherForecastPanel = new JPanel();
		weatherForecastPanel.setBounds(105, 70, 210, 115);
		contentPanel.add(weatherForecastPanel);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		weatherForecastPanel.add(tabbedPane);
		
		JPanel currentWeatherPanel = new JPanel();
		currentWeatherPanel.setBounds(105, 70, 210, 115);
		contentPanel.add(currentWeatherPanel);
		
		historicalDataPanel = new JPanel();
		historicalDataPanel.setBounds(105, 70, 210, 115);
		contentPanel.add(historicalDataPanel);
		
		datePicker = new DatePicker();
		historicalDataPanel.add(datePicker);
		
		weatherAlertsPanel = new JPanel();
		weatherAlertsPanel.setBounds(105, 70, 210, 115);
		contentPanel.add(weatherAlertsPanel);
	}
}
