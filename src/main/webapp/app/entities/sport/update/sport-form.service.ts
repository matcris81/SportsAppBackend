import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { ISport, NewSport } from '../sport.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts ISport for edit and NewSportFormGroupInput for create.
 */
type SportFormGroupInput = ISport | PartialWithRequiredKeyOf<NewSport>;

type SportFormDefaults = Pick<NewSport, 'id'>;

type SportFormGroupContent = {
  id: FormControl<ISport['id'] | NewSport['id']>;
  sportName: FormControl<ISport['sportName']>;
};

export type SportFormGroup = FormGroup<SportFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class SportFormService {
  createSportFormGroup(sport: SportFormGroupInput = { id: null }): SportFormGroup {
    const sportRawValue = {
      ...this.getFormDefaults(),
      ...sport,
    };
    return new FormGroup<SportFormGroupContent>({
      id: new FormControl(
        { value: sportRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      sportName: new FormControl(sportRawValue.sportName, {
        validators: [Validators.required],
      }),
    });
  }

  getSport(form: SportFormGroup): ISport | NewSport {
    return form.getRawValue() as ISport | NewSport;
  }

  resetForm(form: SportFormGroup, sport: SportFormGroupInput): void {
    const sportRawValue = { ...this.getFormDefaults(), ...sport };
    form.reset(
      {
        ...sportRawValue,
        id: { value: sportRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): SportFormDefaults {
    return {
      id: null,
    };
  }
}
