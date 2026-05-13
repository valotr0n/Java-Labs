package lab6.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lab6.client.CompetitionApiClient;
import lab6.model.Competition;
import lab6.model.Participant;
import lab6.model.Stage;

import java.io.IOException;

@WebServlet("/app/admin")
public class AdminServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        long competitionId = parseLong(request.getParameter("competitionId"), 0);
        String action = request.getParameter("action");

        try (CompetitionApiClient client = new CompetitionApiClient(apiBase(request))) {
            if ("saveCompetition".equals(action)) {
                Competition competition = new Competition(competitionId, request.getParameter("title"),
                        request.getParameter("eventDateTime"), request.getParameter("organizers"),
                        request.getParameter("extraInfo"));
                Competition saved = competitionId > 0
                        ? client.updateCompetition(competitionId, competition)
                        : client.createCompetition(competition);
                competitionId = saved.getId();
            } else if ("deleteCompetition".equals(action) && competitionId > 0) {
                client.deleteCompetition(competitionId);
                competitionId = 0;
            } else if ("addStage".equals(action) && competitionId > 0) {
                client.addStage(competitionId, new Stage(0, request.getParameter("stageName"),
                        request.getParameter("stageTime")));
            } else if ("addParticipant".equals(action) && competitionId > 0) {
                client.addParticipant(competitionId, new Participant(0, request.getParameter("participantName"),
                        request.getParameter("participantEmail"), request.getParameter("stageName"),
                        request.getParameter("result")));
            }
        }

        String redirect = request.getContextPath() + "/app?role=admin";
        if (competitionId > 0) {
            redirect += "&competitionId=" + competitionId;
        }
        response.sendRedirect(redirect);
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