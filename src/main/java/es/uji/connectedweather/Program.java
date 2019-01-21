package es.uji.connectedweather;

import java.awt.EventQueue;
import javax.swing.UIManager;
import es.uji.connectedweather.frames.MainFrame;

public class Program
{
	
	public static final boolean DEBUG = true;
	
	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (Exception ex) { }
		EventQueue.invokeLater(Program::run);
	}
	
	private static void run()
	{
		MainFrame mainFrame = new MainFrame();
		mainFrame.show();
	}
	
}
