#include "Graph.h"

Graph::Graph(const char *_dir)
{
	dir = string(_dir);

	K = -1;
	nodes = NULL;
	edges = NULL;
	pnodes = NULL;
	pedges = NULL;
	inL = NULL;
	computed = NULL;
	height = NULL;
	elements = NULL;
	q = NULL;
	degrees = NULL;
	heap = NULL;
	cids = NULL;
	levels = NULL;
	parent = NULL;
	weights = NULL;
	pos_id = NULL;
	//st = NULL;
	t_id = NULL;
	table = NULL;
	log_table = NULL;
}

Graph::~Graph() {
	if(nodes != NULL) delete[] nodes;
	if(edges != NULL) delete[] edges;
	if(pnodes != NULL) delete[] pnodes;
	if(pedges != NULL) delete[] pedges;
	if(inL != NULL) delete[] inL;
	if(computed != NULL) delete[] computed;
	if(height != NULL) delete[] height;
	if(elements != NULL) delete[] elements;
	if(q != NULL) delete[] q;
	if(degrees != NULL) delete[] degrees;
	if(heap != NULL) delete heap;
	if(cids != NULL) delete[] cids;
	if(levels != NULL) delete[] levels;
	if(parent != NULL) delete[] parent;
	if(weights != NULL) delete[] weights;
	if(pos_id != NULL) delete[] pos_id;

	if(t_id != NULL) delete[] t_id;
	if(table != NULL) {
		for(int i = 0;i < nt;i ++) {
			delete[] table[i];
			table[i] = NULL;
		}
		delete[] table;
	}
	if(log_table != NULL) delete[] log_table;
}
void Graph::read_graph(int binary)
{
	FILE *f;
	if(binary) f = open_file((dir + string("/edges.bin")).c_str(), "rb");
	else f = open_file((dir + string("/data/CA-GrQc/edges.txt")).c_str(), "r");
	if(binary) {
		fread(&n, sizeof(int), 1, f);
		fread(&m, sizeof(int), 1, f);
	}
	else fscanf(f, "%d%d", &n, &m);

	nodes = new Node[n];
	edges = new Edge[2*m];
	q = new int[n];
	int edge_c = 0;

	for(int i = 0;i < n;i ++) nodes[i].first = NULL;
	int *buf = NULL;
		for(int i = 0;i < m;i ++) {
			int a, b;
			fscanf(f, "%d%d", &a, &b);

			edges[edge_c].node_id = b;
			edges[edge_c].sc = 1;
			edges[edge_c].deleted = 0;
			edges[edge_c].duplicate = &edges[edge_c+1];
			add_edge(nodes[a], &edges[edge_c]);
			++ edge_c;
			edges[edge_c].node_id = a;
			edges[edge_c].sc = 1;
			edges[edge_c].deleted = 0;
			edges[edge_c].duplicate = &edges[edge_c-1];
			add_edge(nodes[b], &edges[edge_c]);
			++ edge_c;
		}

	fclose(f);
	if(buf != NULL) delete[] buf;
	pnodes = new Node[n];
	pedges = new Edge[2*m];
	for(int i = 0;i < n;i ++) pnodes[i].first = NULL;
	elements = new Element[n];
	for(int i = 0;i < n;i ++) elements[i].value = i;
}

void Graph::all_SC_naive()
{


     int start, end;
        start = clock();
	for(int i = 1;i <= n;i ++) {
		if(i > 1){
        //cout<<kSC(i);
        if(!kSC(i))
        break;
        }
		if(i > 1) output_k_edge_connected_subgraphs();
		for(int j = 0;j < n;j ++)
            {
			for(Edge* e = nodes[j].first;e != NULL;e = e->next) {
				if(!e->deleted)
                {
                    e->sc = i;

                }

				else
                    e->deleted = 0;
				nodes[j].last = e;
			}
		}
	}

	 end = clock();

        printf("all_SC_naive time: %d\n", end-start);
//printf("Here\n");

string output_name = string(dir) + "/all-SC.bin";
FILE *fout = open_file(output_name.c_str(), "wb");
output_all_sc(fout);
fclose(fout);

}

void Graph::output_k_edge_connected_subgraphs() {
	ostringstream os;
	//os<<dir<<"/decomposition_"<<K<<".txt";
	os<<dir<<"/my.txt";

	FILE *fout = open_file(os.str().c_str(), "w");

	int cnt = 0, max_size = 0;

	memset(computed, 0, sizeof(int)*n);
	for(int i = 0;i < n;i ++) if(!computed[i]) {
		int q_c = 1;
		q[0] = i;
		computed[i] = 1;

		for(int j = 0;j < q_c;j ++) {
			int s = q[j];
			for(Edge *e = nodes[s].first;e != NULL&&!e->deleted;e = e->next) if(!computed[e->node_id]) {
				computed[e->node_id] = 1;
				q[q_c] = e->node_id;
				++ q_c;
			}
		}

		if(q_c == 1) continue;

		sort(q, q+q_c);
		fprintf(fout, "%d", q[0]);
		for(int j = 1;j < q_c;j ++) fprintf(fout, ", %d", q[j]);
		fprintf(fout, "\n");

		++ cnt;
		if(q_c > max_size) max_size = q_c;
	}

	//printf("%d %d\n", cnt, max_size);

	fclose(fout);
}


int Graph::kSC(int _K)
 {
	K = _K;

	if(computed == NULL)
        computed = new int[n];
	if(height == NULL)
	height = new int[n];


	if(degrees == NULL)
	degrees = new int[n];

	memset(computed, 0, sizeof(int)*n);
	memset(height, 0, sizeof(int)*n);
	cout<<computed[100];

	int q_c = 0;


	for(int i = 0;i < n;i ++)
        {
		int cnt = 0;
		for(Edge *edge = nodes[i].first; edge != NULL && !edge->deleted;  edge = edge->next)
            {++ cnt;
            //cout<<!edge->deleted;
            }

		if(cnt < K) {
			q[q_c] = i;
			++ q_c;
		}
		degrees[i] = cnt;
		//cout<<cnt;
		//cout<<"\n";
	}

	kcore_optimization(q_c);
	int max_l = 0, non_trivial = 0;
	for(int i = 0;i < n;i ++)
        {
		// if(i%10000 == 0) printf(".");
		if(computed[i])
            continue;
		if(construct_pgraph(i, height) > 1)
		{
			non_trivial = 1;
			//cout<<non_trivial;
			}

		vector<Element *> cc;
		decomposition(i, cc, max_l);
		if(cc.size() == 1)
		{
		for(Element *e = cc[0];e != NULL;e = e->next)
		 computed[e->value] = 1;}
		else remove_inter_edges(cc);
		-- i;
	}
	int max_height = height[0];
	for(int i = 0;i < n;i ++)
	if(height[i] > max_height)
	 max_height = height[i];
	// printf("K: %d, Height: %d, Max L: %d\n", K, max_height, max_l);
	//cout<<non_trivial;
      //              cout<<"\n";
	return non_trivial;
}

void Graph::add_edge(Node &node, Edge *edge) {
	edge->next = NULL;

	if(node.first == NULL) {
		node.first = node.last = edge;
		edge->pre = NULL;
	}
	else {
		node.last->next = edge;
		edge->pre = node.last;
		node.last = edge;
	}
}

void Graph::kcore_optimization(int q_c, int assign_sc) {
	for(int i = 0;i < q_c;i ++) {
		int s = q[i];
		computed[s] = 1;

		for(Edge *edge = nodes[s].first;edge != NULL&&!edge->deleted;edge = edge->next) {
			int t = edge->node_id;

			delete_edge_to_last(nodes[t], edge->duplicate);
			-- degrees[t];

			if(degrees[t] == K-1) {
				q[q_c] = t;
				++ q_c;
			}

			edge->deleted = 1;

			if(assign_sc) edge->sc = edge->duplicate->sc = K-1;
		}

		nodes[s].last = nodes[s].first;
	}
}
void Graph::delete_edge(Node &node, Edge *edge) {
	if(edge->pre == NULL) {
		node.first = edge->next;
		if(edge->next != NULL) edge->next->pre = NULL;
	}
	else {
		if(edge == node.last) node.last = edge->pre;

		Edge *tmp = edge->pre;
		tmp->next = edge->next;

		if(edge->next != NULL) edge->next->pre = tmp;
	}
}

void Graph::merge(int s, int t, Heap *heap) {
	pnodes[s].tail->next = pnodes[t].head;
	pnodes[s].tail = pnodes[t].tail;

	Edge *e = pnodes[t].first;
	Edge *tmp;

	while(e != NULL) {
		tmp = e->next;

		if(e->node_id == s) {
			if(heap != NULL) heap->set_key(s, heap->get_key(s) - e->sc);
			delete_edge(pnodes[e->node_id], e->duplicate);
		}
		else {
			e->duplicate->node_id = s;
			add_edge(pnodes[s], e);
		}

		e = tmp;
	}

	pnodes[t].first = NULL;
}

void Graph::decomposition(int ss, vector<Element *> &cc, int &max_l) {
	if(heap == NULL) heap = new LinearHeap(n);
	if(inL == NULL) {
		inL = new char[n];
		memset(inL, 0, sizeof(char)*n);
	}
	cc.clear();
	int cnt = 0;
	int kk=0;
	while(pnodes[ss].first != NULL) {

		//cout<<"ok";
		int s = ss;
		++ cnt;
		heap->insert(s, 0);
		int q_c = 0;
		int key;
		while(1) {
               // cout<<!heap->extract_max(s, key);
			if(!heap->extract_max(s, key))
            {
               // cout<<"ok";
                break;}
			inL[s] = 1;
			q[q_c] = s;
			++ q_c;
			int new_qc = q_c;
			for(int i = q_c - 1;i < new_qc;i ++) {
				int u = q[i];
				for(Edge *e = pnodes[u].first;e != NULL;e = e->next)
{
                    //cout<<!inL[e->node_id];
                    if(!inL[e->node_id])
                    {
					int new_key = heap->get_key(e->node_id);
					if(new_key < K) {
						if(new_key > 0) heap->remove(e->node_id);
						new_key += e->sc;
						if(new_key >= K) {
							heap->set_key(e->node_id, new_key);
							q[new_qc ++] = e->node_id;
						}
						else heap->insert(e->node_id, new_key);
					}
					else heap->set_key(e->node_id, new_key + e->sc);
				   }
}
				if(u == s) continue;
				heap->set_key(s, heap->get_key(s) + heap->get_key(u));
				heap->set_key(u, 0);
				inL[u] = 0;
				merge(s, u, heap);
			}
		}
		-- q_c;
		//cout<<q_c;
		//cout<<"\n";
		while(q_c > 0&&heap->get_key(q[q_c]) < K) {
                 kk=kk+1;
			int t = q[q_c]; -- q_c;
			cc.push_back(pnodes[t].head);
			heap->set_key(t, 0);
			inL[t] = 0;
			for(Edge *e = pnodes[t].first;e != NULL;e = e->next) delete_edge(pnodes[e->node_id], e->duplicate);
			pnodes[t].first = NULL;
			//cout<<"inside while";
		}
		for(int i = 0;i <= q_c;i ++) {
			heap->set_key(q[i], 0);
			inL[q[i]] = 0;
		}
	}
	if(cnt > max_l) max_l = cnt;
	cc.push_back(pnodes[ss].head);
}


void Graph::remove_inter_edges(const vector<Element *> &cc, int assign_sc) {
	for(int j = 0;j < (int)cc.size();j ++) for(Element *e = cc[j];e != NULL;e = e->next) computed[e->value] = j+1;
	int q_c = 0;
	for(int j = 0;j < (int)cc.size();j ++) for(Element *e = cc[j];e != NULL;e = e->next) {
		int s = e->value;
		Edge *list = nodes[s].first;
		Edge *delete_first = nodes[s].last->next;
		nodes[s].first = nodes[s].last = NULL;
		int cnt = 0;


		while(list != NULL && !list->deleted)
            //cout<<!list->deleted;

            {
			Edge *tmp = list->next;
			if(computed[list->node_id] == computed[s]) {
				if(nodes[s].first == NULL) {
					nodes[s].first = nodes[s].last = list;
					list->pre = NULL;
				}
				else {
					nodes[s].last->next = list;
					list->pre = nodes[s].last;
					nodes[s].last = list;
				}

				++ cnt;
			}
			else {

				list->deleted = 1;

				//cout<<assign_sc;
				if(assign_sc) list->sc = K-1;


				list->next = delete_first;
				if(delete_first != NULL) delete_first->pre = list;
				delete_first = list;
			}
			list = tmp;
		}
		degrees[s] = cnt;
		if(cnt < K) {
			q[q_c] = s;
			++ q_c;
		}
		if(nodes[s].first == NULL) {
			nodes[s].first = nodes[s].last = delete_first;
			if(delete_first != NULL) delete_first->pre = NULL;
		}
		else {
			nodes[s].last->next = delete_first;
			if(delete_first != NULL) delete_first->pre = nodes[s].last;
		}
	}
	for(int j = 0;j < (int)cc.size();j ++) for(Element *e = cc[j];e != NULL;e = e->next) computed[e->value] = 0;
	kcore_optimization(q_c, assign_sc);
}
int Graph::construct_pgraph(int s, int *height) {
	int pedge_c = 0;
	int q_c = 1;
	computed[s] = 1;
	q[0] = s;
	for(int i = 0;i < q_c;i ++) {
		s = q[i];
		++ height[q[i]];
		pnodes[s].head = pnodes[s].tail = &elements[s];
		elements[s].next = NULL;
        //int kk=0;
		for(Edge *edge = nodes[s].first;edge != NULL&&!edge->deleted;edge = edge->next)
            {
          //      kk=kk+1;
          //cout<<!computed[edge->node_id];
			if(!computed[edge->node_id])
                {
				computed[edge->node_id] = 1;
				q[q_c] = edge->node_id;
				++ q_c;
                //cout<<q_c;
			}
			if(edge->node_id > s) {
				int a = s, b = edge->node_id;
				pedges[pedge_c].node_id = b;
				pedges[pedge_c].sc = 1;
				pedges[pedge_c].duplicate = &pedges[pedge_c+1];
				add_edge(pnodes[a], &pedges[pedge_c]);
				++ pedge_c;
				pedges[pedge_c].node_id = a;
				pedges[pedge_c].sc = 1;
				pedges[pedge_c].duplicate = &pedges[pedge_c-1];
				add_edge(pnodes[b], &pedges[pedge_c]);
				++ pedge_c;
			}
		}
		//cout<<kk;
		//cout<<"\n";
	}
	for(int i = 0;i < q_c;i ++) computed[q[i]] = 0;
	//cout<<q_c;
	//cout<<"\n";
	return q_c;
}
void Graph::delete_edge_to_last(Node &node, Edge *edge) {
	edge->deleted = 1;
	if(node.first == node.last) {

#ifdef _DEBUG_
		if(node.first != edge) printf("WA in delete_edge_to_last!\n");
#endif

		return ;
	}

	if(edge->pre == NULL) {
		node.first = edge->next;
		if(edge->next != NULL) edge->next->pre = NULL;
	}
	else {
		if(edge == node.last) node.last = edge->pre;

		Edge *tmp = edge->pre;
		tmp->next = edge->next;

		if(edge->next != NULL) edge->next->pre = tmp;
	}

	edge->next = node.last->next;
	if(edge->next != NULL) edge->next->pre = edge;
	node.last->next = edge;
	edge->pre = node.last;
}
int Graph::find_root(int x, int *parent) {
	int root = x;
	while(parent[root] != root) root = parent[root];

	while(parent[x] != root) {
		int tmp = parent[x];
		parent[x] = root;
		x = tmp;
	}

	return root;
}

void Graph::max_spanning_tree() {
#ifdef _LINUX_
        struct timeval start, end;
        gettimeofday(&start, NULL);
#else
        int start, end;
        start = clock();
#endif

	for(int i = 0;i < n;i ++) pnodes[i].first = pnodes[i].last = NULL;
	int edge_c = 0;

	int max_sc = 0;

	for(int i = 0;i < n;i ++) for(Edge *e = nodes[i].first;e != NULL;e = e->next) {
		if(e->node_id > i) {
			if(e->sc > max_sc) max_sc = e->sc;

			pedges[edge_c].node_id = i;
			pedges[edge_c].sc = e->node_id;
			pedges[edge_c].next = NULL;

			if(pnodes[e->sc].first == NULL) pnodes[e->sc].first = pnodes[e->sc].last = &pedges[edge_c];
			else {
				pnodes[e->sc].last->next = &pedges[edge_c];
				pnodes[e->sc].last = &pedges[edge_c];
			}

			++ edge_c;
		}
	}

	int *parent = new int[n];
	int *rank = new int[n];

	vector<pair<pair<int,int>, int> > vp;
	vp.reserve(n);

	for(int i = 0;i < n;i ++) {
		parent[i] = i;
		rank[i] = 0;
	}

	for(int i = max_sc;i > 0;i --) {
		for(Edge *e = pnodes[i].first;e != NULL;e = e->next) {
			int pa = find_root(e->node_id, parent);
			int pb = find_root(e->sc, parent);

			if(pa == pb) continue;

			vp.push_back(make_pair(make_pair(e->node_id, e->sc), i));

			if(rank[pa] < rank[pb]) parent[pa] = pb;
			else if(rank[pa] > rank[pb]) parent[pb] = pa;
			else {
				parent[pa] = pb;
				++ rank[pb];
			}
		}
	}

	for(int i = 0;i < n;i ++) pnodes[i].first = pnodes[i].last = NULL;

	edge_c = 0;
	for(int i = 0;i < (int)vp.size();i ++) {
		int a = vp[i].first.first, b = vp[i].first.second;

		pedges[edge_c].node_id = b;
		pedges[edge_c].next = pnodes[a].first;
		pnodes[a].first = &pedges[edge_c];
		++ edge_c;

		pedges[edge_c].node_id = a;
		pedges[edge_c].next = pnodes[b].first;
		pnodes[b].first = &pedges[edge_c];
		++ edge_c;
	}

	int c_id = 0;
	int *c_ids = new int[n];
	int *levels = new int[n];

	if(computed == NULL) computed = new int[n];

	memset(computed, 0, sizeof(int)*n);

	//printf("27:");
	//for(Edge *e = pnodes[27].first;e != NULL;e = e->next) printf(" %d", e->node_id);
	//printf("\n");

	for(int i = 0;i < n;i ++) {
		if(computed[i]) continue;

		int q_c = 1;
		q[0] = i;
		levels[i] = 0;
		c_ids[i] = c_id;

		computed[i] = 1;

		for(int j = 0;j < q_c;j ++) {
			for(Edge *e = pnodes[q[j]].first;e != NULL;e = e->next) if(!computed[e->node_id]) {
				//if(q[j] == 27||e->node_id == 27) printf("Edge %d %d %d\n", q[j], e->node_id, levels[q[j]]);
				computed[e->node_id] = 1;
				q[q_c ++] = e->node_id;
				levels[e->node_id] = levels[q[j]]+1;
				c_ids[e->node_id] = c_id;
			}
		}

		++ c_id;
	}

#ifdef _LINUX_
        gettimeofday(&end, NULL);

        long long mtime, seconds, useconds;
        seconds = end.tv_sec - start.tv_sec;
        useconds = end.tv_usec - start.tv_usec;
        mtime = seconds*1000000 + useconds;

        printf("mSPT ltime: %lld\n", mtime);

#else
        end = clock();

        printf("mSPT time: %d\n", end-start);
#endif

	string output_name = string(dir) + "/mSPT.txt";
	FILE *fout = open_file(output_name.c_str(), "w");

	fprintf(fout, "%d %d\n", n, vp.size());
	//if(vp.size() != n-1) printf("WA in tree size!\n");

	for(int i = 0;i < n;i ++) fprintf(fout, "%d %d\n", c_ids[i], levels[i]);

	for(int i = 0;i < (int)vp.size();i ++) fprintf(fout, "%d %d %d\n", vp[i].first.first, vp[i].first.second, vp[i].second);

	fclose(fout);

	delete[] c_ids;
	delete[] levels;
	delete[] parent;
	delete[] rank;
}


void Graph::output_all_sc(FILE *fout) {
	int *buf = new int[3*m];
	vector<pair<int,pair<int,int> > > vp;
	vp.reserve(m);
	FILE *ff = open_file((dir+string("/truss.txt")).c_str(), "w");
	fprintf(ff, "%d %d\n", n, m);
	fwrite(&n, sizeof(int), 1, fout);
	fwrite(&m, sizeof(int), 1, fout);
	int edge_c = 0;
	for(int i = 0;i < n;i ++){
        for(Edge *e = nodes[i].first;e != NULL;e = e->next)
        {if(e->node_id > i)
        {
        //cout<<e->sc;
		vp.push_back(make_pair(e->sc, make_pair(i,e->node_id)));
	     }
        }
	}
	sort(vp.begin(), vp.end());
	for(int i = (int)vp.size()-1;i >= 0;i --)
	{
		buf[edge_c] = vp[i].second.first; buf[edge_c+1] = vp[i].second.second; buf[edge_c+2] = vp[i].first;
		edge_c += 3;

		fprintf(ff, "%d %d %d\n", vp[i].second.first, vp[i].second.second, vp[i].first);
	}
	fclose(ff);
	if(edge_c/3 != m) printf("WA edge count in output_all_sc!\n");
	fwrite(buf, sizeof(int), edge_c, fout);
	delete[] buf;
}

