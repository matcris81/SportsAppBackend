import dayjs from 'dayjs/esm';
import { IPlayer } from 'app/entities/player/player.model';
import { NotificationStatus } from 'app/entities/enumerations/notification-status.model';

export interface INotification {
  id: number;
  type?: string | null;
  content?: string | null;
  status?: keyof typeof NotificationStatus | null;
  creationDate?: dayjs.Dayjs | null;
  player?: IPlayer | null;
}

export type NewNotification = Omit<INotification, 'id'> & { id: null };
