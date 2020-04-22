package halaqat.data.pojos;

public class SemesterGrades {

    private int semesterId;
    private int finalG;
    private int attendance;
    private int behavior;

    private String studentId;

    public SemesterGrades(int semesterId, int finalG, int attendance, int behavior, String studentId) {
        this.semesterId = semesterId;
        this.finalG = finalG;
        this.attendance = attendance;
        this.behavior = behavior;
        this.studentId = studentId;
    }

    public int getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(int semesterId) {
        this.semesterId = semesterId;
    }

    public int getFinalG() {
        return finalG;
    }

    public void setFinalG(int finalG) {
        this.finalG = finalG;
    }

    public int getAttendance() {
        return attendance;
    }

    public void setAttendance(int attendance) {
        this.attendance = attendance;
    }

    public int getBehavior() {
        return behavior;
    }

    public void setBehavior(int behavior) {
        this.behavior = behavior;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }
}