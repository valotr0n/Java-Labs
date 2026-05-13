package lab6.model;

public class Stage {
    private long id;
    private String name;
    private String startsAt;

    public Stage() {
    }

    public Stage(long id, String name, String startsAt) {
        this.id = id;
        this.name = name;
        this.startsAt = startsAt;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartsAt() {
        return startsAt;
    }

    public void setStartsAt(String startsAt) {
        this.startsAt = startsAt;
    }
}