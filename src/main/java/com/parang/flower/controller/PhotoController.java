package com.parang.flower.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.parang.flower.dto.CreatePhotoRequest;
import com.parang.flower.dto.UpdatePhotoRequest;
import com.parang.flower.entity.Photo;
import com.parang.flower.repository.PhotoRepository;
import com.parang.flower.service.PhotoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;

@RestController
@RequestMapping("/photo")
@RequiredArgsConstructor
public class PhotoController {

  private final PhotoService photoService;
  private final PhotoRepository photoRepository;

  // 생성: POST /photos  (파일+JSON)
  @PostMapping(value="/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Map<String,Object>> create(
      @RequestPart("file") MultipartFile file,
      @Valid @RequestPart("data") CreatePhotoRequest req
  ) throws IOException {
    // writer는 로그인 붙이면 서버에서 채움
    var saved = photoService.save(file, req /*, writerFromAuth */);
    return ResponseEntity
        .status(201)
        .body(toResp(saved));
  }

  // 수정(전체 갱신): PUT /photos/{id} (파일은 선택: 있으면 교체, 없으면 유지)
  @PutMapping(value="/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Map<String,Object>> update(
      @PathVariable Long id,
      @RequestPart(value="file", required=false) MultipartFile file,
      @Valid @RequestPart("data") UpdatePhotoRequest req
  ) throws IOException {
    var saved = photoService.save(file, null);
    return ResponseEntity.ok(toResp(saved));
  }

  private Map<String,Object> toResp(Photo p) {
    return Map.of(
      "id", p.getId(),
      "title", p.getTitle(),
      "content", p.getContent(),
      "name", p.getName(),
      "writer", p.getWriter(),
      "url", p.getImagePath(),
      "createdAt", p.getCreatedAt(),
      "updatedAt", p.getUpdatedAt()
    );
  }

  // ---- 유효성 오류 400 응답 ----
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
      Map<String, String> errors = new HashMap<>();
      for (var err : ex.getBindingResult().getFieldErrors()) {
          errors.put(err.getField(), err.getDefaultMessage());
      }
      return ResponseEntity.badRequest().body(Map.of(
              "message", "Validation failed",
              "errors", errors
      ));
  }

  // @GetMapping
  // public List<Map<String, Object>> list() {
  //   return photoRepository.findAll().stream()
  //       .map(p -> Map.of(
  //           "id", p.getId(),
  //           "title", p.getTitle(),
  //           "name", p.getName(),
  //           "url", p.getImagePath(),
  //           "content", p.getContent(),
  //           "writer", p.getWriter(),
  //           "createdAt", p.getCreatedAt(),
  //           "updatedAt", p.getUpdatedAt()
  //       ))
  //       .toList();
  // }
}

