package e.user.madiambu.Model;

public class Data {
    public String title;
    public String body;
    public String type;

    public String riderToken;
    public double longitude;
    public double altitude;

    public Data(String title, String body,String type) {
        this.title = title;
        this.body = body;
        this.type=type;
    }

    public String getRiderToken() {
        return riderToken;
    }

    public void setRiderToken(String riderToken) {
        this.riderToken = riderToken;
    }


    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public Data(String riderToken, double longitude, double altitude,String type) {
        this.riderToken = riderToken;
        this.longitude = longitude;
        this.altitude = altitude;
        this.type=type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
