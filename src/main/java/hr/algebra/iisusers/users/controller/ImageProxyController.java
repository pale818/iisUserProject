package hr.algebra.iisusers.users.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

// Proxies avatar images through the server to avoid CORS errors in the browser
// (external image URLs from ReqRes are on a different origin, so the browser blocks direct access)
@RestController
@RequestMapping("/api/proxy")
public class ImageProxyController {

    private final RestTemplate restTemplate = new RestTemplate();

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
