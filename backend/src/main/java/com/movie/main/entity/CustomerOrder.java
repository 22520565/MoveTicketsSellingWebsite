// package com.movie.main.entity;

// import java.time.LocalDate;

// import jakarta.persistence.Column;
// import jakarta.persistence.Entity;
// import jakarta.persistence.GeneratedValue;
// import jakarta.persistence.GenerationType;
// import jakarta.persistence.Id;
// import jakarta.persistence.Table;
// import lombok.AccessLevel;
// import lombok.Data;
// import lombok.NoArgsConstructor;
// import lombok.Setter;
// import lombok.experimental.FieldNameConstants;

// @Entity
// @Table
// @Data
// @NoArgsConstructor(access = AccessLevel.PRIVATE)
// @FieldNameConstants
// public class CustomerOrder implements Identifiable<Integer> {
// @Id
// @GeneratedValue(strategy = GenerationType.IDENTITY)
// @Column(nullable = false, unique = true, updatable = false)
// @Setter(value = AccessLevel.NONE)
// private Integer id = 0;

// @Column(nullable = false)
// private LocalDate date = LocalDate.now();
// }
