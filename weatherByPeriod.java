package rottiedog.com.mysport;

/**
 * Created by dehuffm on 8/8/2017.
 */

public class weatherByPeriod {
    private String mPeriod;
    private String mReport;

    public weatherByPeriod(){}
    public weatherByPeriod(String period, String report){mPeriod = period; mReport = report;}
    public void setPeriod(String period){mPeriod=period;}
    public String getPeriod(){return mPeriod;}
    public void setReport(String report){mReport=report;}
    public String getReport(){return mReport;}
}
