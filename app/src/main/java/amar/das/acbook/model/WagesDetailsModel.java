package amar.das.acbook.model;

import java.io.Serializable;

public class WagesDetailsModel implements Serializable {
    private static final long serialVersionUID = 42345L;
    private String date;

    public String getIsdeposited() {
        return isdeposited;
    }

    public void setIsdeposited(String isdeposited) {
        this.isdeposited = isdeposited;
    }

    private String isdeposited;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    private String time;
    private String micPath;
    private String description;
    private String id;
    private int p1,p2,p3,p4,wages,deposit;

    public  void setId(String id){
          this.id=id;
    }
    public String getId(){
        return  id;
    }
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMicPath() {
        return micPath;
    }

    public void setMicPath(String micPath) {
        this.micPath = micPath;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getP1() {
        return p1;
    }

    public void setP1(int p1) {
        this.p1 = p1;
    }

    public int getP2() {
        return p2;
    }

    public void setP2(int p2) {
        this.p2 = p2;
    }

    public int getP3() {
        return p3;
    }

    public void setP3(int p3) {
        this.p3 = p3;
    }

    public int getP4() {
        return p4;
    }

    public void setP4(int p4) {
        this.p4 = p4;
    }

    public int getWages() {
        return wages;
    }

    public void setWages(int wages) {
        this.wages = wages;
    }

    public int getDeposit() {
        return deposit;
    }

    public void setDeposit(int deposit) {
        this.deposit = deposit;
    }


}
