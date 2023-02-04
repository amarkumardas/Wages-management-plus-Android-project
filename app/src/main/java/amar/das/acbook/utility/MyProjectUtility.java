package amar.das.acbook.utility;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MyProjectUtility {
    public static int get24hrCurrentTimeRemoveColon() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");//capital HH stands for 24hr time format
       return  Integer.parseInt(sdf.format(new Date()).replaceAll("[:]", ""));//convert 01:30:55 to 13055 by parsing to INTEGER
    }
}
