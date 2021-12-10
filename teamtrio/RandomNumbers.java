package teamtrio;
public final class RandomNumbers
{
	private static java.util.Random r;

	public static int nextNumber()
	{
		if (r == null)
			seed();

		return r.nextInt();
	}

	public static int nextNumber(int ceiling)
	{
		if (r == null)
			seed();

		return r.nextInt(ceiling);
	}

	public static void seed()
	{
		r = new java.util.Random();
	}

	public static void seed(int seed)
	{
		r = new java.util.Random(seed);
	}
}