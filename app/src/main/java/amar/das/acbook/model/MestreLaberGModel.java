package amar.das.acbook.model;

public class MestreLaberGModel {
    String advanceAmount,id;

    byte[] person_img;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


                //default constructed is created here
    public String getAdvanceAmount() {
        return advanceAmount;
    }

    public void setAdvanceAmount(String advanceAmount) {
        this.advanceAmount = advanceAmount;
    }

    public byte[] getPerson_img() {
        return person_img;
    }

    public void setPerson_img(byte[] person_img) {
        this.person_img = person_img;
    }
}
