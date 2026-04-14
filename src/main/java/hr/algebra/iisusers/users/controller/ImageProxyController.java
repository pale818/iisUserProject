package hr.algebra.iisusers.users.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api/proxy")
public class ImageProxyController {

    private final RestTemplate restTemplate = new RestTemplate();

    // Fetches an external image server-side and returns it to the browser.
    // This avoids Cross-Origin-Resource-Policy blocks on images from reqres.in.
    @GetMapping("/image")
    public ResponseEntity<byte[]> proxyImage(@RequestParam String url) {
        byte[] bytes = restTemplate.getForObject(url, byte[].class);
        if (bytes == null) return ResponseEntity.notFound().build();

        String contentType = url.endsWith(".png") ? "image/png" : "image/jpeg";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType)
                .body(bytes);
    }
}
