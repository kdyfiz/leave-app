import {
  entityConfirmDeleteButtonSelector,
  entityCreateButtonSelector,
  entityCreateCancelButtonSelector,
  entityCreateSaveButtonSelector,
  entityDeleteButtonSelector,
  entityDetailsBackButtonSelector,
  entityDetailsButtonSelector,
  entityEditButtonSelector,
  entityTableSelector,
} from '../../support/entity';

describe('LeaveApplication e2e test', () => {
  const leaveApplicationPageUrl = '/leave-application';
  const leaveApplicationPageUrlPattern = new RegExp('/leave-application(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const leaveApplicationSample = {"startDate":"2025-05-22","endDate":"2025-05-22","reason":"whoever yum","submissionDate":"2025-05-21T10:16:24.182Z","status":"PENDING"};

  let leaveApplication;
  // let user;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/users',
      body: {"login":"N{@Ip\\7a8rMTj\\+K\\fNoO9","firstName":"Amalia","lastName":"Keebler","email":"Antonietta_Kemmer@yahoo.com","imageUrl":"blah","langKey":"sandbar fu"},
    }).then(({ body }) => {
      user = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/leave-applications+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/leave-applications').as('postEntityRequest');
    cy.intercept('DELETE', '/api/leave-applications/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/users', {
      statusCode: 200,
      body: [user],
    });

  });
   */

  afterEach(() => {
    if (leaveApplication) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/leave-applications/${leaveApplication.id}`,
      }).then(() => {
        leaveApplication = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (user) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/users/${user.id}`,
      }).then(() => {
        user = undefined;
      });
    }
  });
   */

  it('LeaveApplications menu should load LeaveApplications page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('leave-application');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('LeaveApplication').should('exist');
    cy.url().should('match', leaveApplicationPageUrlPattern);
  });

  describe('LeaveApplication page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(leaveApplicationPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create LeaveApplication page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/leave-application/new$'));
        cy.getEntityCreateUpdateHeading('LeaveApplication');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', leaveApplicationPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/leave-applications',
          body: {
            ...leaveApplicationSample,
            applicant: user,
          },
        }).then(({ body }) => {
          leaveApplication = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/leave-applications+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/leave-applications?page=0&size=20>; rel="last",<http://localhost/api/leave-applications?page=0&size=20>; rel="first"',
              },
              body: [leaveApplication],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(leaveApplicationPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(leaveApplicationPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details LeaveApplication page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('leaveApplication');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', leaveApplicationPageUrlPattern);
      });

      it('edit button click should load edit LeaveApplication page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('LeaveApplication');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', leaveApplicationPageUrlPattern);
      });

      it('edit button click should load edit LeaveApplication page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('LeaveApplication');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', leaveApplicationPageUrlPattern);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of LeaveApplication', () => {
        cy.intercept('GET', '/api/leave-applications/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('leaveApplication').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', leaveApplicationPageUrlPattern);

        leaveApplication = undefined;
      });
    });
  });

  describe('new LeaveApplication page', () => {
    beforeEach(() => {
      cy.visit(`${leaveApplicationPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('LeaveApplication');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of LeaveApplication', () => {
      cy.get(`[data-cy="startDate"]`).type('2025-05-22');
      cy.get(`[data-cy="startDate"]`).blur();
      cy.get(`[data-cy="startDate"]`).should('have.value', '2025-05-22');

      cy.get(`[data-cy="endDate"]`).type('2025-05-21');
      cy.get(`[data-cy="endDate"]`).blur();
      cy.get(`[data-cy="endDate"]`).should('have.value', '2025-05-21');

      cy.get(`[data-cy="reason"]`).type('whereasXXX');
      cy.get(`[data-cy="reason"]`).should('have.value', 'whereasXXX');

      cy.get(`[data-cy="submissionDate"]`).type('2025-05-21T22:06');
      cy.get(`[data-cy="submissionDate"]`).blur();
      cy.get(`[data-cy="submissionDate"]`).should('have.value', '2025-05-21T22:06');

      cy.get(`[data-cy="status"]`).select('PENDING');

      cy.get(`[data-cy="rejectionReason"]`).type('reorient behold wilt');
      cy.get(`[data-cy="rejectionReason"]`).should('have.value', 'reorient behold wilt');

      cy.get(`[data-cy="applicant"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        leaveApplication = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', leaveApplicationPageUrlPattern);
    });
  });
});
