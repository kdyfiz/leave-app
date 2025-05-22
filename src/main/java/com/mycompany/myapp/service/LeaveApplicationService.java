package com.mycompany.myapp.service;

import com.mycompany.myapp.domain.LeaveApplication;
import com.mycompany.myapp.repository.LeaveApplicationRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.mycompany.myapp.domain.LeaveApplication}.
 */
@Service
@Transactional
public class LeaveApplicationService {

    private static final Logger LOG = LoggerFactory.getLogger(LeaveApplicationService.class);

    private final LeaveApplicationRepository leaveApplicationRepository;

    public LeaveApplicationService(LeaveApplicationRepository leaveApplicationRepository) {
        this.leaveApplicationRepository = leaveApplicationRepository;
    }

    /**
     * Save a leaveApplication.
     *
     * @param leaveApplication the entity to save.
     * @return the persisted entity.
     */
    public LeaveApplication save(LeaveApplication leaveApplication) {
        LOG.debug("Request to save LeaveApplication : {}", leaveApplication);
        return leaveApplicationRepository.save(leaveApplication);
    }

    /**
     * Update a leaveApplication.
     *
     * @param leaveApplication the entity to save.
     * @return the persisted entity.
     */
    public LeaveApplication update(LeaveApplication leaveApplication) {
        LOG.debug("Request to update LeaveApplication : {}", leaveApplication);
        return leaveApplicationRepository.save(leaveApplication);
    }

    /**
     * Partially update a leaveApplication.
     *
     * @param leaveApplication the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<LeaveApplication> partialUpdate(LeaveApplication leaveApplication) {
        LOG.debug("Request to partially update LeaveApplication : {}", leaveApplication);

        return leaveApplicationRepository
            .findById(leaveApplication.getId())
            .map(existingLeaveApplication -> {
                if (leaveApplication.getStartDate() != null) {
                    existingLeaveApplication.setStartDate(leaveApplication.getStartDate());
                }
                if (leaveApplication.getEndDate() != null) {
                    existingLeaveApplication.setEndDate(leaveApplication.getEndDate());
                }
                if (leaveApplication.getReason() != null) {
                    existingLeaveApplication.setReason(leaveApplication.getReason());
                }
                if (leaveApplication.getSubmissionDate() != null) {
                    existingLeaveApplication.setSubmissionDate(leaveApplication.getSubmissionDate());
                }
                if (leaveApplication.getStatus() != null) {
                    existingLeaveApplication.setStatus(leaveApplication.getStatus());
                }
                if (leaveApplication.getRejectionReason() != null) {
                    existingLeaveApplication.setRejectionReason(leaveApplication.getRejectionReason());
                }

                return existingLeaveApplication;
            })
            .map(leaveApplicationRepository::save);
    }

    /**
     * Get all the leaveApplications.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<LeaveApplication> findAll(Pageable pageable) {
        LOG.debug("Request to get all LeaveApplications");
        return leaveApplicationRepository.findAll(pageable);
    }

    /**
     * Get all the leaveApplications with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    public Page<LeaveApplication> findAllWithEagerRelationships(Pageable pageable) {
        return leaveApplicationRepository.findAllWithEagerRelationships(pageable);
    }

    /**
     * Get one leaveApplication by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<LeaveApplication> findOne(Long id) {
        LOG.debug("Request to get LeaveApplication : {}", id);
        return leaveApplicationRepository.findOneWithEagerRelationships(id);
    }

    /**
     * Delete the leaveApplication by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        LOG.debug("Request to delete LeaveApplication : {}", id);
        leaveApplicationRepository.deleteById(id);
    }
}
