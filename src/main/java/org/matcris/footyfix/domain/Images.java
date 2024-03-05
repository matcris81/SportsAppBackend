package org.matcris.footyfix.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "images")
public class Images implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "image_id", updatable = false, nullable = false)
    private Long imageId;

    @Lob
    @Column(name = "image_data")
    private String imageData;

    @Column(name = "image_type")
    private String imageType;

    // Getters and Setters

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public String getImageData() {
        return imageData;
    }

    public void setImageData(String imageData) {
        this.imageData = imageData;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    // Override equals and hashCode

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Images)) return false;
        Images images = (Images) o;
        return imageId != null && imageId.equals(images.getImageId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    // toString method

    @Override
    public String toString() {
        return "Images{" + "imageId=" + imageId + ", imageData='" + imageData + '\'' + ", imageType='" + imageType + '\'' + '}';
    }
}
