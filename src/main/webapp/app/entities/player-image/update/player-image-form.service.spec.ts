import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../player-image.test-samples';

import { PlayerImageFormService } from './player-image-form.service';

describe('PlayerImage Form Service', () => {
  let service: PlayerImageFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(PlayerImageFormService);
  });

  describe('Service methods', () => {
    describe('createPlayerImageFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createPlayerImageFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            imageData: expect.any(Object),
          }),
        );
      });

      it('passing IPlayerImage should create a new form with FormGroup', () => {
        const formGroup = service.createPlayerImageFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            imageData: expect.any(Object),
          }),
        );
      });
    });

    describe('getPlayerImage', () => {
      it('should return NewPlayerImage for default PlayerImage initial value', () => {
        const formGroup = service.createPlayerImageFormGroup(sampleWithNewData);

        const playerImage = service.getPlayerImage(formGroup) as any;

        expect(playerImage).toMatchObject(sampleWithNewData);
      });

      it('should return NewPlayerImage for empty PlayerImage initial value', () => {
        const formGroup = service.createPlayerImageFormGroup();

        const playerImage = service.getPlayerImage(formGroup) as any;

        expect(playerImage).toMatchObject({});
      });

      it('should return IPlayerImage', () => {
        const formGroup = service.createPlayerImageFormGroup(sampleWithRequiredData);

        const playerImage = service.getPlayerImage(formGroup) as any;

        expect(playerImage).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IPlayerImage should not enable id FormControl', () => {
        const formGroup = service.createPlayerImageFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewPlayerImage should disable id FormControl', () => {
        const formGroup = service.createPlayerImageFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
