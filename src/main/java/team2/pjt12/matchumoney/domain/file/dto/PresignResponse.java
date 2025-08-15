package team2.pjt12.matchumoney.domain.file.dto;

public class PresignResponse {
    private String uploadUrl; // 브라우저가 PUT 업로드할 URL
    private String publicUrl; // 프론트/DB에 저장할 최종 접근 URL
    private String key;       // S3 object key

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
