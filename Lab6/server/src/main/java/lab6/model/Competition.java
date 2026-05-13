package lab6.model;

import java.util.ArrayList;
import java.util.List;

public class Competition {
    private long id;
    private String title;
    private String eventDateTime;
    private String organizers;
    private String extraInfo;
    private List<Stage> stages = new ArrayList<>();
    private List<Participant> participants = new ArrayList<>();

    public Competition() {
    }

    public Competition(long id, String title, String eventDateTime, String organizers, String extraInfo) {
        this.id = id;
        this.title = title;
        this.eventDateTime = eventDateTime;
        this.organizers = organizers;
        this.extraInfo = extraInfo;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getEventDateTime() {
        return eventDateTime;
    }

    public void setEventDateTime(String eventDateTime) {
        this.eventDateTime = eventDateTime;
    }

    public String getOrganizers() {
        return organizers;
    }

    public void setOrganizers(String organizers) {
        this.organizers = organizers;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public List<Stage> getStages() {
        return stages;
    }

    public void setStages(List<Stage> stages) {
        this.stages = stages == null ? new ArrayList<>() : stages;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants == null ? new ArrayList<>() : participants;
    }
}