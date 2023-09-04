package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;
import ru.job4j.dreamjob.model.User;
import ru.job4j.dreamjob.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserControllerTest {

    private UserService userService;
    private UserController userController;

    @BeforeEach
    public void initServices() {
        userService = mock(UserService.class);
        userController = new UserController(userService);
    }

    @Test
    public void whenRequestRegistrationPageThenGetIt() {
        var view = userController.getRegistrationPage();

        assertThat(view).isEqualTo("users/register");
    }

    @Test
    public void whenPostNewUserThenGetPageWithVacancies() {
        var user = new User(1, "test@mail.com", "test", "password");
        when(userService.save(any())).thenReturn(Optional.of(user));

        var model = new ConcurrentModel();
        var view = userController.register(user, model);

        assertThat(view).isEqualTo("redirect:/vacancies");
    }

    @Test
    public void whenPostNewUserWithNotUniqueEmailThenGetErrorPage() {
        var user = new User(0, null, "Гость", null);
        var expectedException = new RuntimeException("Пользователь с такой почтой уже существует");
        when(userService.save(any())).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = userController.register(new User(), model);
        var actualUser = model.getAttribute("user");
        var actualException = model.getAttribute("message");

        assertThat(view).isEqualTo("errors/404");
        assertThat(actualUser).isEqualTo(user);
        assertThat(actualException).isEqualTo(expectedException.getMessage());
    }

    @Test
    public void whenRequestLoginPageThenGetIt() {
        var view = userController.getLoginPage();

        assertThat(view).isEqualTo("users/login");
    }

    @Test
    public void whenRequestToLoginThenGetPageWithVacancies() {
        var user = new User(1, "test@mai.com", "Гость", "123");
        var request = mock(HttpServletRequest.class);
        var httpSession = mock(HttpSession.class);
        when(request.getSession()).thenReturn(httpSession);
        when(userService.findByEmailAndPassword(any(String.class), any(String.class))).thenReturn(Optional.of(user));

        var model = new ConcurrentModel();
        var view = userController.loginUser(user, model, request);
        /* var actualUser = httpSession.getAttribute("user"); */

        assertThat(view).isEqualTo("redirect:/vacancies");
        /* assertThat(actualUser).isEqualTo(user); */
    }

    @Test
    public void whenRequestToLoginButWrongLoginOrPasswordThenGetEr() {
        var user = new User(0, "test@mai.com", "Гость", "qwerty");
        var error = "Почта или пароль введены неверно";
        var request = mock(HttpServletRequest.class);
        when(userService.findByEmailAndPassword(any(String.class), any(String.class))).thenReturn(Optional.empty());

        var model = new ConcurrentModel();
        var view = userController.loginUser(user, model, request);
        var actualUser = model.getAttribute("user");
        var actualError = model.getAttribute("error");

        assertThat(view).isEqualTo("users/login");
        assertThat(actualUser).isEqualTo(user);
        assertThat(actualError).isEqualTo(error);
    }

    @Test
    public void whenRequestLogoutPageThenGetIt() {
        var httpSession = mock(HttpSession.class);

        var view = userController.logout(httpSession);

        assertThat(view).isEqualTo("redirect:/users/login");
    }

}
