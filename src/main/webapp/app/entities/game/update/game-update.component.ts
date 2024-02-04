import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IPlayer } from 'app/entities/player/player.model';
import { PlayerService } from 'app/entities/player/service/player.service';
import { IGame } from '../game.model';
import { GameService } from '../service/game.service';
import { GameFormService, GameFormGroup } from './game-form.service';

@Component({
  standalone: true,
  selector: 'jhi-game-update',
  templateUrl: './game-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class GameUpdateComponent implements OnInit {
  isSaving = false;
  game: IGame | null = null;

  playersSharedCollection: IPlayer[] = [];

  editForm: GameFormGroup = this.gameFormService.createGameFormGroup();

  constructor(
    protected gameService: GameService,
    protected gameFormService: GameFormService,
    protected playerService: PlayerService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  comparePlayer = (o1: IPlayer | null, o2: IPlayer | null): boolean => this.playerService.comparePlayer(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ game }) => {
      this.game = game;
      if (game) {
        this.updateForm(game);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const game = this.gameFormService.getGame(this.editForm);
    if (game.id !== null) {
      this.subscribeToSaveResponse(this.gameService.update(game));
    } else {
      this.subscribeToSaveResponse(this.gameService.create(game));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IGame>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(game: IGame): void {
    this.game = game;
    this.gameFormService.resetForm(this.editForm, game);

    this.playersSharedCollection = this.playerService.addPlayerToCollectionIfMissing<IPlayer>(this.playersSharedCollection, game.organizer);
  }

  protected loadRelationshipsOptions(): void {
    this.playerService
      .query()
      .pipe(map((res: HttpResponse<IPlayer[]>) => res.body ?? []))
      .pipe(map((players: IPlayer[]) => this.playerService.addPlayerToCollectionIfMissing<IPlayer>(players, this.game?.organizer)))
      .subscribe((players: IPlayer[]) => (this.playersSharedCollection = players));
  }
}
