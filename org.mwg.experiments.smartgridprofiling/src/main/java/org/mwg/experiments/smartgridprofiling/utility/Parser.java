package org.mwg.experiments.smartgridprofiling.utility;

import java.io.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.TreeMap;

/**
 * Created by assaad on 21/06/16.
 */
public class Parser {
    public static void main(String[] args){
        try{
            final String csvdir = "/Users/assaad/work/github/data/consumption/londonpower/";
            BufferedReader br = new BufferedReader(new FileReader(csvdir + "temperature/londonTemperature.csv"));
            TreeMap<Long,Double> temperature = new TreeMap<Long,Double>();
            String line;
            String[] data;
            int duplicate=0;

            long error=0;
            while ((line = br.readLine()) != null) {
                try {
                    data = line.split(",");
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    java.util.Date parsedDate = dateFormat.parse(data[1]);
                    long ts = getTimestamp(parsedDate);
                    if (temperature.keySet().contains(ts)) {
                        duplicate++;
                        //    System.out.println("Duplicate ["+linenum+"]"+data[1]);
                    }
                    else {
                        double val = Double.parseDouble(data[2]);
                        temperature.put(ts, val);
                    }
                }
                catch (Exception ex){
                    error++;
                }

            }
            System.out.println("duplicate: "+duplicate+ " real values: "+temperature.keySet().size()+" parsing errors: "+error);

            error=0;

            String username="";
            String user;
            long timestamp;
            int powerValue=0;
            long globaltotal=0;
            int day;
            double hours;
            double tempValue=0;
            PrintWriter outTraining = null;

            File dir = new File(csvdir + "original/");
            File[] directoryListing = dir.listFiles();
            if (directoryListing != null) {
                for (File file : directoryListing) {
                    if (file.isDirectory() || file.getName().equals(".DS_Store")) {
                        continue;
                    }
                    br = new BufferedReader(new FileReader(file));

                    username = "";


                    br.readLine();
                    while ((line = br.readLine()) != null) {
                        try {
                            data = line.split(",");
                            user=data[0];
                            if(!user.equals(username)){
                                username=user;
                                if(outTraining!=null){
                                    outTraining.flush();
                                    outTraining.close();
                                }
                                File f = new File(csvdir + "NDsim/allusers/" + username + ".csv");
                                FileWriter out = null;
                                if ( f.exists() && !f.isDirectory() ) {
                                    out = new FileWriter(f,true);
                                }
                                else {
                                    out = new FileWriter(f);
                                }
                                outTraining = new PrintWriter(out);
                            }
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                            java.util.Date parsedDate = dateFormat.parse(data[2]);


                            try {
                                timestamp = getTimestamp(parsedDate);
                                powerValue = (int)(Double.parseDouble(data[3])*1000);
                                day=getDay(parsedDate);
                                hours=getHours(parsedDate);
                                try {
                                    tempValue = temperature.get(temperature.lowerKey(timestamp));
                                }
                                catch (Exception ex){
                                    ex.printStackTrace();
                                }
                                //outTraining.println(data[2]+","+timestamp+","+day+","+hours+","+tempValue+","+powerValue);
                                outTraining.println(timestamp+","+day+","+hours+","+tempValue+","+powerValue);
                                globaltotal++;
                            }
                            catch (Exception ex){
                                error++;
                            }

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                    System.out.println("Loaded: "+globaltotal+" errors: "+error);
                }
            }
            if(outTraining!=null) {
                outTraining.flush();
                outTraining.close();
            }
            System.out.println("Loaded: "+globaltotal+" errors: "+error);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private static double getHours(java.util.Date parsedDate) {
        try {

            Calendar cal = Calendar.getInstance();
            cal.setTimeZone(TimeZone.getTimeZone("UTC"));
            cal.setTime(parsedDate);
            return cal.get(Calendar.HOUR_OF_DAY)+cal.get(Calendar.MINUTE)/60.0;
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return -1;
    }

    private static int getDay(java.util.Date parsedDate) {
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTimeZone(TimeZone.getTimeZone("UTC"));
            cal.setTime(parsedDate);
            return cal.get(Calendar.DAY_OF_WEEK);
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return -1;
    }

    public static long getTimestamp(java.util.Date parsedDate) {
        try {
            Timestamp ts = new java.sql.Timestamp(parsedDate.getTime());
            return ts.getTime();
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return -1;
    }

}
