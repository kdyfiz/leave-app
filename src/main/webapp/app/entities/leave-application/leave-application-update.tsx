import React, { useEffect } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Button, Col, FormText, Row } from 'reactstrap';
import { Translate, ValidatedField, ValidatedForm, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { LeaveStatus } from 'app/shared/model/enumerations/leave-status.model';
import { createEntity, getEntity, reset, updateEntity } from './leave-application.reducer';

export const LeaveApplicationUpdate = () => {
  const dispatch = useAppDispatch();

  const navigate = useNavigate();

  const { id } = useParams<'id'>();
  const isNew = id === undefined;

  const users = useAppSelector(state => state.userManagement.users);
  const leaveApplicationEntity = useAppSelector(state => state.leaveApplication.entity);
  const loading = useAppSelector(state => state.leaveApplication.loading);
  const updating = useAppSelector(state => state.leaveApplication.updating);
  const updateSuccess = useAppSelector(state => state.leaveApplication.updateSuccess);
  const leaveStatusValues = Object.keys(LeaveStatus);

  const handleClose = () => {
    navigate(`/leave-application${location.search}`);
  };

  useEffect(() => {
    if (isNew) {
      dispatch(reset());
    } else {
      dispatch(getEntity(id));
    }

    dispatch(getUsers({}));
  }, []);

  useEffect(() => {
    if (updateSuccess) {
      handleClose();
    }
  }, [updateSuccess]);

  const saveEntity = values => {
    if (values.id !== undefined && typeof values.id !== 'number') {
      values.id = Number(values.id);
    }
    values.submissionDate = convertDateTimeToServer(values.submissionDate);

    const entity = {
      ...leaveApplicationEntity,
      ...values,
      applicant: users.find(it => it.id.toString() === values.applicant?.toString()),
    };

    if (isNew) {
      dispatch(createEntity(entity));
    } else {
      dispatch(updateEntity(entity));
    }
  };

  const defaultValues = () =>
    isNew
      ? {
          submissionDate: displayDefaultDateTime(),
        }
      : {
          status: 'PENDING',
          ...leaveApplicationEntity,
          submissionDate: convertDateTimeFromServer(leaveApplicationEntity.submissionDate),
          applicant: leaveApplicationEntity?.applicant?.id,
        };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="leaveApp.leaveApplication.home.createOrEditLabel" data-cy="LeaveApplicationCreateUpdateHeading">
            <Translate contentKey="leaveApp.leaveApplication.home.createOrEditLabel">Create or edit a LeaveApplication</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <ValidatedForm defaultValues={defaultValues()} onSubmit={saveEntity}>
              {!isNew ? (
                <ValidatedField
                  name="id"
                  required
                  readOnly
                  id="leave-application-id"
                  label={translate('global.field.id')}
                  validate={{ required: true }}
                />
              ) : null}
              <ValidatedField
                label={translate('leaveApp.leaveApplication.startDate')}
                id="leave-application-startDate"
                name="startDate"
                data-cy="startDate"
                type="date"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('leaveApp.leaveApplication.endDate')}
                id="leave-application-endDate"
                name="endDate"
                data-cy="endDate"
                type="date"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('leaveApp.leaveApplication.reason')}
                id="leave-application-reason"
                name="reason"
                data-cy="reason"
                type="text"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                  minLength: { value: 10, message: translate('entity.validation.minlength', { min: 10 }) },
                  maxLength: { value: 500, message: translate('entity.validation.maxlength', { max: 500 }) },
                }}
              />
              <ValidatedField
                label={translate('leaveApp.leaveApplication.submissionDate')}
                id="leave-application-submissionDate"
                name="submissionDate"
                data-cy="submissionDate"
                type="datetime-local"
                placeholder="YYYY-MM-DD HH:mm"
                validate={{
                  required: { value: true, message: translate('entity.validation.required') },
                }}
              />
              <ValidatedField
                label={translate('leaveApp.leaveApplication.status')}
                id="leave-application-status"
                name="status"
                data-cy="status"
                type="select"
              >
                {leaveStatusValues.map(leaveStatus => (
                  <option value={leaveStatus} key={leaveStatus}>
                    {translate(`leaveApp.LeaveStatus.${leaveStatus}`)}
                  </option>
                ))}
              </ValidatedField>
              <ValidatedField
                label={translate('leaveApp.leaveApplication.rejectionReason')}
                id="leave-application-rejectionReason"
                name="rejectionReason"
                data-cy="rejectionReason"
                type="text"
                validate={{
                  maxLength: { value: 500, message: translate('entity.validation.maxlength', { max: 500 }) },
                }}
              />
              <ValidatedField
                id="leave-application-applicant"
                name="applicant"
                data-cy="applicant"
                label={translate('leaveApp.leaveApplication.applicant')}
                type="select"
                required
              >
                <option value="" key="0" />
                {users
                  ? users.map(otherEntity => (
                      <option value={otherEntity.id} key={otherEntity.id}>
                        {otherEntity.login}
                      </option>
                    ))
                  : null}
              </ValidatedField>
              <FormText>
                <Translate contentKey="entity.validation.required">This field is required.</Translate>
              </FormText>
              <Button tag={Link} id="cancel-save" data-cy="entityCreateCancelButton" to="/leave-application" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </ValidatedForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

export default LeaveApplicationUpdate;
