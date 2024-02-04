import { IPlayer } from 'app/entities/player/player.model';

export interface IVenue {
  id: number;
  venueName?: string | null;
  address?: string | null;
  players?: IPlayer[] | null;
}

export type NewVenue = Omit<IVenue, 'id'> & { id: null };
