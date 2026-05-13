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
import java.util.List;

@WebServlet("/app")
public class AppServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        String role = request.getParameter("role");
        if (role == null) role = "spectator";
        long competitionId = parseLong(request.getParameter("competitionId"), 0);

        try (CompetitionApiClient client = new CompetitionApiClient(apiBase(request))) {
            List<Competition> competitions = client.getCompetitions();
            Competition selected = competitionId > 0 ? client.getCompetition(competitionId) : null;

            String body = switch (role) {
                case "admin"       -> buildAdminView(competitions, selected, request.getContextPath());
                case "participant" -> buildParticipantView(competitions, selected, request.getContextPath());
                default            -> buildSpectatorView(competitions, selected, request.getContextPath());
            };

            response.getWriter().write(Html.page("Соревнования", body));
        }
    }

    private static String buildAdminView(List<Competition> competitions, Competition selected, String ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append(nav(ctx));
        sb.append("<h1>Администратор соревнований</h1>");

        sb.append("<section><h2>Соревнования</h2><div class=\"actions\">");
        for (Competition c : competitions) {
            sb.append("<a class=\"button\" href=\"").append(ctx).append("/app?role=admin&competitionId=")
              .append(c.getId()).append("\">").append(Html.escape(c.getTitle())).append("</a>");
        }
        sb.append("</div></section>");

        sb.append("<section><h2>").append(selected != null ? "Редактировать соревнование" : "Создать соревнование").append("</h2>");
        sb.append("<form method=\"post\" action=\"").append(ctx).append("/app/admin\">");
        if (selected != null) {
            sb.append("<input type=\"hidden\" name=\"competitionId\"").append(Html.value(Long.toString(selected.getId()))).append(">");
        }
        sb.append("<input type=\"hidden\" name=\"action\" value=\"saveCompetition\">");
        sb.append("<label>Название<input type=\"text\" name=\"title\"").append(selected != null ? Html.value(selected.getTitle()) : "").append(" required></label>");
        sb.append("<label>Дата и время<input type=\"datetime-local\" name=\"eventDateTime\"").append(selected != null ? Html.value(selected.getEventDateTime()) : "").append("></label>");
        sb.append("<label>Организаторы<input type=\"text\" name=\"organizers\"").append(selected != null ? Html.value(selected.getOrganizers()) : "").append("></label>");
        sb.append("<label>Дополнительно<textarea name=\"extraInfo\">").append(selected != null ? Html.escape(selected.getExtraInfo()) : "").append("</textarea></label>");
        sb.append("<button type=\"submit\">Сохранить</button></form></section>");

        if (selected != null) {
            sb.append("<section><form method=\"post\" action=\"").append(ctx).append("/app/admin\">");
            sb.append("<input type=\"hidden\" name=\"competitionId\"").append(Html.value(Long.toString(selected.getId()))).append(">");
            sb.append("<input type=\"hidden\" name=\"action\" value=\"deleteCompetition\">");
            sb.append("<button type=\"submit\" class=\"danger\">Удалить соревнование</button></form></section>");

            sb.append("<section><h2>Этапы</h2>");
            if (!selected.getStages().isEmpty()) {
                sb.append("<table><thead><tr><th>Название</th><th>Начало</th></tr></thead><tbody>");
                for (Stage s : selected.getStages()) {
                    sb.append("<tr><td>").append(Html.escape(s.getName())).append("</td>")
                      .append("<td>").append(Html.escape(s.getStartsAt())).append("</td></tr>");
                }
                sb.append("</tbody></table>");
            }
            sb.append("<h3>Добавить этап</h3><form method=\"post\" action=\"").append(ctx).append("/app/admin\">");
            sb.append("<input type=\"hidden\" name=\"competitionId\"").append(Html.value(Long.toString(selected.getId()))).append(">");
            sb.append("<input type=\"hidden\" name=\"action\" value=\"addStage\">");
            sb.append("<label>Название<input type=\"text\" name=\"stageName\" required></label>");
            sb.append("<label>Время<input type=\"datetime-local\" name=\"stageTime\"></label>");
            sb.append("<button type=\"submit\">Добавить этап</button></form></section>");

            sb.append("<section><h2>Участники</h2>");
            if (!selected.getParticipants().isEmpty()) {
                sb.append("<table><thead><tr><th>Имя</th><th>Email</th><th>Этап</th><th>Результат</th></tr></thead><tbody>");
                for (Participant p : selected.getParticipants()) {
                    sb.append("<tr><td>").append(Html.escape(p.getName())).append("</td>")
                      .append("<td>").append(Html.escape(p.getEmail())).append("</td>")
                      .append("<td>").append(Html.escape(p.getStageName())).append("</td>")
                      .append("<td>").append(Html.escape(p.getResult())).append("</td></tr>");
                }
                sb.append("</tbody></table>");
            }
            sb.append("<h3>Добавить участника</h3><form method=\"post\" action=\"").append(ctx).append("/app/admin\">");
            sb.append("<input type=\"hidden\" name=\"competitionId\"").append(Html.value(Long.toString(selected.getId()))).append(">");
            sb.append("<input type=\"hidden\" name=\"action\" value=\"addParticipant\">");
            sb.append("<label>Имя<input type=\"text\" name=\"participantName\" required></label>");
            sb.append("<label>Email<input type=\"email\" name=\"participantEmail\"></label>");
            sb.append("<label>Этап<input type=\"text\" name=\"stageName\"></label>");
            sb.append("<label>Результат<input type=\"text\" name=\"result\"></label>");
            sb.append("<button type=\"submit\">Добавить</button></form></section>");
        }

        return sb.toString();
    }

    private static String buildParticipantView(List<Competition> competitions, Competition selected, String ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append(nav(ctx));
        sb.append("<h1>Запись на соревнование</h1>");

        if (selected == null) {
            sb.append("<section><h2>Выберите соревнование</h2><div class=\"actions\">");
            for (Competition c : competitions) {
                sb.append("<a class=\"button\" href=\"").append(ctx).append("/app?role=participant&competitionId=")
                  .append(c.getId()).append("\">").append(Html.escape(c.getTitle())).append("</a>");
            }
            sb.append("</div></section>");
        } else {
            sb.append("<section><h2>").append(Html.escape(selected.getTitle())).append("</h2>");
            if (!selected.getEventDateTime().isBlank()) {
                sb.append("<p>Дата: ").append(Html.escape(selected.getEventDateTime())).append("</p>");
            }
            sb.append("</section>");
            sb.append("<section><h2>Регистрация</h2><form method=\"post\" action=\"").append(ctx).append("/app/participant\">");
            sb.append("<input type=\"hidden\" name=\"competitionId\"").append(Html.value(Long.toString(selected.getId()))).append(">");
            sb.append("<label>Имя<input type=\"text\" name=\"participantName\" required></label>");
            sb.append("<label>Email<input type=\"email\" name=\"participantEmail\"></label>");
            if (!selected.getStages().isEmpty()) {
                sb.append("<label>Этап<select name=\"stageName\">");
                for (Stage s : selected.getStages()) {
                    sb.append("<option").append(Html.value(s.getName())).append(">").append(Html.escape(s.getName())).append("</option>");
                }
                sb.append("</select></label>");
            } else {
                sb.append("<label>Этап<input type=\"text\" name=\"stageName\"></label>");
            }
            sb.append("<label>Результат<input type=\"text\" name=\"result\"></label>");
            sb.append("<button type=\"submit\">Зарегистрироваться</button></form></section>");
        }

        return sb.toString();
    }

    private static String buildSpectatorView(List<Competition> competitions, Competition selected, String ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append(nav(ctx));
        sb.append("<h1>Соревнования</h1>");

        if (competitions.isEmpty()) {
            sb.append("<section><p>Соревнований пока нет.</p></section>");
        }

        for (Competition c : competitions) {
            boolean expanded = selected != null && selected.getId() == c.getId();
            String link = expanded
                    ? ctx + "/app?role=spectator"
                    : ctx + "/app?role=spectator&competitionId=" + c.getId();
            sb.append("<section><h2><a href=\"").append(link).append("\">").append(Html.escape(c.getTitle())).append("</a></h2>");
            if (!c.getEventDateTime().isBlank()) {
                sb.append("<p>").append(Html.escape(c.getEventDateTime())).append("</p>");
            }
            if (expanded) {
                if (!selected.getStages().isEmpty()) {
                    sb.append("<h3>Этапы</h3><table><thead><tr><th>Название</th><th>Начало</th></tr></thead><tbody>");
                    for (Stage s : selected.getStages()) {
                        sb.append("<tr><td>").append(Html.escape(s.getName())).append("</td>")
                          .append("<td>").append(Html.escape(s.getStartsAt())).append("</td></tr>");
                    }
                    sb.append("</tbody></table>");
                }
                if (!selected.getParticipants().isEmpty()) {
                    sb.append("<h3>Участники</h3><table><thead><tr><th>Имя</th><th>Этап</th><th>Результат</th></tr></thead><tbody>");
                    for (Participant p : selected.getParticipants()) {
                        sb.append("<tr><td>").append(Html.escape(p.getName())).append("</td>")
                          .append("<td>").append(Html.escape(p.getStageName())).append("</td>")
                          .append("<td>").append(Html.escape(p.getResult())).append("</td></tr>");
                    }
                    sb.append("</tbody></table>");
                }
            }
            sb.append("</section>");
        }

        return sb.toString();
    }

    private static String nav(String ctx) {
        return "<nav class=\"top-nav\">"
                + "<a href=\"" + ctx + "/\">Главная</a>"
                + "<a href=\"" + ctx + "/app?role=admin\">Администратор</a>"
                + "<a href=\"" + ctx + "/app?role=participant\">Участник</a>"
                + "<a href=\"" + ctx + "/app?role=spectator\">Зритель</a>"
                + "</nav>";
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
