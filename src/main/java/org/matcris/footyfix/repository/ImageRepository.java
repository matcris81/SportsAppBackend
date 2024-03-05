package org.matcris.footyfix.repository;

import java.util.UUID;
import org.matcris.footyfix.domain.Images;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ImageRepository extends JpaRepository<Images, Long> {}
