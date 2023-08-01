package ru.job4j.dreamjob.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.dreamjob.model.Vacancy;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Repository
public class MemoryVacancyRepository implements VacancyRepository {

    private int nextId = 1;

    private final Map<Integer, Vacancy> vacancies = new HashMap<>();

    private MemoryVacancyRepository() {
        save(new Vacancy(0, "Intern Java Developer", "First", LocalDateTime.now().minusSeconds(10)));
        save(new Vacancy(1, "Junior Java Developer", "Second", LocalDateTime.now().minusSeconds(50)));
        save(new Vacancy(2, "Junior+ Java Developer", "Third", LocalDateTime.now().minusSeconds(100)));
        save(new Vacancy(3, "Middle Java Developer", "Fourth", LocalDateTime.now().minusSeconds(150)));
        save(new Vacancy(4, "Middle+ Java Developer", "Fifth", LocalDateTime.now().minusSeconds(200)));
        save(new Vacancy(5, "Senior Java Developer", "Sixth", LocalDateTime.now().minusSeconds(250)));
    }

    @Override
    public Vacancy save(Vacancy vacancy) {
        vacancy.setId(nextId++);
        vacancies.put(vacancy.getId(), vacancy);
        return vacancy;
    }

    @Override
    public boolean deleteById(int id) {
        return vacancies.remove(id) != null;
    }

    @Override
    public boolean update(Vacancy vacancy) {
        return vacancies.computeIfPresent(vacancy.getId(), (id, oldVacancy) -> new Vacancy(
                oldVacancy.getId(),
                vacancy.getTitle(),
                vacancy.getDescription(),
                vacancy.getCreationDate())) != null;
    }

    @Override
    public Optional<Vacancy> findById(int id) {
        return Optional.ofNullable(vacancies.get(id));
    }

    @Override
    public Collection<Vacancy> findAll() {
        return vacancies.values();
    }

}
