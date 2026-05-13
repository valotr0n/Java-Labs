package lab6.client;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import lab6.model.Competition;
import lab6.model.Participant;
import lab6.model.Stage;
import org.glassfish.jersey.jsonb.JsonBindingFeature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CompetitionApiClient implements AutoCloseable {
    private final Client client;
    private final WebTarget competitions;

    public CompetitionApiClient(String apiBaseUri) {
        this.client = ClientBuilder.newClient().register(JsonBindingFeature.class);
        this.competitions = client.target(apiBaseUri).path("competitions");
    }

    public List<Competition> getCompetitions() {
        Competition[] result = competitions.request(MediaType.APPLICATION_JSON_TYPE).get(Competition[].class);
        if (result == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(result));
    }

    public Competition getCompetition(long id) {
        return competitions.path(Long.toString(id))
                .request(MediaType.APPLICATION_JSON_TYPE)
                .get(Competition.class);
    }

    public Competition createCompetition(Competition competition) {
        return competitions.request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(competition, MediaType.APPLICATION_JSON_TYPE), Competition.class);
    }

    public Competition updateCompetition(long id, Competition competition) {
        return competitions.path(Long.toString(id))
                .request(MediaType.APPLICATION_JSON_TYPE)
                .put(Entity.entity(competition, MediaType.APPLICATION_JSON_TYPE), Competition.class);
    }

    public void deleteCompetition(long id) {
        competitions.path(Long.toString(id)).request().delete().close();
    }

    public Stage addStage(long competitionId, Stage stage) {
        return competitions.path(Long.toString(competitionId)).path("stages")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(stage, MediaType.APPLICATION_JSON_TYPE), Stage.class);
    }

    public Participant addParticipant(long competitionId, Participant participant) {
        return competitions.path(Long.toString(competitionId)).path("participants")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(participant, MediaType.APPLICATION_JSON_TYPE), Participant.class);
    }

    @Override
    public void close() {
        client.close();
    }
}