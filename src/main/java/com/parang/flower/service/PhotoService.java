package com.parang.flower.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.parang.flower.dto.CreatePhotoRequest;
import com.parang.flower.entity.Photo;
import com.parang.flower.repository.PhotoRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PhotoService {
  private final PhotoRepository repo;

  @Value("${app.upload-dir}")
  private String uploadDir;

  public Photo save(MultipartFile file, CreatePhotoRequest meta) throws IOException {
    Files.createDirectories(Path.of(uploadDir));

    String original = Optional.ofNullable(file.getOriginalFilename()).orElse("image");
    String ext = original.contains(".") ? original.substring(original.lastIndexOf('.')) : "";
    String stored = UUID.randomUUID() + ext;

    Path target = Path.of(uploadDir, stored);
    file.transferTo(target.toFile());

    Photo p = Photo.builder()
        .name(meta != null && meta.getName()!=null && !meta.getName().isBlank() ? meta.getName() : original)
        .writer(meta != null ? meta.getWriter() : null)
        .imagePath("/files/" + stored) // 정적 매핑과 일치!
        .build();
    return repo.save(p);
  }

  public Page<Photo> list(Pageable pageable) {
    return repo.findAll(pageable);
  }

  public Optional<Photo> find(Long id) { return repo.findById(id); }

  @Transactional
  public void delete(Long id) {
    repo.findById(id).ifPresent(p -> {
      // 파일도 같이 제거(선택)
      try {
        Path disk = Path.of(uploadDir, Paths.get(p.getImagePath()).getFileName().toString());
        Files.deleteIfExists(disk);
      } catch (Exception ignored) {}
      repo.delete(p);
    });
  }
}

