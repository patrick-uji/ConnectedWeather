package es.uji.connectedweather;

import java.awt.EventQueue;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import es.uji.connectedweather.frames.MainFrame;

public class Program
{
	
	public static void main(String[] args)
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (UnsupportedLookAndFeelException ex) { }
		catch (ClassNotFoundException ex) { }
		catch (InstantiationException ex) { }
		catch (IllegalAccessException ex) { }
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				MainFrame mainFrame = new MainFrame();
				mainFrame.show();
			}
		});
	}
	
}
