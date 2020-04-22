package halaqat.data.pojos;

public class Semester {

    int id;
    String name;
    String startingDate;
    String endingDate;

    public Semester(int id, String name, String startingDate, String endingDate) {
        this.id = id;
        this.name = name;
        this.startingDate = startingDate;
        this.endingDate = endingDate;
    }

    public Semester(String name, String startingDate, String endingDate) {
        this.name = name;
        this.startingDate = startingDate;
        this.endingDate = endingDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartingDate() {
        return startingDate;
    }

    public void setStartingDate(String startingDate) {
        this.startingDate = startingDate;
    }

    public String getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(String endingDate) {
        this.endingDate = endingDate;
    }
}
