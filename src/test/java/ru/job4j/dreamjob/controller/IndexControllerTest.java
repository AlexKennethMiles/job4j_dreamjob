package ru.job4j.dreamjob.controller;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class IndexControllerTest {
    private IndexController indexController = new IndexController();

    @Test
    public void whenRequestMainPageThenGetIndex() {
        var view = indexController.getIndex();

        assertThat(view).isEqualTo("index");
    }

}
