package ch.hearc.viewlogo.tools;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leonardo.distasio on 01.04.2016.
 */
public class Logo implements Parcelable
{
    private String title;
    private String image;
    private List<FeatureLogo> listFeatureLogo;

    public Logo()
    {
        this.title = "";
        this.image = "";
        this.listFeatureLogo = new ArrayList<FeatureLogo>();
    }

    public Logo(String _title, String _image)
    {
        this.title = _title;
        this.image = _image;
        this.listFeatureLogo = new ArrayList<FeatureLogo>();
    }

    public Logo(Parcel in)
    {
        this.title = in.readString();
        this.image = in.readString();
        this.listFeatureLogo = new ArrayList<FeatureLogo>();
        in.readList(this.listFeatureLogo, FeatureLogo.class.getClassLoader());
    }

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
