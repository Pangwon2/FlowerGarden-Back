package com.parang.flower.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.parang.flower.dto.CreatePhotoRequest;
import com.parang.flower.dto.PhotoResponse;
import com.parang.flower.service.PhotoService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;


@RestController
@RequestMapping("/api/photos")
@RequiredArgsConstructor
public class PhotoController {

  private final PhotoService service;

  @Value("${app.public-base-url:}") // 선택: 절대 URL 만들 때 사용 (프론트 .env와 달라도 됨)
  private String publicBaseUrl;

  private String base(HttpServletRequest req) {
    // publicBaseUrl 지정이 있으면 그걸 사용, 아니면 요청 호스트로
    if (publicBaseUrl != null && !publicBaseUrl.isBlank()) return publicBaseUrl;
    String scheme = req.getScheme(); // http
    String host = req.getServerName(); // localhost
    int port = req.getServerPort(); // 8080
    return scheme + "://" + host + (port == 80 || port == 443 ? "" : ":" + port);
  }

  /** 업로드: multipart/form-data
   *  form-data: file(binary), name(text), writer(text)
   */
  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public PhotoResponse upload(
      @RequestPart("file") MultipartFile file,
      @RequestPart(required = false) String name,
      @RequestPart(required = false) String writer,
      HttpServletRequest req
  ) throws IOException {
    var saved = service.save(file, new CreatePhotoRequest(name, writer));
    return PhotoResponse.from(saved, base(req));
  }

  /** 목록: ?page=0&size=12&sort=createdAt,desc */
  @GetMapping
  public Page<PhotoResponse> list(Pageable pageable, HttpServletRequest req) {
    String b = base(req);
    return service.list(pageable).map(p -> PhotoResponse.from(p, b));
  }

  @GetMapping("/{id}")
  public PhotoResponse get(@PathVariable Long id, HttpServletRequest req) {
    var p = service.find(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    return PhotoResponse.from(p, base(req));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    service.delete(id);
  }
}

