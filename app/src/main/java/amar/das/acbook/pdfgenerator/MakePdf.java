package amar.das.acbook.pdfgenerator;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;

import java.io.File;
import java.io.FileOutputStream;

import amar.das.acbook.utility.ProjectUtility;

public class MakePdf{
    private PdfDocument myPdfDocument;
    private Paint myPaint;
    private PdfDocument.PageInfo myPageInfo;
    private Canvas canvas;
    private PdfDocument.Page mypage;
    public static Integer defaultPageWidth=290,defaultPageHeight=500;
    public static Integer keepTrackOfPageDataHeight;//IT KEEP TRACK TILL HOW MUCH height data is written SO THAT WE CAN MOVE TO NEXT PAGE IF PAGE IS FULL
    public MakePdf(){
       myPdfDocument= new PdfDocument();
        myPaint= new Paint();
    }
    public boolean makeTopHeader1(String headerOrgName, String contact, String whatsappNumber,String email){
        try {//automatically adjustable
            myPaint.setColor(Color.rgb(53, 77, 203));//blue
            keepTrackOfPageDataHeight =myPageInfo.getPageHeight() - Math.abs(25-myPageInfo.getPageHeight());//height end value
                                                                                                                  //make negative to positive number
            canvas.drawRect(4, myPageInfo.getPageHeight()-Math.abs(2-myPageInfo.getPageHeight()), myPageInfo.getPageWidth() - 4,keepTrackOfPageDataHeight , myPaint);

            myPaint.setTextAlign(Paint.Align.LEFT);//text will be in middle
            myPaint.setTypeface(Typeface.create(Typeface.DEFAULT,Typeface.BOLD));
            myPaint.setTextSize(11.0f);
            myPaint.setColor(Color.WHITE);
            canvas.drawText(headerOrgName,  8, myPageInfo.getPageHeight()-Math.abs(13-myPageInfo.getPageHeight()), myPaint);
            myPaint.setTextSize(6.0f);
            canvas.drawText("Contact: " + contact + ", Whatsapp: " + whatsappNumber + ", Email: " + email, 8, myPageInfo.getPageHeight()-Math.abs(21-myPageInfo.getPageHeight()), myPaint);
            return true;
        }catch (Exception ex){
            System.out.println("makeTopHeader1 method error");
            ex.printStackTrace();
            return false;
        }
    }
    public boolean makeSubHeader2ImageDetails(String name, String id, String accountNo, String aadhaarNo, byte [] image, String invoiceNo ){
        try {//automatically adjustable
            myPaint.setTextAlign(Paint.Align.LEFT);
            myPaint.setStyle(Paint.Style.STROKE);//myPaint.setStrokeWidth(2); another propertiees
            myPaint.setColor(Color.BLACK);                                    //28 is value of top
            keepTrackOfPageDataHeight =myPageInfo.getPageHeight() - Math.abs(90-myPageInfo.getPageHeight());//top value is updated because all data will be store in this rectangla
                                                                                                                                           //make negative to positive number
            canvas.drawRect(4, myPageInfo.getPageHeight()-Math.abs(28-myPageInfo.getPageHeight()), myPageInfo.getPageWidth() - 4,keepTrackOfPageDataHeight , myPaint);

            myPaint.setColor(Color.BLACK);
            myPaint.setStrokeWidth(0);
            myPaint.setStyle(Paint.Style.FILL);
            myPaint.setTextSize(7.0f);
            canvas.drawText("NAME: " + name,80, myPageInfo.getPageHeight()-Math.abs(39-myPageInfo.getPageHeight()), myPaint);

            //myPaint.setStrokeWidth(2); another propertiees
            myPaint.setColor(Color.YELLOW);
            canvas.drawRect(myPageInfo.getPageWidth() -Math.abs(80-myPageInfo.getPageWidth()),48, myPageInfo.getPageWidth() -Math.abs(150-myPageInfo.getPageWidth()) ,myPageInfo.getPageHeight() - Math.abs(55-myPageInfo.getPageHeight()) , myPaint);

            myPaint.setColor(Color.BLACK);
            canvas.drawText("ID: " + id,80, myPageInfo.getPageHeight()-Math.abs(54-myPageInfo.getPageHeight()), myPaint);
            canvas.drawText("A/C: " + accountNo+", AADHAAR: "+aadhaarNo,80, myPageInfo.getPageHeight()-Math.abs(69-myPageInfo.getPageHeight()), myPaint);
            myPaint.setTextSize(6.0f);
            canvas.drawText( "CREATED ON: "+ ProjectUtility.get12hrCurrentTimeAndDate(),80, myPageInfo.getPageHeight()-Math.abs(84-myPageInfo.getPageHeight()), myPaint);

            myPaint.setTextAlign(Paint.Align.RIGHT);
            canvas.drawText("INVOICE No. "+invoiceNo,  myPageInfo.getPageWidth()-7, myPageInfo.getPageHeight()-Math.abs(84-myPageInfo.getPageHeight()), myPaint);

            myPaint.setTextAlign(Paint.Align.LEFT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
            Bitmap scaledBitmap=Bitmap.createScaledBitmap(bitmap,65,60,false);//image size
            canvas.drawBitmap(scaledBitmap,5,myPageInfo.getPageHeight()-Math.abs(29-myPageInfo.getPageHeight()),myPaint);
            return true;
        }catch (Exception ex){
            System.out.println("makeImageDetails2 method error****************************");
            ex.printStackTrace();
            return false;
        }
    }
    public  String createFileToSavePdfDocumentAndReturnFilePath3(String externalFileDir,String fileName){
        try {
            File folder = new File( externalFileDir + "/acBookPDF");   //https://stackoverflow.com/questions/65125446/cannot-resolve-method-getexternalfilesdir
            if (!folder.exists()) {//of folder not exist then create folder
                folder.mkdir();//File createNewFile() method returns true if new file is created and false if file already exists.
                System.out.println("Creating acBookPDF folder to store PDF***********************************************");
            }
            File filees = new File( externalFileDir + "/acBookPDF/" + fileName + ".pdf");//path of pdf where it is saved in device
            myPdfDocument.writeTo(new FileOutputStream(filees.getAbsolutePath()));//if FileOutputStream cannot find file then it will create automatically
            return filees.getAbsolutePath();//returning created file absolute path

        }catch (Exception e){
        System.out.println("CREATED PDF NOT COPIED TO DEVICE PDF FILE********************************************");
            e.printStackTrace();
            return null;
        }
    }
    public boolean createNewPage1(Integer pageWidth,Integer pageHeight,Integer pageNumber){
        try {
            myPageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create();
             mypage =  myPdfDocument.startPage(myPageInfo);
             canvas =  mypage.getCanvas();
        }catch (Exception ex){
            ex.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean createdPageFinish2(){
        try {
            myPdfDocument.finishPage(mypage);
        }catch (Exception ex){
            System.out.println("after page finish call you cannot write");
            ex.printStackTrace();
            return false;
        }
        return true;
    }
    public boolean closeDocumentLastOperation4(){//Closes this document. This method should be called after you are done working with the document. After this call the document is considered closed and none of its methods should be called.
      try {
           myPdfDocument.close();
      }catch (Exception ex){
          ex.printStackTrace();
          return false;
      }
        return true;
    }

    public PdfDocument getMyPdfDocument() {
        return myPdfDocument;
    }

    public Paint getMyPaint() {
        return myPaint;
    }

    public PdfDocument.PageInfo getMyPageInfo() {
        return myPageInfo;
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public PdfDocument.Page getMypage() {
        return mypage;
    }

}
