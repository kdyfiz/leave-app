import React from 'react';
import { Route } from 'react-router';

import ErrorBoundaryRoutes from 'app/shared/error/error-boundary-routes';

import LeaveApplication from './leave-application';
import LeaveApplicationDetail from './leave-application-detail';
import LeaveApplicationUpdate from './leave-application-update';
import LeaveApplicationDeleteDialog from './leave-application-delete-dialog';

const LeaveApplicationRoutes = () => (
  <ErrorBoundaryRoutes>
    <Route index element={<LeaveApplication />} />
    <Route path="new" element={<LeaveApplicationUpdate />} />
    <Route path=":id">
      <Route index element={<LeaveApplicationDetail />} />
      <Route path="edit" element={<LeaveApplicationUpdate />} />
      <Route path="delete" element={<LeaveApplicationDeleteDialog />} />
    </Route>
  </ErrorBoundaryRoutes>
);

export default LeaveApplicationRoutes;
