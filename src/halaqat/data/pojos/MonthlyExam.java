package halaqat.data.pojos;

public class MonthlyExam {

    int semesterId;
    String nationalId;
    int examNumber;

    int grade;

    public MonthlyExam(int semesterId, String nationalId, int examNumber, int grade) {
        this.semesterId = semesterId;
        this.nationalId = nationalId;
        this.examNumber = examNumber;
        this.grade = grade;
    }

    public int getSemesterId() {
        return semesterId;
    }

    public void setSemesterId(int semesterId) {
        this.semesterId = semesterId;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public int getExamNumber() {
        return examNumber;
    }

    public void setExamNumber(int examNumber) {
        this.examNumber = examNumber;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }
}
