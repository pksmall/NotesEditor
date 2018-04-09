package gb.pavelkorzhenko.a2l1menuapp;

import android.annotation.SuppressLint;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by small on 11/30/2017.
 */

public class NoteLists implements Serializable {
    @SuppressLint("SimpleDateFormat")
    public static SimpleDateFormat sdf = new SimpleDateFormat("EEE, MMM d, ''yy HH:mm:ss");
    private long id;
    private long hashId;
    private String txtTitle;
    private String txtBody;
    private String txtGeoBody;
    private String txtPubDate;
    private String txtUpdDate;

    public NoteLists() {

    }

    public NoteLists(String title, String body, String geoBody) {
        this.hashId = body.hashCode() + title.hashCode();
        this.txtTitle = title;
        this.txtBody = body;
        this.txtGeoBody = geoBody;
        this.txtPubDate = sdf.format(new Date());
        this.txtUpdDate = sdf.format(new Date());
    }


    public long getHashId() {
        return hashId;
    }

    public void setHashId() {
        this.hashId =  this.txtBody.hashCode() + this.txtTitle.hashCode();
    }

    public void setHashId(long hashID) {
        this.hashId =  hashID;
    }

    public String getTxtTitle() {
        return txtTitle;
    }

    public void setTxtTitle(String txtTitle) {
        this.txtTitle = txtTitle;
    }

    public String getTxtBody() {
        return txtBody;
    }

    public void setTxtBody(String txtBody) {
        this.txtBody = txtBody;
    }

    public String getTxtGeoBody() { return txtGeoBody;  }

    public void setTxtGeoBody(String txtGeoBody) { this.txtGeoBody = txtGeoBody; }

    public String getTxtPubDate() { return txtPubDate;  }

    public void setTxtPubDate(Date txtPubDate) { this.txtPubDate = sdf.format(txtPubDate);  }
    public void setTxtPubDate(String txtPubDate) { this.txtPubDate = txtPubDate;  }

    public String getTxtUpdDate() {return txtUpdDate;    }

    public void setTxtUpdDate(Date txtUpdDate) {        this.txtUpdDate = sdf.format(txtUpdDate);    }
    public void setTxtUpdDate(String txtUpdDate) {        this.txtUpdDate = txtUpdDate;    }

    public long getId() {return id;    }

    public void setId(long id) { this.id = id; }

}
