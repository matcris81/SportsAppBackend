import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IGame, NewGame } from '../game.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IGame for edit and NewGameFormGroupInput for create.
 */
type GameFormGroupInput = IGame | PartialWithRequiredKeyOf<NewGame>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IGame | NewGame> = Omit<T, 'gameDate'> & {
  gameDate?: string | null;
};

type GameFormRawValue = FormValueOf<IGame>;

type NewGameFormRawValue = FormValueOf<NewGame>;

type GameFormDefaults = Pick<NewGame, 'id' | 'gameDate'>;

type GameFormGroupContent = {
  id: FormControl<GameFormRawValue['id'] | NewGame['id']>;
  gameDate: FormControl<GameFormRawValue['gameDate']>;
  price: FormControl<GameFormRawValue['price']>;
  size: FormControl<GameFormRawValue['size']>;
  description: FormControl<GameFormRawValue['description']>;
  venueId: FormControl<GameFormRawValue['venueId']>;
  sportId: FormControl<GameFormRawValue['sportId']>;
  organizer: FormControl<GameFormRawValue['organizer']>;
};

export type GameFormGroup = FormGroup<GameFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class GameFormService {
  createGameFormGroup(game: GameFormGroupInput = { id: null }): GameFormGroup {
    const gameRawValue = this.convertGameToGameRawValue({
      ...this.getFormDefaults(),
      ...game,
    });
    return new FormGroup<GameFormGroupContent>({
      id: new FormControl(
        { value: gameRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        },
      ),
      gameDate: new FormControl(gameRawValue.gameDate),
      price: new FormControl(gameRawValue.price),
      size: new FormControl(gameRawValue.size),
      description: new FormControl(gameRawValue.description),
      venueId: new FormControl(gameRawValue.venueId),
      sportId: new FormControl(gameRawValue.sportId),
      organizer: new FormControl(gameRawValue.organizer),
    });
  }

  getGame(form: GameFormGroup): IGame | NewGame {
    return this.convertGameRawValueToGame(form.getRawValue() as GameFormRawValue | NewGameFormRawValue);
  }

  resetForm(form: GameFormGroup, game: GameFormGroupInput): void {
    const gameRawValue = this.convertGameToGameRawValue({ ...this.getFormDefaults(), ...game });
    form.reset(
      {
        ...gameRawValue,
        id: { value: gameRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */,
    );
  }

  private getFormDefaults(): GameFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      gameDate: currentTime,
    };
  }

  private convertGameRawValueToGame(rawGame: GameFormRawValue | NewGameFormRawValue): IGame | NewGame {
    return {
      ...rawGame,
      gameDate: dayjs(rawGame.gameDate, DATE_TIME_FORMAT),
    };
  }

  private convertGameToGameRawValue(
    game: IGame | (Partial<NewGame> & GameFormDefaults),
  ): GameFormRawValue | PartialWithRequiredKeyOf<NewGameFormRawValue> {
    return {
      ...game,
      gameDate: game.gameDate ? game.gameDate.format(DATE_TIME_FORMAT) : undefined,
    };
  }
}
