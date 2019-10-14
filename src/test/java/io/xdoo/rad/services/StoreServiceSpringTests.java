package io.xdoo.rad.services;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@Slf4j
public class StoreServiceSpringTests {

    @Autowired
    private StoreService service;

    @Test
    public void testInit() {
        assertThat(this.service, is(notNullValue()));
    }

    @Test
    public void testIndexFiles() throws IOException {
        String path = this.getClass().getResource("/files/radzaehler").getPath();
        log.info("path -> {}", path);
        Stream<Path> walk = Files.walk(Paths.get(path));

        List<String> fileNames = walk.filter(Files::isRegularFile)
                .map(x -> x.getFileName().toString()).collect(Collectors.toList());

        fileNames.forEach(x -> {
            if(!x.startsWith("~")) {
                log.info("crunching file {}...", x);
                this.service.indexFile(this.loadFile("/files/radzaehler/" + x));
            }
        });
    }

    private InputStream loadFile(String path) {
        InputStream stream = this.getClass().getResourceAsStream(path);
        return stream;
    }
}
