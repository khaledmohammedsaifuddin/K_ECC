
#include "Utility.h"
#include "Heap.h"
#include "LinearHeap.h"

using namespace std;

struct Bin;

struct Element{
	Element *next;
	int value;
};

struct Edge {
	Edge *pre, *next;
	Edge *duplicate;
	Bin *nontree;
	int node_id, sc;
	char deleted;
};

struct Node {
	Element *head, *tail;
	Edge *first, *last;
};

struct Bin {
	Edge *edge;
	Bin *pre, *next;
};

class Graph {
private:
	string dir;
	int K;
	int n, m;

	Element *elements; // buffer to allocate all elements
	Edge *edges; // buffer to allocate all edges
	Node *nodes;

	Node *pnodes; // nodes of super graph
	Edge *pedges; // edges of super graph

	char *inL; // used in kCD_opt(), kCCkCD(), max_flow()
	int *computed; // used in kCCkCD(), kCSkCD(), construct_pgraph()
	int *height; // record the maximum number of recursive calls in kSC

	int *q; // queque

	int *degrees; // used for k-core optimization

	Heap *heap;

	int *cids; // component id, used for disconnected graphs
	int *levels;

	int *parent;
	int *weights;

	int *pos_id;
	int weights_n;
	int **st;
	int ns, logns;
	int *t_id;
	int nt, logn;
	int **table;

	int *log_table;

public:
	Graph(const char *_dir) ;
	~Graph() ;

	void set_k(int _K) { K = _K; }

	void read_graph(int binary);
	int kSC(int _K); //decomposition based paradigm for computing k-strong components (or k-edge-connected subgraphs)
					 //returns 1 iff there is at least one non-trivial k-strong component
	void output_all_sc(FILE *fout) ;

	void all_SC_naive() ;

	void max_spanning_tree() ;
	void optimization_tree(int output) ;
	void output_k_edge_connected_subgraphs() ;
private:
		int find_root(int x, int *parent) ;

	void remove_inter_edges(const vector<Element *> &cc, int assign_sc = 0) ;

	void decomposition(int s, vector<Element *> &cc, int &max_l) ;
	void merge(int s, int t, Heap *heap) ;
	void add_edge(Node &node, Edge *edge) ;
	void delete_edge(Node &node, Edge *edge) ;
	void delete_edge_to_last(Node &node, Edge *edge) ;
	int construct_pgraph(int s, int *height) ; //compute partition graph for the connected components containing node s, return the number of nodes
	void kcore_optimization(int q_c, int assign_sc = 0) ;
	bool find(int value, const vector<int> &values) ;
};


