package ch.hearc.viewlogo.tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import org.w3c.dom.Comment;

import java.util.ArrayList;
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

    Context pContext;

    public LogoDAO(Context pContext) {
        super(pContext);
        this.pContext = pContext;
    }

    /**
     * @param logo le logo à ajouter à la base
     */
    public void ajouter(Logo logo)
    {
        ContentValues value = new ContentValues();
        value.put(TITLE, logo.getTitle());
        value.put(IMAGE, logo.getImage());

        long idLogo = mDb.insert(TABLE_NAME, null, value);
        FeatureLogo fl;
        FeatureLogoDAO flDAO = new FeatureLogoDAO(pContext);

        for(int i=0; i < 100; i++)
        {
            fl = logo.getListFeatureLogo().get(i);
            fl.setIdLogo(idLogo);
            flDAO.ajouter(fl);
        }
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

        mDb.update(TABLE_NAME, value, ID + " = ?", new String[]{String.valueOf(l.getId())});
    }

    /**
     * @param id l'identifiant du logo à récupérer
     */
    public Logo selectionner(long id)
    {
        Cursor c = mDb.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE "+ ID + " = ?", new String[]{String.valueOf(id)});

        Logo l = cursorToLogo(c);
        return l;
    }

    public List<Logo> getAllLogos()
    {
        Cursor c = mDb.rawQuery("SELECT * FROM " + TABLE_NAME, new String[]{});

        List<Logo> logos = new ArrayList<Logo>();

        c.moveToFirst();

        while (!c.isAfterLast()) {
            Logo logo = cursorToLogo(c);
            logos.add(logo);
            c.moveToNext();
        }
        // assurez-vous de la fermeture du curseur
        c.close();
        return logos;
    }

    private Logo cursorToLogo(Cursor c)
    {
        Logo l = new Logo();
        l.setId(c.getLong(0));
        l.setImage(c.getString(1));
        l.setTitle(c.getString(2));

        return l;
    }
}
