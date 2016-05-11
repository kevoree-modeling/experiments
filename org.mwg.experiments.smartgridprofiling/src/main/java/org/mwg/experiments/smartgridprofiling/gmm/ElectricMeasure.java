package org.mwg.experiments.smartgridprofiling.gmm;

import java.time.LocalDateTime;

/**
 * Created by assaad on 11/05/16.
 */
public class ElectricMeasure {
    public Long timestamp;
    public double value;

    public ElectricMeasure(Long timestamp, Double value) {
        this.timestamp=timestamp;
        this.value=value;
    }

    private double convertTime(){
        java.sql.Timestamp tiempoint= new java.sql.Timestamp(timestamp);
        LocalDateTime ldt= tiempoint.toLocalDateTime();
        double res= ((double)ldt.getHour())/24+((double)ldt.getMinute())/(24*60)+((double)ldt.getSecond())/(24*60*60);
        return res;
    }

    public double[] getVector(){
        double[] result = new double[2];
        result[0]=convertTime()*24;
        result[1]=value;
        return result;
    }
}
