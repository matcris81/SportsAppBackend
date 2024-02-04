import dayjs from 'dayjs/esm';
import { IPlayerImage } from 'app/entities/player-image/player-image.model';
import { IGame } from 'app/entities/game/game.model';
import { INotification } from 'app/entities/notification/notification.model';
import { IPayment } from 'app/entities/payment/payment.model';
import { IVenue } from 'app/entities/venue/venue.model';
import { Gender } from 'app/entities/enumerations/gender.model';

export interface IPlayer {
  id: string;
  name?: string | null;
  username?: string | null;
  email?: string | null;
  password?: string | null;
  dob?: dayjs.Dayjs | null;
  gender?: keyof typeof Gender | null;
  phoneNumber?: string | null;
  playerImage?: IPlayerImage | null;
  organizedGames?: IGame[] | null;
  notifications?: INotification[] | null;
  payments?: IPayment[] | null;
  games?: IGame[] | null;
  venues?: IVenue[] | null;
}

export type NewPlayer = Omit<IPlayer, 'id'> & { id: null };
