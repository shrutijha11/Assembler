import java.io.*;
import java.util.*;
import java.lang.*;


class Code_2019231_2019274{
	public static HashMap<String,String> op_assembly = new HashMap<String,String>(); //Hashmap for opcodes.
	public static HashMap<String,Integer> label_table = new HashMap<String,Integer>(); // Hashmap for labels and their addresses.
	public static HashMap<String,Integer> symbol_table = new HashMap<String,Integer>(); // Hashmap for symbols and their addresses.
	public static HashMap<Integer,String> P2_input = new HashMap<Integer,String>(); // Hashmap for inputs to pass 2 of the assembler.
	public static int flag = 0; // variable initiated for checking the occurence of the STP statement.
	public static boolean label_nd=false; // boolean variable initiated for checking if a label is used without defining.
	public static int lo_co = 0; // integer variable initiated as the location counter. Will also be used as the iterator.
	public static int label_no = 0; // Checking for number of labels.
	public static boolean error = false; // Checking for errors in the input assembly code.

	public static boolean errorHandling(String line, String[] word, boolean error) // Method to be called in pass 1 for checking the errors in input file.
	{		
		try{
			/*System.out.println("errorhandling line:"+line);
			for(int a=0;a<word.length;a++)
				System.out.println("errorHandling array: "+word[a]);*/
		if(op_assembly.containsKey(word[0]) && !word[0].equals("START")) // the opcode table has the opcode provided which isn't start.
		{
			if(line.indexOf("BR")==0 && (word[0].equals("BRZ")||word[0].equals("BRP")||word[0].equals("BRN"))) // if the opcode provided is a branching statement.
			{
				try
				{
					if(!label_table.containsKey(word[1]))  // if the label to be branched doesn't exist in the label table till now, put it in the lable table with a value of -1.
					{
						label_table.put(word[1],-1);
					}

				}
				catch(ArrayIndexOutOfBoundsException e) // error that label isn't existing in the branching statement.
				{
					error=true;
					System.out.println("ERROR: Branch statement must have a label. Please debug the code and rerun.");
				}
			}
		}

		else if(!op_assembly.containsKey(word[0])){     // opcode isn't present in the opcode table,ie. the opcode isn't valid.

			error=true;
			System.out.println("ERROR: Invalid operation. Please debug the code and rerun.");
		}
	}catch(NullPointerException e)
	{

	}

		return error;
	}


	public static String machine_code(String line, String[] w, String output) // method called in the pass 2 in order to finally convert the assembly code to machine language.
	{			
			/*System.out.println("machine_code   "+line);
			for(int b=0;b<w.length;b++)
				System.out.println(w[b]+"#");*/
		if(op_assembly.containsKey(w[0]))    //provided opcode is a valid opcode.
		{
			int address_s=0;     // integers initiated to convert the address of labels and symbols as stored in the tables to their binary values. 
			int address_l=0;    
			output+=op_assembly.get(w[0]); // string which will be the machine code counterpart of the provided assembly code.
			output+=" "; // spacing to seperate the opcode with symbol's address.
			// all the possible opcodes are converted in the following lines.
			if(w[0].equals("CLA")) 
			{

				output+="00000000";

			}
			if(w[0].equals("STP"))
			{
				flag=1;
				output+="00000000";
			}

			if((!w[0].equals("BRZ") && !w[0].equals("BRN") && !w[0].equals("BRP")) && !w[0].equals("CLA") && !w[0].equals("STP"))
			{
				try{
					if(!Character.isDigit(w[1].charAt(0)))
					{
						address_s=symbol_table.get(w[1]);
					}
					String binary_s=Integer.toBinaryString(address_s);
					binary_s=String.format("%8s",binary_s);
					binary_s=binary_s.replace(' ','0');
					output+=binary_s;

				}
				catch(NullPointerException e){
					System.exit(0);
				}
				catch(ArrayIndexOutOfBoundsException e){

					System.out.println("ERROR: Operands provided are not enough for the operation. Please debug and rerun.");
					System.exit(0);
				}
			}

				if(w[0].equals("BRZ") || w[0].equals("BRN") || w[0].equals("BRP"))
				{	try{
					address_l=label_table.get(w[1]);
					String binary_l=Integer.toBinaryString(address_l);
					binary_l=String.format("%8s",binary_l);
					binary_l=binary_l.replace(' ','0');
					output+=binary_l;
				}
				
				catch(NullPointerException e){
					System.exit(0);
				}
				catch(ArrayIndexOutOfBoundsException e){

					System.out.println("ERROR: Operands provided are not enough for the operation. Please debug and rerun.");
					System.exit(0);
				}

				}
		}

		return output;

	}



	public static void main(String args[]){
		
		//The following few lines are to put the opcodes in assembly language with their machine language counterparts.
		

		op_assembly.put("CLA","0000");
		op_assembly.put("LAC","0001");
		op_assembly.put("SAC","0010");
		op_assembly.put("ADD","0011");
		op_assembly.put("SUB","0100");
		op_assembly.put("BRZ","0101");
		op_assembly.put("BRN","0110");
		op_assembly.put("BRP","0111");
		op_assembly.put("INP","1000");
		op_assembly.put("DSP","1001");
		op_assembly.put("MUL","1010");
		op_assembly.put("DIV","1011");
		op_assembly.put("STP","1100");
		
///////////////////////////////////////////////////////////////////////PASS ONE GOES AS FOLLOWS///////////////////////////////////////////////////////////////////////////

		try{
			BufferedReader assemble = new BufferedReader(new FileReader("input.txt")); //reading input from a text file.
			String line = assemble.readLine();

			/*System.out.println("main line:"+line);*/

			String[] ll = line.split("\\s");  // Splitting the given input line using spaces and putting them in an array.
			ArrayList<String> arr_l = new ArrayList<String>(Arrays.asList(ll)); // creating an arraylist of the same array.
			arr_l.removeIf(n->(n.equals(" ") )); // removing any residual spaces in the arraylist.
			ll = arr_l.toArray(new String[0]); // forming the arraylist back in the array.

			/*for(int c=0;c<ll.length;c++)
				System.out.println("main array:" +ll[c]);*/
			String st=ll[0].replaceAll("[^a-zA-Z0-9 ]", ""); // replacing any garbage value in the string(if any) with empty string.
			
			try{
			if(st.equals("START")){    // checking if the line is START or not.
				try{
					lo_co = Integer.valueOf(ll[1]); // updating the value of the location counter with the arguement passed with START.
					/*System.out.println("INSIDE START: "+lo_co);*/

				}
				catch(NumberFormatException e){ // error reported for arguement with START.
					error=true;
					System.out.println("ERROR: Non-integer arguement passed with START. Please debug the code and rerun.");

				}
				catch(ArrayIndexOutOfBoundsException e){ // no arguement found with START.
					error=true;
					System.out.println("ERROR: No arguement is passed with START. PLease debug the code and rerun.");
				}

				if(ll.length > 2){ // multiple arguements found with START.
					error = true;
					System.out.println("ERROR: More than one parameter cannot be passed with START. Please debug the code and rerun.");
				}
				line = assemble.readLine();
				/*System.out.println("line in main after start:"+ line);*/
			}
		}catch(NullPointerException e)
		{}

		String[] ll_f=new String[12]; // creating an array of strings.

		//Now we will check for labels in our instructions.We have defined a criteria for classifying the labels in our docmentation. Labels are found and label table is updated.
		
		
		while(line!= null && !line.equals("STP")){ 
			if(line.indexOf("#")>=0){ 
				int e  = line.indexOf("#");
				String label = line.substring(0,e);
				label=label.replace(" ","");

				if(label_table.containsKey(label) && label_table.get(label)!=-1){ // if label table already has the label with any value other than -1. 
					error=true;
					System.out.println("ERROR: Label defined more than once.");
				}
				else{
					label_table.put(label,lo_co); // otherwise put the label with the location counter value in label table.
					label_no++;
				}

				line=line.substring(e+1); //getting the line after removing the label.
				line=line.trim();
				/*System.out.println("line in main after removing label:"+line);*/
				if(line==null){
					lo_co=lo_co+1;
				}
				}
				// line variable is updated without the label now.

				ll_f = line.split("\\s"); // updating the array intially intiated with the new values without the label. 
				ArrayList<String> arr_f = new ArrayList<String>(Arrays.asList(ll_f)); // making another arraylist for removing spaces again.
				arr_f.removeIf(n->(n.equals(" "))); //removing residual spaces.
				ll_f = arr_f.toArray(new String[0]); // making the array back.

				/*for(int d=0;d<ll_f.length;d++)
					System.out.println("array in main after removing label:"+ ll_f[d]);*/

				if(ll_f.length == 0){
					line = assemble.readLine(); // this is the case in which the instruction is in the next line of the label.
					continue;
					}

				
						
					
				
				if(!ll_f[0].equals("CLA") && !ll_f[0].equals("STP"))
				{
				try{
				boolean m = op_assembly.containsKey(ll_f[0]); // checking if the opcode is legal or not.
				boolean h = symbol_table.containsKey(ll_f[1]); // checking if symbol is legal or not.

				if(m == true && ( !ll_f[0].equals("BRZ")&&!ll_f[0].equals("BRP")&& !ll_f[0].equals("BRN") && !ll_f[0].equals("INP") && !ll_f[0].equals("DSP")) ){
					
						if(!Character.isDigit(ll_f[1].charAt(0)))
							{	//System.out.println(ll_f[1]+" INSERTED");
								symbol_table.put(ll_f[1],-2);
							}
						else if(Character.isDigit(ll_f[1].charAt(0)))
						{
							error=true;
							System.out.println("ERROR: Variables must start with a letter only.");
						}
						if(ll_f.length>2 && ll_f[2].charAt(0) != '/' && ll_f[2].charAt(1) != '*'){
						error = true;
						System.out.println("ERROR:Too many operands provided for the given operation. Please debug the code and rerun.");
					}
				}

					/*else{
						error = true;
						System.out.println("ERROR:Invalid OPcode is used. Please debug the code and rerun.");
						line = assemble.readLine();
						continue;
						}*/

				

				if(ll_f[0].equals("INP")){  //case in which input from terminal is used.
					//System.out.println("INP USED");
						if(h==true){
							symbol_table.put(ll_f[1],lo_co);// if INP is used, then update value against the symbol in the symbol table with location counter value.
							//System.out.println(ll_f[1]+" INSERTED");
						}
						else if(h==false)
						{
							symbol_table.put(ll_f[1],lo_co);
							System.out.println("WARNING: Symbol "+ll_f[1]+" not used in code. Memory wasted.");
							System.out.println();
						}
					
					}

				if(ll_f[0].equals("DSP")){

					if(h==false || symbol_table.get(ll_f[1])==-2)
					{
						error=true;
						System.out.println("ERROR: Symbol "+ll_f[1]+" not declared but used in code.");
					}


				}
					
					
			}
		
				catch(ArrayIndexOutOfBoundsException e){
						error = true;
						System.out.println("ERROR:Operands provided are not enough to complete the operation. Please debug the code and rerun.");
						line = assemble.readLine();
						continue;
					}
				catch(NullPointerException e){
                    }
         }

			line=line.trim();
            line=line.replaceAll("[^a-zA-Z0-9 ]", ""); //removing a garbage value from the string.
            /*System.out.println("line input for error:"+line);
            for(int e=0;e<ll_f.length;e++)
            	System.out.println("array input for error:"+ll_f[e]);*/
			error = errorHandling(line,ll_f,error); // calling the function to check error in input line.
			
			P2_input.put(lo_co,line); // making the hashmap for input of the pass 2.
			lo_co++; // increasing the location counter.
			line = assemble.readLine(); // reading the next line.
			/*System.out.println("new read line in main:"+line);*/
			if(line.equals("STP")){
				flag=1;
				P2_input.put(lo_co,line);
			}

			}
		assemble.close(); // closing the input file reader.
		}
		catch(FileNotFoundException e){
			System.out.println("ERROR:Check the file name and try again.");
			System.exit(0);
		}
		catch(IOException e){
			e.printStackTrace();
			System.exit(0);
		}
		catch(NullPointerException e){
                    }
		if(error){
			System.out.println("ERROR: Output file not created due to error present in code.");
			System.exit(0);
		}
		if(P2_input.size() == 0){
			System.out.println("ERROR:No Code Found.");
			//System.exit(0);
		}


/////////////////////////////////////////////////////////////////// OUTPUTS OF PASS ONE////////////////////////////////////////////////////////////////////////////////////
		
		System.out.println("OPCODE TABLE: ");  // printing opcode table.
        op_assembly.entrySet().forEach(entry->{
        	System.out.println(entry.getKey()+ "              " + entry.getValue());
        	
        });
        System.out.println();


		error  = error || label_nd;     // checking for label error and other labels mentioned in documentation.
		System.out.println("Final error in input file: "+error );
		System.out.println();

		System.out.println("Number of labels in the code: "+label_no); // no. of labels used in the code.
		System.out.println();

		System.out.println("LABEL TABLE: "); // label table.
		label_table.entrySet().forEach(entry->{
          	if (entry.getValue()==-1){
          		System.out.println("ERROR: LABEL "+entry.getKey()+" used but not defined.");
          		label_nd=true;
          	}
          	else{
          		System.out.println(entry.getKey()+ "              "+entry.getValue());
          		}
          	});

		System.out.println();
        
        System.out.println("SYMBOL TABLE: ");  //printing symbol table.
        symbol_table.entrySet().forEach(entry->{
        	if(entry.getValue() == -2){
        		System.out.println("ERROR:SYMBOL "+entry.getKey()+" is not declared in the code.");
        	}
        	else{
        		System.out.println(entry.getKey()+"              " + entry.getValue());
        	}
        });
        System.out.println();

        System.out.println("INPUT TO THE SECOND PASS: ");  // printing pass 2 inputs.
        P2_input.entrySet().forEach(entry->{
        	System.out.println(entry.getKey()+ "              " + entry.getValue());
        });





		/*try{
			
			BufferedWriter op = new BufferedWriter(new FileWriter("output.txt",true));
			for(HashMap.Entry<Integer, String> entry : P2_input.entrySet()){
				String o ="";
				String l = entry.getValue();
				if(l.indexOf("/*")>=0){
					l = l.substring(0,l.indexOf("/*"));
				}
				String[] w1 = l.split("\\s");
				List<String> list1 = new ArrayList<String>(Arrays.asList(w1));
			list1.removeIf(k->(k.equals(" ")));
			w1 = list1.toArray(new String[0]);
			o = machine_code(l,w1,o);
			System.out.println("writing in output file:"+o+'\n');
			op.write(o);
			op.newLine();
			}
		}
			catch(IOException e){
				e.printStackTrace();
			}
			*/



////////////////////////////////////////////////////////////////////////// PASS TWO GOES AS FOLLOWS///////////////////////////////////////////////////////////////////////
			try{
               BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"));  // Buffered writer.
               for(HashMap.Entry<Integer, String> entry : P2_input.entrySet()){   // iterating to the pass 1 outputs.
	                   String line = entry.getValue();
	                   
	                    if (line.indexOf("/*")>=0){ // checking for comments.
	                         line=line.substring(0,line.indexOf("/*"));
	                    }
	                    String[] words=line.split("\\s");  //splitting of the line at the current value to an array.
	                    List<String> list = new ArrayList<String>(Arrays.asList(words)); // making of an arraylist.
			   		list.removeIf(n->(n.equals(" ")) ); // removal of spaces.
			   		words = list.toArray(new String[0]); // getting the original array back.
	                    String t=""; // empty string
	                    
	                    t=machine_code(line,words,t); // getting a machine code for output.
  
	                    writer.write(t+'\n');  // writing in the output fie.
               }    
               writer.close(); // closing the writer.
          }
          catch(IOException e){
               e.printStackTrace();
          } 
          catch(NullPointerException e){
                    }

          if(flag==0){
				System.out.println("WARNING: STP isn't used"); // checking if STP was used or no.
			}

			
		}



	} //end of program.