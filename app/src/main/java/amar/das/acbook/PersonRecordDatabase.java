package amar.das.acbook;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.widget.ImageView;


public class PersonRecordDatabase extends SQLiteOpenHelper {
    public final static int Database_Version=1;

    public final static String DATABASE_NAME="person_db";
    public final static String TABLE_NAME="person_table";
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



    SQLiteDatabase db;

    public PersonRecordDatabase(Context context){
        super(context,DATABASE_NAME,null,Database_Version);
    }
    //f we explicitly insert default NULL into the column then in database blank will be shown instead of NULL
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {//it will execute only once        //NOT NULL OR DEFAULT NOT WORKING AND VARCHAR GIVEN VALUE NOT WORKING HOLDING MORE THAN GIVEN VALUE
     try {//if some error occur it will handle
         sqLiteDatabase.execSQL("CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME VARCHAR(100) DEFAULT NULL,BANKACCOUNT VARCHAR(20) DEFAULT NULL,IFSCCODE VARCHAR(11) DEFAULT NULL,BANKNAME VARCHAR(38) DEFAULT NULL,AADHARCARD VARCHAR(12) DEFAULT NULL,PHONE VARCHAR(10) DEFAULT NULL,TYPE CHAR(1) DEFAULT NULL,FATHERNAME VARCHAR(100) DEFAULT NULL,IMAGE BLOB DEFAULT NULL,ACHOLDER VARCHAR(100) DEFAULT NULL,ACTIVE CHAR(1) DEFAULT 1);");
     }catch(Exception e){
         e.printStackTrace();
     }
    }

    @Override    //i is old version and i1 is new version
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
      sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+TABLE_NAME);
      onCreate(sqLiteDatabase);
    }
    //insertdata
    public boolean insertData(String name, String bankaccount, String ifsccode, String bankname, String aadharcard, String phonenumber, String skill, String fathername, byte[] image,String acholder ) {
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

            //-1 is returned if error occurred. .insert(...) returns the row id of the new inserted record
            long rowid = db.insert(TABLE_NAME, null, cv);
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
      String query="SELECT ID FROM "+TABLE_NAME+ " WHERE NAME='"+name+"'"+" AND FATHERNAME='"+fathername+"'"+
                " AND BANKACCOUNT='"+bankaccount+"'"+" AND PHONE='"+phonenumber+"'"+" AND IFSCCODE='"+ifsccode+"'"+
              " AND AADHARCARD='"+aadharcard+"'"+" AND TYPE='"+type+"'" +
                 " AND BANKNAME='"+bankname+"'"+" AND ACHOLDER='"+acholder+"'";//+" AND IMAGE= '"+image+"'"

      Cursor cursor=db.rawQuery(query,null);
      return cursor;
    }

    public Cursor getData(String query){
        db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(query,null);
        //db.close();//error connection pool has been closed
        return cursor;
    }
    //update
    public boolean updateData(String name, String bankaccount, String ifsccode, String bankname, String aadharcard, String phonenumber, String skill, String fathername, byte[] image,String acholder,String Id ) {
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

            //0 is returned if no record updated and it return number of rows updated
            long rowid = db.update(TABLE_NAME, cv, "ID=?", new String[]{Id});
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

}
