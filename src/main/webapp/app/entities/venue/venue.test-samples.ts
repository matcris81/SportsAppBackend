import { IVenue, NewVenue } from './venue.model';

export const sampleWithRequiredData: IVenue = {
  id: 835,
};

export const sampleWithPartialData: IVenue = {
  id: 27631,
  venueName: 'admire encompass delirious',
  address: 'silver eek',
};

export const sampleWithFullData: IVenue = {
  id: 22245,
  venueName: 'unimpressively furthermore',
  address: 'meh credential',
};

export const sampleWithNewData: NewVenue = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
