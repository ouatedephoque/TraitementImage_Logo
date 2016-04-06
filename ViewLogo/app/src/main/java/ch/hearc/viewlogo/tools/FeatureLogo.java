package ch.hearc.viewlogo.tools;

import android.os.Parcel;
import android.os.Parcelable;

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
    private long idLogo;

    public FeatureLogo(long _id, float _x, float _y, float _scale, float _orientation, long _idLogo)
    {
        this.id = _id;
        this.x = _x;
        this.y = _y;
        this.scale = _scale;
        this.orientation = _orientation;
        this.idLogo = _idLogo;
    }

    public FeatureLogo()
    {
        this.id = -1;
        this.x = 0.0f;
        this.y = 0.0f;
        this.scale = 0.0f;
        this.orientation = 0.0f;
        this.idLogo = -1;
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
