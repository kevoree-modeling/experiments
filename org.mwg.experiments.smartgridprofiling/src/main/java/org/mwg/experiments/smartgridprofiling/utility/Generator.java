package org.mwg.experiments.smartgridprofiling.utility;

import org.mwg.experiments.smartgridprofiling.gmm.ElectricMeasure;

import java.io.File;
import java.io.PrintWriter;
import java.util.Random;

/**
 * Created by assaa_000 on 28/05/2016.
 */
public class Generator {
    public static void main(String[] arg) {
        int num = 100000;
//        generateEqual(num);
//        generateLinear(num);
//        generateCircle(num);
//        generateLineRnd(num);
//        generateConstant(num);
        generateMultiProfile(num);
    }

    public static void generateMultiProfile(int num) {
        try {
            PrintWriter out = new PrintWriter(new File("./multiprofile.csv"));
            long time = System.currentTimeMillis();
            Random rand = new Random();
            int day = 1;
            double hour = 0;
            int temperature;
            double power;

            for (int i = 0; i < num; i++) {
                temperature = rand.nextInt(40) - 5; //-5 to 35
                if (day < 6) {
                    if (hour < 6 || (hour >= 9 && hour <= 18)) {
                        //Simulate not at home
                        power = 160 + rand.nextDouble() * 40 - temperature * 2;
                    } else {
                        //Simulate at home
                        power = 1150 + rand.nextDouble() * 300 - temperature * 20 + hour * 5;
                    }
                } else {
                    power = 1000 - temperature * 2 + hour * 10 - rand.nextDouble() * 200;
                }

                out.println(","+day + "," + hour + "," + temperature + "," + power);
                hour = (hour + 0.5) % 24;
                if(hour==0){
                    day = (day) % 7 + 1; //from 1 to 7
                }
            }
            out.flush();
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();

        }
    }

    public static void generateEqual(int num) {
        try {
            PrintWriter out = new PrintWriter(new File("./equaldist.csv"));
            long time = System.currentTimeMillis();
            Random rand = new Random();
            for (int i = 0; i < num; i++) {
                out.println(time + "," + rand.nextDouble() * 1000);
                time += 30 * 60 * 1000;
            }
            out.flush();
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static int getLinnearRandomNumber(int maxSize) {
        //Get a linearly multiplied random number
        int randomMultiplier = maxSize * (maxSize + 1) / 2;
        Random r = new Random();
        int randomInt = r.nextInt(randomMultiplier);

        //Linearly iterate through the possible values to find the correct one
        int linearRandomNumber = 0;
        for (int i = maxSize; randomInt >= 0; i--) {
            randomInt -= i;
            linearRandomNumber++;
        }

        return linearRandomNumber;
    }

    public static void generateLinear(int num) {
        try {
            PrintWriter out = new PrintWriter(new File("./lineardist.csv"));
            long time = System.currentTimeMillis();
            for (int i = 0; i < num; i++) {
                out.println(time + "," + getLinnearRandomNumber(1000));
                time += 30 * 60 * 1000;
            }
            out.flush();
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    public static void generateCircle(int num) {
        try {
            PrintWriter out = new PrintWriter(new File("./circledist.csv"));
            long time = System.currentTimeMillis();
            Random rand = new Random();
            for (int i = 0; i < num; i++) {
                double t = ElectricMeasure.convertTime(time);
                double d = Math.sqrt(24 * t - t * t);
                if (rand.nextBoolean()) {
                    d = 25 * (20 - d) + 200;
                    out.println(time + "," + d);
                } else {
                    d = 25 * (20 + d) + 200;
                    out.println(time + "," + d);
                }

                time += 30 * 60 * 1000;
            }
            out.flush();
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void generateLine(int num) {
        try {
            PrintWriter out = new PrintWriter(new File("./line.csv"));
            long time = System.currentTimeMillis();
            Random rand = new Random();
            for (int i = 0; i < num; i++) {
                double t = ElectricMeasure.convertTime(time);
                double d = 900 * t / 24;
                out.println(time + "," + d);

                time += 30 * 60 * 1000;
            }
            out.flush();
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void generateLineRnd(int num) {
        try {
            PrintWriter out = new PrintWriter(new File("./linernd.csv"));
            long time = System.currentTimeMillis();
            Random rand = new Random();
            for (int i = 0; i < num; i++) {
                double t = ElectricMeasure.convertTime(time);
                double d = 900 * t / 24 + rand.nextInt(20);
                out.println(time + "," + d);

                time += 30 * 60 * 1000;
            }
            out.flush();
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void generateConstant(int num) {
        try {
            PrintWriter out = new PrintWriter(new File("./constant.csv"));
            long time = System.currentTimeMillis();
            for (int i = 0; i < num; i++) {
                double d = 500;
                out.println(time + "," + d);
            }
            out.flush();
            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
