package teamtrio;
import java.io.IOException;

public final class Arrays
{
	public static Node[] initializeWithDefaultNodeInstances(int length)
	{
		Node[] array = new Node[length];
		for (int i = 0; i < length; i++)
		{
			array[i] = new Node();
		}
		return array;
	}

	public static Edge[] initializeWithDefaultEdgeInstances(int length)
	{
		Edge[] array = new Edge[length];
		for (int i = 0; i < length; i++)
		{
			array[i] = new Edge();
		}
		return array;
	}

	public static Element[] initializeWithDefaultElementInstances(int length)
	{
		Element[] array = new Element[length];
		for (int i = 0; i < length; i++)
		{
			array[i] = new Element();
		}
		return array;
	}

	public static <T extends java.io.Closeable> void deleteArray(T[] array) throws IOException
	{
		for (T element : array)
		{
			if (element != null)
				element.close();
		}
	}

    static Edge[] initializeWithDefaultNodeInstances(Edge pre) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}