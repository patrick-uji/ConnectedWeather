package es.uji.connectedweather;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JComboBox;
import com.github.lgooddatepicker.components.DatePicker;
public class MainFrameDesign extends JFrame
{
	private static final long serialVersionUID = 2869286737217332104L;
	private JComboBox<String> serversComboBox;
	private JPanel contentPanel;
	@SuppressWarnings("unused")
	private MainFrame mainFrame;
	private DatePicker datePicker;
	
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
		setBounds(100, 100, 336, 100);
		contentPanel = new JPanel();
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPanel);
		contentPanel.setLayout(null);
		
		serversComboBox = new JComboBox<String>();
		serversComboBox.setBounds(10, 10, 83, 20);
		contentPanel.add(serversComboBox);
		
		datePicker = new DatePicker();
		datePicker.setLocation(10, 40);
		datePicker.setSize(170, 20);
		contentPanel.add(datePicker);
	}
	
}
