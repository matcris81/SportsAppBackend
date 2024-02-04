import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { PlayerImageService } from '../service/player-image.service';
import { IPlayerImage } from '../player-image.model';
import { PlayerImageFormService, PlayerImageFormGroup } from './player-image-form.service';

@Component({
  standalone: true,
  selector: 'jhi-player-image-update',
  templateUrl: './player-image-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class PlayerImageUpdateComponent implements OnInit {
  isSaving = false;
  playerImage: IPlayerImage | null = null;

  editForm: PlayerImageFormGroup = this.playerImageFormService.createPlayerImageFormGroup();

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected playerImageService: PlayerImageService,
    protected playerImageFormService: PlayerImageFormService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ playerImage }) => {
      this.playerImage = playerImage;
      if (playerImage) {
        this.updateForm(playerImage);
      }
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('footyFixApp.error', { ...err, key: 'error.file.' + err.key })),
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const playerImage = this.playerImageFormService.getPlayerImage(this.editForm);
    if (playerImage.id !== null) {
      this.subscribeToSaveResponse(this.playerImageService.update(playerImage));
    } else {
      this.subscribeToSaveResponse(this.playerImageService.create(playerImage));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPlayerImage>>): void {
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

  protected updateForm(playerImage: IPlayerImage): void {
    this.playerImage = playerImage;
    this.playerImageFormService.resetForm(this.editForm, playerImage);
  }
}
