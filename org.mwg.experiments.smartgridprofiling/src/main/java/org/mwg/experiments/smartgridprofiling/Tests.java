package org.mwg.experiments.smartgridprofiling;

import java.util.Random;

/**
 * Created by assaad on 28/04/16.
 */
public class Tests {
    public static void main(String[] arg) {
        long[] id1 = new long[10];
        long[] id2 = new long[20];
        Random rand=new Random();

        for(int i=0;i<10;i++){
            id1[i]=i+1;
        }

        for(int i=0;i<20;i++){
            id2[i]=i+11;
        }

        //mutate arrays here
        int x = rand.nextInt(10) + 5;

        x=17;
        long[] ida = new long[x];
        long[] idb = new long[id1.length + id2.length - x];


        if (x < id1.length) {
            id1 = shuffle(id1, rand);
            System.arraycopy(id2, 0, idb, 0, id2.length);
            System.arraycopy(id1, 0, ida, 0, x);
            System.arraycopy(id1, x, idb, id2.length, id1.length - x);
        } else if (x > id1.length) {
            id2 = shuffle(id2, rand);
            System.arraycopy(id1, 0, ida, 0, id1.length);
            System.arraycopy(id2, 0, idb, 0, idb.length);
            System.arraycopy(id2, idb.length, ida, id1.length, ida.length - id1.length);
        }

        int cx=0;
    }
    public static long[] shuffle(long[] ids, Random rand){
        for(int i=ids.length-1;i>0;i--){
            int j=rand.nextInt(i+1);
            long temp=ids[i];
            ids[i]=ids[j];
            ids[j]=temp;
        }
        return ids;
    }
}

