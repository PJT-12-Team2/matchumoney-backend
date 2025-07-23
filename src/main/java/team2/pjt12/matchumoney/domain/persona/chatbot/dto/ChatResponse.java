package team2.pjt12.matchumoney.domain.persona.chatbot.dto;

public class ChatResponse {
    private String reply;

    public ChatResponse() {} // Jackson용 기본 생성자

    public ChatResponse(String reply) {
        this.reply = reply;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }
}