package lab6.rest;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import lab6.model.Competition;
import lab6.model.Participant;
import lab6.model.Stage;
import lab6.store.CompetitionStore;

import java.util.Map;

@Path("/competitions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CompetitionResource {
    private final CompetitionStore store = CompetitionStore.getInstance();

    @GET
    public Response all() {
        return Response.ok(store.all()).build();
    }

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") long id) {
        return store.find(id)
                .map(competition -> Response.ok(competition).build())
                .orElseGet(() -> notFound("Соревнование не найдено"));
    }

    @POST
    public Response create(Competition competition, @Context UriInfo uriInfo) {
        try {
            Competition created = store.create(competition);
            return Response.created(uriInfo.getAbsolutePathBuilder().path(Long.toString(created.getId())).build())
                    .entity(created)
                    .build();
        } catch (IllegalArgumentException ex) {
            return badRequest(ex);
        }
    }

    @PUT
    @Path("/{id}")
    public Response update(@PathParam("id") long id, Competition competition) {
        try {
            return store.update(id, competition)
                    .map(updated -> Response.ok(updated).build())
                    .orElseGet(() -> notFound("Соревнование не найдено"));
        } catch (IllegalArgumentException ex) {
            return badRequest(ex);
        }
    }

    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") long id) {
        if (store.delete(id)) {
            return Response.noContent().build();
        }
        return notFound("Соревнование не найдено");
    }

    @POST
    @Path("/{id}/stages")
    public Response addStage(@PathParam("id") long id, Stage stage) {
        try {
            return store.addStage(id, stage)
                    .map(created -> Response.status(Response.Status.CREATED).entity(created).build())
                    .orElseGet(() -> notFound("Соревнование не найдено"));
        } catch (IllegalArgumentException ex) {
            return badRequest(ex);
        }
    }

    @PUT
    @Path("/{id}/stages/{stageId}")
    public Response updateStage(@PathParam("id") long id,
                                @PathParam("stageId") long stageId,
                                Stage stage) {
        try {
            return store.updateStage(id, stageId, stage)
                    .map(updated -> Response.ok(updated).build())
                    .orElseGet(() -> notFound("Соревнование или этап не найден"));
        } catch (IllegalArgumentException ex) {
            return badRequest(ex);
        }
    }

    @DELETE
    @Path("/{id}/stages/{stageId}")
    public Response deleteStage(@PathParam("id") long id, @PathParam("stageId") long stageId) {
        if (store.deleteStage(id, stageId)) {
            return Response.noContent().build();
        }
        return notFound("Соревнование или этап не найден");
    }

    @POST
    @Path("/{id}/participants")
    public Response addParticipant(@PathParam("id") long id, Participant participant) {
        try {
            return store.addParticipant(id, participant)
                    .map(saved -> Response.status(Response.Status.CREATED).entity(saved).build())
                    .orElseGet(() -> notFound("Соревнование не найдено"));
        } catch (IllegalArgumentException ex) {
            return badRequest(ex);
        }
    }

    @PUT
    @Path("/{id}/participants/{participantId}")
    public Response updateParticipant(@PathParam("id") long id,
                                      @PathParam("participantId") long participantId,
                                      Participant participant) {
        try {
            return store.updateParticipant(id, participantId, participant)
                    .map(updated -> Response.ok(updated).build())
                    .orElseGet(() -> notFound("Соревнование или участник не найден"));
        } catch (IllegalArgumentException ex) {
            return badRequest(ex);
        }
    }

    @DELETE
    @Path("/{id}/participants/{participantId}")
    public Response deleteParticipant(@PathParam("id") long id,
                                      @PathParam("participantId") long participantId) {
        if (store.deleteParticipant(id, participantId)) {
            return Response.noContent().build();
        }
        return notFound("Соревнование или участник не найден");
    }

    private static Response badRequest(IllegalArgumentException ex) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(Map.of("error", ex.getMessage()))
                .build();
    }

    private static Response notFound(String message) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(Map.of("error", message))
                .build();
    }
}