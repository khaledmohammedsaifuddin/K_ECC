/* Esra Akbas
main file to run the program
*/
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class MainF {
	public static void main(String[] args) throws IOException { 

                System.out.println("************************* Team Trio *************************");
                System.out.println("Select the dataset (Enter the number):\n 1. General Relativity and Quantum Cosmology \n 2. Condense Matter Physics \n 3. Amazon \n 4. DBLP");
                String file_e=" ",file_k=" ",file_d=" ";
                Scanner in = new Scanner(System.in);
                int data = in.nextInt();
                if (data==1)
                {    file_e="CA-GrQc.txt";
                     file_k="K-ecc_CA-GrQc.txt";
                     file_d="Degree_CA-GrQc.txt";
                } 
                
                if (data==2)
                {    file_e="CA-CondMat.txt";
                     file_k="K-ecc_CA-CondMat.txt";
                     file_d="Degree_CA-CondMat.txt";
                }   
                
                if (data==3)
                {
                    file_e="com-amazon.ungraph.txt";
                    file_k="K-ecc_com-amazon.ungraph.txt";
                    file_d="Degree_com-amazon.ungraph.txt";
                }
                 if (data==4)
                {
                    file_e="com-dblp.ungraph.txt";
                    file_k="K-ecc_com-dblp.ungraph.txt";
                    file_d="Degree_com-dblp.ungraph.txt";
                    
                }
                
                String fileName="/home/scratch1/ksaifud/teamtrio/".concat(file_e);
                String pathtec="/home/scratch1/ksaifud/Equitruss-master";

		TecIndexG tec = new TecIndexSB();
		MyGraph mg = new MyGraph();
		/*** Read Graph ***/
		long startTime = System.nanoTime();
		mg.read_GraphEdgelist(fileName);
		long endTime = System.nanoTime();
                
                Scanner inn = new Scanner(System.in); 
                System.out.printf("Enter 1 to create index, otherwise 2 to upload the index saved file directly:  ");
		int c= inn.nextInt();// c=1 will create index for graph, otherwise it will upload index  from files in given folder pathtec
		if (c == 1) 
                    
                {
                        System.out.printf("Enter 1 to create index using K-ECC, otherwise 2 for using Truss:  ");
                        int d= in.nextInt();// d=1 for K-ecc based index construction and d=2 for truss-based 
                
                        Map<Integer, LinkedHashSet<MyEdge>> klistdict = new HashMap<Integer, LinkedHashSet<MyEdge>>();
                        Map<MyEdge, Integer> trussd = new HashMap<MyEdge, Integer>();		
                       
                        if (d==1)
                            {
                            Set<Integer> hash_Set = new HashSet<Integer>();
                            trussd=mg.readTrussf("/home/scratch1/ksaifud/teamtrio/".concat(file_k));                            
                            String filePath = "/home/scratch1/ksaifud/teamtrio/".concat(file_k);
                            String line;
                            BufferedReader reader = new BufferedReader(new FileReader(filePath));
                            while ((line = reader.readLine()) != null)
                            {
                                String[] parts = line.split(",");
                                if (parts.length >= 2)
                                {
                                    //int  key1 =Integer.parseInt( parts[0]);
                                    //int key2 = Integer.parseInt(parts[1]);
                                    int value = Integer.parseInt(parts[2]);
                                     //MyEdge  e = new MyEdge(key1,key2);
                                     //map.put(e, value);
                                    hash_Set.add(value);
                                } else {
                                    System.out.println("ignoring line: " + line);
                                }
                            }

                            for (int i:hash_Set)
                            {
                                 LinkedHashSet<MyEdge> kedgelist = new LinkedHashSet<MyEdge>();
                                 for (MyEdge key : trussd.keySet()) 
                                 {
                                         if (trussd.get(key)==i)
                                    {                              
                                    kedgelist.add(key);
                                    }                            
                                }
                             //System.out.println(kedgelist.size());
                             klistdict.put(i, kedgelist);                        
                            }
                            }

                       if (d==2){
                        // Truss Decomposition 
                        createDir(pathtec);
			startTime = System.nanoTime();
			klistdict = mg.computeTruss(pathtec, trussd);
			endTime = System.nanoTime();
			System.out.print("truss computation time: ");
			System.out.println((endTime - startTime) / 1000000000.00);
			mg.write_support(pathtec + "/truss.txt", trussd);
                       }
                        
			/**** Create Index ****/
			startTime = System.nanoTime();
			tec.constructIndex(klistdict, trussd, mg);
			endTime = System.nanoTime();
			System.out.print("Index for given graph is created. Index creation time: ");
			System.out.println((endTime - startTime) / 1000000000.00);
			tec.writeIndex(pathtec);
                        
		} 
                if (c==2) {
			File theDir = new File(pathtec);
			if (!theDir.exists()) {
				System.out.println("given path for index files does not exists");
				System.exit(0);
			}
			tec.read_Indexlj(mg, pathtec);
			System.out.println("Index files are read and index is created ");
			//test(tec);
                        
                        String filePath1 = "/home/scratch1/ksaifud/teamtrio/".concat(file_d);
                        String line1;
                        BufferedReader reader1 = new BufferedReader(new FileReader(filePath1));
                        int i=1;
                        Scanner s = new Scanner(System.in);
                        System.out.println("enter k-value: ");
                        int k_value = s.nextInt(); //parameter    
                        List<Integer> L = new ArrayList<>();
                        int m=10;
                        System.out.println("************************* For different percentile of Degree Rank *************************");
                        while ((line1 = reader1.readLine()) != null)
                            {
                                String[] parts1 = line1.split("\n");                            
                                if (i<=100)
                                {
                                    int parts_1 = Integer.parseInt(parts1[0]);
                                    L.add(parts_1);
                                    if (i==100)
                                    {
                                       test(tec,L,k_value,m); 
                                       L = new ArrayList<>();
                                       i=0;
                                       m=m+10;                                    
                                    }
                                }
                                i++;
                            }
                        System.out.println("************************* For different K-value *************************");
                        String line2;
                        BufferedReader reader2 = new BufferedReader(new FileReader(filePath1));
                        int j=1;
                        while ((line2 = reader2.readLine()) != null)
                            {
                                String[] parts2 = line2.split("\n");                           
                                if (j<=300)
                                {
                                    int parts_2 = Integer.parseInt(parts2[0]);
                                    L.add(parts_2);
                                    if (j==300)
                                    {
                                       for (int x=3;x<=6;x++)
                                       {
                                           test(tec,L,x,m=0); 
                                       }                                       
                                        L = new ArrayList<>();                                                                      
                                    }
                                    
                                }
                                if (j>300)
                                {
                                    int parts_2 = Integer.parseInt(parts2[0]);
                                    L.add(parts_2);
                                    if (j==700)
                                    {
                                       for (int x=3;x<=6;x++)
                                       {
                                           test(tec,L,x,m=-1); 
                                       }                                       
                                        L = new ArrayList<>();                                                                      
                                    }
                                }
                                    
                                j++;
                            }
 		}
	}

	public static void test(TecIndexG tec, List<Integer> L, int k_value, int m ) throws IOException {
		Scanner s = new Scanner(System.in);
		//Integer query = Integer.parseInt(s.next()); //parameter
               long overallTime= 0;
                long time= 0;
              
                for (int i:L)
                {
                        //String j=String.valueOf(i);  
                        Integer query = i;
                        //System.out.println(query);
                        final long startTime = System.nanoTime();
			LinkedList<LinkedList<MyEdge>> com = tec.findkCommunityForQuery(query, k_value);
			final long endTime = System.nanoTime();
                        time=((endTime-startTime));
                        //System.out.println((endTime-startTime));
                        overallTime= overallTime+time;
                        if (com.size() == 0) 
                        {
				//System.out.println("There is no community for this query with given truss value");
			} 
                        /*
                        else {
				for (LinkedList<MyEdge> c : com) {
					for (MyEdge e : c) {
						System.out.print("(" + e.s + "," + e.t + "), ");
					}
					System.out.println();
				}
			}
                        */
                       
                }
                
                float T=overallTime/1000000;
                if (m>0)
                System.out.println("Avg. time for degree rank "+m+"% is: "+T/100);
                                
                if (m==0)
                System.out.println("Avg. time for K value "+k_value+" for high degree 30% is: "+T/300);
                
                if (m==-1)
                    System.out.println("Avg. time for K value "+k_value+" for low degree 70% is: "+T/700);
                /*
		do {
			int k = s.nextInt(); //parameter
			long startTime = System.nanoTime();
			LinkedList<LinkedList<MyEdge>> com = tec.findkCommunityForQuery(query, k);
			long endTime = System.nanoTime();

			if (com.size() == 0) 
                        {
				System.out.println("There is no community for this query with given truss value");
			} 
                        else {
				for (LinkedList<MyEdge> c : com) {
					for (MyEdge e : c) {
						System.out.print("(" + e.s + "," + e.t + "), ");
					}
					System.out.println();
				}
			}
			System.out.print("query time:");
			System.out.println((endTime - startTime) / 1000000000.00);
			System.out.println("\n\nenter query node id and  k truss value, -1 to exit");
			query = Integer.parseInt(s.next());
		} while (query != -1);
		s.close();
                */
	}

	public static void createDir(String path) {
		File theDir = new File(path);

		// if the directory does not exist, create it
		if (!theDir.exists()) {
			System.out.println("creating directory: " + path);
			boolean result = false;

			try {
				theDir.mkdir();
				result = true;
			} catch (SecurityException se) {
				System.out.println("there is a problem with creating directory");
			}
		}
	}

}
