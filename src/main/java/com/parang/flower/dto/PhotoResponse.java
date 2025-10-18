package com.parang.flower.dto;

import lombok.Builder;

import java.time.Instant;

import com.parang.flower.entity.Photo;

@Builder
public record PhotoResponse(
        Long id,
        String name,
        String writer,
        String imageUrl,
        Instant createdAt,
        Instant updatedAt
) {
    public static PhotoResponse from(Photo p, String baseUrl) {
        return PhotoResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .writer(p.getWriter())
                .imageUrl(baseUrl + p.getImagePath())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}

