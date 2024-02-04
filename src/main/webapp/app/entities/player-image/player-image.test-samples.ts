import { IPlayerImage, NewPlayerImage } from './player-image.model';

export const sampleWithRequiredData: IPlayerImage = {
  id: 12648,
};

export const sampleWithPartialData: IPlayerImage = {
  id: 3117,
  imageData: '../fake-data/blob/hipster.txt',
};

export const sampleWithFullData: IPlayerImage = {
  id: 19752,
  imageData: '../fake-data/blob/hipster.txt',
};

export const sampleWithNewData: NewPlayerImage = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
