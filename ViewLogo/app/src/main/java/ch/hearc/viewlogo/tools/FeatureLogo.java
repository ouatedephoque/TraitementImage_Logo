package ch.hearc.viewlogo.tools;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by leonardo.distasio on 01.04.2016.
 */
public class FeatureLogo implements Parcelable
{
    private long id;
    private float x;
    private float y;
    private float scale;
    private float orientation;
    private float[] description;
    private long idLogo;

    public FeatureLogo(long _id, float _x, float _y, float _scale, float _orientation, float[] description, long _idLogo)
    {
        this.id = _id;
        this.x = _x;
        this.y = _y;
        this.scale = _scale;
        this.orientation = _orientation;
        this.description = description;
        this.idLogo = _idLogo;
    }

    public FeatureLogo(float _x, float _y, float _scale, float _orientation, float[] _description, long _idLogo)
    {
        this(-1, _x, _y, _scale, _orientation, _description,  _idLogo);
    }

    public FeatureLogo(float _x, float _y, float _scale, float _orientation, float[] _description)
    {
        this(-1, _x, _y, _scale, _orientation, _description, -1);
    }

    public FeatureLogo()
    {
        this(-1, 0.0f, 0.0f, 0.0f, null, -1);
        float[] description;
    }

    public long getId(){ return id;}

    public void setId(long id){ this.id = id; }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public float getOrientation() {
        return orientation;
    }

    public void setOrientation(float orientation) {
        this.orientation = orientation;
    }

    public long getIdLogo(){ return idLogo;}

    public void setIdLogo(long idLogo){ this.idLogo = idLogo; }

    public String getDescription()
    {
        String chaine = "";

        for(int i = 0; i < description.length; i++)
        {
            chaine = chaine+";"+description[i];
        }
        return chaine;
    }

    public float[] getDescriptionFloat()
    {
        return this.description;
    }

    public void setDescription(String _description)
    {
        String[] strArray = _description.split(";");
        description = new float[strArray.length];

        for (int i = 0; i < strArray.length; i++)
        {
            if(!strArray[i].isEmpty())
            {
                description[i] = Float.parseFloat(strArray[i]);
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FeatureLogo> CREATOR = new Creator<FeatureLogo>()
    {
        public FeatureLogo createFromParcel(Parcel in) {
            return new FeatureLogo(in);
        }

        public FeatureLogo[] newArray(int size) {
            return new FeatureLogo[size];
        }
    };

    public FeatureLogo(Parcel in)
    {
        this.x = in.readFloat();
        this.y = in.readFloat();
        this.scale = in.readFloat();
        this.orientation = in.readFloat();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeFloat(x);
        dest.writeFloat(y);
        dest.writeFloat(scale);
        dest.writeFloat(orientation);
    }
}
