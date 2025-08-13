package team2.pjt12.matchumoney.domain.cardsearch.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "CursorPageResponse", description = "커서 기반 페이지네이션 공통 응답 래퍼")
public class CursorPageResponse<T> {
    @ApiModelProperty(value = "조회된 아이템 목록", position = 1
    )
    private List<T> items;

    @ApiModelProperty(value = "다음 페이지 존재 여부", example = "true", position = 2)
    private boolean hasNext;

    @ApiModelProperty(
            value = "다음 페이지 요청 시 사용할 커서(마지막 아이템 id), 없으면 null",
            example = "42",
            position = 3
    )
    private String nextCursor;
}
