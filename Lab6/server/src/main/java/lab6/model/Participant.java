package lab6.model;

public class Participant {
    private long id;
    private String name;
    private String email;
    private String stageName;
    private String result;

    public Participant() {
    }

    public Participant(long id, String name, String email, String stageName, String result) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.stageName = stageName;
        this.result = result;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}