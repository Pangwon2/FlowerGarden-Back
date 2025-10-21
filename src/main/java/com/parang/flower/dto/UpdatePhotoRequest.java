package com.parang.flower.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 수정용(전체 갱신: PUT) — 필수 필드는 그대로 유지
@Getter @NoArgsConstructor @AllArgsConstructor @Builder
public class UpdatePhotoRequest {
  @NotBlank @Size(max=30) private String title;
  @Size(max=300) private String content;
  @NotBlank @Size(max=30) private String name;
  // writer는 서버가 결정
}