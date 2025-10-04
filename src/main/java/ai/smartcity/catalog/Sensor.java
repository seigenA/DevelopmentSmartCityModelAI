package ai.smartcity.catalog;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
public class Sensor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank private String name;          // строка
    @NotBlank private String type;          // строка (e.g. AIR_QUALITY, TRAFFIC)
    @PositiveOrZero
    @Column(name = "install_year")   // <-- главное: задать имя столбца
    private int year;       // число (год установки)
    @PastOrPresent private LocalDate installedAt; // дата
    @Column(length = 5000)
    private String description;             // текст

    // расширение под Задание 3:
    @Min(1) @Max(5) private Integer rating; // рейтинг 1–5 (может быть null)
    private String tags; // "air,pm2.5,outdoor" — хранить CSV, парсить в сервисе
    private String mediaUrl; // отдаем URL файла
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private ai.smartcity.auth.User author;

}
