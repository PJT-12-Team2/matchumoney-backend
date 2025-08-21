# Spring 6/Jakarta면 Tomcat 10.1 사용 권장, JDK17
FROM tomcat:10.1-jdk17-temurin
# 기본 ROOT 앱 삭제
RUN rm -rf /usr/local/tomcat/webapps/ROOT
# 빌드 산출물 WAR을 ROOT로 배포
COPY build/libs/*.war /usr/local/tomcat/webapps/ROOT.war
EXPOSE 8080
# 톰캣 기본 엔트리포인트 사용