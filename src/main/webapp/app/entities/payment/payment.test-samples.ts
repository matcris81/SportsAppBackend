import dayjs from 'dayjs/esm';

import { IPayment, NewPayment } from './payment.model';

export const sampleWithRequiredData: IPayment = {
  id: 24821,
};

export const sampleWithPartialData: IPayment = {
  id: 22843,
  status: 'FAILED',
};

export const sampleWithFullData: IPayment = {
  id: 29979,
  amount: 10311.62,
  dateTime: dayjs('2024-02-04T08:27'),
  status: 'FAILED',
};

export const sampleWithNewData: NewPayment = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
