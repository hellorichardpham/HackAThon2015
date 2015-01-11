package com.example.joshinnis.parser;

import android.content.res.Resources;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeComparator;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.StringTokenizer;


public class MainActivity extends ActionBarActivity {
    //Resources r = getResources();
    Log log;
    static String convertStreamToString(java.io.InputStream is) {
        @SuppressWarnings("resource")
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";

    }

    /*Class that keeps track of the longitude, latidude and name of a bus stop*/
    class stop {
        float lon;
        float lat;
        String name;
    }
    Map<String,stop> bus_stop_hash = new HashMap<String,stop>();


    /*Tracks the time a bus is supposed to arrive at each respective bus stop*/
    class trip{
        String[] time;
    }




    /*Contains the data for each bus route*/
    class route {

        //What Metro number and the name of the route
        int id;
        String name;

        /*The number of bus stops on the route and the id of each bus stop, from first stop to
        * last*/
        int stops;
        String[] stop_ids;


        //0 = Weekday, 1 = Saturday, 2 = Sunday
        //Needed for inserting data in the structure, don't use
        boolean[] day = new boolean[3];

        /*This is where the trip class gets stored, the arrayList is used to store each type of route
        0 = Weekday, 1 = Saturday, 2 = Sunday. The data is stored in the hash table using the trip_id
        number, use an iterator to get info out DO NOT TRY AND USE trip_id TO GET INFORMATION OUT.*/
        List<Map<String,trip>> trips = new ArrayList<Map<String,trip>>();

        /*The Constructor, setup the List and sets the boolean days to false*/
        route(){
            for(int i = 0; i < 3; i++) {
                Map<String, trip> t = new HashMap<String, trip>();
                trips.add(i,t);
                day[i] = false;
            }
        }
    }
    Map<String,route> route_hash = new HashMap<String,route>();


    /*Temporary class and hash table needed to move data around. Do not use in the rest of the code.*/
    class tmp_stop_time{
        String[] time = new String[100];
        String[] stop_id = new String[100];
        int index;
        int day;
    }
    Map<String,tmp_stop_time> stop_time_tmp_hash = new HashMap<String,tmp_stop_time>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Used for debugging, not needed in final code
        TextView v = (TextView)findViewById(R.id.textView);

        /*Gets the data from the raw directories*/
        Resources r = getResources();
        InputStream Is = r.openRawResource(R.raw.stops);
        String stops = convertStreamToString(Is);
        InputStream Is1 = r.openRawResource(R.raw.stop_times);
        String stop_times = convertStreamToString(Is1);
        InputStream Is3 = r.openRawResource(R.raw.trips);
        String trips = convertStreamToString(Is3);

        /*Parses the information needed for the bus stops*/
        String[] s = stops.split("\n");
        for(int i = 0; i < s.length; ++i){
            String[] line = s[i].split(",");
            stop newStop = new stop();
            newStop.lat = Float.parseFloat(line[4]);
            newStop.lon = Float.parseFloat(line[5]);
            newStop.name = line[1];
            bus_stop_hash.put(line[0],newStop);
        }

        /*Gets the data needed from stop_times and stores it in a temporary hashtable to be used
        in the next section*/
        String[] st = stop_times.split("\n");
        for(int i = 0; i < st.length; ++i){
            String[] line = st[i].split(",");
            String line_parser = line[0];
            tmp_stop_time stop_time_route = new tmp_stop_time();
            String[] day = line[0].split("-");

            /*Setup which day it is*/
            if(day[3].equals("Weekday")) stop_time_route.day = 0;
            else if(day[3].equals("Saturday")) stop_time_route.day = 1;
            else if(day[3].equals("Sunday")) stop_time_route.day = 2;

            int j = 0;
            boolean inc = false;

            /*Goes through the list for each trip_id and puts its time and stop id into the
            * temporary data structure*/
            while(line[0].equals(line_parser) && !((i + 1) == st.length)) {
                inc = true;
                stop_time_route.stop_id[j] = line[3];
                stop_time_route.time[j] = line[2];
                stop_time_route.index = j;
                ++j;
                ++i;
                line = st[i].split(",");
            }
            //Needed because the while loop increments one time too many
            if(inc == true) --i;

            stop_time_tmp_hash.put(line_parser,stop_time_route);
        }


        /*Fills in the route_hash hash table, by going through each time a bus leaves the
         * metro station */
        String[] t = trips.split("\n");
        for(int i = 0; i < t.length; ++i){
            /*Split the .txt by line*/
            String[] line = t[i].split(",");

            /*Used to get the metro route number*/
            String[] route_num = line[0].split("-");

            /*If the new trip does not is on a route that is not in the map*/
            if(route_hash.containsKey(route_num[0]) == false){

                /*Create the new route and setup the basic information*/
                route new_route = new route();
                new_route.id = Integer.parseInt(route_num[0]);
                new_route.name = line[3];

                /*Get the information gathered from the previous loop and place in what the stops
                * are for the route*/
                tmp_stop_time stop_time_route = stop_time_tmp_hash.get(line[2]);
                new_route.stops = stop_time_route.index;
                new_route.stop_ids = new String[new_route.stops];
                for(int j = 0; j < new_route.stops; ++j){
                    new_route.stop_ids[j] = stop_time_route.stop_id[j];
                }

                /*Not needed, too tired to take out*/
                new_route.day[stop_time_route.day] = true;


                /*Fill in the specifics for this metro bus trip*/
                trip newtrip = new trip();
                newtrip.time = new String[new_route.stops];
                /*Transfers the time from the temporary data structure to the permanent data
                 structure*/
                for(int j = 0; j < new_route.stops; ++j){
                    newtrip.time[j] = stop_time_route.time[j];
                }

                //Put the new trip in the route
                new_route.trips.get(stop_time_route.day).put(line[2],newtrip);

                //Put the route in the hash table
                route_hash.put(route_num[0],new_route);

            }else if(route_hash.get(route_num[0]).day[stop_time_tmp_hash.get(line[2]).day] == false){
                /*If the route is setup but not for that particular day it goes here, probably not
                * needed, but it felt like it was at 5am*/
                /*Fill in the specifics for this metro bus trip*/
                trip newtrip = new trip();
                newtrip.time = new String[stop_time_tmp_hash.get(line[2]).index];
                /*Transfers the time from the temporary data structure to the permanent data
                 structure*/
                for(int j = 0; j < newtrip.time.length; ++j){
                    newtrip.time[j] = stop_time_tmp_hash.get(line[2]).time[j];
                }

                //Not needed to lazy
                route_hash.get(route_num[0]).day[stop_time_tmp_hash.get(line[2]).day] = true;
                //Put the new trip in the route
                route_hash.get(route_num[0]).trips.get(stop_time_tmp_hash.get(line[2]).day).put(line[2],newtrip);
                //v.setText("There");
            }else {
                /*Fill in the specifics for this metro bus trip*/
                trip newtrip = new trip();
                newtrip.time = new String[stop_time_tmp_hash.get(line[2]).index];
                /*Transfers the time from the temporary data structure to the permanent data
                 structure*/
                for(int j = 0; j < newtrip.time.length; ++j){
                    newtrip.time[j] = stop_time_tmp_hash.get(line[2]).time[j];
                }
                //Put the new trip in the route
                route_hash.get(route_num[0]).trips.get(stop_time_tmp_hash.get(line[2]).day).put(line[2],newtrip);
            }
        }

        //0 Weekday trips.get(0).values()
        //1 Saturday
        //2 Sunday
        Collection<trip> col = route_hash.get("16").trips.get(1).values();
        Iterator<trip> tri = col.iterator();

//        ArrayList<String> timeInputs = new ArrayList<String>();
//        ArrayList<DateTime> dateTimeInputs = new ArrayList<DateTime>();

        DateTime tempDateTime=null;
        int busindex = -1;
        Boolean isFound=false;
        while(tri.hasNext()&&!isFound) {
            trip printtrip = tri.next();
            for (int i = 0; i < printtrip.time.length; ++i) {
                tempDateTime = DateTime.parse(printtrip.time[i], DateTimeFormat.forPattern("HH:mm:ss"));
                if (findNextBusTime(tempDateTime)) {
                    log.v("MainActivity", i+"***");
                    busindex=i;
                    isFound = true;
                    break;
                }
            }
            if(isFound) {
                break;
            }
        }

        String busStopID = route_hash.get("16").stop_ids[busindex];
        String busStopName	= bus_stop_hash.get(busStopID).name;
        //String busTripName = route_hash.get("16").trips.get()
        float yaxis = bus_stop_hash.get(busStopID).lon;
        float xaxis = bus_stop_hash.get(busStopID).lat;
        System.out.println("Bus Stop ID: " + busStopID);
        System.out.println("x: " + xaxis + " y: " + yaxis);

        //System.out.println("Beginning Generate Time");

        //dateTimeInputs = generateTime(timeInputs);
        //System.out.println("Begin Find Next Bus Time");

        //log.v("MainActivity",findNextBusTime(dateTimeInputs));



        //v.setText(route_hash.get("16").trips.get(1).get("2961849-20152D-vs20152D-Saturday-07").time[0]);
    }

//    public static ArrayList<DateTime> generateTime(ArrayList<String> timeInputs) {
//        ArrayList<DateTime> dateTimeInputs = new ArrayList<DateTime>();
//        DateTime fakeTime;
//        for(int i =0; i < timeInputs.size(); i++) {
//            dateTimeInputs.add(fakeTime = DateTime.parse(timeInputs.get(i),
//                    DateTimeFormat.forPattern("HH:mm:ss")));
//        }
//        return dateTimeInputs;
//    }

    //Right now it's returning the next bus time. We want to actually return the index instead?
    public static Boolean findNextBusTime(DateTime timed){
        DateTimeFormatter fmt = DateTimeFormat.forPattern("HH:mm:ss");
        DateTime currentTime = new DateTime();
        if(isDuringOrAfter(currentTime,timed)){
            return true;
        }
        return false;
    }

    public static boolean isDuringOrAfter(DateTime currentTime,
                                          DateTime targetTime) {

        DateTimeFormatter fmt = DateTimeFormat.forPattern("HH:mm:ss");

        //System.out.println("curr: " + fmt.print(currentTime));
        //System.out.println("target: " + fmt.print(targetTime));

        int result = DateTimeComparator.getTimeOnlyInstance().compare(
                targetTime,currentTime);

        switch (result) {
            case 0:
                System.out.println("Current time is equal to target time. " + fmt.print(targetTime));
                return true;
            case 1:
                System.out.println("Current time is after or equal to target time. " + fmt.print(targetTime));
                return true;
            case -1:
                //System.out.println("returning false for " + fmt.print(targetTime));
                return false;
            default:
                System.out.println("wow");
                return false;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}