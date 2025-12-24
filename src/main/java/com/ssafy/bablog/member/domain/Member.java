package com.ssafy.bablog.member.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member {
    private Long id;
    private String email;
    private String password;
    private String name;
    private Gender gender;
    private LocalDate birthDate;
    private BigDecimal heightCm;
    private BigDecimal weightKg;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void update(String name, Gender gender, LocalDate birthDate, BigDecimal heightCm, BigDecimal weightKg) {
        this.name = Objects.isNull(name) ? this.name : name;
        this.gender = Objects.isNull(gender) ? this.gender : gender;
        this.birthDate = Objects.isNull(birthDate) ? this.birthDate : birthDate;
        this.heightCm = Objects.isNull(heightCm) ? this.heightCm : heightCm;
        this.weightKg = Objects.isNull(weightKg) ? this.weightKg : weightKg;
    }
}
