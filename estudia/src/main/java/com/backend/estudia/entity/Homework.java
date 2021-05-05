package com.backend.estudia.entity;

import lombok.*;

import javax.persistence.*;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name="homeworks")
public class Homework implements Serializable  {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="title", nullable = false, length = 50)
    private String title;
    @Column(name="description", length = 500)
    private String description;
    @Column(name="limitDate")
    private LocalDateTime limitDate;
    @Column(name="deliveryFormat", nullable = false, length = 50)
    private String deliveryFormat;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name="homework_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<HomeworkResult> homeworkResults;

}
