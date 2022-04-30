package amar.das.acbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;


public class PersonRecordDatabase extends SQLiteOpenHelper {
    public final static int Database_Version=1;
    public final static String DATABASE_NAME="person_db";

    //table 1
    public final static String TABLE_NAME1="person_details_table";
    public final static String COL_1="ID";
    public final static String COL_2="NAME";
    public final static String COL_3="BANKACCOUNT";
    public final static String COL_4="IFSCCODE";
    public final static String COL_5="BANKNAME";
    public final static String COL_6="AADHARCARD";
    public final static String COL_7="PHONE";
    public final static String COL_8="TYPE";
    public final static String COL_9="FATHERNAME";
    public final static String COL_10="IMAGE";
    public final static String COL_11="ACHOLDER";
    public final static String COL_12="ACTIVE";
    public final static String COL_13="ADVANCE";
    public final static String COL_14="BALANCE";
    public final static String COL_15="LATESTDATE";

    //table 2
    public final static String TABLE_NAME2="wages_table";
    public final static String COL_21="ID";
    public final static String COL_22="DATE";//here date and time is acting like primary key
    public final static String COL_2221="TIME";
    public final static String COL_24="MICPATH";
    public final static String COL_26="DESCRIPTION";
    public final static String COL_27="WAGES";
    public final static String COL_28="DEPOSIT";
    public final static String COL_29="P1";
    public final static String COL_221="P2";
    public final static String COL_222="P3";
    public final static String COL_223="P4";
    public final static String COL_224="ISDEPOSITED";

    //table 3
    public final static String TABLE_NAME3="rate_skills_indicator_table";
    public final static String COL_31="ID";
    public final static String COL_32="R1";
    public final static String COL_33="R2";
    public final static String COL_34="R3";
    public final static String COL_35="R4";
    public final static String COL_36="SKILL1";
    public final static String COL_37="SKILL2";
    public final static String COL_38="SKILL3";
    public final static String COL_39="INDICATOR";
    public final static String COL_325="RATING";
    public final static String COL_326="LEAVINGDATE";
    public final static String COL_327="REFFERAL";


    SQLiteDatabase db;

    public PersonRecordDatabase(Context context){
        super(context,DATABASE_NAME,null,Database_Version);
    }
    //If we explicitly insert default NULL into the column then in database blank will be shown instead of NULL
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {//it will execute only once        //NOT NULL OR DEFAULT NOT WORKING AND VARCHAR GIVEN VALUE NOT WORKING HOLDING MORE THAN GIVEN VALUE
     try {//if some error occur it will handle
         sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME1 + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME VARCHAR(100) DEFAULT NULL,BANKACCOUNT VARCHAR(20) DEFAULT NULL,IFSCCODE VARCHAR(11) DEFAULT NULL,BANKNAME VARCHAR(38) DEFAULT NULL,AADHARCARD VARCHAR(12) DEFAULT NULL,PHONE VARCHAR(10) DEFAULT NULL,TYPE CHAR(1) DEFAULT NULL,FATHERNAME VARCHAR(100) DEFAULT NULL,IMAGE BLOB DEFAULT NULL,ACHOLDER VARCHAR(100) DEFAULT NULL,ACTIVE CHAR(1) DEFAULT 1,ADVANCE NUMERIC DEFAULT NULL,BALANCE NUMERIC DEFAULT NULL,LATESTDATE TEXT DEFAULT NULL);");
         sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME2 + " (ID INTEGER ,DATE TEXT DEFAULT NULL,TIME TEXT DEFAULT NULL,MICPATH TEXT DEFAULT NULL,DESCRIPTION TEXT DEFAULT NULL,WAGES NUMERIC DEFAULT NULL,DEPOSIT NUMERIC DEFAULT NULL,P1 INTEGER DEFAULT NULL,P2 INTEGER DEFAULT NULL,P3 INTEGER DEFAULT NULL,P4 INTEGER DEFAULT NULL,ISDEPOSITED CHAR(1) DEFAULT NULL);");
         sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME3 + " (ID INTEGER PRIMARY KEY NOT NULL ,R1 INTEGER DEFAULT NULL,R2 INTEGER DEFAULT NULL,R3 INTEGER DEFAULT NULL,R4 INTEGER DEFAULT NULL,SKILL1 CHAR(1) DEFAULT NULL,SKILL2 CHAR(1) DEFAULT NULL,SKILL3 CHAR(1) DEFAULT NULL,INDICATOR CHAR(1) DEFAULT NULL,RATING CHAR(1) DEFAULT NULL,LEAVINGDATE VARCHAR(10) DEFAULT NULL,REFFERAL TEXT DEFAULT NULL);");//id is primary key because according to id only data is stored in table 3 so no duplicate
     }catch(Exception e){
         e.printStackTrace();
     }
    }

    @Override    //i is old version and i1 is new version.When we change version then this method is called
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

      sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME1);
      sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME2);
      sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME3);
      Log.d("INDATABASE","ON UPGRADE DROP 3 TABLES");
      onCreate(sqLiteDatabase);
    }
    //insertdata TO table 1
    public boolean insertDataTable1(String name, String bankaccount, String ifsccode, String bankname, String aadharcard, String phonenumber, String skill, String fathername, byte[] image, String acholder ) {
        try {
            db = this.getWritableDatabase();//getting permission
            ContentValues cv = new ContentValues();//to enter data at once it is like hash map
            cv.put(COL_2, name);
            cv.put(COL_3, bankaccount);
            cv.put(COL_4, ifsccode);
            cv.put(COL_5, bankname);
            cv.put(COL_6, aadharcard);
            cv.put(COL_7, phonenumber);
            cv.put(COL_8, skill);
            cv.put(COL_9, fathername);
            cv.put(COL_10, image);
            cv.put(COL_11, acholder);
            cv.put(COL_12,"1");//when new user added then it will be active
            //-1 is returned if error occurred. .insert(...) returns the row id of the new inserted record
            long rowid = db.insert(TABLE_NAME1, null, cv);
            db.close();//closing db after operation performed
            if (rowid == -1)//data not inserted
                return false;
            else
                return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }
     public  Cursor getId(String name, String bankaccount, String ifsccode, String bankname, String aadharcard, String phonenumber, String type, String fathername,String acholder){
        db=this.getWritableDatabase() ;
      String query="SELECT ID FROM "+ TABLE_NAME1 + " WHERE NAME='"+name+"'"+" AND FATHERNAME='"+fathername+"'"+ " AND BANKACCOUNT='"+bankaccount+"'"+" AND PHONE='"+phonenumber+"'"+" AND IFSCCODE='"+ifsccode+"'"+ " AND AADHARCARD='"+aadharcard+"'"+" AND TYPE='"+type+"'" + " AND BANKNAME='"+bankname+"'"+" AND ACHOLDER='"+acholder+"'";
      Cursor cursor=db.rawQuery(query,null);
      return cursor;
    }

    public Cursor getData(String query){
        db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(query,null);
        //db.close();//error connection pool has been closed
        return cursor;
    }
    //update to Table 1
    public boolean updateDataTable1(String name, String bankaccount, String ifsccode, String bankname, String aadharcard, String phonenumber, String skill, String fathername, byte[] image, String acholder, String Id ) {
        try {
            db = this.getWritableDatabase();//getting permission
            ContentValues cv = new ContentValues();//to enter data at once it is like hash map
            cv.put(COL_2, name);
            cv.put(COL_3, bankaccount);
            cv.put(COL_4, ifsccode);
            cv.put(COL_5, bankname);
            cv.put(COL_6, aadharcard);
            cv.put(COL_7, phonenumber);
            cv.put(COL_8, skill);
            cv.put(COL_9, fathername);
            cv.put(COL_10, image);
            cv.put(COL_11, acholder);
            cv.put(COL_12, "1");//when ever user update that usere will become active
            //0 is returned if no record updated and it return number of rows updated
            long rowid = db.update(TABLE_NAME1, cv, "ID=?", new String[]{Id});
            db.close();//closing db after operation performed
            if (rowid == 0)//data not inserted
                return false;
            else
                return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    //insertdata TO table 3
    public boolean insertDataTable3(String id,int r1,int r2,int r3,int r4,String skill1,String skill2,String skill3,String indicator ) {
        try {
            db = this.getWritableDatabase();//getting permission
            ContentValues cv = new ContentValues();//to enter data at once it is like hash map
            cv.put(COL_31, id);
            cv.put(COL_32, r1);
            cv.put(COL_33, r2);
            cv.put(COL_34, r3);
            cv.put(COL_35, r4);
            cv.put(COL_36, skill1);
            cv.put(COL_37, skill2);
            cv.put(COL_38, skill3);
            cv.put(COL_39, indicator);
            //-1 is returned if error occurred. .insert(...) returns the row id of the new inserted record
            long rowid = db.insert(TABLE_NAME3, null, cv);
            db.close();//closing db after operation performed
            if (rowid == -1)//data not inserted
                return false;
            else
                return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    //insertdata TO table 2
    public boolean insert_1_Person_WithWagesTable2(String id, String date,String time, String micPath, String description, int wages, int p1, String isDeposited) {
        try {
            db = this.getWritableDatabase();//getting permission
            ContentValues cv = new ContentValues();//to enter data at once it is like hash map
            cv.put(COL_21, id);
            cv.put(COL_22, date);
            cv.put(COL_2221, time);
            cv.put(COL_24, micPath);
            cv.put(COL_26, description);
            cv.put(COL_27, wages);
            cv.put(COL_29, p1);
           cv.put(COL_224, isDeposited);
            //-1 is returned if error occurred. .insert(...) returns the row id of the new inserted record
            long rowid = db.insert(TABLE_NAME2, null, cv);
            db.close();//closing db after operation performed
            if (rowid == -1)//data not inserted
                return false;
            else
                return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    public boolean insert_2_Person_WithWagesTable2(String id, String date,String time, String micPath, String description, int wages, int p1,int p2, String isDeposited) {
        try {
            db = this.getWritableDatabase();//getting permission
            ContentValues cv = new ContentValues();//to enter data at once it is like hash map
            cv.put(COL_21, id);
            cv.put(COL_22, date);
            cv.put(COL_2221, time);
            cv.put(COL_24, micPath);
            cv.put(COL_26, description);
            cv.put(COL_27, wages);
            cv.put(COL_29, p1);
            cv.put(COL_221, p2);
            cv.put(COL_224, isDeposited);
            //-1 is returned if error occurred. .insert(...) returns the row id of the new inserted record
            long rowid = db.insert(TABLE_NAME2, null, cv);
            db.close();//closing db after operation performed
            if (rowid == -1)//data not inserted
                return false;
            else
                return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    public boolean insert_3_Person_WithWagesTable2(String id, String date,String time, String micPath, String description, int wages, int p1,int p2,int p3, String isDeposited) {
        try {
            db = this.getWritableDatabase();//getting permission
            ContentValues cv = new ContentValues();//to enter data at once it is like hash map
            cv.put(COL_21, id);
            cv.put(COL_22, date);
            cv.put(COL_2221, time);
            cv.put(COL_24, micPath);
            cv.put(COL_26, description);
            cv.put(COL_27, wages);
            cv.put(COL_29, p1);
            cv.put(COL_221, p2);
            cv.put(COL_222, p3);
            cv.put(COL_224, isDeposited);
            //-1 is returned if error occurred. .insert(...) returns the row id of the new inserted record
            long rowid = db.insert(TABLE_NAME2, null, cv);
            db.close();//closing db after operation performed
            if (rowid == -1)//data not inserted
                return false;
            else
                return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    public boolean insert_4_Person_WithWagesTable2(String id, String date,String time, String micPath, String description, int wages, int p1,int p2,int p3,int p4, String isDeposited) {
        try {
            db = this.getWritableDatabase();//getting permission
            ContentValues cv = new ContentValues();//to enter data at once it is like hash map
            cv.put(COL_21, id);
            cv.put(COL_22, date);
            cv.put(COL_2221, time);
            cv.put(COL_24, micPath);
            cv.put(COL_26, description);
            cv.put(COL_27, wages);
            cv.put(COL_29, p1);
            cv.put(COL_221, p2);
            cv.put(COL_222, p3);
            cv.put(COL_223, p4);
            cv.put(COL_224, isDeposited);
            //-1 is returned if error occurred. .insert(...) returns the row id of the new inserted record
            long rowid = db.insert(TABLE_NAME2, null, cv);
            db.close();//closing db after operation performed
            if (rowid == -1)//data not inserted
                return false;
            else
                return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
    public boolean insert_Deposit_Table2(String id, String date,String time, String micPath, String description,int deposite,String isDeposited) {
        try {
            db = this.getWritableDatabase();//getting permission
            ContentValues cv = new ContentValues();//to enter data at once it is like hash map
            cv.put(COL_21, id);
            cv.put(COL_22, date);
            cv.put(COL_2221, time);
            cv.put(COL_24, micPath);
            cv.put(COL_26, description);
            cv.put(COL_28, deposite);
            cv.put(COL_224, isDeposited);
            //-1 is returned if error occurred. .insert(...) returns the row id of the new inserted record
            long rowid = db.insert(TABLE_NAME2, null, cv);
            db.close();//closing db after operation performed
            if (rowid == -1)//data not inserted
                return false;
            else
                return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }


    public boolean updateTable(String query){
        try{
            db=this.getWritableDatabase();
            db.execSQL(query);
            db.close();
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }


}
