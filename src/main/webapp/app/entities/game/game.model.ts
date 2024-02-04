import dayjs from 'dayjs/esm';
import { IPlayer } from 'app/entities/player/player.model';

export interface IGame {
  id: number;
  gameDate?: dayjs.Dayjs | null;
  price?: number | null;
  size?: number | null;
  description?: string | null;
  venueId?: number | null;
  sportId?: number | null;
  organizer?: IPlayer | null;
  players?: IPlayer[] | null;
}

export type NewGame = Omit<IGame, 'id'> & { id: null };
