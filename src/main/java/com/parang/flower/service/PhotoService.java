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
import com.parang.flower.dto.UpdatePhotoRequest;
import com.parang.flower.entity.Photo;
import com.parang.flower.repository.PhotoRepository;

import io.micrometer.common.lang.Nullable;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service @RequiredArgsConstructor
public class PhotoService {
  private final PhotoRepository repo;
  @Value("${app.upload-dir}") private String uploadDir;

  @Transactional
  public Photo save(MultipartFile file, CreatePhotoRequest req /*, String writer */) throws IOException {
    if (file==null || file.isEmpty()) throw new IllegalArgumentException("이미지 파일이 필요합니다.");
    var base = Path.of(uploadDir).toAbsolutePath().normalize();
    Files.createDirectories(base);
    var stored = storeFile(base, file);

    var entity = Photo.builder()
        .title(req.getTitle())
        .content(req.getContent())
        .name(req.getName())
        .writer(req.getWriter()) // 로그인 붙이면 auth로 세팅
        .imagePath("/files/" + stored)
        .build();
    return repo.save(entity);
  }

  @Transactional
  public Photo update(Long id, @Nullable MultipartFile file, UpdatePhotoRequest req /*, String writer */) throws IOException {
    var base = Path.of(uploadDir).toAbsolutePath().normalize();
    Files.createDirectories(base);

    var entity = repo.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Photo not found: " + id));

    // 텍스트 갱신
    entity.setTitle(req.getTitle());
    entity.setContent(req.getContent());
    entity.setName(req.getName());
    // entity.setWriter(writerFromAuth); // 필요 시

    // 파일이 들어오면 교체
    if (file != null && !file.isEmpty()) {
      deleteIfExists(base, entity.getImagePath());
      var stored = storeFile(base, file);
      entity.setImagePath("/files/" + stored);
    }

    return entity; // JPA dirty checking
  }

  private String storeFile(Path base, MultipartFile file) throws IOException {
    var original = Optional.ofNullable(file.getOriginalFilename()).orElse("image");
    var ext = original.contains(".") ? original.substring(original.lastIndexOf('.')).toLowerCase() : "";
    var stored = UUID.randomUUID() + ext;
    file.transferTo(base.resolve(stored));
    return stored;
  }

  private void deleteIfExists(Path base, String imagePath) {
    if (imagePath == null || imagePath.isBlank()) return;
    var filename = imagePath.substring(imagePath.lastIndexOf('/')+1);
    try { Files.deleteIfExists(base.resolve(filename)); } catch (Exception ignore) {}
  }
}


