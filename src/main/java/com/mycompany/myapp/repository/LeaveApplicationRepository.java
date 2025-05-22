package com.mycompany.myapp.repository;

import com.mycompany.myapp.domain.LeaveApplication;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the LeaveApplication entity.
 */
@Repository
public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication, Long> {
    @Query(
        "select leaveApplication from LeaveApplication leaveApplication where leaveApplication.applicant.login = ?#{authentication.name}"
    )
    List<LeaveApplication> findByApplicantIsCurrentUser();

    default Optional<LeaveApplication> findOneWithEagerRelationships(Long id) {
        return this.findOneWithToOneRelationships(id);
    }

    default List<LeaveApplication> findAllWithEagerRelationships() {
        return this.findAllWithToOneRelationships();
    }

    default Page<LeaveApplication> findAllWithEagerRelationships(Pageable pageable) {
        return this.findAllWithToOneRelationships(pageable);
    }

    @Query(
        value = "select leaveApplication from LeaveApplication leaveApplication left join fetch leaveApplication.applicant",
        countQuery = "select count(leaveApplication) from LeaveApplication leaveApplication"
    )
    Page<LeaveApplication> findAllWithToOneRelationships(Pageable pageable);

    @Query("select leaveApplication from LeaveApplication leaveApplication left join fetch leaveApplication.applicant")
    List<LeaveApplication> findAllWithToOneRelationships();

    @Query(
        "select leaveApplication from LeaveApplication leaveApplication left join fetch leaveApplication.applicant where leaveApplication.id =:id"
    )
    Optional<LeaveApplication> findOneWithToOneRelationships(@Param("id") Long id);
}
