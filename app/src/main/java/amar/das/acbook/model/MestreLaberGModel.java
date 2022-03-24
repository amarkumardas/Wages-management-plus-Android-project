package amar.das.acbook.model;

public class MestreLaberGModel {
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
    private String name,id;
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
}
