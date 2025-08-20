package team2.pjt12.matchumoney.domain.file.controller;

import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import team2.pjt12.matchumoney.domain.file.dto.PresignRequest;
import team2.pjt12.matchumoney.domain.file.dto.PresignResponse;
import team2.pjt12.matchumoney.global.security.UserDetailsImpl;

import javax.swing.*;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/files")
@Api(tags = "File API", description = "파일 업로드용 S3 Presigned URL 발급")
public class FileController {


    private final S3Presigner presigner;
    // application.properties 에 넣어도 되고, 없으면 환경변수 사용
    @Value("${S3_BUCKET:#{null}}")
    private String bucketProp;

    public FileController(S3Presigner presigner) {
        this.presigner = presigner;
    }

    @PostMapping("/profile/presign")
    public PresignResponse presignProfile(@RequestBody PresignRequest req) {
        String bucket = bucketProp != null ? bucketProp : System.getenv("S3_BUCKET");
        String region = System.getenv("AWS_REGION");
        if (bucket == null || region == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "S3 설정 누락");
        }

        String contentType = Optional.ofNullable(req.getContentType()).orElse("");
        if (!contentType.startsWith("image/")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미지 타입만 허용됩니다.");
        }

        String filename = Optional.ofNullable(req.getFilename()).orElse("image.jpg");
        String ext = "jpg";
        int dot = filename.lastIndexOf('.');
        if (dot != -1 && dot < filename.length() - 1) {
            ext = filename.substring(dot + 1).toLowerCase();
        }

        long userId = currentUserIdOrFallback();

        // S3 Object Key
        String key = String.format("profiles/%d/%s.%s", userId, UUID.randomUUID(), ext);

        // PUT 요청에 대한 presigned URL 생성
        PutObjectRequest putReq = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest presignReq = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(putReq)
                .build();

        PresignedPutObjectRequest presigned = presigner.presignPutObject(presignReq);

        // 개발용: 버킷 퍼블릭 + 정책을 켰다면 아래 URL로 바로 접근 가능
        // 운영 전환 시 CloudFront 도메인으로 교체 권장
        String publicUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", bucket, region, key);

        return new PresignResponse(presigned.url().toString(), publicUrl, key);
    }

    private long currentUserIdOrFallback() {
         Authentication auth = SecurityContextHolder.getContext().getAuthentication();
         if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UserDetailsImpl cud) {
             return cud.getUser().getUserId();
         }
        return 0L; // 임시값: 필요시 0 대신 임의 고정값 가능
    }
}
