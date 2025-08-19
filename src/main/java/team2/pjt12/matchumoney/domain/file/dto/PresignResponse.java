package team2.pjt12.matchumoney.domain.file.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "S3 Presigned URL 응답 DTO")
public class PresignResponse {
    @ApiModelProperty(
            value = "브라우저가 PUT 업로드할 URL",
            example = "https://s3.ap-northeast-2.amazonaws.com/bucket-name/temp/abc123?X-Amz-Algorithm=AWS4-HMAC-SHA256..."
    )
    private final String uploadUrl; // 브라우저가 PUT 업로드할 URL

    @ApiModelProperty(
            value = "프론트/DB에 저장할 최종 접근 URL",
            example = "https://cdn.matchumoney.com/user/abc123.png"
    )
    private final String publicUrl; // 프론트/DB에 저장할 최종 접근 URL

    @ApiModelProperty(
            value = "S3 object key",
            example = "user/abc123.png"
    )
    private final String key;       // S3 object key

    public PresignResponse(String uploadUrl, String publicUrl, String key) {
        this.uploadUrl = uploadUrl;
        this.publicUrl = publicUrl;
        this.key = key;
    }

    public String getUploadUrl() {
        return uploadUrl;
    }

    public String getPublicUrl() {
        return publicUrl;
    }

    public String getKey() {
        return key;
    }
}
