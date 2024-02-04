import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../sport.test-samples';

import { SportFormService } from './sport-form.service';

describe('Sport Form Service', () => {
  let service: SportFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SportFormService);
  });

  describe('Service methods', () => {
    describe('createSportFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createSportFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            sportName: expect.any(Object),
          }),
        );
      });

      it('passing ISport should create a new form with FormGroup', () => {
        const formGroup = service.createSportFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            sportName: expect.any(Object),
          }),
        );
      });
    });

    describe('getSport', () => {
      it('should return NewSport for default Sport initial value', () => {
        const formGroup = service.createSportFormGroup(sampleWithNewData);

        const sport = service.getSport(formGroup) as any;

        expect(sport).toMatchObject(sampleWithNewData);
      });

      it('should return NewSport for empty Sport initial value', () => {
        const formGroup = service.createSportFormGroup();

        const sport = service.getSport(formGroup) as any;

        expect(sport).toMatchObject({});
      });

      it('should return ISport', () => {
        const formGroup = service.createSportFormGroup(sampleWithRequiredData);

        const sport = service.getSport(formGroup) as any;

        expect(sport).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ISport should not enable id FormControl', () => {
        const formGroup = service.createSportFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewSport should disable id FormControl', () => {
        const formGroup = service.createSportFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
