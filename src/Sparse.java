import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;

public class Sparse{
     public static void main(String[] args)throws IOException {
    	 Date today = new Date();
         Scanner in = null;
         String tripsLine = null;
         String routesLine = null;
         String stopTimesLine = null;
         String[] tripsTokens = null;
         String[] routesTokens = null;
         String[] stopTimesTokens = null;
         ArrayList<String> stopTunes = new ArrayList<String>();

         
         BufferedReader tripsReader = new BufferedReader(new FileReader("trips.txt"));
         BufferedReader routesReader = new BufferedReader(new FileReader("routes.txt"));
         BufferedReader stopTimesReader = new BufferedReader(new FileReader("stop_times.txt"));
         //read the first line in the file, which specifies the
         //inputted matrix's description(dimension,NNZ)
		 
         tripsLine = tripsReader.readLine();
         
         //Retrieve routeID and tripID attributes from trips.txt
         tripsTokens = tripsLine.split(",");
         String routeID = tripsTokens[0];
         String tripID = tripsTokens[2];
         System.out.println(routeID + " " + tripID);
         
         stopTimesLine = stopTimesReader.readLine();
         stopTimesTokens = stopTimesLine.split(",");
         
         
         //find routeID within routes.txt using data from trips.txt
         routesLine = routesReader.readLine();
         routesTokens = routesLine.split(",");
         while( !routeID.equals(routesTokens[0]) ) {
        	 routesLine = routesReader.readLine();
        	 routesTokens = routesLine.split(",");
         }
         System.out.println(routesTokens[0]);
         
         
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
