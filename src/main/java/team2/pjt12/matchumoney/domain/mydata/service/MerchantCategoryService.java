package team2.pjt12.matchumoney.domain.mydata.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 가맹점명을 기반으로 소비 분야를 분류하는 서비스
 * 
 * @author MatchuMoney Team
 * @since 1.0ㄴ
 */
@Slf4j
@Service
public class MerchantCategoryService {
    
    // 가맹점 카테고리별 키워드 맵 (우선순위 순으로 정렬)
    private static final Map<String, List<String>> CATEGORY_KEYWORDS = new LinkedHashMap<>();
    
    static {
        // 교통 - 고속버스, 기차, 대중교통, 택시, 기타
        CATEGORY_KEYWORDS.put("교통", Arrays.asList(
            "지하철", "SUBWAY", "버스", "BUS", "고속버스", "시외버스", "택시", "TAXI",
            "교통카드", "하이패스", "Hi-pass", "톨게이트", "주차장", "PARKING",
            "카카오 T", "카카오T", "우버", "UBER", "타다", "바이크", "킥보드", "모빌리티",
            "기차", "KTX", "무궁화호", "새마을호", "ITX", "SRT", "철도", "역", "STATION",
            "대중교통", "버스정류장", "지하철역", "전철", "열차", "광역버스", "시내버스"
        ));
        
        // 주유 - 주유소, 충전소, 기타
        CATEGORY_KEYWORDS.put("주유", Arrays.asList(
            "주유소", "GS칼텍스", "SK에너지", "현대오일뱅크", "S-OIL", "알뜰주유소", "셀프주유소",
            "충전소", "전기충전소", "수소충전소", "EV충전소", "테슬라 슈퍼차저", "환경부 충전소",
            "휘발유", "경유", "LPG", "가스충전소", "LPG충전소", "연료", "기름", "FUEL",
            "STATION", "충전", "CHARGING", "ENEOS", "알뜰", "셀프", "SELF"
        ));
        
        // 통신 - KT, LGU+, SKT, 기타
        CATEGORY_KEYWORDS.put("통신", Arrays.asList(
            "SK텔레콤", "SKT", "KT", "LG유플러스", "LG U+", "LGU", "유플러스",
            "휴대폰", "핸드폰", "폰", "통신비", "인터넷", "INTERNET", "와이파이", "WIFI",
            "데이터", "통신사", "알뜰폰", "MVNO", "헬로모바일", "한국통신", "케이티",
            "5G", "LTE", "요금제", "통신비", "핸드폰요금", "인터넷요금"
        ));
        
        // 마트/편의점 - SSM, 대형마트, 전통시장, 편의점, 기타
        CATEGORY_KEYWORDS.put("마트/편의점", Arrays.asList(
            // 대형마트
            "이마트", "E-MART", "롯데마트", "LOTTE MART", "홈플러스", "HOMEPLUS",
            "코스트코", "COSTCO", "하나로마트", "농협", "트레이더스", "TRADERS",
            // 편의점
            "GS25", "CU", "세븐일레븐", "7-ELEVEN", "이마트24", "미니스톱", "MINISTOP",
            "편의점", "CVS", "씨유", "지에스", "세븐", "24시", "24", "GS",
            // SSM (Super Super Market)
            "SSM", "슈퍼마켓", "수퍼마켓", "마트", "MART", "SSG", "신세계", "이마트 에브리데이",
            // 전통시장
            "전통시장", "재래시장", "5일장", "시장", "MARKET", "농수산물시장", "청과시장",
            // 기타
            "하이마트", "HI-MART", "전자랜드", "마켓컬리", "쿠팡", "다이소", "아성다이소", "DAISO",
            "생활용품", "잡화", "창고형할인매장"
        ));
        
        // 쇼핑 - SPA브랜드, 면세점, 백화점, 아울렛, 온라인쇼핑, 홈쇼핑, 소셜커머스, 인테리어, 기타
        CATEGORY_KEYWORDS.put("쇼핑", Arrays.asList(
            // 백화점
            "백화점", "롯데백화점", "신세계백화점", "현대백화점", "갤러리아", "AK플라자",
            "동화면세점", "신라면세점", "롯데면세점", "현대면세점",
            // 아울렛
            "아울렛", "OUTLET", "프리미엄아울렛", "여의도아이파크몰", "타임스퀘어",
            // SPA브랜드
            "유니클로", "UNIQLO", "ZARA", "H&M", "무신사", "29CM", "브랜디", "BRANDY",
            "스타일난다", "STYLENANDA", "에이블리", "ABLY", "패션", "FASHION",
            // 온라인쇼핑
            "11번가", "11ST", "G마켓", "GMARKET", "옥션", "AUCTION", "인터파크", "INTERPARK",
            "위메프", "WEMAKEPRICE", "티몬", "TMON", "온라인", "ONLINE", "쇼핑몰", "MALL",
            // 홈쇼핑
            "CJ온스타일", "GS SHOP", "롯데홈쇼핑", "현대홈쇼핑", "홈쇼핑", "TV쇼핑",
            // 소셜커머스
            "쿠팡", "COUPANG", "하프클럽", "위메프", "티몬", "소셜커머스",
            // 인테리어
            "이케아", "IKEA", "한샘", "현대리바트", "까사미아", "인테리어", "가구", "FURNITURE",
            "홈데코", "HOME DECO", "리빙", "LIVING", "가전", "전자제품",
            // 기타
            "의류", "옷", "신발", "SHOES", "가방", "BAG", "쇼핑", "SHOPPING"
        ));
        
        // 푸드 - 일반음식점, 주점, 패밀리레스토랑, 패스트푸드, 점심, 저녁, 배달앱, 기타
        CATEGORY_KEYWORDS.put("푸드", Arrays.asList(
            // 배달앱 (최우선 분류)
            "배달의민족", "배민", "요기요", "쿠팡이츠", "COUPANG EATS", "배달", "딜리버리",
            "배달앱", "우버이츠", "UBER EATS", "배달주문", "푸드딜리버리", "우아한형제들",
            // 패스트푸드
            "맥도날드", "MCDONALD", "버거킹", "BURGER KING", "KFC", "롯데리아", "LOTTERIA",
            "써브웨이", "SUBWAY", "피자헛", "PIZZA HUT", "도미노피자", "DOMINO", "파파존스",
            // 패밀리레스토랑
            "아웃백", "OUTBACK", "TGIF", "베니건스", "패밀리레스토랑", "FAMILY RESTAURANT",
            "빕스", "VIPS", "애슐리", "ASHLEY", "토니로마스", "마르쉐",
            // 일반음식점
            "한식", "중식", "일식", "양식", "분식", "족발", "보쌈", "삼겹살", "갈비",
            "찜닭", "치킨", "피자", "PIZZA", "레스토랑", "RESTAURANT", "푸드", "FOOD",
            "김밥", "떡볶이", "순대", "라면", "국수", "냉면", "비빔밥", "돈까스",
            "세종김밥떡볶이", "신사골감자탕", "천지샤브샤브", "혼다라멘", "엽기떡볶이",
            "칼국수", "고을칼국수", "감자탕", "샤브샤브", "라멘", "모모야",
            "더개미", "The개미", "채움", "알고", "음식점", "식당", "맛집",
            // 주점
            "호프", "맥주", "술집", "주점", "바", "BAR", "칵테일", "위스키", "소주", "맥주집", "치킨호프",
            "이자카야", "포차", "선술집", "호프집", "와인바", "WINE BAR",
            // 기타
            "점심", "저녁", "식사", "MEAL", "런치", "LUNCH", "디너", "DINNER", "브런치", "BRUNCH"
        ));
        
        // 카페/디저트 - 베이커리, 아이스크림, 카페, 기타
        CATEGORY_KEYWORDS.put("카페/디저트", Arrays.asList(
            // 카페
            "스타벅스", "STARBUCKS", "투썸플레이스", "A TWOSOME", "이디야", "EDIYA",
            "커피빈", "COFFEE BEAN", "할리스", "HOLLYS", "탐앤탐스", "TOM N TOMS",
            "메가커피", "MEGA", "카페베네", "CAFFE BENE", "엔젤인어스", "ANGEL-IN-US",
            "빽다방", "컴포즈커피", "더벤티", "폴바셋", "파스쿠찌", "드롭탑",
            "커피", "COFFEE", "카페", "CAFE", "BEAN", "원두", "라떼", "아메리카노",
            // 베이커리
            "파리바게뜨", "PARIS BAGUETTE", "뚜레쥬르", "TOUS LES JOURS", "베이커리", "BAKERY",
            "던킨도넛", "DUNKIN DONUTS", "크리스피크림", "KRISPY KREME", "도넛", "DONUT",
            "빵", "BREAD", "케이크", "CAKE", "마카롱", "MACARON",
            // 아이스크림
            "아이스크림", "ICE CREAM", "베스킨라빈스", "BASKIN ROBBINS", "설빙", "SULBING",
            "빙수", "BINGSU", "30cm 아이스크림", "하겐다즈", "HAAGEN DAZS",
            // 기타 디저트
            "디저트", "DESSERT", "젤라또", "GELATO", "와플", "WAFFLE", "팬케이크", "PANCAKE",
            "타르트", "TART", "푸딩", "PUDDING", "티라미수", "마들렌"
        ));
        
        // 뷰티/피트니스 - 드럭스토어, 피트니스, 헤어, 화장품, 기타
        CATEGORY_KEYWORDS.put("뷰티/피트니스", Arrays.asList(
            // 드럭스토어
            "올리브영", "OLIVE YOUNG", "롭스", "LOHBS", "부츠", "BOOTS", "세포라", "SEPHORA",
            "드럭스토어", "DRUG STORE", "코스메틱", "COSMETIC",
            // 화장품
            "미샤", "MISSHA", "더페이스샵", "THE FACE SHOP", "이니스프리", "INNISFREE",
            "에뛰드하우스", "ETUDE HOUSE", "네이처리퍼블릭", "NATURE REPUBLIC",
            "화장품", "뷰티", "BEAUTY", "향수", "PERFUME", "스킨케어", "SKINCARE",
            "아모레퍼시픽", "LG생활건강", "헤라", "설화수", "라네즈", "씨제이올리브네트웍스",
            // 헤어
            "미용실", "헤어샵", "HAIR SHOP", "토니앤가이", "준오헤어", "이가자", "장인컷",
            "파마", "염색", "컷", "CUT", "헤어", "HAIR", "미용", "BEAUTY", "살롱", "SALON",
            // 피트니스
            "헬스장", "GYM", "헬스클럽", "FITNESS", "피트니스", "요가", "YOGA", "필라테스", "PILATES",
            "수영장", "SWIMMING POOL", "골프연습장", "GOLF", "스포츠센터", "SPORTS CENTER",
            "웨이트", "WEIGHT", "운동", "EXERCISE", "트레이닝", "TRAINING"
        ));
        
        // 무실적 - 할인, 적립
        CATEGORY_KEYWORDS.put("무실적", Arrays.asList(
            "할인", "DISCOUNT", "적립", "POINT", "포인트", "캐시백", "CASHBACK",
            "쿠폰", "COUPON", "혜택", "BENEFIT", "리워드", "REWARD", "마일리지", "MILEAGE",
            "적립금", "환급", "REFUND", "보상", "COMPENSATION", "무료", "FREE",
            "증정", "GIFT", "선물", "PRESENT", "이벤트", "EVENT", "프로모션", "PROMOTION"
        ));
        
        // 공과금/렌탈 - 공과금, 렌탈, 기타
        CATEGORY_KEYWORDS.put("공과금/렌탈", Arrays.asList(
            // 공과금
            "전기요금", "가스요금", "수도요금", "공과금", "한국전력", "KEPCO", "도시가스",
            "상하수도", "인터넷요금", "케이블TV", "CATV", "관리비", "아파트관리비",
            "종합부동산세", "재산세", "자동차세", "건강보험료", "국민연금",
            // 렌탈
            "렌탈", "RENTAL", "정수기렌탈", "비데렌탈", "공기청정기렌탈", "매트리스렌탈",
            "코웨이", "COWAY", "웅진코웨이", "청호나이스", "현대렌탈케어", "SK매직",
            "LG전자렌탈", "삼성전자렌탈", "리스", "LEASE", "월렌탈"
        ));
        
        // 병원/약국 - 병원, 약국, 기타
        CATEGORY_KEYWORDS.put("병원/약국", Arrays.asList(
            // 병원
            "병원", "HOSPITAL", "의원", "클리닉", "CLINIC", "한의원", "ORIENTAL CLINIC",
            "치과", "DENTAL", "안과", "EYE CLINIC", "이비인후과", "ENT", "피부과", "DERMATOLOGY",
            "정형외과", "ORTHOPEDIC", "내과", "INTERNAL MEDICINE", "외과", "SURGERY",
            "산부인과", "OBSTETRICS", "소아과", "PEDIATRICS", "신경과", "NEUROLOGY",
            "메디컬", "MEDICAL", "헬스케어", "HEALTHCARE", "의료", "종합병원", "대학병원",
            // 약국
            "약국", "PHARMACY", "온누리약국", "드러그스토어", "DRUG STORE", "처방전",
            "의약품", "MEDICINE", "약사", "PHARMACIST", "조제", "처방", "PRESCRIPTION"
        ));
        
        // 애완동물 - 동물병원, 펫샵, 기타
        CATEGORY_KEYWORDS.put("애완동물", Arrays.asList(
            // 동물병원
            "동물병원", "ANIMAL HOSPITAL", "수의원", "VET", "수의사", "VETERINARIAN",
            "펫클리닉", "PET CLINIC", "동물의료", "ANIMAL MEDICAL",
            // 펫샵
            "펫샵", "PET SHOP", "애완동물용품", "PET SUPPLIES", "반려동물", "PET",
            "강아지", "DOG", "고양이", "CAT", "사료", "PET FOOD", "간식", "TREATS",
            "장난감", "TOY", "목줄", "LEASH", "케이지", "CAGE", "캐리어", "CARRIER",
            "펫", "애완", "반려", "동물", "ANIMAL", "펫용품", "애완용품"
        ));
        
        // 교육/육아 - 국민행복, 문화센터, 아이행복, 어린이집, 유치원, 학습지, 학원, 기타
        CATEGORY_KEYWORDS.put("교육/육아", Arrays.asList(
            // 어린이집/유치원
            "어린이집", "DAYCARE", "유치원", "KINDERGARTEN", "보육원", "놀이방", "키즈카페",
            "KIDS CAFE", "아이돌봄", "베이비시터", "BABYSITTER",
            // 학원
            "학원", "ACADEMY", "교습소", "과외", "TUTORING", "영어학원", "수학학원",
            "예체능학원", "피아노학원", "태권도장", "발레학원", "미술학원",
            // 학습지
            "학습지", "WORKBOOK", "교재", "참고서", "문제집", "구몬", "KUMON", "재능교육",
            "윤선생", "대교", "웅진씽크빅", "비상교육", "천재교육",
            // 문화센터
            "문화센터", "CULTURE CENTER", "평생교육원", "주민센터", "도서관", "LIBRARY",
            "백화점문화센터", "마트문화센터",
            // 기타
            "교육", "EDUCATION", "육아", "CHILDCARE", "아이행복카드", "국민행복카드",
            "학비", "등록금", "TUITION", "교육비", "수업료", "강습비"
        ));
        
        // 자동차/하이패스 - 보험, 정비, 차/중고차, 하이패스, 기타, 렌터카, 자동차
        CATEGORY_KEYWORDS.put("자동차/하이패스", Arrays.asList(
            // 자동차 관련
            "자동차", "CAR", "AUTO", "차량", "VEHICLE", "승용차", "SUV", "트럭", "TRUCK",
            "현대자동차", "HYUNDAI", "기아", "KIA", "삼성", "SAMSUNG", "쌍용", "SSANGYOUNG",
            // 중고차
            "중고차", "USED CAR", "SK엔카", "ENCAR", "케이카", "KCAR", "중고나라",
            // 렌터카
            "렌터카", "RENT A CAR", "롯데렌터카", "LOTTE RENT", "AJ렌터카", "쏘카", "SOCAR",
            "그린카", "GREEN CAR", "카셰어링", "CAR SHARING", "차량대여",
            // 정비
            "정비소", "GARAGE", "카센터", "CAR CENTER", "자동차정비", "타이어", "TIRE",
            "배터리", "BATTERY", "오일교환", "OIL CHANGE", "세차", "CAR WASH",
            "자동세차", "손세차", "카케어", "CAR CARE",
            // 보험
            "자동차보험", "CAR INSURANCE", "삼성화재", "현대해상", "DB손해보험", "메리츠화재",
            "한화손해보험", "KB손해보험", "AXA손해보험",
            // 하이패스
            "하이패스", "Hi-pass", "HIPASS", "톨게이트", "TOLL GATE", "고속도로", "HIGHWAY",
            "통행료", "TOLL FEE", "도로공사", "한국도로공사"
        ));
        
        // 레저/스포츠 - 경기관람, 골프, 테마파크, 게임, 기타
        CATEGORY_KEYWORDS.put("레저/스포츠", Arrays.asList(
            // 골프
            "골프", "GOLF", "골프장", "GOLF COURSE", "골프연습장", "DRIVING RANGE",
            "골프클럽", "GOLF CLUB", "캐디", "CADDY", "그린피", "GREEN FEE",
            // 테마파크
            "롯데월드", "LOTTE WORLD", "에버랜드", "EVERLAND", "서울랜드", "SEOUL LAND",
            "테마파크", "THEME PARK", "놀이공원", "AMUSEMENT PARK", "워터파크", "WATER PARK",
            "대명리조트", "한화리조트", "용인자연농원",
            // 경기관람
            "야구장", "BASEBALL", "축구장", "FOOTBALL", "농구", "BASKETBALL", "배구", "VOLLEYBALL",
            "경기장", "STADIUM", "체육관", "GYM", "스포츠관람", "SPORTS", "티켓", "TICKET",
            "올림픽공원", "잠실", "고척", "문학", "대구", "광주", "부산",
            // 게임
            "PC방", "PC ROOM", "노래방", "KARAOKE", "볼링장", "BOWLING", "당구장", "BILLIARDS",
            "오락실", "ARCADE", "게임", "GAME", "VR", "코인노래방",
            // 기타 레저
            "수영장", "SWIMMING", "스키장", "SKI RESORT", "스노우보드", "SNOWBOARD",
            "등산", "HIKING", "캠핑", "CAMPING", "낚시", "FISHING", "사우나", "SAUNA",
            "찜질방", "온천", "HOT SPRING", "스파", "SPA", "마사지", "MASSAGE"
        ));
        
        // OTT/영화/문화 - 공연/전시, 도서, 음원사이트, 영화, 기타, 디지털구독
        CATEGORY_KEYWORDS.put("OTT/영화/문화", Arrays.asList(
            // 영화
            "CGV", "롯데시네마", "LOTTE CINEMA", "메가박스", "MEGABOX", "영화관",
            "시네마", "CINEMA", "무비", "MOVIE", "상영관", "극장", "THEATER",
            "영화표", "영화티켓", "IMAX", "4DX", "스크린X",
            // OTT/디지털구독
            "넷플릭스", "NETFLIX", "유튜브프리미엄", "YOUTUBE PREMIUM", "디즈니플러스", "DISNEY+",
            "웨이브", "WAVVE", "티빙", "TVING", "쿠팡플레이", "COUPANG PLAY",
            "왓챠", "WATCHA", "OTT", "스트리밍", "STREAMING", "구독", "SUBSCRIPTION",
            "아마존프라임", "AMAZON PRIME", "애플TV", "APPLE TV", "HBO",
            // 음원사이트
            "멜론", "MELON", "지니", "GENIE", "벅스", "BUGS", "플로", "FLO",
            "애플뮤직", "APPLE MUSIC", "스포티파이", "SPOTIFY", "음원", "MUSIC",
            "음악", "사운드클라우드", "SOUNDCLOUD",
            // 도서
            "교보문고", "KYOBO", "영풍문고", "알라딘", "ALADIN", "YES24", "인터파크",
            "서점", "BOOKSTORE", "도서", "BOOK", "전자책", "E-BOOK", "밀리의서재",
            "리디북스", "RIDIBOOKS", "교보eBook", "크레마",
            // 공연/전시
            "콘서트", "CONCERT", "공연", "PERFORMANCE", "뮤지컬", "MUSICAL", "연극", "PLAY",
            "오페라", "OPERA", "발레", "BALLET", "클래식", "CLASSIC", "재즈", "JAZZ",
            "박물관", "MUSEUM", "미술관", "ART GALLERY", "전시관", "EXHIBITION",
            "갤러리", "GALLERY", "아트센터", "ART CENTER", "문화센터", "CULTURE CENTER",
            // 기타 문화
            "문화", "CULTURE", "예술", "ART", "레전드", "인터파크티켓", "YES24티켓",
            "멜론티켓", "옥션티켓"
        ));
        
        // 간편결제 - 네이버페이, 삼성페이, 카카오페이, PAYCO, APP, 기타
        CATEGORY_KEYWORDS.put("간편결제", Arrays.asList(
            "네이버페이", "NAVER PAY", "카카오페이", "KAKAO PAY", "삼성페이", "SAMSUNG PAY",
            "PAYCO", "페이코", "토스", "TOSS", "페이", "PAY", "간편결제", "SIMPLE PAY",
            "모바일페이", "MOBILE PAY", "디지털지갑", "DIGITAL WALLET", "앱카드", "APP CARD",
            "LG페이", "LG PAY", "페이북", "PAYBOOK", "시럽페이", "SYRUP PAY",
            "NICE 결제대행", "NICE", "결제대행", "PG", "결제", "PAYMENT",
            "카카오_선물하기", "선물하기", "상품권", "기프트카드", "GIFT CARD", "포인트", "POINT"
        ));
        
        // 항공마일리지 - 대한항공, 아시아나항공, 제주항공, 에어부산, 진에어, 이스타항공, 티웨이항공, 기타, 저가항공
        CATEGORY_KEYWORDS.put("항공마일리지", Arrays.asList(
            // 대형항공사
            "대한항공", "KOREAN AIR", "KAL", "아시아나항공", "ASIANA", "AAR",
            // 저가항공사 (LCC)
            "제주항공", "JEJU AIR", "에어부산", "AIR BUSAN", "진에어", "JIN AIR",
            "이스타항공", "EASTAR JET", "티웨이항공", "T'WAY AIR", "플라이강원", "FLY GANGWON",
            "에어로케이", "AERO K", "에어프레미아", "AIR PREMIA",
            // 해외항공사
            "유나이티드", "UNITED", "아메리칸항공", "AMERICAN AIRLINES", "델타항공", "DELTA",
            "일본항공", "JAL", "전일본공수", "ANA", "중국남방항공", "CHINA SOUTHERN",
            "중국동방항공", "CHINA EASTERN", "캐세이퍼시픽", "CATHAY PACIFIC", "싱가포르항공", "SINGAPORE AIRLINES",
            // 기타
            "항공", "AIRLINE", "항공사", "비행기", "AIRPLANE", "마일리지", "MILEAGE",
            "항공료", "AIRFARE", "항공권", "AIR TICKET", "스카이패스", "SKYPASS",
            "아시아나클럽", "ASIANA CLUB", "저가항공", "LCC", "FSC"
        ));
        
        // 공항라운지/PP - PP, 공항라운지, 라운지키, 기타
        CATEGORY_KEYWORDS.put("공항라운지/PP", Arrays.asList(
            "공항라운지", "AIRPORT LOUNGE", "라운지", "LOUNGE", "프라이어리티패스", "PRIORITY PASS",
            "PP", "라운지키", "LOUNGE KEY", "드래곤패스", "DRAGON PASS",
            "인천공항", "ICN", "김포공항", "GMP", "부산공항", "PUS", "제주공항", "CJU",
            "공항", "AIRPORT", "터미널", "TERMINAL", "출입국", "IMMIGRATION",
            "VIP라운지", "VIP LOUNGE", "비즈니스라운지", "BUSINESS LOUNGE",
            "SKY라운지", "MATINA라운지", "허브라운지", "HUB LOUNGE"
        ));
        
        // 프리미엄 - 프리미엄 서비스, 바우처, 기타
        CATEGORY_KEYWORDS.put("프리미엄", Arrays.asList(
            "프리미엄", "PREMIUM", "VIP", "럭셔리", "LUXURY", "하이엔드", "HIGH END",
            "바우처", "VOUCHER", "쿠폰", "COUPON", "프리미엄서비스", "PREMIUM SERVICE",
            "컨시어지", "CONCIERGE", "개인맞춤", "PERSONAL", "전용", "EXCLUSIVE",
            "멤버십", "MEMBERSHIP", "클럽", "CLUB", "골드", "GOLD", "플래티넘", "PLATINUM",
            "다이아몬드", "DIAMOND", "블랙", "BLACK", "프라이빗", "PRIVATE"
        ));
        
        // 여행/숙박 - 렌터카, 리조트, 여행사, 호텔, 기타, 항공권, 온라인 여행사
        CATEGORY_KEYWORDS.put("여행/숙박", Arrays.asList(
            // 호텔
            "호텔", "HOTEL", "리조트", "RESORT", "모텔", "MOTEL", "펜션", "PENSION",
            "게스트하우스", "GUEST HOUSE", "숙박", "ACCOMMODATION", "숙소", "LODGING",
            "롯데호텔", "LOTTE HOTEL", "신라호텔", "SHILLA HOTEL", "조선호텔", "WESTIN CHOSUN",
            "하얏트", "HYATT", "메리어트", "MARRIOTT", "힐튼", "HILTON", "인터컨티넨탈", "INTERCONTINENTAL",
            "그랜드하얏트", "파크하얏트", "웨스틴", "WESTIN", "쉐라톤", "SHERATON",
            // 온라인 숙박
            "에어비앤비", "AIRBNB", "야놀자", "YANOLJA", "여기어때", "GOODCHOICE",
            "호텔스닷컴", "HOTELS.COM", "부킹닷컴", "BOOKING.COM", "아고다", "AGODA",
            // 여행사
            "여행사", "TRAVEL AGENCY", "하나투어", "HANA TOUR", "모두투어", "MODE TOUR",
            "온라인투어", "ONLINE TOUR", "인터파크투어", "INTERPARK TOUR", "롯데관광", "LOTTE TOUR",
            "노랑풍선", "YELLOW BALLOON", "투어2000", "TOUR 2000",
            // 온라인 여행사
            "익스피디아", "EXPEDIA", "스카이스캐너", "SKYSCANNER", "카약", "KAYAK",
            "트립닷컴", "TRIP.COM", "트립어드바이저", "TRIPADVISOR",
            // 항공권
            "항공권", "FLIGHT TICKET", "항공료", "AIRFARE", "여행", "TRAVEL", "관광", "TOURISM",
            "패키지여행", "PACKAGE TOUR", "자유여행", "FREE TRAVEL", "해외여행", "OVERSEAS TRAVEL"
        ));
        
        // 해외 - 수수료우대, 해외이용, 해외직구, 기타
        CATEGORY_KEYWORDS.put("해외", Arrays.asList(
            "해외", "OVERSEAS", "FOREIGN", "INTERNATIONAL", "해외결제", "OVERSEAS PAYMENT",
            "해외이용", "OVERSEAS USE", "외화", "FOREIGN CURRENCY", "환전", "EXCHANGE",
            "수수료우대", "FEE WAIVER", "수수료면제", "NO FEE", "해외수수료", "OVERSEAS FEE",
            "해외직구", "OVERSEAS SHOPPING", "직구", "CROSS BORDER", "해외쇼핑", "GLOBAL SHOPPING",
            "아마존", "AMAZON", "이베이", "EBAY", "알리익스프레스", "ALIEXPRESS",
            "타오바오", "TAOBAO", "큐텐", "QTEN", "아이허브", "IHERB",
            "USD", "EUR", "JPY", "CNY", "달러", "DOLLAR", "유로", "EURO", "엔", "YEN",
            "위안", "YUAN", "파운드", "POUND", "호주달러", "AUD", "캐나다달러", "CAD"
        ));
        
        // 비즈니스
        CATEGORY_KEYWORDS.put("비즈니스", Arrays.asList(
            "비즈니스", "BUSINESS", "업무", "WORK", "회사", "COMPANY", "법인", "CORPORATE",
            "사무용품", "OFFICE SUPPLIES", "문구", "STATIONERY", "복사", "COPY", "인쇄", "PRINT",
            "회계", "ACCOUNTING", "세무", "TAX", "컨설팅", "CONSULTING", "변호사", "LAWYER",
            "공증", "NOTARY", "등기", "REGISTRATION", "특허", "PATENT", "상표", "TRADEMARK",
            "광고", "ADVERTISING", "마케팅", "MARKETING", "홍보", "PR", "출판", "PUBLISHING",
            "교육", "EDUCATION", "세미나", "SEMINAR", "컨퍼런스", "CONFERENCE",
            "소프트웨어", "SOFTWARE", "라이센스", "LICENSE", "구독", "SUBSCRIPTION"
        ));
    }
    
    /**
     * 가맹점명을 분석하여 적절한 소비 분야를 반환합니다.
     * 
     * @param merchantName 가맹점명
     * @return 분류된 소비 분야 (기타 포함)
     */
    public String categorizeByMerchantName(String merchantName) {
        if (merchantName == null || merchantName.trim().isEmpty()) {
            log.debug("가맹점명이 null 또는 비어있음");
            return "기타";
        }
        
        String normalizedName = normalizeText(merchantName);
        log.debug("가맹점명 분류 시도: '{}' -> 정규화: '{}'", merchantName, normalizedName);
        
        // 우선순위에 따라 카테고리 매칭
        for (Map.Entry<String, List<String>> entry : CATEGORY_KEYWORDS.entrySet()) {
            String category = entry.getKey();
            List<String> keywords = entry.getValue();
            
            for (String keyword : keywords) {
                if (containsKeyword(normalizedName, keyword)) {
                    log.debug("매칭 성공: '{}' -> '{}' (키워드: '{}')", merchantName, category, keyword);
                    return category;
                }
            }
        }
        
        log.debug("매칭 실패: '{}' -> '기타'", merchantName);
        return "기타";
    }
    
    /**
     * 텍스트를 정규화합니다 (대소문자, 공백, 특수문자 처리)
     * 매칭의 확률을 올립니다.
     * 
     * @param text 원본 텍스트
     * @return 정규화된 텍스트
     */
    private String normalizeText(String text) {
        return text.toUpperCase()
                  .replaceAll("\\s+", "")      // 공백 제거
                  .replaceAll("[^A-Z0-9가-힣*_\\-\\.]", ""); // 특수문자 제거 (영문, 숫자, 한글, *, _, -, . 유지)
    }
    
    /**
     * 정규화된 가맹점명에 키워드가 포함되어 있는지 확인합니다.
     * 
     * @param normalizedMerchantName 정규화된 가맹점명
     * @param keyword 검색할 키워드
     * @return 포함 여부
     */
    private boolean containsKeyword(String normalizedMerchantName, String keyword) {
        String normalizedKeyword = normalizeText(keyword);
        return normalizedMerchantName.contains(normalizedKeyword);
    }
    
    /**
     * 모든 지원되는 카테고리 목록을 반환합니다.
     * 
     * @return 카테고리 목록
     */
    public Set<String> getSupportedCategories() {
        Set<String> categories = new LinkedHashSet<>(CATEGORY_KEYWORDS.keySet());
        categories.add("기타");
        return categories;
    }
    
    /**
     * 특정 카테고리의 키워드 목록을 반환합니다.
     * 
     * @param category 카테고리명
     * @return 키워드 목록
     */
    public List<String> getCategoryKeywords(String category) {
        return CATEGORY_KEYWORDS.getOrDefault(category, Collections.emptyList());
    }

    /**
     * 가맹점명 분류 결과를 상세히 반환합니다.
     *
     * @param merchantName 가맹점명
     * @return 분류 결과 정보
     */
    public Map<String, Object> categorizeWithDetails(String merchantName) {
        Map<String, Object> result = new HashMap<>();
        result.put("originalName", merchantName);
        result.put("normalizedName", normalizeText(merchantName));
        result.put("category", categorizeByMerchantName(merchantName));
        result.put("supportedCategories", getSupportedCategories());

        return result;
    }
}
