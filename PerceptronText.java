import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class PerceptronText {
	//Dictionary of all the words where the value is the index of the word
	public Map<String, Integer> dictionary = new HashMap<String,Integer>();
	public ArrayList<Integer> train_labels = new ArrayList<Integer>();
	public ArrayList<Map> all_documents = new ArrayList<Map>();
	public ArrayList<Map> all_documents_test = new ArrayList<Map>();
	public ArrayList<Integer> test_labels = new ArrayList<Integer>();
	//public ArrayList<>
	public Map<Integer, int[]> weights_map = new HashMap<>();
	
	public void initialize_weights(){
		int dictionary_size = dictionary.size();
		int[] weight = new int[dictionary_size];
		Random rand = new Random();
		for(int j=0;j<8;j++){
			weight = new int[dictionary_size];
			//Randomly initialize a value chosen between 1 and 10
			int random_val = rand.nextInt(10);
//			int random_val=2;
			System.out.println(random_val);
			for(int i=0;i<dictionary.size();i++){
				weight[i]=random_val;
			}
			weights_map.put(new Integer(j), weight);
		}
		/*int[] retrieved_weight = weights_map.get(1);
		for(int j=0;j<7;j++){
			System.out.println(retrieved_weight[j]);
		}
		*/
	}
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
        // System.out.println(dictionary.size());
    	//System.out.println(index);
       // System.out.println(all_documents.size());
       
	}
	public void training(){
		Map<Integer,Integer> doc = new HashMap<Integer,Integer>();
		int[] class_weights = new int[8];
		int alpha = 0;
		int[] predicted_class_wt;
		int[] actual_class_wt;
		int iter=0;
		int[] weight_class;
		
		do{
			for(int i=0;i<all_documents.size();i++){
				doc = all_documents.get(i);
				class_weights = new int[8];
				for(int j=0;j<8;j++){
					weight_class = weights_map.get(new Integer(j));
					for(Integer key:doc.keySet()){
						//System.out.println(doc.get(key));
						//System.out.println(weight_class[key]);
						class_weights[j]+= doc.get(key)*weight_class[key];
						//System.out.println("Class weights are"+class_weights[j]);
					}
				}
				//Find max class weight
				//System.out.println("Finding max class weight");
				int max = 0;
				int index = 0;
				for(int l=0;l<class_weights.length;l++){
					if (l==0){
						max = class_weights[l];
						index = 0;
					}
					if (class_weights[l]>max){
						max = class_weights[l];
						index = l;
					}
				}
		//		System.out.println("Class weight"+max);
		//		System.out.println("Actual class"+train_labels.get(i));
		//		System.out.println("Predicted class"+ index);
				if (train_labels.get(i)==index){
					continue;//do nothing
				}
				else{
					//update_weights
		//			System.out.println("The weights have to be updated");
					alpha = 1000/(1000+iter);
					actual_class_wt = weights_map.get(train_labels.get(i));
					predicted_class_wt = weights_map.get(index);
					for (Integer key:doc.keySet()){
						actual_class_wt[key]+=(alpha*doc.get(key));
						predicted_class_wt[key]-=(alpha*doc.get(key));
					}
					weights_map.put(train_labels.get(i), actual_class_wt);
					weights_map.put(index, predicted_class_wt);
				}
				
			}
			iter+=1;
			
		}while(iter<10);	
	}
	
	public void testing(){
		int iter =0;
		Map<Integer,Integer> doc = new HashMap<Integer,Integer>();
		int[] weight_class;
		int[] class_weights = new int[8];
		int alpha = 0;
		int[] predicted_class_wt;
		int[] actual_class_wt;
		int count_correct = 0;
		int[] total = new int[8];
		int[][] confusion = new int[8][8];
		for (int it2=0;it2<8;it2++){
			total[it2] = 0;
			for(int it3=0;it3<8;it3++){
				confusion[it2][it3]=0;
			}
		}
		for(int i=0;i<all_documents_test.size();i++){
			doc = all_documents_test.get(i);
			class_weights = new int[8];
			for(int j=0;j<8;j++){
				weight_class = weights_map.get(new Integer(j));
				for(Integer key:doc.keySet()){
					//System.out.println(doc.get(key));
					//System.out.println(weight_class[key]);
					class_weights[j]+= doc.get(key)*weight_class[key];
					//System.out.println("Class weights are"+class_weights[j]);
				}
			}	
			//print train labels
			/*for(int j=0;j<class_weights.length;j++){
				System.out.println(class_weights[j]);
			}*/
			//Find max class weight
			//System.out.println("Finding max class weight");
			int max = 0;
			int index = 0;
			for(int l=0;l<class_weights.length;l++){
				if (l==0){
					max = class_weights[l];
					index = 0;
				}
				if (class_weights[l]>max){
					max = class_weights[l];
					index = l;
				}
			}
			
			//System.out.println("Actual class"+test_labels.get(i));
			//System.out.println("Predicted class"+ index);
			total[test_labels.get(i)]++;
			confusion[test_labels.get(i)][index]++;
			if (test_labels.get(i)==index){
				count_correct++;//do nothing
			}
		}	
		System.out.println(count_correct);
		int accuracy = (count_correct * 100 / all_documents_test.size());
		System.out.println("The accuracy is "+accuracy);
		for (int it2=0;it2<8;it2++){
			for(int it3=0;it3<8;it3++){
				System.out.print((confusion[it2][it3]*100/total[it2])+"\t");
			}
			System.out.println();
		}
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
	
	public static void main(String args[])throws IOException{
		PerceptronText pt = new PerceptronText();
		String training_file = "/home/sanjana/AI/8category/8category.training.txt";
		String testing_file = "/home/sanjana/AI/8category/8category.testing.txt";
		pt.readTrainfile(training_file);
		pt.initialize_weights();
		pt.training();
		pt.readTestfile(testing_file);
		pt.testing();
	}
}
