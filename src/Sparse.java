import java.io.*;
import java.util.Scanner;

public class Sparse{
     public static void main(String[] args)throws IOException {
         Scanner in = null;
         String line = null;
         String[] tokens = null;

         in = new Scanner(new File(args[0]));
         //read the first line in the file, which specifies the
         //inputted matrix's description(dimension,NNZ)
		 
         line = in.nextLine();
         
         //Split by Commas because CSV file
         tokens = line.split(",");
         for(int i =0; i < tokens.length; i++)
         System.out.println(tokens[i]);

         /*
         int lineNum = 0;//counter to keep track of the line number
         while(in.hasNext()){
         //as long as there is a line, keep looping
             line=in.nextLine();//read the line and save the string
             tokens = line.split("\\s+");//split line into tokens
             if(tokens.length == 1){ //check if its an empty line, then skip it
                continue; //ignore the rest of the code and loop back again
             }
             if(tokens.length > 1){
             //increment the line number by one for each line read
                 lineNum++;
        //increment location to distinguish betwen Matrix A and Matrix B
             }
             
             int row = Integer.parseInt(tokens[0]); //save the data accordingly
             int column = Integer.parseInt(tokens[1]);
             double data = Double.parseDouble(tokens[2]);
             */
         }
		 }
