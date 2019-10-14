package io.xdoo.rad.api;

import io.xdoo.rad.services.StoreService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;

@RestController
public class RadController {

    private final StoreService storeService;

    public RadController(StoreService storeService) {
        this.storeService = storeService;
    }

    @PostMapping("/import")
    public void upload(@RequestParam MultipartFile[] files) {
        for(MultipartFile file : files) {
            try {
                this.storeService.indexFile(file.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
