package es.uji.connectedweather.frames;

import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;

public class SettingsDialog
{
	
	private MainFrame mainFrame;
	private SettingsDialogDesign design;
	
	public SettingsDialog(MainFrame mainFrame)
	{
		this.design = new SettingsDialogDesign(this);
		this.mainFrame = mainFrame;
	}
	
	public SettingsDialogDesign getDesign()
	{
		return design;
	}
	
	public void okButton_Click(ActionEvent e)
	{
		mainFrame.settingsDialog_OK();
		close();
	}
	
	public void cancelButton_Click(ActionEvent e)
	{
		close();
	}
	
	public void show()
	{
		MainFrameDesign mainFrameDesign = mainFrame.getDesign();
		design.setLocationRelativeTo(mainFrameDesign);
		mainFrameDesign.setEnabled(false);
		design.setVisible(true);
	}
	
	public void close()
	{
		mainFrame.getDesign().setEnabled(true);
		design.setVisible(false);
	}
	
	public void settingsDialog_Closing(WindowEvent e)
	{
		mainFrame.getDesign().setEnabled(true);
	}
	
}
