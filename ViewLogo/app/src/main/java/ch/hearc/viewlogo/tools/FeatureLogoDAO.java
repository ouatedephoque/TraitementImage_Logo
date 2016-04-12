package ch.hearc.viewlogo.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jeshon.assuncao on 05.04.2016.
 */
public class FeatureLogoDAO extends DAOBase{
    public static final String TABLE_NAME = DatabaseHandler.FEATURELOGO_TABLE_NAME;
    public static final String ID = DatabaseHandler.FEATURELOGO_ID;
    public static final String X = DatabaseHandler.FEATURELOGO_X;
    public static final String Y = DatabaseHandler.FEATURELOGO_Y;
    public static final String SCALE = DatabaseHandler.FEATURELOGO_SCALE;
    public static final String ORIENTATION = DatabaseHandler.FEATURELOGO_ORIENTATION;
    public static final String LOGO = DatabaseHandler.FEATURELOGO_LOGO;

    public static final String TABLE_CREATE = DatabaseHandler.FEATURELOGO_TABLE_CREATE;
    public static final String TABLE_DROP =  DatabaseHandler.FEATURELOGO_TABLE_DROP;

    public FeatureLogoDAO(Context pContext) {
        super(pContext);
    }

    /**
     * @param fl le FeatureLogo à ajouter à la base
     */
    public void ajouter(FeatureLogo fl)
    {
        ContentValues value = new ContentValues();
        value.put(X, fl.getX());
        value.put(Y, fl.getY());
        value.put(SCALE, fl.getScale());
        value.put(ORIENTATION, fl.getOrientation());
        value.put(LOGO, fl.getIdLogo());

        open();
        mDb.insert(TABLE_NAME, null, value);
        close();
    }

    /**
     * @param id l'identifiant du FeatureLogo à supprimer
     */
    public void supprimer(long id)
    {
        mDb.delete(TABLE_NAME, ID + " = ?", new String[]{String.valueOf(id)});
    }

    /**
     * @param fl le FeatureLogo modifié
     */
    public void modifier(FeatureLogo fl)
    {
        ContentValues value = new ContentValues();
        value.put(X, fl.getX());
        value.put(Y, fl.getY());
        value.put(SCALE, fl.getScale());
        value.put(ORIENTATION, fl.getOrientation());
        value.put(LOGO, fl.getIdLogo());

        mDb.update(TABLE_NAME, value, ID + " = ?", new String[]{String.valueOf(fl.getId())});
    }

    /**
     * @param id l'identifiant du FeatureLogo à récupérer
     */
    public FeatureLogo selectionner(long id)
    {
        Cursor c = mDb.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE "+ ID + " = ?", new String[]{String.valueOf(id)});

        FeatureLogo fl = new FeatureLogo();
        fl.setId(c.getLong(0));
        fl.setX(c.getFloat(1));
        fl.setY(c.getFloat(2));
        fl.setScale(c.getFloat(3));
        fl.setOrientation(c.getFloat(4));
        fl.setIdLogo(c.getLong(5));

        return fl;
    }

    public List<FeatureLogo> selectAllFromLogo(long idLogo)
    {
        open();
        Cursor c = mDb.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE "+ LOGO + " = ?", new String[]{String.valueOf(idLogo)});

        List<FeatureLogo> list = new ArrayList<>();
        FeatureLogo fl = new FeatureLogo();

        if(c.moveToFirst())
        {
            while (!c.isAfterLast()) {
                fl.setId(c.getLong(0));
                fl.setX(c.getFloat(1));
                fl.setY(c.getFloat(2));
                fl.setScale(c.getFloat(3));
                fl.setOrientation(c.getFloat(4));
                fl.setIdLogo(idLogo);

                list.add(fl);
                c.moveToNext();
            }
        }

        close();

        return list;
    }
}
