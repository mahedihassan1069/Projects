package e.user.madiambu;

public class AddDriver {
    String DriverId;
    String DriverName;
    String DriverCode;
    String DriverNumber;

    public AddDriver()
    {

    }

    public AddDriver(String driverId, String driverName, String driverCode, String driverNumber) {
        DriverId = driverId;
        DriverName = driverName;
        DriverCode = driverCode;
        DriverNumber = driverNumber;
    }

    public String getDriverId() {
        return DriverId;
    }

    public void setDriverId(String driverId) {
        DriverId = driverId;
    }

    public String getDriverName() {
        return DriverName;
    }

    public void setDriverName(String driverName) {
        DriverName = driverName;
    }

    public String getDriverCode() {
        return DriverCode;
    }

    public void setDriverCode(String driverCode) {
        DriverCode = driverCode;
    }

    public String getDriverNumber() {
        return DriverNumber;
    }

    public void setDriverNumber(String driverNumber) {
        DriverNumber = driverNumber;
    }
}


