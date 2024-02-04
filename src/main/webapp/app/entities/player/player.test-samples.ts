import dayjs from 'dayjs/esm';

import { IPlayer, NewPlayer } from './player.model';

export const sampleWithRequiredData: IPlayer = {
  id: 'a23857fc-f1ac-496f-84f9-64ccdf08acf0',
};

export const sampleWithPartialData: IPlayer = {
  id: '6918c259-7f28-4369-9dba-26e55fd6e9bf',
  name: 'once',
  username: 'yet arid',
  password: 'gaze',
  dob: dayjs('2024-02-04'),
  gender: 'MALE',
};

export const sampleWithFullData: IPlayer = {
  id: 'e2e55104-d3aa-4712-a49d-cf6fd36fc730',
  name: 'someplace gah dismember',
  username: 'when unless',
  email: 'Jermey.Sawayn@yahoo.com',
  password: 'boo',
  dob: dayjs('2024-02-03'),
  gender: 'MALE',
  phoneNumber: 'ew woot sugar',
};

export const sampleWithNewData: NewPlayer = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
