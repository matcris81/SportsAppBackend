import { IPlayer } from 'app/entities/player/player.model';

export interface IPlayerImage {
  id: number;
  imageData?: string | null;
  player?: IPlayer | null;
}

export type NewPlayerImage = Omit<IPlayerImage, 'id'> & { id: null };
