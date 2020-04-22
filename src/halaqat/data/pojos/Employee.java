package halaqat.data.pojos;

public class Employee {
    public static final int STATE_ON_WORK = 0;
    public static final int STATE_IN_VACATION = 1;
    public static final int STATE_RETIRED = 2;
    public static final int STATE_FIRED = 3;

    public static final int TYPE_TEACHER = 0;
    public static final int TYPE_ADMINISTRATOR = 1;

    private String nationalID;
    private String firstName;
    private String middleName;
    private String lastName;
    private int jobTitle;
    private String address;
    private String qualification;
    private int employeeState;
    private String nationality;
    private String mobileNo;
    private String registrationDate;
    private String birthDay;

    private String password;


    public Employee(String nationalID, String firstName, String middleName, String lastName, int jobTitle, String address, String qualification, int employeeState, String nationality, String mobileNo, String registrationDate, String birthDay) {
        this.nationalID = nationalID;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.jobTitle = jobTitle;
        this.address = address;
        this.qualification = qualification;
        this.employeeState = employeeState;
        this.nationality = nationality;
        this.mobileNo = mobileNo;
        this.registrationDate = registrationDate;
        this.birthDay = birthDay;
    }


    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(String birthDay) {
        this.birthDay = birthDay;
    }

    public String getNationalID() {
        return nationalID;
    }

    public void setNationalID(String nationalID) {
        this.nationalID = nationalID;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public int getEmployeeState() {
        return employeeState;
    }

    public void setEmployeeState(int employeeState) {
        this.employeeState = employeeState;
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

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public int getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(int jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}