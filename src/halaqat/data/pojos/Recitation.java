package halaqat.data.pojos;

public class Recitation {
    public static final int TYPE_NEW = 0;
    public static final int TYPE_REVISION = 1;

    public static final int GRADE_EXCELLENT = 3;
    public static final int GRADE_VERY_GOOD = 2;
    public static final int GRADE_GOOD = 1;
    public static final int GRADE_NOT_MEMORIZED = 0;


    private int grade;
    private String date;
    private int type;
    private int startAyah;
    private int endAyah;
    private String startSorah;
    private String endSorah;

    private String studentId;

    public Recitation(int grade, String date, int type, int startAyah, int endAyah, String startSorah, String endSorah, String studentId) {
        this.grade = grade;
        this.date = date;
        this.type = type;
        this.startAyah = startAyah;
        this.endAyah = endAyah;
        this.startSorah = startSorah;
        this.endSorah = endSorah;
        this.studentId = studentId;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setgrade(int n) {
        grade = n;
    }

    public void settype(int n) {
        type = n;
    }

    public void setstartAyah(int n) {
        startAyah = n;
    }

    public void setendAyah(int n) {
        endAyah = n;
    }

    public void setstartSorah(String n) {
        startSorah = n;
    }

    public void setendSorah(String n) {
        endSorah = n;
    }

    public int getgrade() {
        return grade;
    }

    public int gettype() {
        return type;
    }

    public int getstartAyah() {
        return startAyah;
    }

    public int getendAyah() {
        return endAyah;
    }

    public String getstartSorah() {
        return startSorah;
    }

    public String getendSorah() {
        return endSorah;
    }

    public String gradeText() {
        switch (grade) {
            case GRADE_EXCELLENT:
                return "ممتاز";
            case GRADE_VERY_GOOD:
                return "جيد جداً";
            case GRADE_GOOD:
                return "جيد";
            case GRADE_NOT_MEMORIZED:
                return "لم يحفظ";
        }
        return "";
    }

}