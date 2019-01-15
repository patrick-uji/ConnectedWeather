package es.uji.connectedweather.adapters;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import javax.swing.DefaultListModel;
import javax.swing.JList;

public class SwingListModelAsList<T> implements List<T>
{
	
	private DefaultListModel<T> listModel;
	
	public SwingListModelAsList(JList<T> list)
	{
		this.listModel = (DefaultListModel<T>)list.getModel();
	}

	@Override
	public boolean add(T e)
	{
		listModel.addElement(e);
		return true;
	}

	@Override
	public void add(int index, T element)
	{
		listModel.add(index, element);
	}

	@Override
	public boolean addAll(Collection<? extends T> c)
	{
		for (T currElement : c)
		{
			listModel.addElement(currElement);
		}
		return !c.isEmpty();
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c)
	{
		for (T currElement : c)
		{
			listModel.add(index, currElement);
			index++;
		}
		return !c.isEmpty();
	}

	@Override
	public void clear()
	{
		listModel.clear();
	}

	@Override
	public boolean contains(Object o)
	{
		return listModel.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c)
	{
		for (Object currElement : c)
		{
			if (!listModel.contains(currElement))
			{
				return false;
			}
		}
		return true;
	}

	@Override
	public T get(int index)
	{
		return listModel.get(index);
	}

	@Override
	public int indexOf(Object o)
	{
		return listModel.indexOf(o);
	}

	@Override
	public boolean isEmpty()
	{
		return listModel.isEmpty();
	}

	@Override
	public Iterator<T> iterator()
	{
		return listIterator();
	}

	@Override
	public int lastIndexOf(Object o)
	{
		return listModel.lastIndexOf(o);
	}

	@Override
	public ListIterator<T> listIterator()
	{
		return new EnumerationAsListIterator<T>(listModel.elements());
	}

	@Override
	public ListIterator<T> listIterator(int index)
	{
		ListIterator<T> iterator = listIterator();
		while (index > 0)
		{
			iterator.next();
			index--;
		}
		return iterator;
	}

	@Override
	public boolean remove(Object o)
	{
		return listModel.removeElement(o);
	}

	@Override
	public T remove(int index)
	{
		T element = listModel.getElementAt(index);
		listModel.removeElementAt(index);
		return element;
	}

	@Override
	public boolean removeAll(Collection<?> c)
	{
		int lastSize = listModel.size();
		for (Object currElement : c)
		{
			listModel.removeElement(currElement);
		}
		return listModel.size() != lastSize;
	}

	@Override
	public boolean retainAll(Collection<?> c)
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public T set(int index, T element)
	{
		return listModel.set(index, element);
	}

	@Override
	public int size()
	{
		return listModel.size();
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex)
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object[] toArray()
	{
		return listModel.toArray();
	}

	@Override
	@SuppressWarnings({"unchecked", "hiding"})
	public <T> T[] toArray(T[] a)
	{
		T[] copy = (T[])listModel.toArray();
		if (a.length < copy.length)
		{
			return copy;
		}
		else
		{
			for (int currIndex = 0; currIndex < copy.length; currIndex++)
			{
				a[currIndex] = copy[currIndex];
			}
			return a;
		}
	}
	
}
