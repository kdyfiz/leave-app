import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Col, Row } from 'reactstrap';
import { TextFormat, Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './leave-application.reducer';

export const LeaveApplicationDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const leaveApplicationEntity = useAppSelector(state => state.leaveApplication.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="leaveApplicationDetailsHeading">
          <Translate contentKey="leaveApp.leaveApplication.detail.title">LeaveApplication</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{leaveApplicationEntity.id}</dd>
          <dt>
            <span id="startDate">
              <Translate contentKey="leaveApp.leaveApplication.startDate">Start Date</Translate>
            </span>
          </dt>
          <dd>
            {leaveApplicationEntity.startDate ? (
              <TextFormat value={leaveApplicationEntity.startDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="endDate">
              <Translate contentKey="leaveApp.leaveApplication.endDate">End Date</Translate>
            </span>
          </dt>
          <dd>
            {leaveApplicationEntity.endDate ? (
              <TextFormat value={leaveApplicationEntity.endDate} type="date" format={APP_LOCAL_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="reason">
              <Translate contentKey="leaveApp.leaveApplication.reason">Reason</Translate>
            </span>
          </dt>
          <dd>{leaveApplicationEntity.reason}</dd>
          <dt>
            <span id="submissionDate">
              <Translate contentKey="leaveApp.leaveApplication.submissionDate">Submission Date</Translate>
            </span>
          </dt>
          <dd>
            {leaveApplicationEntity.submissionDate ? (
              <TextFormat value={leaveApplicationEntity.submissionDate} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <span id="status">
              <Translate contentKey="leaveApp.leaveApplication.status">Status</Translate>
            </span>
          </dt>
          <dd>{leaveApplicationEntity.status}</dd>
          <dt>
            <span id="rejectionReason">
              <Translate contentKey="leaveApp.leaveApplication.rejectionReason">Rejection Reason</Translate>
            </span>
          </dt>
          <dd>{leaveApplicationEntity.rejectionReason}</dd>
          <dt>
            <Translate contentKey="leaveApp.leaveApplication.applicant">Applicant</Translate>
          </dt>
          <dd>{leaveApplicationEntity.applicant ? leaveApplicationEntity.applicant.login : ''}</dd>
        </dl>
        <Button tag={Link} to="/leave-application" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/leave-application/${leaveApplicationEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

export default LeaveApplicationDetail;
