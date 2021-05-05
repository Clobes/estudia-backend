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
@Table(name="homeworksResults")
public class HomeworkResult implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name="title", length = 50)
    private String title;
    @Column(name="limitDate")
    private LocalDateTime deliveryDate;

    @ManyToOne
    @JoinColumn(name="student_id")
    private User user;

    /* Página de contenido */
    @Column(name="content")
    @Lob
    private String content;

    /* Archivo */
    @Column(name="type", length = 50)
    private String type;
    @Column(name="extension", length = 10)
    private String extension;
    @Column(name="data")
    @Lob
    private byte[] data;
    
    /* Comentarios */
    @OneToMany
    @JoinColumn(name="homework_result_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private List<Comment> comments;

}
