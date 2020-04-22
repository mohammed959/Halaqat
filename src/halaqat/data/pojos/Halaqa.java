package halaqat.data.pojos;

public class Halaqa {
    private int numberOfNewLine;
    private int numberOfReviewLine;
    private String name;

    private String teacherId;

    private Employee teacher;

    public Halaqa(int numberOfNewLine, int numberOfReviewLine, String name, String teacherId) {
        this.numberOfNewLine = numberOfNewLine;
        this.numberOfReviewLine = numberOfReviewLine;
        this.name = name;
        this.teacherId = teacherId;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public Employee getTeacher() {
        return teacher;
    }

    public void setTeacher(Employee teacher) {
        this.teacher = teacher;
    }

    public void setnumberOfNewLine(int n) {
        numberOfNewLine = n;
    }

    public void setnumberOfReviewLine(int n) {
        numberOfReviewLine = n;
    }

    public void setname(String n) {
        name = n;
    }

    public int getnumberOfNewLine() {
        return numberOfNewLine;
    }

    public int getnumberOfReviewLine() {
        return numberOfReviewLine;
    }

    public String getname() {
        return name;
    }

}