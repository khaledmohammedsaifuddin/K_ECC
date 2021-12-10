package teamtrio;
public final class RefObject<T>
{
	public T argValue;
	public RefObject(T refArg)
	{
		argValue = refArg;
	}

    RefObject(int[] height) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    RefObject(Integer[] height) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
}