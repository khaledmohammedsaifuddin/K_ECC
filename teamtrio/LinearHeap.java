package teamtrio;
import java.io.*;

public class LinearHeap implements Heap
{
	private int n;
	private int po; //the potential maximum key
	private int[] head;
	private int[] pre;
	private int[] next;
	private int[] key;

	public LinearHeap(int _n)
	{
		n = _n;
		po = -1;

		head = new int[n];

		pre = new int[n];
		next = new int[n];
		key = new int[n];

		for (int i = 0;i < n;i++)
		{
			head[i] = -1;
			key[i] = 0;
		}
	}

        @Override
	public final void insert(int id, int value)
	{

		key[id] = value;
		pre[id] = -1;
		next[id] = head[value];
		if (head[value] != -1)
		{
			pre[head[value]] = id;
		}
		head[value] = id;

		//printf("Linked Lists::insert(id %d, value %d)\n", id, value);

		if (value > po)
		{
			po = value;
		}
	}

        @Override
	public final int get_key(int id)
	{
		return key[id];
	}
        @Override
	public final void set_key(int id, int value)
	{
		key[id] = value;
	}
        @Override
	public final boolean exist(int id)
	{
		return key[id] != 0;
	}
        @Override
	public final void get_max(teamtrio.RefObject<Integer> id, teamtrio.RefObject<Integer> value)
	{
		while (po >= 0 && head[po] == -1)
		{
			--po;
		}

		id.argValue = head[po];
		value.argValue = key[id.argValue];
	}

        @Override
	public final int extract_max(int id, int value)
	{
		while (po >= 0 && head[po] == -1)
		{
			--po;
		}

		if (po < 0)
		{
			//printf("Heap is empty...\n");
			return 0;
		}

		id = head[po];
		value = key[id];

		head[po] = next[id];
		if (head[po] != -1)
		{
			pre[head[po]] = -1;
		}

		//printf("Linked Lists::extract_max(id %d, value %d)\n", id, value);

		return 1;
	}

        @Override
	public final void remove(int id)
	{
		if (pre[id] == -1)
		{

			head[key[id]] = next[id];
			if (next[id] != -1)
			{
				pre[next[id]] = -1;
			}
		}
		else
		{
			int pid = pre[id];
			next[pid] = next[id];
			if (next[id] != -1)
			{
				pre[next[id]] = pid;
			}
		}
	}

        @Override
	public final void update(int id, int value)
	{
		remove(id);
		insert(id, value);
	}

	public final void clear()
	{
		for (int i = 0;i <= po;i++)
		{
			head[i] = -1;
		}

		po = -1;
	}

    
}