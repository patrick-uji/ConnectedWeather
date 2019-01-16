package es.uji.connectedweather.frames;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.DefaultComboBoxModel;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Dialog.ModalExclusionType;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SettingsDialogDesign extends JFrame
{
	
	private static final long serialVersionUID = -6863394188988181906L;
	private JComboBox<String> temperatureUnitsBox;
	private SettingsDialog settingsDialog;
	private JCheckBox[] paramCheckBoxes;
	private JPanel contentPanel;
	
	public SettingsDialogDesign(SettingsDialog settingsDialog)
	{
		this();
		this.settingsDialog = settingsDialog;
	}
	
	public JComboBox<String> getTemperatureUnitsBox()
	{
		return temperatureUnitsBox;
	}
	
	public JCheckBox[] getParamCheckBoxes()
	{
		return paramCheckBoxes;
	}

	/**
	 * Create the frame.
	 */
	public SettingsDialogDesign()
	{
		setTitle("ConnectedWeather - Settings");
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				settingsDialog.settingsDialog_Closing(e);
			}
		});
		setResizable(false);
		setType(Type.UTILITY);
		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);
		setBounds(100, 100, 238, 285);
		contentPanel = new JPanel();
		contentPanel.setBackground(new Color(153, 204, 255));
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPanel);
		contentPanel.setLayout(new BorderLayout(0, 0));
		
		JPanel unitsPanel = new JPanel();
		unitsPanel.setBackground(new Color(153, 204, 255));
		contentPanel.add(unitsPanel, BorderLayout.NORTH);
		unitsPanel.setLayout(new GridLayout(0, 2, 0, 0));
		
		JLabel temperatureUnitsLabel = new JLabel("Temperature units:");
		unitsPanel.add(temperatureUnitsLabel);
		
		temperatureUnitsBox = new JComboBox<String>();
		temperatureUnitsBox.setModel(new DefaultComboBoxModel<String>(new String[] {"C\u00BA", "F\u00BA"}));
		unitsPanel.add(temperatureUnitsBox);
		
		JPanel paramsPanel = new JPanel();
		paramsPanel.setBackground(new Color(153, 204, 255));
		contentPanel.add(paramsPanel);
		paramsPanel.setLayout(new GridLayout(0, 2, 0, 0));
		
		JCheckBox dateCheckBox = new JCheckBox("date");
		dateCheckBox.setBackground(new Color(230, 230, 250));
		paramsPanel.add(dateCheckBox);
		
		JCheckBox cityCheckBox = new JCheckBox("city");
		cityCheckBox.setBackground(new Color(230, 230, 250));
		paramsPanel.add(cityCheckBox);
		
		JCheckBox countryCheckBox = new JCheckBox("country");
		countryCheckBox.setBackground(new Color(230, 230, 250));
		paramsPanel.add(countryCheckBox);
		
		JCheckBox conditionCheckBox = new JCheckBox("condition");
		conditionCheckBox.setBackground(new Color(230, 230, 250));
		paramsPanel.add(conditionCheckBox);
		
		JCheckBox temperatureCheckBox = new JCheckBox("temperature");
		temperatureCheckBox.setBackground(new Color(230, 230, 250));
		paramsPanel.add(temperatureCheckBox);
		
		JCheckBox humidityCheckBox = new JCheckBox("humidity");
		humidityCheckBox.setBackground(new Color(230, 230, 250));
		paramsPanel.add(humidityCheckBox);
		
		JCheckBox windCheckBox = new JCheckBox("wind");
		windCheckBox.setBackground(new Color(230, 230, 250));
		paramsPanel.add(windCheckBox);
		
		JCheckBox windDegreeCheckBox = new JCheckBox("wind_degree");
		windDegreeCheckBox.setBackground(new Color(230, 230, 250));
		paramsPanel.add(windDegreeCheckBox);
		
		JCheckBox visibilityCheckBox = new JCheckBox("visibility");
		visibilityCheckBox.setBackground(new Color(230, 230, 250));
		paramsPanel.add(visibilityCheckBox);
		
		JCheckBox cloudsCheckBox = new JCheckBox("clouds");
		cloudsCheckBox.setBackground(new Color(230, 230, 250));
		paramsPanel.add(cloudsCheckBox);
		
		JCheckBox pressureCheckBox = new JCheckBox("pressure");
		pressureCheckBox.setBackground(new Color(230, 230, 250));
		paramsPanel.add(pressureCheckBox);
		
		JCheckBox precipitationCheckBox = new JCheckBox("precipitation");
		precipitationCheckBox.setBackground(new Color(230, 230, 250));
		paramsPanel.add(precipitationCheckBox);
		
		JCheckBox minTempCheckBox = new JCheckBox("min_temp");
		minTempCheckBox.setBackground(new Color(230, 230, 250));
		paramsPanel.add(minTempCheckBox);
		
		JCheckBox maxTempCheckBox = new JCheckBox("max_temp");
		maxTempCheckBox.setBackground(new Color(230, 230, 250));
		paramsPanel.add(maxTempCheckBox);
		
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setBackground(new Color(153, 204, 255));
		contentPanel.add(buttonsPanel, BorderLayout.SOUTH);
		buttonsPanel.setLayout(new GridLayout(0, 2, 40, 0));
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				settingsDialog.cancelButton_Click(e);
			}
		});
		buttonsPanel.add(cancelButton);
		
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				settingsDialog.okButton_Click(e);
			}
		});
		buttonsPanel.add(okButton);
		SwingUtilities.getRootPane(okButton).setDefaultButton(okButton);
		
		this.paramCheckBoxes = new JCheckBox[] {
			dateCheckBox, cityCheckBox, countryCheckBox, conditionCheckBox,
			temperatureCheckBox, humidityCheckBox, windCheckBox, windDegreeCheckBox,
			visibilityCheckBox, cloudsCheckBox, pressureCheckBox, precipitationCheckBox,
			minTempCheckBox, maxTempCheckBox
		};
	}
	
}
