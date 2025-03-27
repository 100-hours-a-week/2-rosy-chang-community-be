package kakao.community_backend.repository;

import kakao.community_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // 이메일로 사용자 찾기
    Optional<User> findByEmailAndIsDeletedFalse(String email);

    // 닉네임으로 사용자 찾기
    Optional<User> findByNickname(String nickname);

    // 삭제되지 않은 모든 사용자 찾기
    List<User> findByIsDeletedFalse();

    // JPQL을 사용한 쿼리 예시
    @Query("SELECT u FROM User u WHERE u.isDeleted = false ORDER BY u.createdAt DESC")
    List<User> findAllActiveUsers();
}