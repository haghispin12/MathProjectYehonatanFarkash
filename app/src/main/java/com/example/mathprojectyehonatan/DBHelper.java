package com.example.mathprojectyehonatan;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class DBHelper {
     package com.hag.mathexerciseproject.classesDB;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.annotation.Nullable;

import com.hag.mathexerciseproject.classesModel.User;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

    public class DBHelper extends SQLiteOpenHelper {

        private static final String DATABASENAME = "user.db";
        private static final String TABLE_RECORD = "tblusers";
        private static final int DATABASEVERSION = 1;
        // ?
        private static final String COLUMN_ID = "_id";
        private static final String COLUMN_SCORE = "score";
        private static final String COLUMN_NAME = "name";
        //private static final String COLUMN_PASSWORD = "password";
        private static final String COLUMN_RATE = "rate";
        private static final String COLUMN_PICTURE = "image";

        private static final String[] allColumns = {COLUMN_ID, COLUMN_NAME, COLUMN_RATE,COLUMN_PICTURE,COLUMN_SCORE};

        private static final String CREATE_TABLE_USER = "CREATE TABLE IF NOT EXISTS " +
                TABLE_RECORD + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME + " TEXT UNIQUE," +
                COLUMN_SCORE + " INT," +
                COLUMN_RATE + " INT," +
                COLUMN_PICTURE + " BLOB );";

        private SQLiteDatabase database; // access to table

        public DBHelper(@Nullable Context context) {
            super(context, DATABASENAME, null, DATABASEVERSION);
        }


        // creating the database
        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase)
        {
            sqLiteDatabase.execSQL(CREATE_TABLE_USER);
        }

        // in case of version upgrade -> new schema
        // database version
        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORD);
            onCreate(sqLiteDatabase);
        }


        // get the user back with the id
        // also possible to return only the id
        public long insert(User user, Context context){
            database = getWritableDatabase(); // get access to write the database
            ContentValues values = new ContentValues();
            values.put(COLUMN_NAME, user.getUserName());
            values.put(COLUMN_RATE, user.getRating());
            values.put(COLUMN_SCORE, user.getMyScore());

            // stored as Binary Large OBject ->  BLOB
            try {
                values.put(COLUMN_PICTURE, getBytes(context,user.getUri()));
            } catch (IOException e) {
                e.printStackTrace();
            }

            long id = database.insert(TABLE_RECORD, null, values);
            user.setId(id);
            database.close();
            return id;
        }

//        // remove a specific user from the table
//        public void deleteUser(User user)
//        {
//
//        }
//
//        public void deleteById(long id )
//        {
//            database = getWritableDatabase(); // get access to write e data
//            database.delete(TABLE_RECORD, COLUMN_ID + " = " + id, null);
//            database.close(); // close the database
//        }
//
//        // update a specific user
//        public void update(User user)
//        {
//            database = getWritableDatabase();
//            ContentValues values = new ContentValues();
//            values.put(COLUMN_ID, user.getId());
//            values.put(COLUMN_NAME, user.getUserName());
//            values.put(COLUMN_RATE, user.getRating());
//            // stored as Binary Large OBject ->  BLOB
//            values.put(COLUMN_PICTURE, getBytes(user.getBitmap()));
//            database.update(TABLE_RECORD, values, COLUMN_ID + "=" + user.getId(), null);
//            database.close();
//
//        }
//
//        // return all rows in table
//        public ArrayList<User> selectAll(){
//            database = getReadableDatabase(); // get access to read the database
//            ArrayList<User> users = new ArrayList<>();
//            Cursor cursor = database.query(TABLE_RECORD, allColumns, null, null, null, null, null); // cursor points at a certain row
//            if (cursor.getCount() > 0) {
//                while (cursor.moveToNext()) {
//                    String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
//                    int rating = cursor.getInt(cursor.getColumnIndex(COLUMN_RATE));
//                    int score = cursor.getInt(cursor.getColumnIndex(COLUMN_SCORE));
//                    byte[] bytes = cursor.getBlob(cursor.getColumnIndex(COLUMN_PICTURE));
//
//                    Bitmap bitmap = getImage(bytes);
//                    long id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID));
//                    User user= new User(id,name,rating,bitmap,score);
//                    users.add(user);
//                }
//            }
//            cursor.close();
//            database.close();
//            return users;
//        }

        //
        // I prefer using this one...
        //
        public ArrayList<User> genericSelectByUserName(String userName)
        {
            String[] vals = { userName };
            // if using the rawQuery
            // String query = "SELECT * FROM " + TABLE_RECORD + " WHERE " + COLUMN_NAME + " = ?";
            String column = COLUMN_NAME;
            return select(column,vals);
        }


        // INPUT: notice two options rawQuery should look like
        // rawQuery("SELECT id, name FROM people WHERE name = ? AND id = ?", new String[] {"David", "2"});
        // OUTPUT: arraylist - number of elements accordingly
        public ArrayList<User> select(String column,String[] values)
        {
            database = getReadableDatabase(); // get access to read the database
            ArrayList<User> users = new ArrayList<>();
            // Two options,
            // since query cannot be created in compile time there is no difference
            //Cursor cursor = database.rawQuery(query, values);
            Cursor cursor= database.query(TABLE_RECORD, allColumns, COLUMN_NAME +" = ? ", values, null, null, null); // cursor points at a certain row
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String name = cursor.getString(cursor.getColumnIndex(COLUMN_NAME));
                    int rating = cursor.getInt(cursor.getColumnIndex(COLUMN_RATE));
                    byte[] bytes = cursor.getBlob(cursor.getColumnIndex(COLUMN_PICTURE));
                    int score = cursor.getInt(cursor.getColumnIndex(COLUMN_SCORE));
                    Bitmap bitmap = getImage(bytes);
                    long id = cursor.getLong(cursor.getColumnIndex(COLUMN_ID));
                    User user= new User(id,name,rating,bitmap,score);
                    users.add(user);
                }// end while
            } // end if
            database.close();
            return users;
        }

        public static byte[] getBytes(Context context, Uri uri) throws IOException {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(),uri);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
            return stream.toByteArray();
        }


        // convert from bitmap to byte array
        private  byte[] getBytes(Bitmap bitmap) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            return stream.toByteArray();
        }

        // convert from byte array to bitmap
        private  Bitmap getImage(byte[] image) {
            return BitmapFactory.decodeByteArray(image, 0, image.length);
        }


    }
