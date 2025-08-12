package team2.pjt12.matchumoney.domain.education.domain;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WebtoonVO {
    private Long id;
    private String title;
    private String fileDownUrl;
    private Integer typeCode;
}