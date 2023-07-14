package ru.job4j.dreamjob.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class MemoryCandidateRepository implements CandidateRepository {

    private static final MemoryCandidateRepository INSTANCE = new MemoryCandidateRepository();

    private int nextId = 1;

    private final Map<Integer, Candidate> candidates = new HashMap<>();

    private MemoryCandidateRepository() {
        save(new Candidate(1, "Alex Miles", "Java Developer", LocalDateTime.now().minusDays(1)));
        save(new Candidate(2, "Petr Arsentev", "Senior Java Developer", LocalDateTime.now().minusDays(5)));
        save(new Candidate(3, "Korobeinikov Stas ", "Mentor", LocalDateTime.now().minusDays(10)));
        save(new Candidate(4, "Esipov Alexey ", "Mentor", LocalDateTime.now().minusDays(15)));
        save(new Candidate(5, "Andrei Hincu ", "Mentor", LocalDateTime.now().minusDays(20)));
        save(new Candidate(6, "Kartashova Elena ", "Mentor", LocalDateTime.now().minusDays(25)));
    }

    public static MemoryCandidateRepository getInstance() {
        return INSTANCE;
    }

    @Override
    public Candidate save(Candidate candidate) {
        candidate.setId(nextId++);
        candidates.put(candidate.getId(), candidate);
        return candidate;
    }

    @Override
    public boolean deleteById(int id) {
        return candidates.remove(id) != null;
    }

    @Override
    public boolean update(Candidate candidate) {
        candidates.computeIfPresent(
                candidate.getId(), (id, oldCandidate) -> new Candidate(
                        oldCandidate.getId(),
                        candidate.getName(),
                        candidate.getDescription(),
                        candidate.getCreationDate()
                ));
        return false;
    }

    @Override
    public Optional<Candidate> findById(int id) {
        return Optional.ofNullable(candidates.get(id));
    }

    @Override
    public Collection<Candidate> findAll() {
        return candidates.values();
    }
}
