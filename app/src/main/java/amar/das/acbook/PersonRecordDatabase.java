package amar.das.acbook;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class PersonRecordDatabase extends SQLiteOpenHelper {
    public final static int Database_Version=6;//5to update db version just increase thie value by 1.when this value is increase then constructor is called
    public final static String DATABASE_NAME="person_db";

    //table 1
    public final static String TABLE_NAME1="person_details_table";
    public final static String COL_1_ID ="ID";
    public final static String COL_2_NAME ="NAME";
    public final static String COL_3_BANKAC ="BANKACCOUNT";
    public final static String COL_4_IFSCCODE ="IFSCCODE";
    public final static String COL_5_BANKNAME ="BANKNAME";
    public final static String COL_6_AADHAAR ="AADHARCARD";
    public final static String COL_7_PHONE ="PHONE";
    public final static String COL_8_SKILL ="TYPE";
    public final static String COL_9_FATHERNAME ="FATHERNAME";
    public final static String COL_10_IMAGE ="IMAGE";
    public final static String COL_11_ACHOLDER ="ACHOLDER";
    public final static String COL_12_ACTIVE ="ACTIVE";
    public final static String COL_13_ADVANCE ="ADVANCE";
    public final static String COL_14_BALANCE ="BALANCE";
    public final static String COL_15_LATESTDATE ="LATESTDATE";
    public final static String COL_16_TIME ="TIME";//To arrange todays enter data to show on top

    //table 2
    public final static String TABLE_NAME2="wages_table";
    public final static String COL_21_ID ="ID";
    public final static String COL_22_DATE ="DATE";//here date and time and id is acting like primary key
    public final static String COL_23_TIME ="TIME";
    public final static String COL_24_MICPATH ="MICPATH";
    public final static String COL_25_DESCRIPTION ="DESCRIPTION";
    public final static String COL_26_WAGES ="WAGES";
    public final static String COL_27_DEPOSIT ="DEPOSIT";
    public final static String COL_28_P1 ="P1";
    public final static String COL_29_P2 ="P2";
    public final static String COL_291_P3 ="P3";
    public final static String COL_292_P4 ="P4";
    public final static String COL_293_ISDEPOSITED ="ISDEPOSITED";

    //table 3
    public final static String TABLE_NAME3="rate_skills_indicator_table";
    public final static String COL_31_ID ="ID";
    public final static String COL_32_R1 ="R1";
    public final static String COL_33_R2 ="R2";
    public final static String COL_34_R3 ="R3";
    public final static String COL_35_R4 ="R4";
    public final static String COL_36_SKILL1 ="SKILL1";
    public final static String COL_37_SKILL2 ="SKILL2";
    public final static String COL_38_SKILL3 ="SKILL3";
    public final static String COL_39_INDICATOR ="INDICATOR";
    public final static String COL_391_RATING ="RATING";
    public final static String COL_392_LEAVINGDATE ="LEAVINGDATE";
    public final static String COL_393_REFFERAL ="REFFERAL";
    public final static String COL_394_INVOICE1 ="PDF1";//or invoice
    public final static String COL_395_INVOICE2 ="PDF2";
    public final static String COL_396_PDFSEQUENCE ="PDFSEQUENCE";


    SQLiteDatabase db;

    public PersonRecordDatabase(Context context){
        super(context,DATABASE_NAME,null,Database_Version);//The reason of passing null is you want the standard SQLiteCursor behaviour. If you want to implement a specialized Cursor you can get it by by extending the Cursor class( this is for doing additional operations on the query results). And in these cases, you can use the CursorFactory class to return an instance of your Cursor implementation. Here is the document for that https://stackoverflow.com/questions/11643294/what-is-the-use-of-sqlitedatabase-cursorfactory-in-android
        System.out.println("constructor db*****************************");
    }
    //If we explicitly insert default NULL into the column then in database blank will be shown instead of NULL
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {//it will execute only once        //NOT NULL OR DEFAULT NOT WORKING AND VARCHAR GIVEN VALUE NOT WORKING HOLDING MORE THAN GIVEN VALUE
//     try {//if some error occur it will handle
//         sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME1 + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME VARCHAR(100) DEFAULT NULL,BANKACCOUNT VARCHAR(20) DEFAULT NULL,IFSCCODE VARCHAR(11) DEFAULT NULL,BANKNAME VARCHAR(38) DEFAULT NULL,AADHARCARD VARCHAR(12) DEFAULT NULL,PHONE VARCHAR(10) DEFAULT NULL,TYPE CHAR(1) DEFAULT NULL,FATHERNAME VARCHAR(100) DEFAULT NULL,IMAGE BLOB DEFAULT NULL,ACHOLDER VARCHAR(100) DEFAULT NULL,ACTIVE CHAR(1) DEFAULT 1,ADVANCE NUMERIC DEFAULT NULL,BALANCE NUMERIC DEFAULT NULL,LATESTDATE TEXT DEFAULT NULL,TIME TEXT DEFAULT NULL);");
//         sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME2 + " (ID INTEGER ,DATE TEXT DEFAULT NULL,TIME TEXT DEFAULT NULL,MICPATH TEXT DEFAULT NULL,DESCRIPTION TEXT DEFAULT NULL,WAGES NUMERIC DEFAULT NULL,DEPOSIT NUMERIC DEFAULT NULL,P1 INTEGER DEFAULT NULL,P2 INTEGER DEFAULT NULL,P3 INTEGER DEFAULT NULL,P4 INTEGER DEFAULT NULL,ISDEPOSITED CHAR(1) DEFAULT NULL);");
//         sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME3 + " (ID INTEGER PRIMARY KEY NOT NULL ,R1 INTEGER DEFAULT NULL,R2 INTEGER DEFAULT NULL,R3 INTEGER DEFAULT NULL,R4 INTEGER DEFAULT NULL,SKILL1 CHAR(1) DEFAULT NULL,SKILL2 CHAR(1) DEFAULT NULL,SKILL3 CHAR(1) DEFAULT NULL,INDICATOR CHAR(1) DEFAULT NULL,RATING CHAR(1) DEFAULT NULL,LEAVINGDATE VARCHAR(10) DEFAULT NULL,REFFERAL TEXT DEFAULT NULL,PDF1 BLOB DEFAULT NULL,PDF2 BLOB DEFAULT NULL,PDFSEQUENCE INTEGER DEFAULT 1);");//id is primary key because according to id only data is stored in table 3 so no duplicate
//     }catch(Exception e){
//         e.printStackTrace();
//     }
        System.out.println("oncreate*****************************************");
    onUpgrade(sqLiteDatabase,0,Database_Version);
    }

    @Override    //i is old version and i1 is new version.When we change version then this method is called
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        System.out.println("on upgrade*******************************************");
        if(oldVersion < 5){//if version is less then 5 then will execute
            System.out.println("old****************5"+oldVersion);
            try {//if some error occur it will handle
                sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME1 + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME VARCHAR(100) DEFAULT NULL,BANKACCOUNT VARCHAR(20) DEFAULT NULL,IFSCCODE VARCHAR(11) DEFAULT NULL,BANKNAME VARCHAR(38) DEFAULT NULL,AADHARCARD VARCHAR(12) DEFAULT NULL,PHONE VARCHAR(10) DEFAULT NULL,TYPE CHAR(1) DEFAULT NULL,FATHERNAME VARCHAR(100) DEFAULT NULL,IMAGE BLOB DEFAULT NULL,ACHOLDER VARCHAR(100) DEFAULT NULL,ACTIVE CHAR(1) DEFAULT 1,ADVANCE NUMERIC DEFAULT NULL,BALANCE NUMERIC DEFAULT NULL,LATESTDATE TEXT DEFAULT NULL,TIME TEXT DEFAULT '0');");
                sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME2 + " (ID INTEGER ,DATE TEXT DEFAULT NULL,TIME TEXT DEFAULT NULL,MICPATH TEXT DEFAULT NULL,DESCRIPTION TEXT DEFAULT NULL,WAGES NUMERIC DEFAULT NULL,DEPOSIT NUMERIC DEFAULT NULL,P1 INTEGER DEFAULT NULL,P2 INTEGER DEFAULT NULL,P3 INTEGER DEFAULT NULL,P4 INTEGER DEFAULT NULL,ISDEPOSITED CHAR(1) DEFAULT NULL);");
                sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME3 + " (ID INTEGER PRIMARY KEY NOT NULL ,R1 INTEGER DEFAULT NULL,R2 INTEGER DEFAULT NULL,R3 INTEGER DEFAULT NULL,R4 INTEGER DEFAULT NULL,SKILL1 CHAR(1) DEFAULT NULL,SKILL2 CHAR(1) DEFAULT NULL,SKILL3 CHAR(1) DEFAULT NULL,INDICATOR CHAR(1) DEFAULT NULL,RATING CHAR(1) DEFAULT NULL,LEAVINGDATE VARCHAR(10) DEFAULT NULL,REFFERAL TEXT DEFAULT NULL,PDF1 BLOB DEFAULT NULL,PDF2 BLOB DEFAULT NULL,PDFSEQUENCE INTEGER DEFAULT 0);");//id is primary key because according to id only data is stored in table 3 so no duplicate
            }catch(Exception e){
                e.printStackTrace();
            }
        }
//      sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME1);
//      sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME2);
//      sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME3);
        else if(oldVersion < 6) { //if version is less then 6 then will execute
            System.out.println("old****************6"+oldVersion);
            sqLiteDatabase.execSQL("ALTER TABLE " + TABLE_NAME1 + " ADD COLUMN TIME TEXT DEFAULT '0'");//make this column integerADDED NEW COLUMN TO TABLE3 AND VERSION IS 4
            sqLiteDatabase.execSQL("ALTER TABLE " + TABLE_NAME3 + " ADD COLUMN PDFSEQUENCE INTEGER DEFAULT 0");
        }
     // Log.d("INDATABASE","ON UPGRADE DROP 3 TABLES");
      //onCreate(sqLiteDatabase);
    }
    //insertdata TO table 1
    public boolean insertDataTable1(String name, String bankaccount, String ifsccode, String bankname, String aadharcard, String phonenumber, String skill, String fathername, byte[] image, String acholder ) {
        try {
            db = this.getWritableDatabase();//getting permission
            ContentValues cv = new ContentValues();//to enter data at once it is like hash map
            cv.put(COL_2_NAME, name);
            cv.put(COL_3_BANKAC, bankaccount);
            cv.put(COL_4_IFSCCODE, ifsccode);
            cv.put(COL_5_BANKNAME, bankname);
            cv.put(COL_6_AADHAAR, aadharcard);
            cv.put(COL_7_PHONE, phonenumber);
            cv.put(COL_8_SKILL, skill);
            cv.put(COL_9_FATHERNAME, fathername);
            cv.put(COL_10_IMAGE, image);
            cv.put(COL_11_ACHOLDER, acholder);
            cv.put(COL_12_ACTIVE,"1");//when new user added then it will be active
            //-1 is returned if error occurred. .insert(...) returns the row id of the new inserted record
            long rowid = db.insert(TABLE_NAME1, null, cv);
           // db.close();//closing db after operation performed
            if (rowid == -1)//data not inserted
                return false;
            else
                return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            if(db != null)
                db.close();
        }

    }
     public  Cursor getId(String name, String bankaccount, String ifsccode, String bankname, String aadharcard, String phonenumber, String type, String fathername,String acholder){
            db = this.getWritableDatabase();//error when closing db or cursor
            String query = "SELECT ID FROM " + TABLE_NAME1 + " WHERE NAME='" + name + "'" + " AND FATHERNAME='" + fathername + "'" + " AND BANKACCOUNT='" + bankaccount + "'" + " AND PHONE='" + phonenumber + "'" + " AND IFSCCODE='" + ifsccode + "'" + " AND AADHARCARD='" + aadharcard + "'" + " AND TYPE='" + type + "'" + " AND BANKNAME='" + bankname + "'" + " AND ACHOLDER='" + acholder + "'";
            Cursor cursor = db.rawQuery(query, null);
            return cursor;
     }

    public Cursor getData(String query){//error when closing db or cursor so dont close cursor
            db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery(query, null);
            return cursor;
    }
    //update to Table 1
    public boolean updateDataTable1(String name, String bankaccount, String ifsccode, String bankname, String aadharcard, String phonenumber, String skill, String fathername, byte[] image, String acholder, String Id ) {
        try {
            db = this.getWritableDatabase();//getting permission
            ContentValues cv = new ContentValues();//to enter data at once it is like hash map
            cv.put(COL_2_NAME, name);
            cv.put(COL_3_BANKAC, bankaccount);
            cv.put(COL_4_IFSCCODE, ifsccode);
            cv.put(COL_5_BANKNAME, bankname);
            cv.put(COL_6_AADHAAR, aadharcard);
            cv.put(COL_7_PHONE, phonenumber);
            cv.put(COL_8_SKILL, skill);
            cv.put(COL_9_FATHERNAME, fathername);
            cv.put(COL_10_IMAGE, image);
            cv.put(COL_11_ACHOLDER, acholder);
           // cv.put(COL_12, "1");//when ever user update that usere will become active
            //0 is returned if no record updated and it return number of rows updated
            int rowid = db.update(TABLE_NAME1, cv, "ID=?", new String[]{Id});
            //db.close();//closing db after operation performed
            if(rowid!=1)//if update return 1 then data is updated else not updated
                return false;

                return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }finally {
            if(db != null)
                db.close();
        }
    }
    //insertdata TO table 3
    public boolean insertDataTable3(String id,int r1,int r2,int r3,int r4,String skill1,String skill2,String skill3,String indicator ) {
        try {
            db = this.getWritableDatabase();//getting permission
            ContentValues cv = new ContentValues();//to enter data at once it is like hash map
            cv.put(COL_31_ID, id);
            cv.put(COL_32_R1, r1);
            cv.put(COL_33_R2, r2);
            cv.put(COL_34_R3, r3);
            cv.put(COL_35_R4, r4);
            cv.put(COL_36_SKILL1, skill1);
            cv.put(COL_37_SKILL2, skill2);
            cv.put(COL_38_SKILL3, skill3);
            cv.put(COL_39_INDICATOR, indicator);
            //-1 is returned if error occurred. .insert(...) returns the row id of the new inserted record
            long rowid = db.insert(TABLE_NAME3, null, cv);
           // db.close();//closing db after operation performed
            if (rowid == -1)//data not inserted
                return false;
            else
                return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            if(db != null)
                db.close();
        }
    }
    //insertdata TO table 2
    public boolean insert_1_Person_WithWagesTable2(String id, String date,String time, String micPath, String description, int wages, int p1, String isDeposited) {
        try {
            db = this.getWritableDatabase();//getting permission
            ContentValues cv = new ContentValues();//to enter data at once it is like hash map
            cv.put(COL_21_ID, id);
            cv.put(COL_22_DATE, date);
            cv.put(COL_23_TIME, time);
            cv.put(COL_24_MICPATH, micPath);
            cv.put(COL_25_DESCRIPTION, description);
            cv.put(COL_26_WAGES, wages);
            cv.put(COL_28_P1, p1);
           cv.put(COL_293_ISDEPOSITED, isDeposited);
            //-1 is returned if error occurred. .insert(...) returns the row id of the new inserted record
            long rowid = db.insert(TABLE_NAME2, null, cv);
           // db.close();//closing db after operation performed
            if (rowid == -1)//data not inserted
                return false;
            else
                return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            if(db != null)
                db.close();
        }
    }
    public boolean insert_2_Person_WithWagesTable2(String id, String date,String time, String micPath, String description, int wages, int p1,int p2, String isDeposited) {
        try {
            db = this.getWritableDatabase();//getting permission
            ContentValues cv = new ContentValues();//to enter data at once it is like hash map
            cv.put(COL_21_ID, id);
            cv.put(COL_22_DATE, date);
            cv.put(COL_23_TIME, time);
            cv.put(COL_24_MICPATH, micPath);
            cv.put(COL_25_DESCRIPTION, description);
            cv.put(COL_26_WAGES, wages);
            cv.put(COL_28_P1, p1);
            cv.put(COL_29_P2, p2);
            cv.put(COL_293_ISDEPOSITED, isDeposited);
            //-1 is returned if error occurred. .insert(...) returns the row id of the new inserted record
            long rowid = db.insert(TABLE_NAME2, null, cv);
           // db.close();//closing db after operation performed
            if (rowid == -1)//data not inserted
                return false;
            else
                return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            if(db != null)
                db.close();
        }
    }
    public boolean insert_3_Person_WithWagesTable2(String id, String date,String time, String micPath, String description, int wages, int p1,int p2,int p3, String isDeposited) {
        try {
            db = this.getWritableDatabase();//getting permission
            ContentValues cv = new ContentValues();//to enter data at once it is like hash map
            cv.put(COL_21_ID, id);
            cv.put(COL_22_DATE, date);
            cv.put(COL_23_TIME, time);
            cv.put(COL_24_MICPATH, micPath);
            cv.put(COL_25_DESCRIPTION, description);
            cv.put(COL_26_WAGES, wages);
            cv.put(COL_28_P1, p1);
            cv.put(COL_29_P2, p2);
            cv.put(COL_291_P3, p3);
            cv.put(COL_293_ISDEPOSITED, isDeposited);
            //-1 is returned if error occurred. .insert(...) returns the row id of the new inserted record
            long rowid = db.insert(TABLE_NAME2, null, cv);
           // db.close();//closing db after operation performed
            if (rowid == -1)//data not inserted
                return false;
            else
                return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            if(db != null)
                db.close();
        }
    }
    public boolean insert_4_Person_WithWagesTable2(String id, String date,String time, String micPath, String description, int wages, int p1,int p2,int p3,int p4, String isDeposited) {
        try {
            db = this.getWritableDatabase();//getting permission
            ContentValues cv = new ContentValues();//to enter data at once it is like hash map
            cv.put(COL_21_ID, id);
            cv.put(COL_22_DATE, date);
            cv.put(COL_23_TIME, time);
            cv.put(COL_24_MICPATH, micPath);
            cv.put(COL_25_DESCRIPTION, description);
            cv.put(COL_26_WAGES, wages);
            cv.put(COL_28_P1, p1);
            cv.put(COL_29_P2, p2);
            cv.put(COL_291_P3, p3);
            cv.put(COL_292_P4, p4);
            cv.put(COL_293_ISDEPOSITED, isDeposited);
            //-1 is returned if error occurred. .insert(...) returns the row id of the new inserted record
            long rowid = db.insert(TABLE_NAME2, null, cv);
           // db.close();//closing db after operation performed
            if (rowid == -1)//data not inserted
                return false;
            else
                return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            if(db != null)
                db.close();
        }
    }
    public boolean insert_Deposit_Table2(String id, String date,String time, String micPath, String description,int deposite,String isDeposited) {
        try {
            db = this.getWritableDatabase();//getting permission
            ContentValues cv = new ContentValues();//to enter data at once it is like hash map
            cv.put(COL_21_ID, id);
            cv.put(COL_22_DATE, date);
            cv.put(COL_23_TIME, time);
            cv.put(COL_24_MICPATH, micPath);
            cv.put(COL_25_DESCRIPTION, description);
            cv.put(COL_27_DEPOSIT, deposite);
            cv.put(COL_293_ISDEPOSITED, isDeposited);
            //-1 is returned if error occurred. .insert(...) returns the row id of the new inserted record
            long rowid = db.insert(TABLE_NAME2, null, cv);
           // db.close();//closing db after operation performed
            if (rowid == -1)//data not inserted
                return false;
            else
                return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            if(db != null)
                db.close();
        }
    }
    public boolean updateTable(String query){
        try{
            db=this.getWritableDatabase();
            db.execSQL(query);
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }finally {
            if(db != null)
                db.close();
        }
    }
    public boolean update_1_TABLE_NAME2(String date, String time, String remarks, String micPath, int wages , int p1 , String id , String date2, String time2){
        try {
            db = this.getWritableDatabase();//getting permission
            ContentValues cv = new ContentValues();//to enter data at once it is like hash map
            cv.put(COL_22_DATE, date);
            cv.put(COL_23_TIME, time);
            cv.put(COL_24_MICPATH, micPath);
            cv.put(COL_25_DESCRIPTION, remarks);
            cv.put(COL_26_WAGES, wages);
            cv.put(COL_28_P1, p1);
            //100 % it will update
            int rowid = db.update(TABLE_NAME2,cv,"ID= '"+id+"'"+" AND DATE= '"+date2+"'"+" AND TIME= '"+time2+"'",null);
           // db.close();//closing db after operation performed
            if(rowid!=1)//if update return 1 then data is updated else not updated
                return false;
             return  true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            if(db != null)
                db.close();
        }
    }
    public boolean update_2_TABLE_NAME2(String date, String time, String remarks, String micPath, int wages , int p1, int p2 , String id , String date2, String time2){
        try {
            db = this.getWritableDatabase();//getting permission
            ContentValues cv = new ContentValues();//to enter data at once it is like hash map
            cv.put(COL_22_DATE, date);
            cv.put(COL_23_TIME, time);
            cv.put(COL_24_MICPATH, micPath);
            cv.put(COL_25_DESCRIPTION, remarks);
            cv.put(COL_26_WAGES, wages);
            cv.put(COL_28_P1, p1);
            cv.put(COL_29_P2, p2);
            //100 % it will update
            int rowid = db.update(TABLE_NAME2,cv,"ID= '"+id+"'"+" AND DATE= '"+date2+"'"+" AND TIME= '"+time2+"'",null);
           // db.close();//closing db after operation performed
            if(rowid!=1)//if update return 1 then data is updated else not updated
                return false;
            return  true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            if(db != null)
                db.close();
        }
    }
    public boolean update_3_TABLE_NAME2(String date, String time, String remarks, String micPath, int wages , int p1, int p2, int p3 , String id , String date2, String time2){
        try {
            db = this.getWritableDatabase();//getting permission
            ContentValues cv = new ContentValues();//to enter data at once it is like hash map
            cv.put(COL_22_DATE, date);
            cv.put(COL_23_TIME, time);
            cv.put(COL_24_MICPATH, micPath);
            cv.put(COL_25_DESCRIPTION, remarks);
            cv.put(COL_26_WAGES, wages);
            cv.put(COL_28_P1, p1);
            cv.put(COL_29_P2, p2);
            cv.put(COL_291_P3, p3);
            //100 % it will update
            int rowid = db.update(TABLE_NAME2,cv,"ID= '"+id+"'"+" AND DATE= '"+date2+"'"+" AND TIME= '"+time2+"'",null);
            //db.close();//closing db after operation performed
            if(rowid!=1)//if update return 1 then data is updated else not updated
                return false;

            return  true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            if(db != null)
                db.close();
        }
    }
    public boolean update_4_TABLE_NAME2(String date, String time, String remarks, String micPath, int wages , int p1 , int p2, int p3, int p4, String id , String date2, String time2){
        try {
            db = this.getWritableDatabase();//getting permission
            ContentValues cv = new ContentValues();//to enter data at once it is like hash map
            cv.put(COL_22_DATE, date);
            cv.put(COL_23_TIME, time);
            cv.put(COL_24_MICPATH, micPath);
            cv.put(COL_25_DESCRIPTION, remarks);
            cv.put(COL_26_WAGES, wages);
            cv.put(COL_28_P1, p1);
            cv.put(COL_29_P2, p2);
            cv.put(COL_291_P3, p3);
            cv.put(COL_292_P4, p4);
            //100 % it will update
            int rowid = db.update(TABLE_NAME2,cv,"ID= '"+id+"'"+" AND DATE= '"+date2+"'"+" AND TIME= '"+time2+"'",null);
           // db.close();//closing db after operation performed
            if(rowid!=1)//if update return 1 then data is updated else not updated
                return false;

            return  true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            if(db != null)
                db.close();
        }
    }
    public boolean update_Deposit_TABLE_NAME2(String date,String time, String micPath, String description,int deposite,String id , String date2, String time2) {
        try {
            db = this.getWritableDatabase();//getting permission
            ContentValues cv = new ContentValues();//to enter data at once it is like hash map
            cv.put(COL_22_DATE, date);
            cv.put(COL_23_TIME, time);
            cv.put(COL_24_MICPATH, micPath);
            cv.put(COL_25_DESCRIPTION, description);
            cv.put(COL_27_DEPOSIT, deposite);

             //100 % it will update
            int rowid = db.update(TABLE_NAME2,cv,"ID= '"+id+"'"+" AND DATE= '"+date2+"'"+" AND TIME= '"+time2+"'",null);
           // db.close();//closing db after operation performed
            if(rowid!=1)//if update return 1 then data is updated else not updated
                return false;

            return  true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            if(db != null)
                db.close();
        }
    }
    public boolean update_Rating_TABLE_NAME3(String rate,String referal,String leavingDate,String id){
        try {
            db = this.getWritableDatabase();//getting permission
            ContentValues cv = new ContentValues();//to enter data at once it is like hash map
            cv.put(COL_391_RATING, rate);
            cv.put(COL_393_REFFERAL, referal);
            cv.put(COL_392_LEAVINGDATE, leavingDate);
            //100 % it will update
            int rowid = db.update(TABLE_NAME3,cv,"ID= '"+id+"'",null);
            if(rowid!=1)//if update return 1 then data is updated else not updated
                return false;

            return  true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            if(db != null)
                db.close();
        }
    }
    public boolean insertPdf(String id,byte [] pdf,int whichPdf1or2){
        try {
            db = this.getWritableDatabase();//getting permission
            ContentValues cv = new ContentValues();//to enter data at once it is like hash map
            if(whichPdf1or2==1) {
                cv.put(COL_394_INVOICE1, pdf);//pdf1
            }else {//if whichPdf1or2==2
                cv.put(COL_395_INVOICE2, pdf);//pdf2
            }
            //100 % it will update return 1 if updated
             int rowid = db.update(TABLE_NAME3,cv,"ID= '"+id+"'",null);
            // db.close();//closing db after operation performed
            if(rowid==1)//if update return 1 then data is updated else not updated
                return true;

            return  false;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            if(db != null)
                db.close();
        }
    }
    @SuppressLint("SuspiciousIndentation")
    public boolean deleteRows(String id, String tableName){
        try {
            db = this.getWritableDatabase();//getting permission
            int row = db.delete(tableName, "ID= '" + id + "'", null);
            if (row > 0)//delete method return number of record deleted
                return true;

            return false;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }finally {
            if(db != null)
            db.close();
        }
    }

}
