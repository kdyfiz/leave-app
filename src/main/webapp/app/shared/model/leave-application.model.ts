import dayjs from 'dayjs';
import { IUser } from 'app/shared/model/user.model';
import { LeaveStatus } from 'app/shared/model/enumerations/leave-status.model';

export interface ILeaveApplication {
  id?: number;
  startDate?: dayjs.Dayjs;
  endDate?: dayjs.Dayjs;
  reason?: string;
  submissionDate?: dayjs.Dayjs;
  status?: keyof typeof LeaveStatus;
  rejectionReason?: string | null;
  applicant?: IUser;
}

export const defaultValue: Readonly<ILeaveApplication> = {};
