package team2.pjt12.matchumoney.domain.mydata.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.sql.Timestamp;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardVO {
    private Integer cardId; // 카드 ID (카드고릴라 idx) - card_product.card_product_id
    private String name; // 카드 이름 - card_product.name
    private String type; // 카드 종류 (신용, 체크, unknown) - card_product.type
    private Boolean isAvailable; // 발급 가능 여부 - card_product.is_available
    private Integer issuerId; // 발급사 ID (FK) - card_product.issuer_id
    private String annualFee; // 연회비 정보 - card_product.annual_fee
    private Integer preMonthMoney; // 전월 실적 금액 (원) - card_product.pre_month_money
    private Boolean onlineOnly; // 온라인 전용 카드 여부 - card_product.online_only
    private String cardImageUrl; // 카드 이미지 URL - card_product.card_image_url
    private String requestPcUrl; // PC 신청 링크 - card_product.request_pc_url
    private String requestMobileUrl; // 모바일 신청 링크 - card_product.request_mobile_url
    private String annualFeeDetail; // 연회비 상세 설명 - card_product.annual_fee_detail
    private String corpPrContainer; // 카드사 PR 컨테이너 - card_product.corp_pr_container
    private String corpPrDetail; // 카드사 PR 상세 - card_product.corp_pr_detail
    private String corpTips; // 고릴라 TIP - card_product.corp_tips
    private Timestamp createdTime; // 생성일시 - card_product.created_time
    private Timestamp lastModifiedTime; // 최종 수정일시 - card_product.last_modified_time
    private Long personaId; // 페르소나 ID (FK) - card_product.persona_id
    private String issuer; // 발급사명 - card_product.issuer
}