package lab6.store;

import lab6.model.Competition;
import lab6.model.Participant;
import lab6.model.Stage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class CompetitionStore {
    private static final CompetitionStore INSTANCE = new CompetitionStore();

    private final Map<Long, Competition> competitions = new ConcurrentHashMap<>();
    private final AtomicLong competitionIds = new AtomicLong(1);
    private final AtomicLong stageIds = new AtomicLong(1);
    private final AtomicLong participantIds = new AtomicLong(1);

    private CompetitionStore() {
        Competition competition = new Competition(0, "Весенняя олимпиада по программированию",
                "2026-05-20T10:00", "Кафедра ПОВТиАС", "Спонсор: учебный центр ДГТУ");
        competition.getStages().add(new Stage(0, "Открытие", "2026-05-20T10:00"));
        competition.getStages().add(new Stage(0, "Отборочный этап", "2026-05-20T11:00"));
        competition.getStages().add(new Stage(0, "Финал", "2026-05-20T15:00"));
        competition.getParticipants().add(new Participant(0, "Иван Петров", "ivan@example.com", "Отборочный этап", "85 баллов"));
        create(competition);
    }

    public static CompetitionStore getInstance() {
        return INSTANCE;
    }

    public synchronized Competition create(Competition data) {
        validateCompetition(data);
        Competition competition = copy(data);
        competition.setId(competitionIds.getAndIncrement());
        competition.setStages(assignStageIds(competition.getStages()));
        competition.setParticipants(assignParticipantIds(competition.getParticipants()));
        competitions.put(competition.getId(), competition);
        return copy(competition);
    }

    public List<Competition> all() {
        List<Competition> result = new ArrayList<>();
        for (Competition competition : competitions.values()) {
            result.add(copy(competition));
        }
        result.sort(Comparator.comparingLong(Competition::getId));
        return result;
    }

    public Optional<Competition> find(long id) {
        Competition competition = competitions.get(id);
        return competition == null ? Optional.empty() : Optional.of(copy(competition));
    }

    public synchronized Optional<Competition> update(long id, Competition data) {
        Competition current = competitions.get(id);
        if (current == null) {
            return Optional.empty();
        }
        validateCompetition(data);
        current.setTitle(data.getTitle().trim());
        current.setEventDateTime(trim(data.getEventDateTime()));
        current.setOrganizers(trim(data.getOrganizers()));
        current.setExtraInfo(trim(data.getExtraInfo()));
        current.setStages(assignStageIds(data.getStages()));
        current.setParticipants(assignParticipantIds(data.getParticipants()));
        return Optional.of(copy(current));
    }

    public synchronized boolean delete(long id) {
        return competitions.remove(id) != null;
    }

    public synchronized Optional<Stage> addStage(long competitionId, Stage data) {
        Competition competition = competitions.get(competitionId);
        if (competition == null) {
            return Optional.empty();
        }
        validateStage(data);
        Stage stage = new Stage(stageIds.getAndIncrement(), data.getName().trim(), trim(data.getStartsAt()));
        competition.getStages().add(stage);
        return Optional.of(copy(stage));
    }

    public synchronized Optional<Stage> updateStage(long competitionId, long stageId, Stage data) {
        Competition competition = competitions.get(competitionId);
        if (competition == null) {
            return Optional.empty();
        }
        validateStage(data);
        for (Stage stage : competition.getStages()) {
            if (stage.getId() == stageId) {
                stage.setName(data.getName().trim());
                stage.setStartsAt(trim(data.getStartsAt()));
                return Optional.of(copy(stage));
            }
        }
        return Optional.empty();
    }

    public synchronized boolean deleteStage(long competitionId, long stageId) {
        Competition competition = competitions.get(competitionId);
        return competition != null && competition.getStages().removeIf(stage -> stage.getId() == stageId);
    }

    public synchronized Optional<Participant> addParticipant(long competitionId, Participant data) {
        Competition competition = competitions.get(competitionId);
        if (competition == null) {
            return Optional.empty();
        }
        validateParticipant(data);
        for (Participant participant : competition.getParticipants()) {
            if (!isBlank(data.getEmail()) && data.getEmail().equalsIgnoreCase(participant.getEmail())) {
                participant.setName(data.getName().trim());
                participant.setStageName(trim(data.getStageName()));
                participant.setResult(trim(data.getResult()));
                return Optional.of(copy(participant));
            }
        }
        Participant participant = new Participant(participantIds.getAndIncrement(), data.getName().trim(),
                trim(data.getEmail()), trim(data.getStageName()), trim(data.getResult()));
        competition.getParticipants().add(participant);
        return Optional.of(copy(participant));
    }

    public synchronized Optional<Participant> updateParticipant(long competitionId, long participantId, Participant data) {
        Competition competition = competitions.get(competitionId);
        if (competition == null) {
            return Optional.empty();
        }
        validateParticipant(data);
        for (Participant participant : competition.getParticipants()) {
            if (participant.getId() == participantId) {
                participant.setName(data.getName().trim());
                participant.setEmail(trim(data.getEmail()));
                participant.setStageName(trim(data.getStageName()));
                participant.setResult(trim(data.getResult()));
                return Optional.of(copy(participant));
            }
        }
        return Optional.empty();
    }

    public synchronized boolean deleteParticipant(long competitionId, long participantId) {
        Competition competition = competitions.get(competitionId);
        return competition != null && competition.getParticipants().removeIf(participant -> participant.getId() == participantId);
    }

    private List<Stage> assignStageIds(List<Stage> stages) {
        List<Stage> result = new ArrayList<>();
        if (stages == null) {
            return result;
        }
        for (Stage stage : stages) {
            validateStage(stage);
            long id = stage.getId() > 0 ? stage.getId() : stageIds.getAndIncrement();
            result.add(new Stage(id, stage.getName().trim(), trim(stage.getStartsAt())));
        }
        return result;
    }

    private List<Participant> assignParticipantIds(List<Participant> participants) {
        List<Participant> result = new ArrayList<>();
        if (participants == null) {
            return result;
        }
        for (Participant participant : participants) {
            validateParticipant(participant);
            long id = participant.getId() > 0 ? participant.getId() : participantIds.getAndIncrement();
            result.add(new Participant(id, participant.getName().trim(), trim(participant.getEmail()),
                    trim(participant.getStageName()), trim(participant.getResult())));
        }
        return result;
    }

    private static void validateCompetition(Competition competition) {
        if (competition == null || isBlank(competition.getTitle())) {
            throw new IllegalArgumentException("Название соревнования обязательно");
        }
    }

    private static void validateStage(Stage stage) {
        if (stage == null || isBlank(stage.getName())) {
            throw new IllegalArgumentException("Название этапа обязательно");
        }
    }

    private static void validateParticipant(Participant participant) {
        if (participant == null || isBlank(participant.getName())) {
            throw new IllegalArgumentException("Имя участника обязательно");
        }
    }

    private static Competition copy(Competition source) {
        Competition copy = new Competition(source.getId(), source.getTitle(), source.getEventDateTime(),
                source.getOrganizers(), source.getExtraInfo());
        List<Stage> stages = new ArrayList<>();
        for (Stage stage : source.getStages()) {
            stages.add(copy(stage));
        }
        copy.setStages(stages);
        List<Participant> participants = new ArrayList<>();
        for (Participant participant : source.getParticipants()) {
            participants.add(copy(participant));
        }
        copy.setParticipants(participants);
        return copy;
    }

    private static Stage copy(Stage source) {
        return new Stage(source.getId(), source.getName(), source.getStartsAt());
    }

    private static Participant copy(Participant source) {
        return new Participant(source.getId(), source.getName(), source.getEmail(),
                source.getStageName(), source.getResult());
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static String trim(String value) {
        return value == null ? "" : value.trim();
    }
}