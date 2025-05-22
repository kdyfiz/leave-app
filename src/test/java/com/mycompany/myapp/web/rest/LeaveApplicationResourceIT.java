package com.mycompany.myapp.web.rest;

import static com.mycompany.myapp.domain.LeaveApplicationAsserts.*;
import static com.mycompany.myapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mycompany.myapp.IntegrationTest;
import com.mycompany.myapp.domain.LeaveApplication;
import com.mycompany.myapp.domain.User;
import com.mycompany.myapp.domain.enumeration.LeaveStatus;
import com.mycompany.myapp.repository.LeaveApplicationRepository;
import com.mycompany.myapp.repository.UserRepository;
import com.mycompany.myapp.service.LeaveApplicationService;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link LeaveApplicationResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class LeaveApplicationResourceIT {

    private static final LocalDate DEFAULT_START_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_START_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_END_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_END_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_REASON = "AAAAAAAAAA";
    private static final String UPDATED_REASON = "BBBBBBBBBB";

    private static final Instant DEFAULT_SUBMISSION_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_SUBMISSION_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final LeaveStatus DEFAULT_STATUS = LeaveStatus.PENDING;
    private static final LeaveStatus UPDATED_STATUS = LeaveStatus.APPROVED;

    private static final String DEFAULT_REJECTION_REASON = "AAAAAAAAAA";
    private static final String UPDATED_REJECTION_REASON = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/leave-applications";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LeaveApplicationRepository leaveApplicationRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private LeaveApplicationRepository leaveApplicationRepositoryMock;

    @Mock
    private LeaveApplicationService leaveApplicationServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLeaveApplicationMockMvc;

    private LeaveApplication leaveApplication;

    private LeaveApplication insertedLeaveApplication;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LeaveApplication createEntity(EntityManager em) {
        LeaveApplication leaveApplication = new LeaveApplication()
            .startDate(DEFAULT_START_DATE)
            .endDate(DEFAULT_END_DATE)
            .reason(DEFAULT_REASON)
            .submissionDate(DEFAULT_SUBMISSION_DATE)
            .status(DEFAULT_STATUS)
            .rejectionReason(DEFAULT_REJECTION_REASON);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        leaveApplication.setApplicant(user);
        return leaveApplication;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static LeaveApplication createUpdatedEntity(EntityManager em) {
        LeaveApplication updatedLeaveApplication = new LeaveApplication()
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .reason(UPDATED_REASON)
            .submissionDate(UPDATED_SUBMISSION_DATE)
            .status(UPDATED_STATUS)
            .rejectionReason(UPDATED_REJECTION_REASON);
        // Add required entity
        User user = UserResourceIT.createEntity();
        em.persist(user);
        em.flush();
        updatedLeaveApplication.setApplicant(user);
        return updatedLeaveApplication;
    }

    @BeforeEach
    void initTest() {
        leaveApplication = createEntity(em);
    }

    @AfterEach
    void cleanup() {
        if (insertedLeaveApplication != null) {
            leaveApplicationRepository.delete(insertedLeaveApplication);
            insertedLeaveApplication = null;
        }
    }

    @Test
    @Transactional
    void createLeaveApplication() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the LeaveApplication
        var returnedLeaveApplication = om.readValue(
            restLeaveApplicationMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(leaveApplication)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            LeaveApplication.class
        );

        // Validate the LeaveApplication in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertLeaveApplicationUpdatableFieldsEquals(returnedLeaveApplication, getPersistedLeaveApplication(returnedLeaveApplication));

        insertedLeaveApplication = returnedLeaveApplication;
    }

    @Test
    @Transactional
    void createLeaveApplicationWithExistingId() throws Exception {
        // Create the LeaveApplication with an existing ID
        leaveApplication.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLeaveApplicationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(leaveApplication)))
            .andExpect(status().isBadRequest());

        // Validate the LeaveApplication in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkStartDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        leaveApplication.setStartDate(null);

        // Create the LeaveApplication, which fails.

        restLeaveApplicationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(leaveApplication)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkEndDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        leaveApplication.setEndDate(null);

        // Create the LeaveApplication, which fails.

        restLeaveApplicationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(leaveApplication)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkReasonIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        leaveApplication.setReason(null);

        // Create the LeaveApplication, which fails.

        restLeaveApplicationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(leaveApplication)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkSubmissionDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        leaveApplication.setSubmissionDate(null);

        // Create the LeaveApplication, which fails.

        restLeaveApplicationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(leaveApplication)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        leaveApplication.setStatus(null);

        // Create the LeaveApplication, which fails.

        restLeaveApplicationMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(leaveApplication)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllLeaveApplications() throws Exception {
        // Initialize the database
        insertedLeaveApplication = leaveApplicationRepository.saveAndFlush(leaveApplication);

        // Get all the leaveApplicationList
        restLeaveApplicationMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(leaveApplication.getId().intValue())))
            .andExpect(jsonPath("$.[*].startDate").value(hasItem(DEFAULT_START_DATE.toString())))
            .andExpect(jsonPath("$.[*].endDate").value(hasItem(DEFAULT_END_DATE.toString())))
            .andExpect(jsonPath("$.[*].reason").value(hasItem(DEFAULT_REASON)))
            .andExpect(jsonPath("$.[*].submissionDate").value(hasItem(DEFAULT_SUBMISSION_DATE.toString())))
            .andExpect(jsonPath("$.[*].status").value(hasItem(DEFAULT_STATUS.toString())))
            .andExpect(jsonPath("$.[*].rejectionReason").value(hasItem(DEFAULT_REJECTION_REASON)));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLeaveApplicationsWithEagerRelationshipsIsEnabled() throws Exception {
        when(leaveApplicationServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLeaveApplicationMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(leaveApplicationServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllLeaveApplicationsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(leaveApplicationServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restLeaveApplicationMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(leaveApplicationRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getLeaveApplication() throws Exception {
        // Initialize the database
        insertedLeaveApplication = leaveApplicationRepository.saveAndFlush(leaveApplication);

        // Get the leaveApplication
        restLeaveApplicationMockMvc
            .perform(get(ENTITY_API_URL_ID, leaveApplication.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(leaveApplication.getId().intValue()))
            .andExpect(jsonPath("$.startDate").value(DEFAULT_START_DATE.toString()))
            .andExpect(jsonPath("$.endDate").value(DEFAULT_END_DATE.toString()))
            .andExpect(jsonPath("$.reason").value(DEFAULT_REASON))
            .andExpect(jsonPath("$.submissionDate").value(DEFAULT_SUBMISSION_DATE.toString()))
            .andExpect(jsonPath("$.status").value(DEFAULT_STATUS.toString()))
            .andExpect(jsonPath("$.rejectionReason").value(DEFAULT_REJECTION_REASON));
    }

    @Test
    @Transactional
    void getNonExistingLeaveApplication() throws Exception {
        // Get the leaveApplication
        restLeaveApplicationMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingLeaveApplication() throws Exception {
        // Initialize the database
        insertedLeaveApplication = leaveApplicationRepository.saveAndFlush(leaveApplication);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the leaveApplication
        LeaveApplication updatedLeaveApplication = leaveApplicationRepository.findById(leaveApplication.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedLeaveApplication are not directly saved in db
        em.detach(updatedLeaveApplication);
        updatedLeaveApplication
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .reason(UPDATED_REASON)
            .submissionDate(UPDATED_SUBMISSION_DATE)
            .status(UPDATED_STATUS)
            .rejectionReason(UPDATED_REJECTION_REASON);

        restLeaveApplicationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedLeaveApplication.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedLeaveApplication))
            )
            .andExpect(status().isOk());

        // Validate the LeaveApplication in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedLeaveApplicationToMatchAllProperties(updatedLeaveApplication);
    }

    @Test
    @Transactional
    void putNonExistingLeaveApplication() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        leaveApplication.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLeaveApplicationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, leaveApplication.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(leaveApplication))
            )
            .andExpect(status().isBadRequest());

        // Validate the LeaveApplication in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLeaveApplication() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        leaveApplication.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLeaveApplicationMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(leaveApplication))
            )
            .andExpect(status().isBadRequest());

        // Validate the LeaveApplication in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLeaveApplication() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        leaveApplication.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLeaveApplicationMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(leaveApplication)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the LeaveApplication in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLeaveApplicationWithPatch() throws Exception {
        // Initialize the database
        insertedLeaveApplication = leaveApplicationRepository.saveAndFlush(leaveApplication);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the leaveApplication using partial update
        LeaveApplication partialUpdatedLeaveApplication = new LeaveApplication();
        partialUpdatedLeaveApplication.setId(leaveApplication.getId());

        partialUpdatedLeaveApplication.endDate(UPDATED_END_DATE).reason(UPDATED_REASON).submissionDate(UPDATED_SUBMISSION_DATE);

        restLeaveApplicationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLeaveApplication.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLeaveApplication))
            )
            .andExpect(status().isOk());

        // Validate the LeaveApplication in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLeaveApplicationUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedLeaveApplication, leaveApplication),
            getPersistedLeaveApplication(leaveApplication)
        );
    }

    @Test
    @Transactional
    void fullUpdateLeaveApplicationWithPatch() throws Exception {
        // Initialize the database
        insertedLeaveApplication = leaveApplicationRepository.saveAndFlush(leaveApplication);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the leaveApplication using partial update
        LeaveApplication partialUpdatedLeaveApplication = new LeaveApplication();
        partialUpdatedLeaveApplication.setId(leaveApplication.getId());

        partialUpdatedLeaveApplication
            .startDate(UPDATED_START_DATE)
            .endDate(UPDATED_END_DATE)
            .reason(UPDATED_REASON)
            .submissionDate(UPDATED_SUBMISSION_DATE)
            .status(UPDATED_STATUS)
            .rejectionReason(UPDATED_REJECTION_REASON);

        restLeaveApplicationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLeaveApplication.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLeaveApplication))
            )
            .andExpect(status().isOk());

        // Validate the LeaveApplication in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLeaveApplicationUpdatableFieldsEquals(
            partialUpdatedLeaveApplication,
            getPersistedLeaveApplication(partialUpdatedLeaveApplication)
        );
    }

    @Test
    @Transactional
    void patchNonExistingLeaveApplication() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        leaveApplication.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLeaveApplicationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, leaveApplication.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(leaveApplication))
            )
            .andExpect(status().isBadRequest());

        // Validate the LeaveApplication in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLeaveApplication() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        leaveApplication.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLeaveApplicationMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(leaveApplication))
            )
            .andExpect(status().isBadRequest());

        // Validate the LeaveApplication in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLeaveApplication() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        leaveApplication.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLeaveApplicationMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(leaveApplication)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the LeaveApplication in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLeaveApplication() throws Exception {
        // Initialize the database
        insertedLeaveApplication = leaveApplicationRepository.saveAndFlush(leaveApplication);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the leaveApplication
        restLeaveApplicationMockMvc
            .perform(delete(ENTITY_API_URL_ID, leaveApplication.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return leaveApplicationRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected LeaveApplication getPersistedLeaveApplication(LeaveApplication leaveApplication) {
        return leaveApplicationRepository.findById(leaveApplication.getId()).orElseThrow();
    }

    protected void assertPersistedLeaveApplicationToMatchAllProperties(LeaveApplication expectedLeaveApplication) {
        assertLeaveApplicationAllPropertiesEquals(expectedLeaveApplication, getPersistedLeaveApplication(expectedLeaveApplication));
    }

    protected void assertPersistedLeaveApplicationToMatchUpdatableProperties(LeaveApplication expectedLeaveApplication) {
        assertLeaveApplicationAllUpdatablePropertiesEquals(
            expectedLeaveApplication,
            getPersistedLeaveApplication(expectedLeaveApplication)
        );
    }
}
