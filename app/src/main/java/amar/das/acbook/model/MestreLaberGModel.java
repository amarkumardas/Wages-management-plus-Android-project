package amar.das.acbook.model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;

public class MestreLaberGModel implements Serializable,Comparable<MestreLaberGModel>  {
    private static final long serialVersionUID = 42L;
    public int getAdvanceAmount() {
        return advanceAmount;
    }

    public void setAdvanceAmount(int advanceAmount) {
        this.advanceAmount = advanceAmount;
    }

    public int getBalanceAmount() {
        return balanceAmount;
    }

    public void setBalanceAmount(int balanceAmount) {
        this.balanceAmount = balanceAmount;
    }

    private int advanceAmount,balanceAmount;
    private String name;
    private String id;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    private String time;
    public String getLatestDate() {
        return latestDate;
    }

    public void setLatestDate(String latestDate) {
        this.latestDate = latestDate;
    }

    private String latestDate;
    private byte[] person_img;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
                //default constructed is created here

    public byte[] getPerson_img() {
        return person_img;
    }
    public void setPerson_img(byte[] person_img) {
        this.person_img = person_img;
    }

    @Override
    public int compareTo(MestreLaberGModel obj1) {//natural sorting latest date desc
        DateFormat f = new SimpleDateFormat("dd-MM-yyyy");
        try {
            return f.parse(obj1.getLatestDate()).compareTo(f.parse(this.getLatestDate()));
        } catch (ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }//sort data by taking latestdate in desc order.so that search would be easy.






























//    public static Comparator<MestreLaberGModel> s =new Comparator<MestreLaberGModel>() {
//        @Override
//        public int compare(MestreLaberGModel a, MestreLaberGModel b) {
//           // return a.getAdvanceAmount()-b.getAdvanceAmount();
//             return -b.getTime().compareTo(a.getTime());
//           // return 1;
//        }
//    };
}
