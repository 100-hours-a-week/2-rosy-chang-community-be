// DuplicateResourceException.java
package kakao.community_backend.exception;

import lombok.Getter;

@Getter
public class DuplicateResourceException extends RuntimeException {
    private final String field;

    public DuplicateResourceException(String field, String message) {
        super(message);
        this.field = field;
    }
}