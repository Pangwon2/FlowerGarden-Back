package com.parang.flower.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePhotoRequest {

    private Long id;                // null이면 생성, 값 있으면 수정

    @NotBlank
    @Size(max = 30)     // 엔티티(title) 제약과 일치
    private String title;     // 게시물 제목

    @Size(max = 300)    // 엔티티(content) 제약과 일치
    private String content;   // 게시물 내용(선택)

    @NotBlank
    @Size(max = 30)     // 엔티티(name) 제약과 일치
    private String name;      // 표시 이름(이미지명/사진 이름)

    @Size(max = 10)
    private String writer;    // ⚠️ 임시로 허용. 로그인 붙이면 서버에서 세팅!
}
