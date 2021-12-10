package teamtrio;
import java.util.*;
import java.io.*;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
public class Graph 
{
	private String dir = "";
	private int K;
	private int n;
	private int m;
	
        private Element[] elements; // buffer to allocate all elements
	private Edge[] edges; // buffer to allocate all edges
	private Node[] nodes;
	private Node[] pnodes; // nodes of super graph
	private Edge[] pedges; // edges of super graph
	
        private char []inL; // used in kCD_opt(), kCCkCD(), max_flow()
	private int[] computed; // used in kCCkCD(), kCSkCD(), construct_pgraph()
	private int []height; // record the maximum number of recursive calls in kSC
	private int[] q; // queque
	private int[] degrees; // used for k-core optimization
        private Heap heap;
	
	public Graph()
	{
		//dir = _dir;
		//System.out.println(dir);
		K = -1;
		nodes = null;
		edges = null;
		pnodes = null;
		pedges = null;
		inL = null;
		computed = null;
		height = null;
		elements = null;
		q = null;
		degrees = null;
		heap = null;
		
	}
/*
        
 Here below part is reading the text file as a graph.
 text file is nothing but the edge list.
 
        Here below you will see we have cretaed two readers: one for just reading the 
        first line of the text file, reading the number of edge and nodes.
        and second reader is reading the graph except the first line.
      
 */
public final void read_graph(String file_e) 
	{
                String path ="/home/scratch1/ksaifud/teamtrio/";
                FileReader reader1 = null;
            try {
                reader1 = new FileReader(path.concat(file_e));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, null, ex);
            }
		BufferedReader br1 = new BufferedReader(reader1);
		String lin1;
                int a11=0, b11=0;
            try {
		int i=0;
                while ((lin1 = br1.readLine()) != null) {
                    if (i==0)
                    {
                    StringTokenizer stt;
		    String Lin=lin1;
                    String a;
		    String [] b;
                    stt = new StringTokenizer(Lin, "	");
		    a = stt.nextToken();
                    b = a.split(" ");
		    a11=Integer.parseInt(b[0]);
		    b11=Integer.parseInt(b[1]);
                    }
                    i=i+1;
                }            } catch (IOException ex) {
                Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, null, ex);
            }
            
                 n=a11;
                 m=b11;

		nodes = teamtrio.Arrays.initializeWithDefaultNodeInstances(n);
		edges = teamtrio.Arrays.initializeWithDefaultEdgeInstances(2 * m);
		q = new int[n];
		int edge_c = 0;
		for (int i = 0;i < n;i++)
		{
			nodes[i].first = null;
		}
		int buf[] = null;
                FileReader reader = null;
            try {
                reader = new FileReader(path.concat(file_e));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, null, ex);
            }
		BufferedReader br = new BufferedReader(reader);
		String lin;
		int noe = 0;
		int a1=0,b1=0;
            try {
		int i=0;
                while ((lin = br.readLine()) != null) {
                    if (i>0)
                    {
                    StringTokenizer stt;
		    String [] b;
                    b = lin.split(" ");
		    a1=Integer.parseInt(b[0]);
		    b1=Integer.parseInt(b[1]);
		    edges[edge_c].node_id = b1;
                    edges[edge_c].sc = 1;
                    edges[edge_c].deleted = 0;
                    edges[edge_c].duplicate = edges[edge_c+1];
                    add_edge(nodes[a1], edges[edge_c]);
                    ++ edge_c;
                    edges[edge_c].node_id = a1;
                    edges[edge_c].sc = 1;
                    edges[edge_c].deleted = 0;
                    edges[edge_c].duplicate = edges[edge_c-1];
                    add_edge(nodes[b1], edges[edge_c]);
                    ++ edge_c;
                    
                    noe++;
                }
                    i=i+1;
                }            } catch (IOException ex) {
                Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, null, ex);
            }
		
		int numberOfEdge = noe;
		//System.out.println(numberOfEdge);
		
		if (buf != null)
		{
			buf = null;
		}
		pnodes = teamtrio.Arrays.initializeWithDefaultNodeInstances(n);
		pedges = teamtrio.Arrays.initializeWithDefaultEdgeInstances(2 * m);
		for (int i = 0;i < n;i++)
		{
			pnodes[i].first = null;
		}
		elements = teamtrio.Arrays.initializeWithDefaultElementInstances(n);
		for (int i = 0;i < n;i++)
		{
			elements[i].value = i;
		}
	}

/*
This function is basically the main function.
Here it calls the kSC() that computes the k-ECC value from the graph
We then pass the output to write as a text file.

*/
public final void all_SC_naive(String file_e)
	{
	
            long startTime = System.nanoTime();
            for (int i = 1;i <= n;i++)
		{
			if (i > 1)
                    {
			
                        if (kSC(i) == 0)
			{
                            //System.out.println("ok-ksc");
			break;
			}
                    }
			//if(i > 1) output_k_edge_connected_subgraphs();
			for (int j = 0;j < n;j++)
			{
				for (Edge e = nodes[j].first;e != null;e = e.next)
				{
					if (e.deleted== 0)/////////////////////////***********************************///////////////
					{
						e.sc = i;
                                                //System.out.println("hamim");
                                                //System.out.println(i);
					}
					else
					{
						e.deleted = 0;
					}
					nodes[j].last = e;
				}
			}
		}
           long endTime = System.nanoTime();
           System.out.print("Decomposition time: ");
           System.out.println((endTime - startTime) / 1000000000.00);
	//System.out.print("Here\n");
	
    output_all_sc(file_e);
            
	}

/*
This fucntion() is just for writing a output text file for each edge k value 
*/
public final void output_all_sc(String file_e)
	{
            String path ="/home/scratch1/ksaifud/teamtrio/".concat("K-ecc_");
            int[] buf = new int[3 * m];
		ArrayList<teamtrio.Pair<Integer,teamtrio.Pair<Integer,Integer>> > vp = new ArrayList<teamtrio.Pair<Integer,teamtrio.Pair<Integer,Integer>> >();
		vp.ensureCapacity(m);		
            PrintWriter ff=null;
            try {
                ff = new PrintWriter(path.concat(file_e));
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, null, ex);
            }
                //ff.println("" +n+ " " + m);
		//fwrite(n, (Integer.SIZE / Byte.SIZE), 1, fout);
		//fwrite(m, (Integer.SIZE / Byte.SIZE), 1, fout);
		int edge_c = 0;
		for (int i = 0;i < n;i++)
		{
			for (Edge  e = nodes[i].first;e != null;e = e.next)
			{
				if (e.node_id > i)
				{
				//System.out.println(e.sc);
			vp.add(new teamtrio.Pair<>(e.sc, new teamtrio.Pair<>(i,e.node_id)));
				}
			}
		}
                System.out.println();
		//Collections.sort(vp);
		for (int i = (int)vp.size() - 1;i >= 0;i--)
		{
			buf[edge_c] = vp.get(i).second.first;
			buf[edge_c + 1] = vp.get(i).second.second;
			buf[edge_c + 2] = vp.get(i).first;
			edge_c += 3;

			ff.println(""+vp.get(i).second.first+"," +vp.get(i).second.second+","+ vp.get(i).first);
		}
		ff.close();
		if (edge_c / 3 != m)
		{
			System.out.print("WA edge count in output_all_sc!\n");
		}
		buf = null;
	}
/*
This fucntion goes though each edge and compute the k-ecc value. 
it decomposes by the uding of decompose() [later] the input graph into strong connected components.

*/
public final int kSC(int _K)
	{
		K = _K;
		if (computed == null)
		{
			computed = new int[n];
		}
		if (height == null)
		{
			height = new int[n];
		}
		if (degrees == null)
		{
			degrees = new int[n];
		}
                
                Arrays.fill(computed, 0, n, (byte)0);
                Arrays.fill(height, 0, n, (byte)0);
                
               // System.out.println(computed[0]);
               // System.out.println("com");
		int q_c = 0;
		for (int i = 0;i < n;i++)
		{
			int cnt = 0;
			for (Edge edge = nodes[i].first;edge != null && edge.deleted==0;edge = edge.next)///////////////////////////**********************////////////////
			{
				++cnt;
                                
			}
			if (cnt < K)
			{
				q[q_c] = i;
				++q_c;
			}
			degrees[i] = cnt;
                        
		}

		kcore_optimization(q_c);
		int max_l = 0;
		int non_trivial = 0;
		for (int i = 0;i < n;i++)
		{
			// if(i%10000 == 0) printf(".");
			if (computed[i] != 0)
			{
				continue;
			}
			//tangible.RefObject<Integer> tempRef_height = new tangible.RefObject<Integer>(height);
			if (construct_pgraph(i, height) > 1)
			{
				//height[0] = tempRef_height.argValue;
				non_trivial = 1;
                                //System.out.println("h"+" "+non_trivial);
			}
                        
			//else
			//{
			//	height[0] = tempRef_height.argValue;
			//}
			ArrayList<Element> cc = new ArrayList<Element>();
			//tangible.RefObject<Integer> tempRef_max_l = new tangible.RefObject<Integer>(max_l);
			//System.out.println("ok-d");
                        decomposition(i, cc, max_l);/////////////////////////////////////////////
                        
			//max_l = tempRef_max_l.argValue;
			if (cc.size() == 1)
			{
			for (Element  e = cc.get(0);e != null;e = e.next)
			{
			 computed[e.value] = 1;
			}
			}
			else
			{
				remove_inter_edges(cc);/////////////////////////////////////////////
			}
			--i;
		}
		int max_height = height[0];
		for (int i = 0;i < n;i++)
		{
		if (height[i] > max_height)
		{
		 max_height = height[i];
		}
		}
                //System.out.println(non_trivial);
		// printf("K: %d, Height: %d, Max L: %d\n", K, max_height, max_l);
		return non_trivial;
	}
/*
This is the super graph constructuion part
*/
private int construct_pgraph(int s, int[] height)
	{
		int pedge_c = 0;
		int q_c = 1;
		computed[s] = 1;
		q[0] = s;
		for (int i = 0;i < q_c;i++)
		{
			s = q[i];
			++height[q[i]];
			pnodes[s].head = pnodes[s].tail = elements[s];
			elements[s].next = null;
                        int kk=0;
			for (Edge  edge = nodes[s].first;edge != null && edge.deleted==0;edge = edge.next)////////////////*********************////////////////////
			{
			//kk=kk+1;	
                            if (computed[edge.node_id] == 0) //////////////////////////*******************************////////////////////////////
				{
					computed[edge.node_id] = 1;
					q[q_c] = edge.node_id;
					++q_c;
                                        
				}
				if (edge.node_id > s)
				{
					int a = s;
					int b = edge.node_id;
					pedges[pedge_c].node_id = b;
					pedges[pedge_c].sc = 1;
					pedges[pedge_c].duplicate = pedges[pedge_c + 1];
					add_edge(pnodes[a], pedges[pedge_c]);
					++pedge_c;
					pedges[pedge_c].node_id = a;
					pedges[pedge_c].sc = 1;
					pedges[pedge_c].duplicate = pedges[pedge_c - 1];
					add_edge(pnodes[b], pedges[pedge_c]);
					++pedge_c;
				}
			}
                        //System.out.println(kk);
		}
		for (int i = 0;i < q_c;i++)
		{
			computed[q[i]] = 0;
		}
                //System.out.println(q_c);
		return q_c;
	}
/*
This function() is called by the kSC() function to decompose the graph.
*/
private void decomposition(int ss, ArrayList<Element > cc, int max_l)
	{
		if (heap == null)
		{
		 heap = new LinearHeap(n);
		}
		if (inL == null)
		{
			inL = new char[n];
		}
		cc.clear();
		int cnt = 0;
                int kk=0;
                //System.out.println("ok1");
		while (pnodes[ss].first != null)
		{       //System.out.println("ok");
			int s = ss;
			++cnt;
			heap.insert(s, 0);
			int q_c = 0;
			int key=0;
			while (true)
			{
				//tangible.RefObject<Integer> tempRef_s = new tangible.RefObject<Integer>(s);
				//tangible.RefObject<Integer> tempRef_key = new tangible.RefObject<Integer>(key);
				if (heap.extract_max(s, key) == 0)///////////////////////////////***************************//////////////////////////////
				{
					
                                        //key = tempRef_key.argValue;
					//s = tempRef_s.argValue;
					break;
				}
				//else
				//{
				//	key = tempRef_key.argValue;
				//}
				//inL = tangible.StringFunctions.changeCharacter(inL, s, 1);
                                inL[s]=1;
				q[q_c] = s;
				++q_c;
				int new_qc = q_c;
				for (int i = q_c - 1;i < new_qc;i++)
				{
					int u = q[i];
					for (Edge  e = pnodes[u].first;e != null;e = e.next)
					{
						if (inL[e.node_id] == 0)////////////////////////////////***************************????????????????????//
						{
						int new_key = heap.get_key(e.node_id);
						if (new_key < K)
						{
							if (new_key > 0)
							{
								heap.remove(e.node_id);
							}
							new_key += e.sc;
							if (new_key >= K)
							{
								heap.set_key(e.node_id, new_key);
								q[new_qc++] = e.node_id;
							}
							else
							{
								heap.insert(e.node_id, new_key);
							}
						}
						else
						{
							heap.set_key(e.node_id, new_key + e.sc);
						}
						}
					}
					if (u == s)
					{
						continue;
					}
					heap.set_key(s, heap.get_key(s) + heap.get_key(u));
					heap.set_key(u, 0);
					inL[u]=0;
					merge(s, u, heap); ////////////////////////////////
				}
			}
                        
			--q_c;
                        //System.out.println(q_c);
			while (q_c > 0 && heap.get_key(q[q_c]) < K)
			{       kk=kk+1;
				int t = q[q_c];
				--q_c;
				cc.add(pnodes[t].head);
				heap.set_key(t, 0);
				inL[t]=0;
				for (Edge  e = pnodes[t].first;e != null;e = e.next)
				{
					delete_edge(pnodes[e.node_id], e.duplicate); ///////////////////////
				}
				pnodes[t].first = null;
                                //System.out.println("ok");
			}
			for (int i = 0;i <= q_c;i++)
			{
				heap.set_key(q[i], 0);
				inL[q[i]]=0;
			}
                        
		}
                
		if (cnt > max_l)
		{ 
                    //System.out.println("if");
                    	max_l = cnt;
		}
		cc.add(pnodes[ss].head);
	}

/*
remove_inter_edges() to remove the edges that don't fullfill the requirement of k-edge connectivity
*/
private void remove_inter_edges(final ArrayList<Element > cc)
	{
		remove_inter_edges(cc, 0);
	}

private void remove_inter_edges(final ArrayList<Element > cc, int assign_sc)
	{
		for (int j = 0;j < (int)cc.size();j++)
		{
			for (Element  e = cc.get(j);e != null;e = e.next)
			{
				computed[e.value] = j + 1;
			}
		}
		int q_c = 0;
		for (int j = 0;j < (int)cc.size();j++)
		{
			for (Element  e = cc.get(j);e != null;e = e.next)
			{
			int s = e.value;
			Edge list = nodes[s].first;
			Edge delete_first = nodes[s].last.next;
			nodes[s].first = nodes[s].last = null;
			int cnt = 0;
			while (list != null && list.deleted == 0)///////////////////////////////**********************????????????????????///
			{

				Edge tmp = list.next;
				if (computed[list.node_id] == computed[s])
				{
					if (nodes[s].first == null)
					{
						nodes[s].first = nodes[s].last = list;
						list.pre = null;
					}
					else
					{
						nodes[s].last.next = list;
						list.pre = nodes[s].last;
						nodes[s].last = list;
					}

					++cnt;
				}
				else
				{
					list.deleted = 1;
					if (assign_sc != 0)//////////////////////////////////////*******************/////////
					{
						list.sc = K - 1;
					}
					list.next = delete_first;
					if (delete_first != null)
					{
						delete_first.pre = list;
					}
					delete_first = list;
				}
				list = tmp;
			}
			degrees[s] = cnt;
			if (cnt < K)
			{
				q[q_c] = s;
				++q_c;
			}
			if (nodes[s].first == null)
			{
				nodes[s].first = nodes[s].last = delete_first;
				if (delete_first != null)
				{
					delete_first.pre = null;
				}
			}
			else
			{
				nodes[s].last.next = delete_first;
				if (delete_first != null)
				{
					delete_first.pre = nodes[s].last;
				}
			}
			}
		}
		for (int j = 0;j < (int)cc.size();j++)
		{
			for (Element  e = cc.get(j);e != null;e = e.next)
			{
				computed[e.value] = 0;
			}
		}
		kcore_optimization(q_c, assign_sc);
	}

private void merge(int s, int t, Heap heap)
	{
		pnodes[s].tail.next = pnodes[t].head;
		pnodes[s].tail = pnodes[t].tail;
		Edge e = pnodes[t].first;
		Edge tmp;
		while (e != null)
		{
			tmp = e.next;
			if (e.node_id == s)
			{
				if (heap != null)
				{
					heap.set_key(s, heap.get_key(s) - e.sc);
				}
				delete_edge(pnodes[e.node_id], e.duplicate);///////////////////////
			}
			else
			{
				e.duplicate.node_id = s;
				add_edge(pnodes[s], e);///////////////////////////
			}
			e = tmp;
		}
		pnodes[t].first = null;
	}
private void add_edge(Node node, Edge edge)
	{
		edge.next = null;
		if (node.first == null)
		{
			node.first = node.last = edge;
			edge.pre = null;
		}
		else
		{
			node.last.next = edge;
			edge.pre = node.last;
			node.last = edge;
		}
	}
private void delete_edge(Node node, Edge edge)
	{
		if (edge.pre == null)
		{
			node.first = edge.next;
			if (edge.next != null)
			{
				edge.next.pre = null;
			}
		}
		else
		{
			if (edge == node.last)
			{
				node.last = edge.pre;
			}

			Edge tmp = edge.pre;
			tmp.next = edge.next;

			if (edge.next != null)
			{
				edge.next.pre = tmp;
			}
		}
	}

/*
Here it optimizes the k-ECC by removing unnecessary edges, to do that we use delete_edge(), remove_inter_edge() and delete_edge_to_last() functions.
*/

private void kcore_optimization(int q_c)
	{
		kcore_optimization(q_c, 0);
	}
private void kcore_optimization(int q_c, int assign_sc)
	{
		for (int i = 0;i < q_c;i++)
		{
			int s = q[i];
			computed[s] = 1;
			for (Edge  edge = nodes[s].first;edge != null && edge.deleted==0; edge = edge.next)/////////////////////***********************///////////////////
			{
				//System.out.println("hamim"+edge.deleted);
                                int t = edge.node_id;
				delete_edge_to_last(nodes[t], edge.duplicate);
				--degrees[t];
				if (degrees[t] == K - 1)
				{
					q[q_c] = t;
					++q_c;
				}
				edge.deleted = 1;
				if (assign_sc != 0)
				{
					edge.sc = edge.duplicate.sc = K - 1;
				}
			}
			nodes[s].last = nodes[s].first;
		}
	}


private void delete_edge_to_last(Node node, Edge edge)
	{
		edge.deleted = 1;
		if (node.first == node.last)
		{
			return;
		}

		if (edge.pre == null)
		{
			node.first = edge.next;
			if (edge.next != null)
			{
				edge.next.pre = null;
			}
		}
		else
		{
			if (edge == node.last)
			{
				node.last = edge.pre;
			}
                        Edge tmp = edge.pre;
                        
			//Edge[] tmp = new Edge(edge.pre);
			tmp.next = edge.next;

			if (edge.next != null)
			{
				edge.next.pre = tmp;
			}
		}

		edge.next = node.last.next;
		if (edge.next != null)
		{
			edge.next.pre = edge;
		}
		node.last.next = edge;
		edge.pre = node.last;
	}
}				 //returns 1 iff there is at least one non-trivial k-strong component
/*
public final void max_spanning_tree()
	{

		for (int i = 0;i < n;i++)
		{
			pnodes[i].first = pnodes[i].last = null;
		}
		int edge_c = 0;

		int max_sc = 0;

		for (int i = 0;i < n;i++)
		{
			for (Edge  e = nodes[i].first;e != null;e = e.next)
			{
			if (e.node_id > i)
			{
				if (e.sc > max_sc)
				{
					max_sc = e.sc;
				}

				pedges[edge_c].node_id = i;
				pedges[edge_c].sc = e.node_id;
				pedges[edge_c].next = null;

				if (pnodes[e.sc].first == null)
				{
					pnodes[e.sc].first = pnodes[e.sc].last = pedges[edge_c];
				}
				else
				{
					pnodes[e.sc].last.next = pedges[edge_c];
					pnodes[e.sc].last = pedges[edge_c];
				}

				++edge_c;
			}
			}
		}

		int[] parent = new int[n];
		int[] rank = new int[n];

		ArrayList<teamtrio.Pair<teamtrio.Pair<Integer,Integer>, Integer>> vp = new ArrayList<teamtrio.Pair<teamtrio.Pair<Integer,Integer>, Integer>>();
		vp.ensureCapacity(n);

		for (int i = 0;i < n;i++)
		{
			parent[i] = i;
			rank[i] = 0;
		}

		for (int i = max_sc;i > 0;i--)
		{
			for (Edge  e = pnodes[i].first;e != null;e = e.next)
			{
				int pa = find_root(e.node_id, parent); /////////////////////////////////////////
				int pb = find_root(e.sc, parent);/////////////////////////////////////////////

				if (pa == pb)
				{
					continue;
				}
                             
				vp.add(new teamtrio.Pair<>(new teamtrio.Pair<>(e.node_id, e.sc), i));

				if (rank[pa] < rank[pb])
				{
					parent[pa] = pb;
				}
				else if (rank[pa] > rank[pb])
				{
					parent[pb] = pa;
				}
				else
				{
					parent[pa] = pb;
					++rank[pb];
				}
			}
		}

		for (int i = 0;i < n;i++)
		{
			pnodes[i].first = pnodes[i].last = null;
		}

		edge_c = 0;
		for (int i = 0;i < (int)vp.size();i++)
		{
			int a = vp.get(i).first.first;
			int b = vp.get(i).first.second;

			pedges[edge_c].node_id = b;
			pedges[edge_c].next = pnodes[a].first;
			pnodes[a].first = pedges[edge_c];
			++edge_c;

			pedges[edge_c].node_id = a;
			pedges[edge_c].next = pnodes[b].first;
			pnodes[b].first = pedges[edge_c];
			++edge_c;
		}

		int c_id = 0;
		int[] c_ids = new int[n];
		int[] levels = new int[n];

		if (computed == null)
		{
			computed = new int[n];
		}
                //memset(computed, 0, sizeof(int)*n);
                Arrays.fill(computed, 0, n, (byte)0);
		for (int i = 0;i < n;i++)
		{
			if (computed[i] != 0)
			{
				continue;
			}

			int q_c = 1;
			q[0] = i;
			levels[i] = 0;
			c_ids[i] = c_id;

			computed[i] = 1;

			for (int j = 0;j < q_c;j++)
			{
				for (Edge  e = pnodes[q[j]].first;e != null;e = e.next)
				{
					if (computed[e.node_id] == 0)
					{
					//if(q[j] == 27||e->node_id == 27) printf("Edge %d %d %d\n", q[j], e->node_id, levels[q[j]]);
					computed[e.node_id] = 1;
					q[q_c++] = e.node_id;
					levels[e.node_id] = levels[q[j]] + 1;
					c_ids[e.node_id] = c_id;
					}
				}
			}

			++c_id;
		}

		String output_name = new String(dir) + "/mSPT.txt";
		//FILE fout = open_file(output_name, "w");
                PrintWriter fout=null;
            try {
                fout = new PrintWriter(dir+"/mSPT.txt");
            } catch (FileNotFoundException ex) {
                Logger.getLogger(Graph.class.getName()).log(Level.SEVERE, null, ex);
            }
                fout.println("" +n+ " " + vp.size());
		//fprintf(fout, "%d %d\n", n, vp.size());
		//if(vp.size() != n-1) printf("WA in tree size!\n");

		for (int i = 0;i < n;i++)
		{
			fout.println(""+ c_ids[i]+" "+ levels[i]);
                        
		}

		for (int i = 0;i < (int)vp.size();i++)
		{
			fout.println(""+ vp.get(i).first.first+" "+ vp.get(i).first.second+" "+vp.get(i).second);
		}

		fout.close();

		c_ids = null;
		levels = null;
		parent = null;
		rank = null;
	}
private int find_root(int x, int[] parent)
		{
			int root = x;
			while (parent[root] != root)
			{
				root = parent[root];
			}

			while (parent[x] != root)
			{
				int tmp = parent[x];
				parent[x] = root;
				x = tmp;
			}

			return root;
		}

       }
*/