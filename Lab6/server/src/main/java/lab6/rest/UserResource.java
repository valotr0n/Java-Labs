package lab6.rest;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import lab6.model.User;
import lab6.store.UserStore;

import java.util.Map;

@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UserResource {
    private final UserStore store = UserStore.getInstance();

    @GET
    public Response all() {
        return Response.ok(store.all()).build();
    }

    @GET
    @Path("/search")
    public Response findByQuery(@QueryParam("email") String email) {
        return findUser(email);
    }

    @GET
    @Path("/{email}")
    public Response findByPath(@PathParam("email") String email) {
        return findUser(email);
    }

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response createFromForm(@FormParam("firstName") String firstName,
                                   @FormParam("lastName") String lastName,
                                   @FormParam("email") String email,
                                   @FormParam("password") String password,
                                   @Context UriInfo uriInfo) {
        return create(new User(firstName, lastName, email, password), uriInfo);
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createFromJson(User user, @Context UriInfo uriInfo) {
        return create(user, uriInfo);
    }

    @PUT
    @Path("/{email}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response update(@PathParam("email") String email, User user) {
        try {
            return store.update(email, user)
                    .map(updated -> Response.ok(updated).build())
                    .orElseGet(() -> notFound("Пользователь не найден"));
        } catch (IllegalArgumentException ex) {
            return badRequest(ex);
        }
    }

    @DELETE
    @Path("/{email}")
    public Response delete(@PathParam("email") String email) {
        if (store.delete(email)) {
            return Response.noContent().build();
        }
        return notFound("Пользователь не найден");
    }

    private Response create(User user, UriInfo uriInfo) {
        try {
            User created = store.add(user);
            return Response.created(uriInfo.getAbsolutePathBuilder().path(created.getEmail()).build())
                    .entity(created)
                    .build();
        } catch (IllegalArgumentException ex) {
            return badRequest(ex);
        }
    }

    private Response findUser(String email) {
        return store.find(email)
                .map(user -> Response.ok(user).build())
                .orElseGet(() -> notFound("Пользователь не найден"));
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