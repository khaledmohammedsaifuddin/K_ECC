package teamtrio;
public interface Heap
{
	void insert(int id, int value);
	void remove(int id);
	void get_max(teamtrio.RefObject<Integer> id, teamtrio.RefObject<Integer> value);
	int extract_max(int id, int value);
	boolean exist(int id);
	int get_key(int id);
	void set_key(int id, int k);
	void update(int id, int value);
}