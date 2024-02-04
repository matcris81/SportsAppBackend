import dayjs from 'dayjs/esm';

import { INotification, NewNotification } from './notification.model';

export const sampleWithRequiredData: INotification = {
  id: 22330,
};

export const sampleWithPartialData: INotification = {
  id: 15809,
  type: 'concerning geez',
  status: 'UNREAD',
};

export const sampleWithFullData: INotification = {
  id: 3920,
  type: 'psst',
  content: '../fake-data/blob/hipster.txt',
  status: 'READ',
  creationDate: dayjs('2024-02-03T17:22'),
};

export const sampleWithNewData: NewNotification = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
