package amar.das.acbook.utility;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import amar.das.acbook.model.MestreLaberGModel;

public class ProjectUtility {
    public static int get24hrCurrentTimeRemoveColon() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");//capital HH stands for 24hr time format
       return  Integer.parseInt(sdf.format(new Date()).replaceAll("[:]", ""));//convert 01:30:55 to 13055 by parsing to INTEGER initial 0 is removed
    }
    public static String convertLongIntegerToCommaSeparatedString(Long number) {//https://www.geeksforgeeks.org/convert-the-number-from-international-system-to-indian-system/
        String inputString = String.valueOf(number);//converting integer to string
        StringBuilder result = new StringBuilder();
        //when length is odd then place , after 2 digit and when length is even then place , after 1 digit
        for (int i = 0; i < inputString.length(); i++) {
            if (inputString.length() > 3 && i < (inputString.length() - 3)) {//i < (inputString.length() - 3) TO PREVENT last 3 digit to add , because last 3 digit dont contain comma
                if (inputString.length() % 2 == 0) {//if length is even
                    if (i % 2 == 0) {
                        result.append(inputString.charAt(i));
                        result.append(",");
                    } else {
                        result.append(inputString.charAt(i));
                    }
                } else if (inputString.length() % 2 != 0) {//if length is odd
                    if (i != 0 && i % 2 != 0) {//to prevent when i=0
                        result.append(inputString.charAt(i));
                        result.append(",");
                    } else {
                        result.append(inputString.charAt(i));
                    }
                } else {//else is important to add
                    result.append(inputString.charAt(i));
                }
            } else {
                result.append(inputString.charAt(i));
            }
        }
        return result.toString();
    }
    public static String get12hrCurrentTimeAndDate(){
//        final Calendar current = Calendar.getInstance();//to get current date and time
//        Date d = Calendar.getInstance().getTime();//To get time
//        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:a");//a stands for is AM or PM
//        return current.get(Calendar.DAY_OF_MONTH) + "-" + (current.get(Calendar.MONTH) + 1) + "-" + current.get(Calendar.YEAR) + "at" + sdf.format(d);

        SimpleDateFormat formatter = new SimpleDateFormat("dd - MM - yyyy (hh:mm a)");
        return formatter.format(new Date());
    }

    public static void sortArrayList(ArrayList<MestreLaberGModel> arrayListShouldMayOrMayNotContainNullAtFirstHalfAndSecondHalfNotNull) {
        /**
         *This function will sort Arraylist in such a way that all LatestDate which is null will be at top.
         * and based on that it will sort the remaining record*/

        int nullCountInArraylist[]=countNullAndTodayLatestdateAndBringAllLatestDateWhichIsNullAtTop(arrayListShouldMayOrMayNotContainNullAtFirstHalfAndSecondHalfNotNull);//it will return null count and lastestdate count and swap all lastestdate which is null and bring at top of arrayList

        if(nullCountInArraylist[0]== 0 && nullCountInArraylist[1] == 0){//if today Latestdate is not there and new person not there then execute this
            Collections.sort(arrayListShouldMayOrMayNotContainNullAtFirstHalfAndSecondHalfNotNull.subList(0, arrayListShouldMayOrMayNotContainNullAtFirstHalfAndSecondHalfNotNull.size()));//natural sorting based on latestdate desc comparator implemented

         }else {//sorting to original array in asc order by taking latestdate
            Collections.sort(arrayListShouldMayOrMayNotContainNullAtFirstHalfAndSecondHalfNotNull.subList(nullCountInArraylist[0], arrayListShouldMayOrMayNotContainNullAtFirstHalfAndSecondHalfNotNull.size()), new Comparator<MestreLaberGModel>() {
                DateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                @Override
                public int compare(MestreLaberGModel obj1, MestreLaberGModel obj2) {
                    try {
                        return sdf.parse(obj1.getLatestDate()).compareTo(sdf.parse(obj2.getLatestDate()));
                    } catch (ParseException e) {
                        throw new IllegalArgumentException(e);
                    }
                }
            });
            //arraylist Manipulation
            if (nullCountInArraylist[1] != 0) { //if there is today latestdate then sort last part using time desc order
                Collections.sort(arrayListShouldMayOrMayNotContainNullAtFirstHalfAndSecondHalfNotNull.subList(arrayListShouldMayOrMayNotContainNullAtFirstHalfAndSecondHalfNotNull.size() - nullCountInArraylist[1], arrayListShouldMayOrMayNotContainNullAtFirstHalfAndSecondHalfNotNull.size()), (obj1, obj2) -> {
                    Date obj1Date = null, obj2Date = null;
                    SimpleDateFormat format24hrs = new SimpleDateFormat("HH:mm:ss aa");//24 hrs format
                    SimpleDateFormat format12hrs = new SimpleDateFormat("hh:mm:ss aa");//12 hrs format
                    try {
                        obj1Date = format12hrs.parse(obj1.getTime());
                        obj2Date = format12hrs.parse(obj2.getTime());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
//                    String obj1StringDate=format24hrs.format(obj1Date);
//                    String obj2StringDate=format24hrs.format(obj2Date);
                    return Integer.parseInt(format24hrs.format(obj2Date).replaceAll("[:]", "").substring(0, 6)) - Integer.parseInt(format24hrs.format(obj1Date).replaceAll("[:]", "").substring(0, 6));
                });//first making "00:59:30 PM" to 5930 .removing start 0 and :,AM,PM.PARSING TO INTEGER so that start 0 will remove.//sort   time in desc order.index start from 0 n-1.this will keep todays time on top so that search would be easy.arraylist is already sorted so we are sorting only last half obj which has todays time
            }
            //since array list already sorted in asc order so just reversing to get desc order
            reverseArrayListUsingIndex(arrayListShouldMayOrMayNotContainNullAtFirstHalfAndSecondHalfNotNull,nullCountInArraylist[0], arrayListShouldMayOrMayNotContainNullAtFirstHalfAndSecondHalfNotNull.size() - nullCountInArraylist[1]);
        }//else
    }
    public static int[] countNullAndTodayLatestdateAndBringAllLatestDateWhichIsNullAtTop(ArrayList<MestreLaberGModel> al) {
        int arr[]=new int[2];
        int nullIndex=0;
        // LocalDate todayDate = LocalDate.now();//current date; return 2022-05-01
        // String currentDateDBPattern =""+ todayDate.getDayOfMonth()+"-"+ todayDate.getMonthValue()+"-"+ todayDate.getYear();//converted to 1-5-2022
        for (int i = 0; i < al.size(); i++) {
            if(al.get(i).getLatestDate()== null){
                swapAndBringNullObjAtTopOfArrayList(al,nullIndex,i);
                nullIndex++;
                arr[0]++;//nullCount
            }                                                    //today date
            else if(al.get(i).getLatestDate().equals(""+ LocalDate.now().getDayOfMonth()+"-"+ LocalDate.now().getMonthValue()+"-"+ LocalDate.now().getYear())){
                arr[1]++;//todayLatestDateCount
            }
        }
        return arr;
    }
    public static void swapAndBringNullObjAtTopOfArrayList(ArrayList<MestreLaberGModel> al, int nullIndex, int i) {
        MestreLaberGModel nullObj=al.get(i);
        MestreLaberGModel nonNullObj=al.get(nullIndex);
        al.set(i,nonNullObj);
        al.set(nullIndex,nullObj);
    }
    public static <T> void reverseArrayListUsingIndex(List<T> al, int start, int end){
        while (start < end) {
            T a=al.get(start);
            T b=al.get(end-1);
            al.set(start,b);
            al.set(end-1,a);
            start++;
            end--;
        }
    }
}
