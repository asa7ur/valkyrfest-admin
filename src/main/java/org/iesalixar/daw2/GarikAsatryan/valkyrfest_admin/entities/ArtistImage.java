package org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "artist_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "artist")
@EqualsAndHashCode(exclude = "artist")
public class ArtistImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;
}