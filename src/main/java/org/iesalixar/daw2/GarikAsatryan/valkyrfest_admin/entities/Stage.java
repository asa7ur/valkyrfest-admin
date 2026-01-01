package org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"performances", "sponsors"})
@EqualsAndHashCode(exclude = {"performances", "sponsors"})
public class Stage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "{msg.validation.required}")
    @Size(max = 100, message = "{msg.validation.size}")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotNull(message = "{msg.validation.required}")
    @Min(value = 1, message = "{msg.validation.positive}")
    @Column(name = "capacity", nullable = false)
    private Long capacity;

    @OneToMany(mappedBy = "stage", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Performance> performances = new ArrayList<>();

    @ManyToMany(mappedBy = "stages")
    private List<Sponsor> sponsors = new ArrayList<>();
}
