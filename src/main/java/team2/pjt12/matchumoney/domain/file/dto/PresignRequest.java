package team2.pjt12.matchumoney.domain.file.dto;

public class PresignRequest {
    private String filename;     // 예: avatar.png
    private String contentType;  // 예: image/png

    public PresignRequest() {}

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
