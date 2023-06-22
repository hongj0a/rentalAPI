package dejay.rnd.billyG.controller;

import dejay.rnd.billyG.service.KmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class KmsController {

    private final KmsService kmsService;

    @GetMapping("/encrypt")
    public String encrypt(@RequestParam String plainText) {
        return kmsService.encrypt(plainText);
    }

    @PostMapping("/decrypt")
    public String decrypt(@RequestBody Map<String,Object> body ) {

        return kmsService.decrypt(body.get("encryptedText").toString());
    }
}