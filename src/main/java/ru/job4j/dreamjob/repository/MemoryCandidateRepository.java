package ru.job4j.dreamjob.repository;

import net.jcip.annotations.ThreadSafe;
import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Candidate;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
@Repository
public class MemoryCandidateRepository implements CandidateRepository {

    private AtomicInteger nextId = new AtomicInteger(0);

    private final Map<Integer, Candidate> candidates = new ConcurrentHashMap<>();

    private MemoryCandidateRepository() {
        save(new Candidate(1, "Alex Miles", "Java Developer", LocalDateTime.now().minusDays(1), 1, 0));
        save(new Candidate(2, "Petr Arsentev", "Senior Java Developer", LocalDateTime.now().minusDays(5), 6, 0));
        save(new Candidate(3, "Korobeinikov Stas ", "Mentor", LocalDateTime.now().minusDays(10), 1, 0));
        save(new Candidate(4, "Esipov Alexey ", "Mentor", LocalDateTime.now().minusDays(15), 2, 0));
        save(new Candidate(5, "Andrei Hincu ", "Mentor", LocalDateTime.now().minusDays(20), 3, 0));
        save(new Candidate(6, "Kartashova Elena ", "Mentor", LocalDateTime.now().minusDays(25), 4, 0));
    }

    @Override
    public Candidate save(Candidate candidate) {
        candidate.setId(nextId.incrementAndGet());
        candidates.put(candidate.getId(), candidate);
        return candidate;
    }

    @Override
    public boolean deleteById(int id) {
        return candidates.remove(id) != null;
    }

    @Override
    public boolean update(Candidate candidate) {
        return candidates.computeIfPresent(
                candidate.getId(), (id, oldCandidate) -> new Candidate(
                        oldCandidate.getId(),
                        candidate.getName(),
                        candidate.getDescription(),
                        candidate.getCreationDate(),
                        candidate.getCityId(),
                        candidate.getFileId()
                )) != null;
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
