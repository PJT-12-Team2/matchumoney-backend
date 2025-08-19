package team2.pjt12.matchumoney.domain.saving.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@Builder
@ApiModel(value = "BankLoginRequest", description = "은행 로그인 요청 DTO")
public class BankLoginRequestDTO {
    @ApiModelProperty(value = "은행 번호", example = "0004", required = true)
    private String bankCode;
    @ApiModelProperty(value = "사용자 은행 아이디", example = "STAR11112", required = true)
    private String id;
    @ApiModelProperty(value = "사용자 은행 비밀번호", example = "12345!", required = true)
    private String password;
    @ApiModelProperty(value = "사용자 생년월일 (YYYYMMDD)", example = "20250730", required = true)
    private String birthDate;


    public BankLoginRequestDTO() {

    }

}
