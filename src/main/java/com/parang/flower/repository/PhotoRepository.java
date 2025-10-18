package com.parang.flower.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.parang.flower.entity.Photo;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
}

