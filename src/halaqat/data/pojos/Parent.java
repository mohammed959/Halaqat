package halaqat.data.pojos;

public class Parent
{
    private String nationalID;
    private String firstName;
    private String middleName;
    private String lastName;
    private String mobileN;
    private String nationality;
    private String address;

    private String password;

    public Parent(String nationalID, String firstName, String middleName, String lastName, String mobileN, String nationality, String address) {
        this.nationalID = nationalID;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.mobileN = mobileN;
        this.nationality = nationality;
        this.address = address;
    }

    public String getNationalID() {
        return nationalID;
    }

    public void setNationalID(String nationalID) {
        this.nationalID = nationalID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMobileN() {
        return mobileN;
    }

    public void setMobileN(String mobileN) {
        this.mobileN = mobileN;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}