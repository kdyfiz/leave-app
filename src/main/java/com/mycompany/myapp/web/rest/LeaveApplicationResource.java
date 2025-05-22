package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.domain.LeaveApplication;
import com.mycompany.myapp.repository.LeaveApplicationRepository;
import com.mycompany.myapp.service.LeaveApplicationService;
import com.mycompany.myapp.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link com.mycompany.myapp.domain.LeaveApplication}.
 */
@RestController
@RequestMapping("/api/leave-applications")
public class LeaveApplicationResource {

    private static final Logger LOG = LoggerFactory.getLogger(LeaveApplicationResource.class);

    private static final String ENTITY_NAME = "leaveApplication";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final LeaveApplicationService leaveApplicationService;

    private final LeaveApplicationRepository leaveApplicationRepository;

    public LeaveApplicationResource(
        LeaveApplicationService leaveApplicationService,
        LeaveApplicationRepository leaveApplicationRepository
    ) {
        this.leaveApplicationService = leaveApplicationService;
        this.leaveApplicationRepository = leaveApplicationRepository;
    }

    /**
     * {@code POST  /leave-applications} : Create a new leaveApplication.
     *
     * @param leaveApplication the leaveApplication to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new leaveApplication, or with status {@code 400 (Bad Request)} if the leaveApplication has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<LeaveApplication> createLeaveApplication(@Valid @RequestBody LeaveApplication leaveApplication)
        throws URISyntaxException {
        LOG.debug("REST request to save LeaveApplication : {}", leaveApplication);
        if (leaveApplication.getId() != null) {
            throw new BadRequestAlertException("A new leaveApplication cannot already have an ID", ENTITY_NAME, "idexists");
        }
        leaveApplication = leaveApplicationService.save(leaveApplication);
        return ResponseEntity.created(new URI("/api/leave-applications/" + leaveApplication.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, leaveApplication.getId().toString()))
            .body(leaveApplication);
    }

    /**
     * {@code PUT  /leave-applications/:id} : Updates an existing leaveApplication.
     *
     * @param id the id of the leaveApplication to save.
     * @param leaveApplication the leaveApplication to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated leaveApplication,
     * or with status {@code 400 (Bad Request)} if the leaveApplication is not valid,
     * or with status {@code 500 (Internal Server Error)} if the leaveApplication couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<LeaveApplication> updateLeaveApplication(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody LeaveApplication leaveApplication
    ) throws URISyntaxException {
        LOG.debug("REST request to update LeaveApplication : {}, {}", id, leaveApplication);
        if (leaveApplication.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, leaveApplication.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!leaveApplicationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        leaveApplication = leaveApplicationService.update(leaveApplication);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, leaveApplication.getId().toString()))
            .body(leaveApplication);
    }

    /**
     * {@code PATCH  /leave-applications/:id} : Partial updates given fields of an existing leaveApplication, field will ignore if it is null
     *
     * @param id the id of the leaveApplication to save.
     * @param leaveApplication the leaveApplication to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated leaveApplication,
     * or with status {@code 400 (Bad Request)} if the leaveApplication is not valid,
     * or with status {@code 404 (Not Found)} if the leaveApplication is not found,
     * or with status {@code 500 (Internal Server Error)} if the leaveApplication couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<LeaveApplication> partialUpdateLeaveApplication(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody LeaveApplication leaveApplication
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update LeaveApplication partially : {}, {}", id, leaveApplication);
        if (leaveApplication.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, leaveApplication.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!leaveApplicationRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<LeaveApplication> result = leaveApplicationService.partialUpdate(leaveApplication);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, leaveApplication.getId().toString())
        );
    }

    /**
     * {@code GET  /leave-applications} : get all the leaveApplications.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of leaveApplications in body.
     */
    @GetMapping("")
    public ResponseEntity<List<LeaveApplication>> getAllLeaveApplications(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        @RequestParam(name = "eagerload", required = false, defaultValue = "true") boolean eagerload
    ) {
        LOG.debug("REST request to get a page of LeaveApplications");
        Page<LeaveApplication> page;
        if (eagerload) {
            page = leaveApplicationService.findAllWithEagerRelationships(pageable);
        } else {
            page = leaveApplicationService.findAll(pageable);
        }
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /leave-applications/:id} : get the "id" leaveApplication.
     *
     * @param id the id of the leaveApplication to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the leaveApplication, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<LeaveApplication> getLeaveApplication(@PathVariable("id") Long id) {
        LOG.debug("REST request to get LeaveApplication : {}", id);
        Optional<LeaveApplication> leaveApplication = leaveApplicationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(leaveApplication);
    }

    /**
     * {@code DELETE  /leave-applications/:id} : delete the "id" leaveApplication.
     *
     * @param id the id of the leaveApplication to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLeaveApplication(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete LeaveApplication : {}", id);
        leaveApplicationService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
