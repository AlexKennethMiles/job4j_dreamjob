package ru.job4j.dreamjob.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.job4j.dreamjob.configuration.DatasourceConfiguration;
import ru.job4j.dreamjob.model.User;

import java.util.List;
import java.util.Properties;

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class Sql2oUserRepositoryTest {

    private static Sql2oUserRepository sql2oUserRepository;

    @BeforeAll
    public static void initRepositories() throws Exception {
        var properties = new Properties();
        try (var inputStream = Sql2oCandidateRepositoryTest.class.getClassLoader().getResourceAsStream("connection.properties")) {
            properties.load(inputStream);
        }
        var url = properties.getProperty("datasource.url");
        var username = properties.getProperty("datasource.username");
        var password = properties.getProperty("datasource.password");

        var configuration = new DatasourceConfiguration();
        var datasource = configuration.connectionPool(url, username, password);
        var sql2o = configuration.databaseClient(datasource);

        sql2oUserRepository = new Sql2oUserRepository(sql2o);
    }

    @AfterEach
    public void clearVacancies() {
        sql2oUserRepository.deleteAllUsers();
    }

    @Test
    public void whenDontSaveThenGetAll() {
        var savedUsers = sql2oUserRepository.findAllUsers();
        assertThat(savedUsers.size()).isEqualTo(0);
        assertThat(savedUsers).isEqualTo(emptyList());
    }

    @Test
    public void whenSaveThenGetAll() {
        var user = sql2oUserRepository.save(new User(0, "mail@gmail.com", "name", "password")).get();
        var savedUsers = sql2oUserRepository.findAllUsers();
        assertThat(savedUsers.size()).isEqualTo(1);
        assertThat(savedUsers).isEqualTo(List.of(user));
    }

    @Test
    public void whenSaveThenGetSame() {
        var user = sql2oUserRepository.save(new User(0, "mail@gmail.com", "name", "password")).get();
        var savedUser = sql2oUserRepository.findByEmailAndPassword(user.getEmail(), user.getPassword()).get();
        assertThat(savedUser).isEqualTo(user);
    }

    @Test
    public void whenSaveTwoSameEmailButSaveOnlyOne() {
        var user1 = sql2oUserRepository.save(new User(0, "mail@gmail.com", "name", "password")).get();
        var user2 = sql2oUserRepository.save(new User(0, "mail@gmail.com", "ANOTHER", "password")).get();
        var savedUser1 = sql2oUserRepository.findByEmailAndPassword(user1.getEmail(), user1.getPassword()).get();
        var savedUser2 = sql2oUserRepository.findByEmailAndPassword(user2.getEmail(), user2.getPassword()).get();
        assertThat(savedUser1).isEqualTo(user1);
        assertThat(savedUser2).isEqualTo(user1);
    }

    @Test
    public void whenSaveSeveralUsersAndGetAll() {
        var user1 = sql2oUserRepository.save(new User(0, "mail@gmail.com", "gmail", "password")).get();
        var user2 = sql2oUserRepository.save(new User(0, "yahoo@yahoo.com", "yahoo", "123456")).get();
        var user3 = sql2oUserRepository.save(new User(0, "hotmail@hotmail.com", "hotmail", "qwerty")).get();
        var savedUser1 = sql2oUserRepository.findByEmailAndPassword(user1.getEmail(), user1.getPassword()).get();
        var savedUser2 = sql2oUserRepository.findByEmailAndPassword(user2.getEmail(), user2.getPassword()).get();
        var savedUser3 = sql2oUserRepository.findByEmailAndPassword(user3.getEmail(), user3.getPassword()).get();
        assertThat(savedUser1).isEqualTo(user1);
        assertThat(savedUser2).isEqualTo(user2);
        assertThat(savedUser3).isEqualTo(user3);
    }

    @Test
    public void whenGetValidAndInvalidUser() {
        var validUser = sql2oUserRepository.save(new User(0, "mail@gmail.com", "name", "password")).get();
        var invalidUser = new User(0, "d{a]q,m@13.+", "?n5o9vek", "to$##2{a");
        var savedUser = sql2oUserRepository.findByEmailAndPassword(validUser.getEmail(), validUser.getPassword()).get();
        var unsavedUser = sql2oUserRepository.findByEmailAndPassword(invalidUser.getEmail(), invalidUser.getPassword());
        assertThat(savedUser).isEqualTo(validUser);
        assertThat(unsavedUser).isEqualTo(empty());
    }
}
