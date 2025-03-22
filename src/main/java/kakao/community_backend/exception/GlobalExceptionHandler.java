// GlobalExceptionHandler.java
package kakao.community_backend.exception;

import kakao.community_backend.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<String>> handleRuntimeException(RuntimeException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(500, e.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<List<Map<String, String>>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        List<Map<String, String>> errors = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            Map<String, String> errorMap = new HashMap<>();
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errorMap.put("field", fieldName);
            errorMap.put("message", errorMessage);
            errors.add(errorMap);
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(400, "요청 데이터가 유효하지 않습니다", errors));
    }

    // 이메일 또는 닉네임 중복 예외
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleDuplicateResourceException(
            DuplicateResourceException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("field", ex.getField());
        error.put("message", ex.getMessage());

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(409, "이미 사용 중인 " + ex.getField() + "입니다", error));
    }

    // 사용자 정의 예외 클래스들에 대한 추가 핸들러들
}