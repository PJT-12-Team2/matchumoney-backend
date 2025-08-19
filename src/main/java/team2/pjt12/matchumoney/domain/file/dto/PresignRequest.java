package team2.pjt12.matchumoney.domain.file.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "S3 Presigned URL 요청 DTO")
public class PresignRequest {

    @ApiModelProperty(
            value = "업로드할 파일 이름",
            example = "avatar.png",
            required = true
    )
    private String filename;     // 예: avatar.png

    @ApiModelProperty(
            value = "업로드할 파일의 MIME 타입",
            example = "image/png",
            required = true
    )
    private String contentType;  // 예: image/png

    public PresignRequest() {
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
