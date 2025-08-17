package team2.pjt12.matchumoney.domain.quiz.controller;

import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import team2.pjt12.matchumoney.domain.quiz.dto.req.QuizAnswerRequestDTO;
import team2.pjt12.matchumoney.domain.quiz.dto.res.QuizHistoryResponseDTO;
import team2.pjt12.matchumoney.domain.quiz.dto.res.QuizProblemResponseDTO;
import team2.pjt12.matchumoney.domain.quiz.dto.res.QuizResultResponseDTO;
import team2.pjt12.matchumoney.domain.quiz.dto.res.QuizStatsResponseDTO;
import team2.pjt12.matchumoney.domain.quiz.service.QuizService;
import team2.pjt12.matchumoney.global.security.UserDetailsImpl;
import team2.pjt12.matchumoney.global.success.SuccessResponse;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/quiz")
@Slf4j
@Api(tags = "퀴즈", description = "금융 퀴즈 문제 조회/제출/통계 API")
public class QuizController {

    private final QuizService quizService;

    @GetMapping("/today")
    @ApiOperation(
            value = "오늘의 퀴즈 문제 조회",
            notes = "사용자별로 오늘 풀어야 할 금융 퀴즈 문제를 제공합니다. 하루에 최대 2개까지 풀 수 있으며, 이미 오늘 문제를 모두 푼 경우 400 오류가 발생할 수 있습니다."
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "퀴즈 문제 조회 성공"),
            @ApiResponse(code = 401, message = "인증 실패"),
            @ApiResponse(code = 400, message = "오늘 풀 수 있는 문제가 없음 (최대 2개 제한)")
    })
    public SuccessResponse<QuizProblemResponseDTO> getTodayQuiz(
            @ApiParam(hidden = true) Authentication authentication) {
        Long userId = getUserId(authentication);
        QuizProblemResponseDTO response = quizService.getTodayQuiz(userId);
        return new SuccessResponse<>(response);
    }

    @PostMapping("/submit")
    @ApiOperation(
            value = "퀴즈 정답 제출",
            notes = "사용자가 오늘의 퀴즈 문제에 대한 답을 제출합니다. 정답일 경우 경험치 10점을 획득합니다."
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "퀴즈 정답 제출 결과"),
            @ApiResponse(code = 400, message = "요청 데이터 오류 또는 이미 제출한 문제"),
            @ApiResponse(code = 401, message = "인증 실패")
    })
    public SuccessResponse<QuizResultResponseDTO> submitAnswer(
            @ApiParam(hidden = true) Authentication authentication,
            @ApiParam(value = "정답 제출 요청 DTO", required = true)
            @RequestBody @Valid QuizAnswerRequestDTO requestDTO) {
        Long userId = getUserId(authentication);
        QuizResultResponseDTO response = quizService.submitAnswer(userId, requestDTO);
        return new SuccessResponse<>(response);
    }

    @GetMapping("/stats")
    @ApiOperation(
            value = "사용자 퀴즈 통계 조회",
            notes = "사용자가 지금까지 푼 금융 퀴즈의 정답/오답 수, 연속 정답(스트릭), 누적 경험치 등을 제공합니다."
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "퀴즈 통계 조회 성공"),
            @ApiResponse(code = 401, message = "인증 실패")
    })
    public SuccessResponse<QuizStatsResponseDTO> getUserStats(
            @ApiParam(hidden = true) Authentication authentication) {
        Long userId = getUserId(authentication);
        QuizStatsResponseDTO response = quizService.getUserQuizStats(userId);
        return new SuccessResponse<>(response);
    }

    @GetMapping("/today/completed")
    @ApiOperation(
            value = "오늘의 퀴즈 완료 여부 조회",
            notes = "오늘 퀴즈를 모두 풀었는지 여부를 반환합니다. 하루에 2개 문제를 모두 풀었으면 true, 아직 풀 수 있는 문제가 있으면 false를 반환합니다."
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "오늘의 퀴즈 완료 여부 반환 (2개 모두 완료 시 true)"),
            @ApiResponse(code = 401, message = "인증 실패")
    })
    public SuccessResponse<Boolean> checkTodayQuizCompleted(
            @ApiParam(hidden = true) Authentication authentication) {
        Long userId = getUserId(authentication);
        boolean completed = quizService.hasCompletedTodayQuiz(userId);
        return new SuccessResponse<>(completed);
    }

    @GetMapping("/history")
    @ApiOperation(
            value = "퀴즈 이력 조회",
            notes = "사용자가 최근 풀었던 퀴즈 이력을 최대 5개까지 조회합니다. 각 이력에는 문제, 정답, 사용자 답안, 해설이 포함됩니다."
    )
    @ApiResponses({
            @ApiResponse(code = 200, message = "퀴즈 이력 조회 성공"),
            @ApiResponse(code = 401, message = "인증 실패")
    })
    public SuccessResponse<List<QuizHistoryResponseDTO>> getQuizHistory(
            @ApiParam(hidden = true) Authentication authentication) {
        Long userId = getUserId(authentication);
        List<QuizHistoryResponseDTO> history = quizService.getQuizHistory(userId);
        return new SuccessResponse<>(history);
    }

    private Long getUserId(Authentication authentication) {
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return userDetails.getUser().getUserId();
    }
}
