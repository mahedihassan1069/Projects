package e.user.madiambu;

public class HospitalInfo {

    public String hname;
    public String hemail;
    public String hmobile;

    public Double hlat;
    public Double hlong;

    public HospitalInfo()
    {

    }

    public HospitalInfo(String hname, String hemail, String hmobile, Double hlat, Double hlong) {
        this.hname = hname;
        this.hemail = hemail;
        this.hmobile = hmobile;
        this.hlat = hlat;
        this.hlong = hlong;
    }

    public String getHname() {
        return hname;
    }

    public void setHname(String hname) {
        this.hname = hname;
    }

    public String getHemail() {
        return hemail;
    }

    public void setHemail(String hemail) {
        this.hemail = hemail;
    }

    public String getHmobile() {
        return hmobile;
    }

    public void setHmobile(String hmobile) {
        this.hmobile = hmobile;
    }

    public Double getHlat() {
        return hlat;
    }

    public void setHlat(Double hlat) {
        this.hlat = hlat;
    }

    public Double getHlong() {
        return hlong;
    }

    public void setHlong(Double hlong) {
        this.hlong = hlong;
    }
}
