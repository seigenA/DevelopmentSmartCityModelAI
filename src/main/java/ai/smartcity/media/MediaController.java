package ai.smartcity.media;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;


import java.io.IOException;
import java.nio.file.*;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


@RestController
@RequestMapping("/api/media")
public class MediaController {
    @Value("${storage.media-dir}") private String mediaDirStr;
    @Value("${storage.max-size-mb}") private long maxMb;


    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public Map<String,String> upload(@RequestPart("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "empty file");
        String name = Optional.ofNullable(file.getOriginalFilename()).orElse("").toLowerCase();
        if(!(name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") || name.endsWith(".pdf")))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "only jpg/png/pdf allowed");
        if(file.getSize() > maxMb*1024*1024)
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "file too large");


        Path mediaDir = Paths.get(mediaDirStr);
        Files.createDirectories(mediaDir);
        String fname = UUID.randomUUID() + "-" + name.replaceAll("\\s+","_");
        Path dst = mediaDir.resolve(fname);
        file.transferTo(dst.toFile());
        return Map.of("url", "/api/media/files/"+fname);
    }


    @GetMapping("/files/{name}")
    public ResponseEntity<Resource> get(@PathVariable String name) throws IOException {
        Path p = Paths.get(mediaDirStr).resolve(name).normalize();
        if(!Files.exists(p)) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        Resource res = new UrlResource(p.toUri());
        return ResponseEntity.ok(res);
    }
}