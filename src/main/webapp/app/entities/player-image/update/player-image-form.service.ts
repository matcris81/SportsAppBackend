import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IPlayerImage, NewPlayerImage } from '../player-image.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IPlayerImage for edit and NewPlayerImageFormGroupInput for create.
 */
type PlayerImageFormGroupInput = IPlayerImage | PartialWithRequiredKeyOf<NewPlayerImage>;

type PlayerImageFormDefaults = Pick<NewPlayerImage, 'id'>;

type PlayerImageFormGroupContent = {
  id: FormControl<IPlayerImage['id'] | NewPlayerImage['id']>;
  imageData: FormControl<IPlayerImage['imageData']>;
};

export type PlayerImageFormGroup = FormGroup<PlayerImageFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class PlayerImageFormService {
  createPlayerImageFormGroup(playerImage: PlayerImageFormGroupInput = { id: null }): PlayerImageFormGroup {
    const playerImageRawValue = {
      ...this.getFormDefaults(),
      ...playerImage,
    };
    return new FormGroup<PlayerImageFormGroupContent>({
      id: new FormControl(
        { value: playerImageRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      imageData: new FormControl(playerImageRawValue.imageData),
    });
  }

  getPlayerImage(form: PlayerImageFormGroup): IPlayerImage | NewPlayerImage {
    return form.getRawValue() as IPlayerImage | NewPlayerImage;
  }

  resetForm(form: PlayerImageFormGroup, playerImage: PlayerImageFormGroupInput): void {
    const playerImageRawValue = { ...this.getFormDefaults(), ...playerImage };
    form.reset(
      {
        ...playerImageRawValue,
        id: { value: playerImageRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): PlayerImageFormDefaults {
    return {
      id: null,
    };
  }
}
