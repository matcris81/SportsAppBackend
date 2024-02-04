import { ISport, NewSport } from './sport.model';

export const sampleWithRequiredData: ISport = {
  id: 6414,
  sportName: 'spritz',
};

export const sampleWithPartialData: ISport = {
  id: 4527,
  sportName: 'gosh whoa',
};

export const sampleWithFullData: ISport = {
  id: 1262,
  sportName: 'the considering since',
};

export const sampleWithNewData: NewSport = {
  sportName: 'ha',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
