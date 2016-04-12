package ch.hearc.viewlogo.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.List;

/**
 * Created by jeshon.assuncao on 05.04.2016.
 */
public class LogoDAO extends DAOBase{
    public static final String TABLE_NAME = DatabaseHandler.LOGO_TABLE_NAME;
    public static final String ID = DatabaseHandler.LOGO_ID;
    public static final String TITLE = DatabaseHandler.LOGO_TITLE;
    public static final String IMAGE = DatabaseHandler.LOGO_IMAGE;

    public static final String TABLE_CREATE = DatabaseHandler.LOGO_TABLE_CREATE;
    public static final String TABLE_DROP =  DatabaseHandler.LOGO_TABLE_DROP;

    private Context pContext;

    public LogoDAO(Context pContext)
    {
        super(pContext);
        this.pContext = pContext;
    }

    /**
     * @param l le logo à ajouter à la base
     */
    public void ajouter(Logo l)
    {
        ContentValues value = new ContentValues();
        value.put(TITLE, l.getTitle());
        value.put(IMAGE, l.getImage());

        mDb.insert(TABLE_NAME, null, value);
    }

    /**
     * @param id l'identifiant du logo à supprimer
     */
    public void supprimer(long id)
    {
        mDb.delete(TABLE_NAME, ID + " = ?", new String[] {String.valueOf(id)});
    }

    /**
     * @param l le logo modifié
     */
    public void modifier(Logo l)
    {
        ContentValues value = new ContentValues();
        value.put(TITLE, l.getTitle());
        value.put(IMAGE, l.getImage());

        mDb.update(TABLE_NAME, value, ID  + " = ?", new String[] {String.valueOf(l.getId())});
    }

    /**
     * @param id l'identifiant du logo à récupérer
     */
    public Logo selectionner(long id)
    {
        Cursor c = mDb.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE "+ ID + " = ?", new String[]{String.valueOf(id)});

        Logo l = new Logo();
        l.setId(c.getLong(0));
        l.setImage(c.getString(1));
        l.setTitle(c.getString(2));

        FeatureLogoDAO featureLogoDAO = new FeatureLogoDAO(pContext);
        List<FeatureLogo> list = featureLogoDAO.selectAllFromLogo(l.getId());

        l.setListFeatureLogo(list);

        return l;
    }
}
