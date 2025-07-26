package team2.pjt12.matchumoney.domain.saving.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
public class BankLoginRequestDTO {
    @ApiModelProperty(value = "사용자 은행 아이디", example = "STARA1111", required = true)
    private String id;
    @ApiModelProperty(value = "사용자 은행 비밀번호", example = "12345!", required = true)
    private String password;
//    private String bankName; // ex: "국민은행"
}
