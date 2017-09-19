package rottiedog.com.mysport;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by dehuffm on 5/26/2017.
 */

public class Sports implements Parcelable {

    int id = -1;
    String name = null;
    int icon = -1;
    int picture = -1;
    boolean selected = false;

    public Sports(int id, String name, int icon, int picture, boolean selected) {
        super();
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.picture = picture;
        this.selected = selected;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getIcon() {
        return icon;
    }
    public void setIcon(int icon) {
        this.icon = icon;
    }
    public int getPicture() {return picture;}
    public void setPicture(int picture) {this.picture = picture;}
    public boolean isSelected() {
        return selected;
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    public Sports(Parcel in){
        this.id = in.readInt();
        this.name = in.readString();
        this.icon = in.readInt();
        this.picture = in.readInt();
        this.selected = (Boolean) in.readValue( null );
    }

    public int describeContents(){
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.icon);
        dest.writeInt(this.picture);
        dest.writeValue( this.selected );
     }
    public static final Creator<Sports> CREATOR
            = new Creator<Sports>() {
        public Sports createFromParcel(Parcel in) {
            return new Sports(in);
        }

        public Sports[] newArray(int size) {
            return new Sports[size];
        }
    };
}