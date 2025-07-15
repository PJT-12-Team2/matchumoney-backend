CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       social_provider VARCHAR(50),             -- 소셜 로그인 제공자 (nullable)
                       social_id VARCHAR(255),                  -- 소셜에서 주는 id (nullable)
                       email VARCHAR(255) NOT NULL UNIQUE,      -- 이메일 (일반로그인/소셜공용)
                       password VARCHAR(255),                   -- 비밀번호 (소셜로그인은 null)
                       nickname VARCHAR(50) NOT NULL,           -- 닉네임
                       profile_image_url VARCHAR(500),          -- 프로필 이미지 URL
                       created_time DATETIME NOT NULL,          -- 생성 시간
                       last_modified_time DATETIME NOT NULL,    -- 수정 시간
                       is_social_login BOOLEAN NOT NULL DEFAULT FALSE -- 소셜로그인 여부
);