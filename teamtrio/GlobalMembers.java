package teamtrio;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

public class GlobalMembers
{
	public static void display_usage()
	{
		System.out.print("[0]exe\t [1]gen-gc-query [2]graph_dir [3]number_of_vertices\n");
		System.out.print("\t\t number_of_vertices can be -1 or [2,30]\n");
		System.out.print("\t [1]process-update-query [2]graph_dir [3]update_file\n");
		System.out.print("\t [1]count-cc [2]graph_dir\n");
		System.out.print("\t [1]extract-mcc [2]graph_dir\n");
		System.out.print("\t [1]min-max-conn [2]graph_dir\n");
		System.out.print("\t [1]transform-edge-to-binary [2]graph_dir\n");
		System.out.print("\t [1]kSC [2]graph_dir [3]k [4]output or not\n");
		System.out.print("\t [1]all-sc [2]graph_dir [3]naive or bottom-up [4]output or not\n");
		System.out.print("\t [1]spt-opt [2]graph_dir [3]output or not\n");
		System.out.print("\t [1]gen-update-query [2]graph_dir [3]gen-query or not\n");
		System.out.print("\t [1]gen-smcc-query [2]graph_dir [3]query_size\n");
		System.out.print("\t [1]smcc-mSPT [2]graph_dir [3]query\n");
		System.out.print("\t [1]sc [2]graph_dir [3]query [4]method 0,1,2\n");
	}

	public static void main( String[] args)
	{
                int argc=args.length;
		if (argc < 2)
		{
				System.out.print("hamim");
                                display_usage();
		}
		
		if (args[0].compareTo("all-sc")==0)
		{
			if (argc < 2)
			{
				display_usage();
			}
                
                        
                System.out.println("************************* Team Trio *************************");
                System.out.println("Select the dataset (Enter the number):\n 1. General Relativity and Quantum Cosmology \n 2. Condense Matter Physics \n 3. Amazon \n 4. DBLP");
                String file_e=" ";
                Scanner in = new Scanner(System.in);
                int data = in.nextInt();
                if (data==1)
                {    file_e="CA-GrQc.txt";
                     
                } 
                
                if (data==2)
                {    file_e="CA-CondMat.txt";
                     
                }   
                
                if (data==3)
                {
                    file_e="com-amazon.ungraph.txt";
                    
                }
                 if (data==4)
                {
                    file_e="com-dblp.ungraph.txt";
                    
                }
                 
                Graph graph = new Graph();
                graph.read_graph(file_e);
                if (args[1].compareTo("naive") == 0)
                {
                        graph.all_SC_naive(file_e);
                }
                //graph.max_spanning_tree();
		}
	}

}