package halaqat.data.pojos;

public class Attendance {
    private String studentNationalId;
    private String date;
    private boolean present;

    public Attendance(String studentNationalId, String date, boolean present) {
        this.studentNationalId = studentNationalId;
        this.date = date;
        this.present = present;
    }

    public String getStudentNationalId() {
        return studentNationalId;
    }

    public void setStudentNationalId(String studentNationalId) {
        this.studentNationalId = studentNationalId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }
}
