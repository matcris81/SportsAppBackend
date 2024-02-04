import dayjs from 'dayjs/esm';

import { IGame, NewGame } from './game.model';

export const sampleWithRequiredData: IGame = {
  id: 28841,
};

export const sampleWithPartialData: IGame = {
  id: 25292,
  gameDate: dayjs('2024-02-03T18:01'),
  price: 29233.96,
};

export const sampleWithFullData: IGame = {
  id: 20730,
  gameDate: dayjs('2024-02-04T07:34'),
  price: 22846.17,
  size: 6634,
  description: 'selfishly ack yuck',
  venueId: 3131,
  sportId: 24315,
};

export const sampleWithNewData: NewGame = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
