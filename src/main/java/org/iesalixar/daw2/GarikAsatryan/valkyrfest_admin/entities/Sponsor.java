package org.iesalixar.daw2.GarikAsatryan.valkyrfest_admin.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sponsors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "stages")
@EqualsAndHashCode(exclude = "stages")
public class Sponsor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "{msg.validation.required}")
    @Size(max = 100, message = "{msg.validation.size}")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotEmpty(message = "{msg.validation.required}")
    @Size(max = 20, message = "{msg.validation.size}")
    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @NotEmpty(message = "{msg.validation.required}")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", message = "{msg.validation.email}")
    @Size(max = 100, message = "{msg.validation.size}")
    @Column(name = "email", unique = true, nullable = false, length = 100)
    private String email;

    @NotNull(message = "{msg.validation.required}")
    @Column(name = "contribution", precision = 10, scale = 2, nullable = false)
    private BigDecimal contribution;

    private String image;

    @ManyToMany
    @JoinTable(
            name = "sponsor_stage",
            joinColumns = @JoinColumn(name = "sponsor_id"),
            inverseJoinColumns = @JoinColumn(name = "stage_id")
    )
    private List<Stage> stages = new ArrayList<>();
}
