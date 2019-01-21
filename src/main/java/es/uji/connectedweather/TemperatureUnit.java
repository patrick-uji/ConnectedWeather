package es.uji.connectedweather;

public enum TemperatureUnit
{
	
	CELSIUS(" C�"), FAHRENHEIT(" F�");
	
	private String symbol;
	
	private TemperatureUnit(String symbol)
	{
		this.symbol = symbol;
	}
	
	public String getSymbol()
	{
		return symbol;
	}
	
}
