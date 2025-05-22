import React, { useEffect, useState } from 'react';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { Button, Table } from 'reactstrap';
import { JhiItemCount, JhiPagination, TextFormat, Translate, getPaginationState } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faSort, faSortDown, faSortUp } from '@fortawesome/free-solid-svg-icons';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';
import { ASC, DESC, ITEMS_PER_PAGE, SORT } from 'app/shared/util/pagination.constants';
import { overridePaginationStateWithQueryParams } from 'app/shared/util/entity-utils';
import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntities } from './leave-application.reducer';

export const LeaveApplication = () => {
  const dispatch = useAppDispatch();

  const pageLocation = useLocation();
  const navigate = useNavigate();

  const [paginationState, setPaginationState] = useState(
    overridePaginationStateWithQueryParams(getPaginationState(pageLocation, ITEMS_PER_PAGE, 'id'), pageLocation.search),
  );

  const leaveApplicationList = useAppSelector(state => state.leaveApplication.entities);
  const loading = useAppSelector(state => state.leaveApplication.loading);
  const totalItems = useAppSelector(state => state.leaveApplication.totalItems);

  const getAllEntities = () => {
    dispatch(
      getEntities({
        page: paginationState.activePage - 1,
        size: paginationState.itemsPerPage,
        sort: `${paginationState.sort},${paginationState.order}`,
      }),
    );
  };

  const sortEntities = () => {
    getAllEntities();
    const endURL = `?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`;
    if (pageLocation.search !== endURL) {
      navigate(`${pageLocation.pathname}${endURL}`);
    }
  };

  useEffect(() => {
    sortEntities();
  }, [paginationState.activePage, paginationState.order, paginationState.sort]);

  useEffect(() => {
    const params = new URLSearchParams(pageLocation.search);
    const page = params.get('page');
    const sort = params.get(SORT);
    if (page && sort) {
      const sortSplit = sort.split(',');
      setPaginationState({
        ...paginationState,
        activePage: +page,
        sort: sortSplit[0],
        order: sortSplit[1],
      });
    }
  }, [pageLocation.search]);

  const sort = p => () => {
    setPaginationState({
      ...paginationState,
      order: paginationState.order === ASC ? DESC : ASC,
      sort: p,
    });
  };

  const handlePagination = currentPage =>
    setPaginationState({
      ...paginationState,
      activePage: currentPage,
    });

  const handleSyncList = () => {
    sortEntities();
  };

  const getSortIconByFieldName = (fieldName: string) => {
    const sortFieldName = paginationState.sort;
    const order = paginationState.order;
    if (sortFieldName !== fieldName) {
      return faSort;
    }
    return order === ASC ? faSortUp : faSortDown;
  };

  return (
    <div>
      <h2 id="leave-application-heading" data-cy="LeaveApplicationHeading">
        <Translate contentKey="leaveApp.leaveApplication.home.title">Leave Applications</Translate>
        <div className="d-flex justify-content-end">
          <Button className="me-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="leaveApp.leaveApplication.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to="/leave-application/new" className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="leaveApp.leaveApplication.home.createLabel">Create new Leave Application</Translate>
          </Link>
        </div>
      </h2>
      <div className="table-responsive">
        {leaveApplicationList && leaveApplicationList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th className="hand" onClick={sort('id')}>
                  <Translate contentKey="leaveApp.leaveApplication.id">ID</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('id')} />
                </th>
                <th className="hand" onClick={sort('startDate')}>
                  <Translate contentKey="leaveApp.leaveApplication.startDate">Start Date</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('startDate')} />
                </th>
                <th className="hand" onClick={sort('endDate')}>
                  <Translate contentKey="leaveApp.leaveApplication.endDate">End Date</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('endDate')} />
                </th>
                <th className="hand" onClick={sort('reason')}>
                  <Translate contentKey="leaveApp.leaveApplication.reason">Reason</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('reason')} />
                </th>
                <th className="hand" onClick={sort('submissionDate')}>
                  <Translate contentKey="leaveApp.leaveApplication.submissionDate">Submission Date</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('submissionDate')} />
                </th>
                <th className="hand" onClick={sort('status')}>
                  <Translate contentKey="leaveApp.leaveApplication.status">Status</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('status')} />
                </th>
                <th className="hand" onClick={sort('rejectionReason')}>
                  <Translate contentKey="leaveApp.leaveApplication.rejectionReason">Rejection Reason</Translate>{' '}
                  <FontAwesomeIcon icon={getSortIconByFieldName('rejectionReason')} />
                </th>
                <th>
                  <Translate contentKey="leaveApp.leaveApplication.applicant">Applicant</Translate> <FontAwesomeIcon icon="sort" />
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {leaveApplicationList.map((leaveApplication, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`/leave-application/${leaveApplication.id}`} color="link" size="sm">
                      {leaveApplication.id}
                    </Button>
                  </td>
                  <td>
                    {leaveApplication.startDate ? (
                      <TextFormat type="date" value={leaveApplication.startDate} format={APP_LOCAL_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    {leaveApplication.endDate ? (
                      <TextFormat type="date" value={leaveApplication.endDate} format={APP_LOCAL_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>{leaveApplication.reason}</td>
                  <td>
                    {leaveApplication.submissionDate ? (
                      <TextFormat type="date" value={leaveApplication.submissionDate} format={APP_DATE_FORMAT} />
                    ) : null}
                  </td>
                  <td>
                    <Translate contentKey={`leaveApp.LeaveStatus.${leaveApplication.status}`} />
                  </td>
                  <td>{leaveApplication.rejectionReason}</td>
                  <td>{leaveApplication.applicant ? leaveApplication.applicant.login : ''}</td>
                  <td className="text-end">
                    <div className="btn-group flex-btn-group-container">
                      <Button
                        tag={Link}
                        to={`/leave-application/${leaveApplication.id}`}
                        color="info"
                        size="sm"
                        data-cy="entityDetailsButton"
                      >
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button
                        tag={Link}
                        to={`/leave-application/${leaveApplication.id}/edit?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`}
                        color="primary"
                        size="sm"
                        data-cy="entityEditButton"
                      >
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button
                        onClick={() =>
                          (window.location.href = `/leave-application/${leaveApplication.id}/delete?page=${paginationState.activePage}&sort=${paginationState.sort},${paginationState.order}`)
                        }
                        color="danger"
                        size="sm"
                        data-cy="entityDeleteButton"
                      >
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="leaveApp.leaveApplication.home.notFound">No Leave Applications found</Translate>
            </div>
          )
        )}
      </div>
      {totalItems ? (
        <div className={leaveApplicationList && leaveApplicationList.length > 0 ? '' : 'd-none'}>
          <div className="justify-content-center d-flex">
            <JhiItemCount page={paginationState.activePage} total={totalItems} itemsPerPage={paginationState.itemsPerPage} i18nEnabled />
          </div>
          <div className="justify-content-center d-flex">
            <JhiPagination
              activePage={paginationState.activePage}
              onSelect={handlePagination}
              maxButtons={5}
              itemsPerPage={paginationState.itemsPerPage}
              totalItems={totalItems}
            />
          </div>
        </div>
      ) : (
        ''
      )}
    </div>
  );
};

export default LeaveApplication;
