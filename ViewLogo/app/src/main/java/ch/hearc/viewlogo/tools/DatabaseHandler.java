package ch.hearc.viewlogo.tools;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by jeshon.assuncao on 05.04.2016.
 */
public class DatabaseHandler extends SQLiteOpenHelper {
    public static final String LOGO_ID = "id";
    public static final String LOGO_TITLE = "title";
    public static final String LOGO_IMAGE = "image";
    public static final String LOGO_FEATURES = "listFeatureLogo";
    public static final String LOGO_TABLE_NAME = "Logo";

    public static final String LOGO_TABLE_CREATE = "CREATE TABLE " + LOGO_TABLE_NAME + "(" + LOGO_ID + "INTEGER PRIMARY KEY AUTOINCREMENT, " + LOGO_TITLE + " TEXT, " +  LOGO_IMAGE + " TEXT);";
    public static final String LOGO_TABLE_DROP = "DROP TABLE IF EXISTS " + LOGO_TABLE_NAME + ";";

    public static final String FEATURELOGO_ID = "id";
    public static final String FEATURELOGO_X = "x";
    public static final String FEATURELOGO_Y = "y";
    public static final String FEATURELOGO_SCALE = "scale";
    public static final String FEATURELOGO_ORIENTATION = "orientation";
    public static final String FEATURELOGO_LOGO = "idLogo";
    public static final String FEATURELOGO_TABLE_NAME = "FeatureLogo";

    public static final String FEATURELOGO_TABLE_CREATE = "CREATE TABLE " + FEATURELOGO_TABLE_NAME + "(" + FEATURELOGO_ID + "INTEGER PRIMARY KEY AUTOINCREMENT, " + FEATURELOGO_X + " REAL, " +  FEATURELOGO_Y + " REAL, " +  FEATURELOGO_SCALE + " REAL, " +  FEATURELOGO_ORIENTATION + " REAL, " +  FEATURELOGO_LOGO + " INTEGER);";
    public static final String FEATURELOGO_TABLE_DROP = "DROP TABLE IF EXISTS " + FEATURELOGO_TABLE_NAME + ";";

    public DatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version)
    {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(LOGO_TABLE_CREATE);
        db.execSQL(FEATURELOGO_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL(LOGO_TABLE_DROP);
        db.execSQL(FEATURELOGO_TABLE_DROP);
        onCreate(db);
    }
}
