package org.matcris.footyfix.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import org.matcris.footyfix.domain.Images;
import org.matcris.footyfix.repository.ImageRepository;
import org.matcris.footyfix.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

@RestController
@RequestMapping("/api/images")
@Transactional
public class ImageResource {

    private final Logger log = LoggerFactory.getLogger(ImageResource.class);
    private static final String ENTITY_NAME = "images";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ImageRepository imageRepository;

    public ImageResource(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    /**
     * {@code POST  /images} : Create a new image.
     *
     * @param images the image to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new image, or with status {@code 400 (Bad Request)} if the image has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Images> createImage(@RequestBody Images images) throws URISyntaxException {
        log.debug("REST request to save Images : {}", images);
        if (images.getImageId() != null) {
            throw new BadRequestAlertException("A new image cannot already have an ID", ENTITY_NAME, "idexists");
        }
        Images result = imageRepository.save(images);
        return ResponseEntity
            .created(new URI("/api/images/" + result.getImageId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getImageId().toString()))
            .body(result);
    }

    /**
     * {@code GET  /images/:imageId} : get the "id" image.
     *
     * @param imageId the id of the image to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the image, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{imageId}")
    public ResponseEntity<Images> getImage(@PathVariable Long imageId) {
        log.debug("REST request to get Images : {}", imageId);
        Optional<Images> images = imageRepository.findById(imageId);
        return ResponseUtil.wrapOrNotFound(images);
    }
    // Additional methods for update, delete, etc., can be implemented here following the pattern above.
}
