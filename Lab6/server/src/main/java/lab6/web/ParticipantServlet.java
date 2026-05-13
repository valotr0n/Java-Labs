package lab6.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lab6.client.CompetitionApiClient;
import lab6.model.Participant;

import java.io.IOException;

@WebServlet("/app/participant")
public class ParticipantServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        long competitionId = parseLong(request.getParameter("competitionId"), 0);
        if (competitionId > 0) {
            try (CompetitionApiClient client = new CompetitionApiClient(apiBase(request))) {
                client.addParticipant(competitionId, new Participant(0, request.getParameter("participantName"),
                        request.getParameter("participantEmail"), request.getParameter("stageName"),
                        request.getParameter("result")));
            }
        }
        response.sendRedirect(request.getContextPath() + "/app?role=participant&competitionId=" + competitionId);
    }

    private static String apiBase(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
                + request.getContextPath() + "/api";
    }

    private static long parseLong(String value, long defaultValue) {
        try {
            return value == null || value.isBlank() ? defaultValue : Long.parseLong(value);
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }
}