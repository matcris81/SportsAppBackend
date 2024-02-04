import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IPlayerImage } from 'app/entities/player-image/player-image.model';
import { PlayerImageService } from 'app/entities/player-image/service/player-image.service';
import { IGame } from 'app/entities/game/game.model';
import { GameService } from 'app/entities/game/service/game.service';
import { IVenue } from 'app/entities/venue/venue.model';
import { VenueService } from 'app/entities/venue/service/venue.service';
import { Gender } from 'app/entities/enumerations/gender.model';
import { PlayerService } from '../service/player.service';
import { IPlayer } from '../player.model';
import { PlayerFormService, PlayerFormGroup } from './player-form.service';

@Component({
  standalone: true,
  selector: 'jhi-player-update',
  templateUrl: './player-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class PlayerUpdateComponent implements OnInit {
  isSaving = false;
  player: IPlayer | null = null;
  genderValues = Object.keys(Gender);

  playerImagesCollection: IPlayerImage[] = [];
  gamesSharedCollection: IGame[] = [];
  venuesSharedCollection: IVenue[] = [];

  editForm: PlayerFormGroup = this.playerFormService.createPlayerFormGroup();

  constructor(
    protected playerService: PlayerService,
    protected playerFormService: PlayerFormService,
    protected playerImageService: PlayerImageService,
    protected gameService: GameService,
    protected venueService: VenueService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  comparePlayerImage = (o1: IPlayerImage | null, o2: IPlayerImage | null): boolean => this.playerImageService.comparePlayerImage(o1, o2);

  compareGame = (o1: IGame | null, o2: IGame | null): boolean => this.gameService.compareGame(o1, o2);

  compareVenue = (o1: IVenue | null, o2: IVenue | null): boolean => this.venueService.compareVenue(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ player }) => {
      this.player = player;
      if (player) {
        this.updateForm(player);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const player = this.playerFormService.getPlayer(this.editForm);
    if (player.id !== null) {
      this.subscribeToSaveResponse(this.playerService.update(player));
    } else {
      this.subscribeToSaveResponse(this.playerService.create(player));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPlayer>>): void {
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

  protected updateForm(player: IPlayer): void {
    this.player = player;
    this.playerFormService.resetForm(this.editForm, player);

    this.playerImagesCollection = this.playerImageService.addPlayerImageToCollectionIfMissing<IPlayerImage>(
      this.playerImagesCollection,
      player.playerImage,
    );
    this.gamesSharedCollection = this.gameService.addGameToCollectionIfMissing<IGame>(this.gamesSharedCollection, ...(player.games ?? []));
    this.venuesSharedCollection = this.venueService.addVenueToCollectionIfMissing<IVenue>(
      this.venuesSharedCollection,
      ...(player.venues ?? []),
    );
  }

  protected loadRelationshipsOptions(): void {
    this.playerImageService
      .query({ filter: 'player-is-null' })
      .pipe(map((res: HttpResponse<IPlayerImage[]>) => res.body ?? []))
      .pipe(
        map((playerImages: IPlayerImage[]) =>
          this.playerImageService.addPlayerImageToCollectionIfMissing<IPlayerImage>(playerImages, this.player?.playerImage),
        ),
      )
      .subscribe((playerImages: IPlayerImage[]) => (this.playerImagesCollection = playerImages));

    this.gameService
      .query()
      .pipe(map((res: HttpResponse<IGame[]>) => res.body ?? []))
      .pipe(map((games: IGame[]) => this.gameService.addGameToCollectionIfMissing<IGame>(games, ...(this.player?.games ?? []))))
      .subscribe((games: IGame[]) => (this.gamesSharedCollection = games));

    this.venueService
      .query()
      .pipe(map((res: HttpResponse<IVenue[]>) => res.body ?? []))
      .pipe(map((venues: IVenue[]) => this.venueService.addVenueToCollectionIfMissing<IVenue>(venues, ...(this.player?.venues ?? []))))
      .subscribe((venues: IVenue[]) => (this.venuesSharedCollection = venues));
  }
}
