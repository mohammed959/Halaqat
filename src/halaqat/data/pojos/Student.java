package halaqat.data.pojos;

public class Student {
    public static final int STATE_ENROLLED = 0;
    public static final int STATE_NOT_ENROLLED = 1;

    private String nationalID;
    private String firstName;
    private String middleName;
    private String lastName;
    private String birthDate;
    private String registrationDate;
    private int state;
    private int schoollvl;
    private String halaqaName;
    private String parentId;
    private String password;

    private Parent parent;

    public static final String[] SCHOOL_LEVELS =
            {"أول إبتدائي"
                    , "ثاني إبتدائي"
                    , "ثالث إبتدائي"
                    , "رابع إبتدائي"
                    , "خامس إبتدائي"
                    , "سادس إبتدائي"
                    , "أول متوسط"
                    , "ثاني متوسط"
                    , "ثالث متوسط"
                    , "أول ثانوي"
                    , "ثاني ثانوي"
                    , "ثالث ثانوي"};


    public Student(String nationalID, String firstName, String middleName, String lastName, String birthDate, String registrationDate, int state, int schoollvl, String halaqaName, String parentId) {
        this.nationalID = nationalID;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.registrationDate = registrationDate;
        this.state = state;
        this.schoollvl = schoollvl;
        this.halaqaName = halaqaName;
        this.parentId = parentId;
    }

    public Student(String nationalID, String firstName, String middleName, String lastName, String birthDate, int schoollvl, String halaqaName, String parentId) {
        this.nationalID = nationalID;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.schoollvl = schoollvl;
        this.halaqaName = halaqaName;
        this.parentId = parentId;
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

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Parent getParent() {
        return parent;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }

    public String getHalaqaName() {
        return halaqaName;
    }

    public void setHalaqaName(String halaqaName) {
        this.halaqaName = halaqaName;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public void setFirstName(String n) {
        firstName = n;
    }

    public void setNationalID(String n) {
        nationalID = n;
    }

    public void setState(int n) {
        state = n;
    }

    public void setSchoollvl(int n) {
        schoollvl = n;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getNationalID() {
        return nationalID;
    }

    public int getState() {
        return state;
    }

    public int getSchoollvl() {
        return schoollvl;
    }

    public String getSchoolLevelName() {
        return SCHOOL_LEVELS[schoollvl - 1];
    }
} 