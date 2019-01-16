package es.uji.connectedweather.adapters;

import java.util.Enumeration;
import java.util.ListIterator;

public class EnumerationAsListIterator<T> implements ListIterator<T>
{
	
    private Enumeration<T> enumeration;

    public EnumerationAsListIterator(Enumeration<T> enumeration)
    {
        this.enumeration = enumeration;
    }

    public boolean hasNext()
    {
        return enumeration.hasMoreElements();
    }

    public T next()
    {
        return enumeration.nextElement();
    }

    public void remove()
    {
        throw new UnsupportedOperationException();
    }

	@Override
	public void add(T e)
	{
        throw new UnsupportedOperationException();
	}

	@Override
	public boolean hasPrevious()
	{
        throw new UnsupportedOperationException();
	}

	@Override
	public int nextIndex()
	{
        throw new UnsupportedOperationException();
	}

	@Override
	public T previous()
	{
        throw new UnsupportedOperationException();
	}

	@Override
	public int previousIndex()
	{
        throw new UnsupportedOperationException();
	}

	@Override
	public void set(T e)
	{
        throw new UnsupportedOperationException();
	}
}
