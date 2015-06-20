package knn1;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeSet;

class queueElements1 implements Comparable<queueElements1>{
	int classlabel;
	Double similarity;
	public queueElements1(int label,Double sim){
		this.classlabel = label;
		this.similarity = sim;
	}
	@Override
	public int compareTo(queueElements1 o) {
		// TODO Auto-generated method stub
		return this.similarity.compareTo(o.similarity);
	}
	
}

class queueElements2 implements Comparable<queueElements2>{
	int classlabel;
	Double similarity;
	public queueElements2(int label,Double sim){
		this.classlabel = label;
		this.similarity = sim;
	}
	@Override
	public int compareTo(queueElements2 o) {
		// TODO Auto-generated method stub
		return o.similarity.compareTo(this.similarity);
	}
	
}

public class knn1_text {
	public Map<String, Integer> dictionary = new HashMap<String,Integer>();
	public ArrayList<Integer> train_labels = new ArrayList<Integer>();
	public ArrayList<Map> all_documents = new ArrayList<Map>();
	public ArrayList<Map> all_documents_test = new ArrayList<Map>();
	public ArrayList<Integer> test_labels = new ArrayList<Integer>();
	
	public void readTrainfile(String filepath)throws IOException{
		String contents;
		String[] split_contents;
		BufferedReader buffer = new BufferedReader(new FileReader(filepath));
		int index = 0;
		
		//Create a dictionary of words
		int line_num = 1;
        while((contents = buffer.readLine()) != null)
        {  	
        	//Vector (Map) with the index of the word in dictionary as key and word count as value
        	Map<Integer,Integer> document = new HashMap<Integer,Integer>();
        	//System.out.println(contents);
        	split_contents = contents.split(" ");
        	train_labels.add(Integer.parseInt(split_contents[0]));
        	for(int i=1;i<split_contents.length;i++){
        			String[] word_count = split_contents[i].split(":");
        			//System.out.println(word_count[0]);
        			if(dictionary.containsKey(word_count[0])){
        				
        				//do nothing
        			}
        			else{
        				dictionary.put(word_count[0],index);
        				index+=1;
        			}
        			  //Create the vector representations for each document
        			document.put(dictionary.get(word_count[0]), Integer.parseInt(word_count[1]));
        	}
        	/*for (Map.Entry<String, Integer> entry : dictionary.entrySet()) {
        	    String key = entry.getKey();
        	    Integer value = entry.getValue();
        	    System.out.println(key+" ->"+ value);
        	
        	}*/
        	//System.out.println("The line number is"+line_num+" "+document.size());
        	//All Documents is an arraylist of all documents in the file
        	all_documents.add(document);
        	line_num+=1;
        	/*for (Map.Entry<Integer, Integer> entry : document.entrySet()) {
        	    Integer key = entry.getKey();
        	    Integer value = entry.getValue();
        	    System.out.println("Key"+key+" Value"+ value);
        	
        	}*/
        	
        }
      //  System.out.println(dictionary.size());
    	//System.out.println(index);
    	//System.out.println(all_documents.size());
       
	}
	public void readTestfile(String filepath)throws IOException{
		String contents;
		String[] split_contents;
		BufferedReader buffer = new BufferedReader(new FileReader(filepath));
		int index = 0;
		int line_num = 1;
        while((contents = buffer.readLine()) != null)
        {  	
        	//Vector (Map) with the index of the word in dictionary as key and word count as value
        	Map<Integer,Integer> document = new HashMap<Integer,Integer>();
        	//System.out.println(contents);
        	split_contents = contents.split(" ");
        	test_labels.add(Integer.parseInt(split_contents[0]));
        	for(int i=1;i<split_contents.length;i++){
        			String[] word_count = split_contents[i].split(":");
        			  //Create the vector representations for each document
        			if(dictionary.containsKey(word_count[0])){
        				document.put(dictionary.get(word_count[0]), Integer.parseInt(word_count[1]));
        			}
        			else{
        				continue;
        			}
        	}
        	//System.out.println("The line number is"+line_num+" "+document.size());
        	//All Documents is an arraylist of all documents in the file
        	all_documents_test.add(document);
        	line_num+=1;
        	/*for (Map.Entry<Integer, Integer> entry : document.entrySet()) {
        	    Integer key = entry.getKey();
        	    Integer value = entry.getValue();
        	    System.out.println("Key"+key+" Value"+ value);
        	
        	}*/
        	
        }
        // System.out.println(dictionary.size());
    	//System.out.println(index);
       // System.out.println(all_documents_test.size());

	}
	
	public void predict_manhattan(){
		int k_near=6;
		Map<Integer,Integer> unknown_doc = new HashMap<Integer,Integer>();
		Map<Integer,Integer> known_doc = new HashMap<Integer,Integer>();
		int unknown_label = 0;
		int training_size = all_documents.size();
		double[] similarity;
	
		int accuracy_count = 0;
		int[][] confusion = new int[8][8];
		for (int it2=0;it2<8;it2++){
			for(int it3=0;it3<8;it3++){
				confusion[it2][it3]=0;
			}
		}
	
		Integer[] occurrence = new Integer[8];
		for(int i=0;i<all_documents_test.size();i++){
		//for(int i=0;i<1;i++){
			unknown_doc = all_documents_test.get(i);
			unknown_label = test_labels.get(i);
			similarity = new double[training_size];
		
			occurrence = new Integer[10];
			//Initialize occurrence
			for (int z=0;z<8;z++){
				occurrence[z] = Integer.valueOf(0);
			}
			
		    PriorityQueue<queueElements1> queue = new PriorityQueue<queueElements1>();
		     
			for (int j=0;j<all_documents.size();j++){
			//	System.out.println(j);
				
				known_doc = all_documents.get(j);
				
				
				Set<Integer> index_set = new TreeSet<Integer>();
				ArrayList<Integer> index_list = new ArrayList<Integer>();
				
				double training_doclen = 0.0;
				double test_doclen = 0.0;
				for (Map.Entry<Integer, Integer> entry : known_doc.entrySet()) {
					Integer key = entry.getKey();
					training_doclen+=(entry.getValue().doubleValue());
					//System.out.println("Key"+key+" ->"+ value);
        	
				}
				//System.out.println("Test doc");
				for (Map.Entry<Integer, Integer> entry : unknown_doc.entrySet()) {
					Integer key = entry.getKey();
					test_doclen+=(entry.getValue().doubleValue());
					//System.out.println("Key"+key+" ->"+ value);
        	
				}
				index_set.addAll(known_doc.keySet());
				index_set.addAll(unknown_doc.keySet());
				
				index_list.addAll(index_set);
			
				
				for (int k=0;k<index_list.size();k++){
					double val = 0.00;
					
					
					if((known_doc.get(index_list.get(k)) != null) && (unknown_doc.get(index_list.get(k)) != null)){
				
						
						val = Math.abs((known_doc.get(index_list.get(k))/training_doclen)-(unknown_doc.get(index_list.get(k))/test_doclen));
	
						
					}
					else if ((known_doc.get(index_list.get(k)) != null) && (unknown_doc.get(index_list.get(k)) == null))
					{
						
						val = Math.abs((known_doc.get(index_list.get(k))/training_doclen)-0);
						
					}
					else{
						
						val = Math.abs((unknown_doc.get(index_list.get(k))/test_doclen)-0);
						
					}
					
					
					similarity[j]+=val;
					
				}
				
				queueElements1 q = new queueElements1(train_labels.get(j),similarity[j]);
				queue.add(q);
				
			}	
			//Find the k maximum values and their corresponding labels for similarity
			//System.out.println("The top k elements are");
			for(int top=0;top<k_near;top++){
				queueElements1 top_elem = new queueElements1(0,0.0);
				top_elem = queue.poll();
				
				//System.out.println(top+" "+top_elem.classlabel+"->"+top_elem.similarity);
				int label = top_elem.classlabel;
				occurrence[label]++;
			}
			
			//Find labels with most number of occurrences
			int max = 0;
			int predicted_label = 0;
			for(int n=0;n<8;n++){
				if (n==0){
					max = occurrence[n];
					predicted_label =  n;
				}
				if(occurrence[n]>max){
					max = occurrence[n];
					predicted_label = n;
				}
				
			}
			confusion[unknown_label][predicted_label]++;
	//		System.out.println(predicted_label);
			if (predicted_label==unknown_label){
				accuracy_count++;
			}
			
			
		}
		//System.out.println(accuracy_count);
		double accuracy = (accuracy_count * 100.0)/test_labels.size();
		System.out.println("The accuracy is "+ accuracy);
		for (int it2=0;it2<8;it2++){
			for(int it3=0;it3<8;it3++){
				System.out.print(confusion[it2][it3]+"\t");
			}
			System.out.println();
		}
	}
	
	public void predict_eucledian(){
		
		int k_near=9;
		Map<Integer,Integer> unknown_doc = new HashMap<Integer,Integer>();
		Map<Integer,Integer> known_doc = new HashMap<Integer,Integer>();
		int unknown_label = 0;
		int training_size = all_documents.size();
		double[] similarity;
		int accuracy_count = 0;
		int[][] confusion = new int[8][8];
		for (int it2=0;it2<8;it2++){
			for(int it3=0;it3<8;it3++){
				confusion[it2][it3]=0;
			}
		}
		Integer[] occurrence = new Integer[8];
		for(int i=0;i<all_documents_test.size();i++){
			unknown_doc = all_documents_test.get(i);
			unknown_label = test_labels.get(i);
			similarity = new double[training_size];
			occurrence = new Integer[10];
			//Initialize occurrence
			for (int z=0;z<8;z++){
				occurrence[z] = Integer.valueOf(0);
			}
			
		    PriorityQueue<queueElements1> queue = new PriorityQueue<queueElements1>();
		     
			for (int j=0;j<all_documents.size();j++){
				
				known_doc = all_documents.get(j);
				
				Set<Integer> index_set = new TreeSet<Integer>();
				ArrayList<Integer> index_list = new ArrayList<Integer>();
				double training_doclen = 0.0;
				double test_doclen = 0.0;
				for (Map.Entry<Integer, Integer> entry : known_doc.entrySet()) {
					Integer key = entry.getKey();
					training_doclen+=(entry.getValue().doubleValue()*entry.getValue().doubleValue());
					//System.out.println("Key"+key+" ->"+ value);
        	
				}
				//System.out.println("Test doc");
				for (Map.Entry<Integer, Integer> entry : unknown_doc.entrySet()) {
					Integer key = entry.getKey();
					test_doclen+=(entry.getValue().doubleValue()*entry.getValue().doubleValue());
					//System.out.println("Key"+key+" ->"+ value);
        	
				}
				index_set.addAll(known_doc.keySet());
				index_set.addAll(unknown_doc.keySet());
				
				index_list.addAll(index_set);
			
				
				for (int k=0;k<index_list.size();k++){
					
					double val1 = 0.00;
					
					
					if((known_doc.get(index_list.get(k)) != null) && (unknown_doc.get(index_list.get(k)) != null)){
						
						double val = ((known_doc.get(index_list.get(k))*1.0/Math.sqrt(training_doclen))-(unknown_doc.get(index_list.get(k))*1.0/Math.sqrt(test_doclen)));
						val1 = val*val;
						//System.out.println("if val"+val);
						
					}
					else if ((known_doc.get(index_list.get(k)) != null) && (unknown_doc.get(index_list.get(k)) == null))
					{
						
						double val = ((known_doc.get(index_list.get(k))/Math.sqrt(training_doclen))-0)*1.0;
						val1 = val*val;
						//System.out.println("else if val"+val);
					}
					else{
						
						double val = ((unknown_doc.get(index_list.get(k))/Math.sqrt(test_doclen))-0)*1.0;
						val1 = val*val;
						//System.out.println("else val"+val);
					}
					//System.out.println("outside val"+val);
					
					similarity[j]+=val1;
					//System.out.println("Intermediate Similarity is "+similarity[j]);
				}
				similarity[j] = Math.sqrt(similarity[j]);
				//System.out.println("Similarity is "+similarity[j]);
				queueElements1 q = new queueElements1(train_labels.get(j),similarity[j]);
				queue.add(q);
				
			}	
			//Find the k maximum values and their corresponding labels for similarity
			//System.out.println("The top k elements are");
			for(int top=0;top<k_near;top++){
				queueElements1 top_elem = new queueElements1(0,0.0);
				top_elem = queue.poll();
				
				//System.out.println(top+" "+top_elem.classlabel+"->"+top_elem.similarity);
				int label = top_elem.classlabel;
				occurrence[label]++;
			}
			
			//Find labels with most number of occurrences
			int max = 0;
			int predicted_label = 0;
			for(int n=0;n<8;n++){
				if (n==0){
					max = occurrence[n];
					predicted_label =  n;
				}
				if(occurrence[n]>max){
					max = occurrence[n];
					predicted_label = n;
				}
				
			}
//			System.out.println(predicted_label);
			confusion[unknown_label][predicted_label]++;
			if (predicted_label==unknown_label){
				accuracy_count++;
			}
			
			
		}
		System.out.println(accuracy_count);
		double accuracy = (accuracy_count * 100.0)/test_labels.size();
		System.out.println("The accuracy is "+ accuracy);
		System.out.println("The confusion matrix is");
		for (int it2=0;it2<8;it2++){
			for(int it3=0;it3<8;it3++){
				System.out.print(confusion[it2][it3]+"\t");
			}
			System.out.println();
		}
	}
	
	public void predict_cosine(){
		int k_near=3;
		Map<Integer,Integer> unknown_doc = new HashMap<Integer,Integer>();
		Map<Integer,Integer> known_doc = new HashMap<Integer,Integer>();
		int unknown_label = 0;
		int training_size = all_documents.size();
		double[] similarity;
		//Double[] copy_similarity;
		//Double[] new_similarity ;
		int[] total = new int[8];
		int accuracy_count = 0;
		//Integer[] closest_labels;
		//Map<Integer,Integer> occurrence_count = new HashMap<Integer,Integer>();
		Integer[] occurrence = new Integer[8];
		int[][] confusion = new int[10][10];
		for (int it2=0;it2<8;it2++){
			total[it2]=0;
			for(int it3=0;it3<8;it3++){
				confusion[it2][it3]=0;
			}
		}
		//for(int i=0;i<all_documents_test.size();i++){
		for(int i=0;i<all_documents_test.size();i++){
			unknown_doc = all_documents_test.get(i);
			unknown_label = test_labels.get(i);
			similarity = new double[training_size];
			//copy_similarity = new Double[training_size];
			//occurrence_count = new HashMap<Integer,Integer>();
			total[unknown_label]++;
			occurrence = new Integer[10];
			//Initialize occurrence
			for (int z=0;z<8;z++){
				occurrence[z] = Integer.valueOf(0);
			}
			
		    PriorityQueue<queueElements2> queue = new PriorityQueue<queueElements2>();
		       // queue.add("short");
		       // queue.add("very long indeed");
		       // queue.add("medium");
		       // while (queue.size() != 0)
		       // {
		       //     System.out.println(queue.remove());
		       // }
		//	closest_labels = new Integer[k];
			for (int j=0;j<all_documents.size();j++){
			//	System.out.println(j);
				
				known_doc = all_documents.get(j);
				//find common indexes
				//find uncommon indexes
				
				Set<Integer> index_set = new TreeSet<Integer>();
				ArrayList<Integer> index_list = new ArrayList<Integer>();
				/*for (Integer key: known_doc.keySet()){
					index_set.add(key);
				}
				for (Integer key:unknown_doc.keySet()){
					index_set.add(key);
				}*/
				for (Map.Entry<Integer, Integer> entry : known_doc.entrySet()) {
					Integer key = entry.getKey();
					Integer value = entry.getValue();
					//System.out.println("Key"+key+" ->"+ value);
        	
				}
				//System.out.println("Test doc");
				for (Map.Entry<Integer, Integer> entry : unknown_doc.entrySet()) {
					Integer key = entry.getKey();
					Integer value = entry.getValue();
					//System.out.println("Key"+key+" ->"+ value);
        	
				}
				index_set.addAll(known_doc.keySet());
				index_set.addAll(unknown_doc.keySet());
				
				index_list.addAll(index_set);
				/*System.out.println("These are the in testing indexes");
				for(int u=0;u<index_list.size();u++){
					if(unknown_doc.containsKey(index_list.get(u))){
						System.out.print(unknown_doc.get(index_list.get(u))+" ");
					}
				}
				System.out.println();
				System.out.println();
				System.out.println("These are in the training indexes for j="+j);
				for(int u=0;u<index_list.size();u++){
					if(known_doc.containsKey(index_list.get(u))){
						System.out.print(known_doc.get(index_list.get(u))+" ");
					}
				}*/
				//if(j==10){break;}
				double total_num = 0.0;
				double total_den1 = 0.0;
				double total_den2 = 0.0;
				for (int k=0;k<index_list.size();k++){
					//System.out.println("j="+j);
					//System.out.println("Unknown digit feature"+unknown_digit[l]);
					//System.out.println("Known digit feature"+known_digit[l]);
					double num;
					double den1;
					double den2;
					
					if(known_doc.containsKey(index_list.get(k))&& unknown_doc.containsKey(index_list.get(k))){
						
						num = (known_doc.get(index_list.get(k))*unknown_doc.get(index_list.get(k)));
						den1 = Math.pow(known_doc.get(index_list.get(k)), 2);
						den2 = Math.pow(unknown_doc.get(index_list.get(k)), 2);
						
						
					}
					else if (known_doc.containsKey(index_list.get(k))&& !unknown_doc.containsKey(index_list.get(k)))
					{
						
						num = (known_doc.get(index_list.get(k))*0);
						den1 = Math.pow(known_doc.get(index_list.get(k)), 2);
						den2 = Math.pow(0, 2);
						
					}
					else{
						num = (0*unknown_doc.get(index_list.get(k)));
						den1 = Math.pow(0, 2);
						den2 = Math.pow(unknown_doc.get(index_list.get(k)), 2);
					}
					
					total_num+=num;
					total_den1+=den1;
					total_den2+=den2;
					
					//System.out.println("Intermediate Similarity is "+similarity[j]);
				}
				//System.out.println(j);
				similarity[j] = (total_num)/(Math.sqrt(total_den1)*Math.sqrt(total_den2));
				//System.out.println("Similarity is "+similarity[j]);
				queueElements2 q = new queueElements2(train_labels.get(j),similarity[j]);
				queue.add(q);
				
			}	
			//Find the k maximum values and their corresponding labels for similarity
			//System.out.println("The top k elements are");
			for(int top=0;top<k_near;top++){
				queueElements2 top_elem = new queueElements2(0,0.0);
				top_elem = queue.poll();
				
				//System.out.println(top+" "+top_elem.classlabel+"->"+top_elem.similarity);
				int label = top_elem.classlabel;
				occurrence[label]++;
			}
			
			/*System.out.println("These are the occurrence counts");
			for (Map.Entry<Integer, Integer> entry : occurrence_count.entrySet()) {
					Integer key = entry.getKey();
					Integer value = entry.getValue();
					System.out.println("Key"+key+" Value"+ value);
        	
			}*/
			//Find labels with most number of occurrences
			int max = 0;
			int predicted_label = 0;
			for(int n=0;n<8;n++){
				if (n==0){
					max = occurrence[n];
					predicted_label =  n;
				}
				if(occurrence[n]>max){
					max = occurrence[n];
					predicted_label = n;
				}
				
			}
			//System.out.println(predicted_label);
			confusion[unknown_label][predicted_label]++;
			if (predicted_label==unknown_label){
				accuracy_count++;
			}
			
			
		}
		System.out.println(accuracy_count);
		double accuracy = (accuracy_count * 100.0)/test_labels.size();
		System.out.println("The accuracy is "+accuracy);
		System.out.println("\t0\t1\t2\t3\t4\t5\t6\t7\t");
		System.out.println("___________________________________________________________________");
		for (int it2=0;it2<8;it2++){
			System.out.print(it2+"|\t");
			for(int it3=0;it3<8;it3++){
				double cval = (confusion[it2][it3]*100.0/total[it2]);
				System.out.print((double)Math.round(cval*100)/100+"\t");
			}
			System.out.println();
		}
	}
	
	public static void main(String[] args) throws IOException{
		long startTime = System.currentTimeMillis();
		knn1_text knn = new knn1_text();
		String training_file = "/home/sanjana/AI/8category/8category.training.txt";
		String testing_file = "/home/sanjana/AI/8category/8category.testing.txt";
		knn.readTrainfile(training_file);
		knn.readTestfile(testing_file);
		//knn.predict_manhattan();
		//knn.predict_eucledian();
		knn.predict_cosine();
		long endTime   = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		System.out.println("Total time is"+totalTime);
		
	}

}

