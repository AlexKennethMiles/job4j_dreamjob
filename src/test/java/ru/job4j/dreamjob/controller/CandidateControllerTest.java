package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.ConcurrentModel;
import org.springframework.web.multipart.MultipartFile;
import ru.job4j.dreamjob.dto.FileDto;
import ru.job4j.dreamjob.model.Candidate;
import ru.job4j.dreamjob.model.City;
import ru.job4j.dreamjob.service.CandidateService;
import ru.job4j.dreamjob.service.CityService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.now;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CandidateControllerTest {

    private CandidateService candidateService;
    private CityService cityService;
    private CandidateController candidateController;
    private MultipartFile testFile;

    @BeforeEach
    public void initServices() {
        candidateService = mock(CandidateService.class);
        cityService = mock(CityService.class);
        candidateController = new CandidateController(candidateService, cityService);
        testFile = new MockMultipartFile("testFile.img", new byte[]{1, 2, 3});
    }

    @Test
    public void whenRequestCandidateListPageThenGetPageWithCandidates() {
        var candidate1 = new Candidate(1, "test1", "description", now(), 1, 1);
        var candidate2 = new Candidate(2, "test2", "description", now(), 4, 2);
        var expectedCandidates = List.of(candidate1, candidate2);
        when(candidateService.findAll()).thenReturn(expectedCandidates);

        var model = new ConcurrentModel();
        var view = candidateController.getAll(model);
        var actualCandidates = model.getAttribute("candidates");

        assertThat(view).isEqualTo("candidates/list");
        assertThat(actualCandidates).isEqualTo(expectedCandidates);
    }

    @Test
    public void whenRequestCandidateCreationPageThenGetCreationPage() {
        var city1 = new City(1, "Москва");
        var city2 = new City(2, "Санкт-Петербург");
        var expectedCities = List.of(city1, city2);
        when(cityService.findAll()).thenReturn(expectedCities);

        var model = new ConcurrentModel();
        var view = candidateController.getCreationPage(model);
        var actualCities = model.getAttribute("cities");

        assertThat(view).isEqualTo("candidates/create");
        assertThat(actualCities).isEqualTo(expectedCities);
    }

    @Test
    public void whenPostNewCandidateThenGetPageWithCandidates() throws IOException {
        var candidate = new Candidate(1, "test", "description", now(), 1, 1);
        var fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        var candidateArgumentCapture = ArgumentCaptor.forClass(Candidate.class);
        var fileDtoArgumentCapture = ArgumentCaptor.forClass(FileDto.class);
        when(candidateService.save(candidateArgumentCapture.capture(), fileDtoArgumentCapture.capture())).thenReturn(candidate);

        var model = new ConcurrentModel();
        var view = candidateController.create(candidate, testFile, model);
        var actualCandidate = candidateArgumentCapture.getValue();
        var actualFileDto = fileDtoArgumentCapture.getValue();

        assertThat(view).isEqualTo("redirect:/candidates");
        assertThat(actualCandidate).isEqualTo(candidate);
        assertThat(actualFileDto).usingRecursiveComparison().isEqualTo(fileDto);
    }

    @Test
    public void whenSomeExceptionThrowWhileEditCandidateThenGetErrorPage() {
        var expectedException = new RuntimeException("Failed to write file");
        when(candidateService.save(any(), any())).thenThrow(expectedException);

        var model = new ConcurrentModel();
        var view = candidateController.create(new Candidate(), testFile, model);
        var actualException = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualException).isEqualTo(expectedException.getMessage());
    }

    @Test
    public void whenRequestCandidateByIdThenGetCandidatePage() {
        var candidate = new Candidate(1, "updated", "description", now(), 1, 1);
        var city1 = new City(1, "Москва");
        var city2 = new City(2, "Санкт-Петербург");
        var expectedCities = List.of(city1, city2);
        when(cityService.findAll()).thenReturn(expectedCities);
        when(candidateService.findById(any(Integer.class))).thenReturn(Optional.of(candidate));

        var model = new ConcurrentModel();
        var view = candidateController.getById(model, 1);
        var actualCities = model.getAttribute("cities");
        var actualCandidate = model.getAttribute("candidate");

        assertThat(view).isEqualTo("candidates/one");
        assertThat(actualCandidate).isEqualTo(candidate);
        assertThat(actualCities).isEqualTo(expectedCities);
    }

    @Test
    public void whenRequestCandidateByIncorrectIdThenGetCandidatePage() {
        var exceptedException = new RuntimeException("Кандидат с указанным идентификатором не найден");
        when(candidateService.findById(any(Integer.class))).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = candidateController.getById(model, 1);
        var actualException = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualException).isEqualTo(exceptedException.getMessage());
    }

    @Test
    public void whenPostUpdatedCandidateThenGetPageWithCandidates() throws IOException {
        var candidate = new Candidate(1, "test", "description", now(), 1, 1);
        var fileDto = new FileDto(testFile.getOriginalFilename(), testFile.getBytes());
        var candidateArgumentCapture = ArgumentCaptor.forClass(Candidate.class);
        var fileDtoArgumentCapture = ArgumentCaptor.forClass(FileDto.class);
        when(candidateService.update(candidateArgumentCapture.capture(), fileDtoArgumentCapture.capture())).thenReturn(true);

        var model = new ConcurrentModel();
        var view = candidateController.update(candidate, testFile, model);
        var actualCandidate = candidateArgumentCapture.getValue();
        var actualFileDto = fileDtoArgumentCapture.getValue();

        assertThat(view).isEqualTo("redirect:/candidates");
        assertThat(actualCandidate).isEqualTo(candidate);
        assertThat(actualFileDto).usingRecursiveComparison().isEqualTo(fileDto);
    }

    @Test
    public void whenPostUpdatedCandidateButNotUpdateThenGetErrorPage() throws IOException {
        var exceptedException = new RuntimeException("Кандидат с указанным идентификатором не найден");
        when(candidateService.update(any(), any())).thenReturn(false);

        var model = new ConcurrentModel();
        var view = candidateController.update(new Candidate(), testFile, model);
        var actualException = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualException).isEqualTo(exceptedException.getMessage());
    }

    @Test
    public void whenSomeExceptionThrowsThenGetErrorPage() {
        var exceptedException = new RuntimeException("Failed to write file");
        when(candidateService.update(any(), any())).thenThrow(exceptedException);

        var model = new ConcurrentModel();
        var view = candidateController.update(new Candidate(), testFile, model);
        var actualException = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualException).isEqualTo(exceptedException.getMessage());
    }

    @Test
    public void whenDeleteCandidateByIdThenGetPageWithCandidates() {
        when(candidateService.deleteById(any(Integer.class))).thenReturn(true);

        var model = new ConcurrentModel();
        var view = candidateController.delete(model, 1);

        assertThat(view).isEqualTo("redirect:/candidates");
    }

    @Test
    public void whenCandidateWasNotDeletedThenGetPageWithCandidates() {
        var exceptedException = new RuntimeException("Кандидат с указанным идентификатором не найден");
        when(candidateService.deleteById(any(Integer.class))).thenReturn(false);

        var model = new ConcurrentModel();
        var view = candidateController.delete(model, 1);
        var actualException = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualException).isEqualTo(exceptedException.getMessage());
    }

}
