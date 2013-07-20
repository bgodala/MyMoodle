package com.moodletest;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/*
 * DBAdapter
 * Dan Armendariz
 * Computer Science E-76
 * 
 * Provides an interface through which we can perform queries
 * against the SQLite database.
 */

public class DBAdapter {

	// define the layout of our table in fields
	// "_id" is used by Android for Content Providers and should
	// generally be an auto-incrementing key in every table.
	public static final String KEY_ROWID = "_id";
	public static final String KEY_COURSENAME = "coursename";
	public static final String KEY_URLID = "urlid";
	public static final String KEY_URL = "url";
	public static final String KEY_ASSIGNNAME = "assignname";
	public static final String KEY_ASSIGNCONT = "assigncont";

	// define some SQLite database fields
	// Take a look at your DB on the emulator with:
	// 	adb shell 
	//  sqlite3 /data/data/<pkg_name>/databases/<DB_NAME>
	private static final String DB_NAME = "data.sqlite";
	private static final String DB_TABLE = "assignments";
	private static final int    DB_VER = 1;

	// a SQL statement to create a new table
	private static final String DB_CREATE = 
		"CREATE TABLE assignments (_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
		 "courseid TEXT NOT NULL, coursename TEXT NOT NULL, assignid TEXT, assignname TEXT);";


	// define an extension of the SQLiteOpenHelper to handle the
	// creation and upgrade of a table
	private static class DatabaseHelper extends SQLiteOpenHelper {

		// Class constructor
		DatabaseHelper(Context c) {
			// instantiate a SQLiteOpenHelper by passing it
			// the context, the database's name, a CursorFactory 
			// (null by default), and the database version.
			super(c, DB_NAME, null, DB_VER);
		}

		// called by the parent class when a DB doesn't exist
		public void onCreate(SQLiteDatabase db) {
			// Execute our DB_CREATE statement
			//db.execSQL(DB_CREATE);
		}
		
		// called by the parent when a DB needs to be upgraded
		public void onUpgrade(SQLiteDatabase db, int oldVer, int newVer) {
			// remove the old version and create a new one.
			// If we were really upgrading we'd try to move data over
			db.execSQL("DROP TABLE IF EXISTS "+DB_TABLE);
			onCreate(db);
		}
	}


	// useful fields in the class
    private final Context context;	
    private DatabaseHelper helper;
    private SQLiteDatabase db;

    // DBAdapter class constructor
	public DBAdapter(Context c) {
		this.context = c;
	}
	
	/** Open the DB, or throw a SQLException if we cannot open
	  * or create a new DB.
	  */ 
	public DBAdapter open() throws SQLException {
		// instantiate a DatabaseHelper class (see above)
		helper = new DatabaseHelper(context);

		// the SQLiteOpenHelper class (a parent of DatabaseHelper)
		// has a "getWritableDatabase" method that returns an
		// object of type SQLiteDatabase that represents an open
		// connection to the database we've opened (or created).
		db = helper.getWritableDatabase();

		return this;
	}
	
	/** Close the DB
	  */
	public void close() {
		helper.close();
	}

	/** Insert a user and password into the db
	  * 
	  * @param user username (string)
	  * @param pass user's password (string)
	  * @return the row id, or -1 on failure
	 */
	/*public long insertUser(String user, String pass) {
		ContentValues vals = new ContentValues();
		vals.put(KEY_USER, user);
		vals.put(KEY_PASS, pass);
		return db.insert(DB_TABLE, null, vals);
	}*/

	/** Authenticate a user by querying the table to see
	  * if that user and password exist. We expect only one row
	  * to be returned if that combination exists, and if so, we
	  * have successfully authenticated.
	  * 
	  * @param user username (string)
	  * @param pass user's password (string)
	  * @return true if authenticated, false otherwise
	 */
	
	public boolean isOpen()
	{		
		return db.isOpen();
	}
	
	public void assignTableCreate(String userid){
		
		db.execSQL("CREATE TABLE usr_assign_table_"+userid +"(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				 "courseid TEXT NOT NULL,urlid TEXT,url TEXT, assignname TEXT,heading TEXT,assigncont TEXT);");
		
	}
	public void courseTableCreate(String userid){
		
		db.execSQL("CREATE TABLE usr_course_table_"+userid +"(_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
				 "courseid TEXT NOT NULL,fullname TEXT NOT NULL);");
		
	}
	public void assignTableInsertDetails(String courseid,String urlid,String url,String name,String userid){
		db.execSQL("INSERT INTO usr_assign_table_"+userid + "(courseid,urlid,url,assignname) VALUES ("+courseid+","+"'"+urlid+"'"+",'"+url+"',"+"'"+name+"'"+");");
	}
	public void courseTableInsertDetails(String courseid,String name,String userid){
		db.execSQL("INSERT INTO usr_course_table_"+userid + "(courseid,fullname) VALUES ("+courseid+","+"'"+name+"'"+");");
	}
	public Cursor assignTableGetContent(String urlid,String courseid,String userid){
		
		final String[] str ;
		Cursor cursor =//db.rawQuery("SELECT name FROM sqlite_master WHERE name=?", new String[]{tablename});
				db.query(
				"usr_assign_table_"+userid, // table to perform the query
				str=new String[] { "assigncont"}, //resultset columns/fields
				"courseid=? AND urlid=?" ,//condition or selection	
				new String[] {courseid,urlid},  //selection arguments (fills in '?' above)
				null,  //groupBy
				null,  //having
				null   //orderBy
			);
		
		cursor.moveToFirst();
		//System.out.println("content is : "+cursor.getString(0));
		return cursor;
	}
	public Cursor courseTableGetContent(String userid){
		
		final String[] str ;
		Cursor cursor =//db.rawQuery("SELECT name FROM sqlite_master WHERE name=?", new String[]{tablename});
				db.query(
				"usr_course_table_"+userid, // table to perform the query
				str=new String[] { "courseid"}, //resultset columns/fields
				"" ,//condition or selection	
				null,//new String[] {courseid,urlid},  //selection arguments (fills in '?' above)
				null,  //groupBy
				null,  //having
				null   //orderBy
			);
		
		cursor.moveToFirst();
		//System.out.println("content is : "+cursor.getString(0));
		return cursor;
	}
	public Cursor courseTableGetContent(String userid,String courseid){
		
		final String[] str ;
		Cursor cursor =//db.rawQuery("SELECT name FROM sqlite_master WHERE name=?", new String[]{tablename});
				db.query(
				"usr_course_table_"+userid, // table to perform the query
				str=new String[] { "courseid"}, //resultset columns/fields
				"courseid=?" ,//condition or selection	
				new String[] {courseid},  //selection arguments (fills in '?' above)
				null,  //groupBy
				null,  //having
				null   //orderBy
			);
		
		cursor.moveToFirst();
		//System.out.println("content is : "+cursor.getString(0));
		return cursor;
	}
	public void assignTableInsertContent(String urlid,String courseid,String userid,String content){
		//content=content.replaceAll("\"","\\\"\\" );
		//db.execSQL("UPDATE usr_assign_table_"+userid + "SET assigncont='"+content+"' WHERE courseid="+courseid+" AND urlid="+urlid+";");
		//db.rawQuery("UPDATE usr_assign_table_"+userid + " SET assigncont=? WHERE courseid=? AND urlid=?", new String[]{content,courseid,urlid});
		ContentValues val = new ContentValues();
		val.put("assigncont", content);
		db.update("usr_assign_table_"+userid, val, "urlid=? AND courseid=?",new String[]{urlid,courseid});
	}
	public boolean queryTable(String tablename){
		System.out.println("table name is : "+tablename);
		
		final String[] str ;
		Cursor cursor =db.rawQuery("SELECT name FROM sqlite_master WHERE name=?", new String[]{tablename});
		
		cursor.moveToFirst();
		System.out.println("number of tables is : "+cursor.getCount());
		
		return cursor.getCount()==1?true:false;
	}
	/* public Cursor authenticateUser(String name) {
		// Perform a database query
		final String[] str ;
		Cursor cursor = db.query(
				DB_TABLE, // table to perform the query
				str=new String[] { "name" }, //resultset columns/fields
				//KEY_DEPT+"=? AND "+KEY_PASS+"=?", //condition or selection
						KEY_DEPT+"=?",
				new String[] { name},  //selection arguments (fills in '?' above)
				null,  //groupBy
				null,  //having
				null   //orderBy
			);

		// if a Cursor object was returned by the query and
		// that query returns exactly 1 row, then we've authenticated
		cursor.moveToFirst();
		System.out.println("names are "+cursor.getString(0));
		return cursor;
	}
	public Cursor getContact(String name,String department) {
		// Perform a database query
		final String[] str ;
		Cursor cursor = db.query(
				DB_TABLE, // table to perform the query
				str=new String[] { "name","department","email","mobile","office" }, //resultset columns/fields
				KEY_DEPT+"=? AND "+KEY_NAME+"=?", //condition or selection	
				new String[] { department,name},  //selection arguments (fills in '?' above)
				null,  //groupBy
				null,  //having
				null   //orderBy
			);

		// if a Cursor object was returned by the query and
		// that query returns exactly 1 row, then we've authenticated
		cursor.moveToFirst();
		System.out.println("names are "+cursor.getString(0));
		return cursor;
	}
	
	public Cursor getDepartmentList(String category) {
		// Perform a database query
		final String[] str ;
		Cursor cursor = db.query(
				DB_TABLE, // table to perform the query
				str=new String[] { "department"}, //resultset columns/fields
				KEY_CAT+"=?", //condition or selection	
				new String[] { category},  //selection arguments (fills in '?' above)
				null,  //groupBy
				null,  //having
				null   //orderBy
			);

		// if a Cursor object was returned by the query and
		// that query returns exactly 1 row, then we've authenticated
		cursor.moveToFirst();
		System.out.println("names are "+cursor.getString(0));
		return cursor;
	}
	*/
	/*public Cursor getAssignmentList(String assid, String courseid) {
		// Perform a database query
		final String[] str ;
		Cursor cursor = db.query(
				DB_TABLE, // table to perform the query
				str=new String[] { "assignid"}, //resultset columns/fields
				 KEY_COURSEID+"=? AND "+KEY_ASSIGNID+"=?", //condition or selection	
				new String[] {courseid,assid},  //selection arguments (fills in '?' above)
				null,  //groupBy
				null,  //having
				null   //orderBy
			);

		// if a Cursor object was returned by the query and
		// that query returns exactly 1 row, then we've authenticated
		System.out.println(cursor.getCount());
		//cursor.moveToFirst();
		//System.out.println("names are "+cursor.getString(0));
		return cursor;
	}*/
	
	public void populateDatabase(ArrayList<String> courseid,ArrayList<String> coursename,ArrayList<String> assid,ArrayList<String> assname ){
		db.execSQL("delete from assignments");
		for(int i=0;i<courseid.size();i++)
		db.execSQL("INSERT INTO assignments (courseid,coursename,assignid,assignname) VALUES ("+courseid.get(i).toString()+","+"'"+coursename.get(i).toString()+"'"+","+assid.get(i).toString()+","+"'"+assname.get(i).toString()+"'"+");");
	}
	

}