package es.uji.connectedweather;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JComboBox;
public class MainFrameDesign extends JFrame
{
	private static final long serialVersionUID = 2869286737217332104L;
	private JComboBox<String> serversComboBox;
	private JPanel contentPanel;
	private MainFrame mainFrame;
	
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
		serversComboBox.setBounds(10, 11, 83, 20);
		contentPanel.add(serversComboBox);
	}
	
}
