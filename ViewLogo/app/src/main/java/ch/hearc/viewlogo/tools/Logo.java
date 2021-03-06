package ch.hearc.viewlogo.tools;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import mpi.cbg.fly.Feature;

/**
 * Created by leonardo.distasio on 01.04.2016.
 */
public class Logo implements Parcelable
{
    private long id;
    private String title;
    private String image;
    private List<FeatureLogo> listFeatureLogo;
    private List<Feature> listFeatureSift;

    public Logo()
    {
        this.id = -1;
        this.title = "";
        this.image = "";
        this.listFeatureLogo = new ArrayList<FeatureLogo>();
        this.listFeatureSift = new ArrayList<Feature>();

        createListFeature();
    }

    public Logo(String _title, String _image)
    {
        this.id = -1;
        this.title = _title;
        this.image = _image;
        this.listFeatureLogo = new ArrayList<FeatureLogo>();
        this.listFeatureSift = new ArrayList<Feature>();

        createListFeature();
    }

    public Logo(long _id, String _title, String _image)
    {
        this.id = _id;
        this.title = _title;
        this.image = _image;
        this.listFeatureLogo = new ArrayList<FeatureLogo>();
        this.listFeatureSift = new ArrayList<Feature>();

        createListFeature();
    }

    public Logo(Parcel in)
    {
        this.id = in.readLong();
        this.title = in.readString();
        this.image = in.readString();
        this.listFeatureLogo = new ArrayList<FeatureLogo>();
        in.readList(this.listFeatureLogo, FeatureLogo.class.getClassLoader());

        createListFeature();
    }
    public long getId(){ return id; }

    public void setId(long id){ this.id = id; }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<FeatureLogo> getListFeatureLogo()
    {
        if(listFeatureLogo == null) this.listFeatureLogo = new ArrayList<FeatureLogo>();
        return listFeatureLogo;
    }

    public void setListFeatureLogo(List<FeatureLogo> listFeatureLogo)
    {
        for (FeatureLogo fl: listFeatureLogo)
        {
            this.listFeatureLogo.add(fl);
        }
    }

    private void createListFeature()
    {
        for(FeatureLogo fl: listFeatureLogo)
        {
            float[] location = {fl.getX(), fl.getY()};
            this.listFeatureSift.add(new Feature(fl.getScale(), fl.getOrientation(), location, fl.getDescriptionFloat()));
        }
        Log.i("Taille Feature : ", this.listFeatureSift.size()+"");
    }

    public List<Feature> getListFeatureSift()
    {
        if(listFeatureSift.size() <= 0)
        {
            createListFeature();
        }
        return listFeatureSift;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
        dest.writeString(title);
        dest.writeString(image);
        dest.writeList(listFeatureLogo);
    }

    public static final Creator<Logo> CREATOR = new Creator<Logo>()
    {
        public Logo createFromParcel(Parcel in) {
            return new Logo(in);
        }

        public Logo[] newArray(int size) {
            return new Logo[size];
        }
    };

    public void addFeatureLogo(FeatureLogo fl)
    {
        this.listFeatureLogo.add(fl);
    }

    public void addAllFeature(List<FeatureLogo> listFL)
    {
        if(listFL.size() > 0)
        {
            if(this.listFeatureLogo == null) this.listFeatureLogo = new ArrayList<FeatureLogo>();
            this.listFeatureLogo.addAll(listFL);
        }
    }
}
