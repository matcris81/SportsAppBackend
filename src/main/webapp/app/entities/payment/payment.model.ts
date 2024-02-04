import dayjs from 'dayjs/esm';
import { IPlayer } from 'app/entities/player/player.model';
import { PaymentStatus } from 'app/entities/enumerations/payment-status.model';

export interface IPayment {
  id: number;
  amount?: number | null;
  dateTime?: dayjs.Dayjs | null;
  status?: keyof typeof PaymentStatus | null;
  player?: IPlayer | null;
}

export type NewPayment = Omit<IPayment, 'id'> & { id: null };
