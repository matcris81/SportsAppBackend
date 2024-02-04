import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IPlayer } from 'app/entities/player/player.model';
import { PlayerService } from 'app/entities/player/service/player.service';
import { NotificationStatus } from 'app/entities/enumerations/notification-status.model';
import { NotificationService } from '../service/notification.service';
import { INotification } from '../notification.model';
import { NotificationFormService, NotificationFormGroup } from './notification-form.service';

@Component({
  standalone: true,
  selector: 'jhi-notification-update',
  templateUrl: './notification-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class NotificationUpdateComponent implements OnInit {
  isSaving = false;
  notification: INotification | null = null;
  notificationStatusValues = Object.keys(NotificationStatus);

  playersSharedCollection: IPlayer[] = [];

  editForm: NotificationFormGroup = this.notificationFormService.createNotificationFormGroup();

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected notificationService: NotificationService,
    protected notificationFormService: NotificationFormService,
    protected playerService: PlayerService,
    protected activatedRoute: ActivatedRoute,
  ) {}

  comparePlayer = (o1: IPlayer | null, o2: IPlayer | null): boolean => this.playerService.comparePlayer(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ notification }) => {
      this.notification = notification;
      if (notification) {
        this.updateForm(notification);
      }

      this.loadRelationshipsOptions();
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
    const notification = this.notificationFormService.getNotification(this.editForm);
    if (notification.id !== null) {
      this.subscribeToSaveResponse(this.notificationService.update(notification));
    } else {
      this.subscribeToSaveResponse(this.notificationService.create(notification));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<INotification>>): void {
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

  protected updateForm(notification: INotification): void {
    this.notification = notification;
    this.notificationFormService.resetForm(this.editForm, notification);

    this.playersSharedCollection = this.playerService.addPlayerToCollectionIfMissing<IPlayer>(
      this.playersSharedCollection,
      notification.player,
    );
  }

  protected loadRelationshipsOptions(): void {
    this.playerService
      .query()
      .pipe(map((res: HttpResponse<IPlayer[]>) => res.body ?? []))
      .pipe(map((players: IPlayer[]) => this.playerService.addPlayerToCollectionIfMissing<IPlayer>(players, this.notification?.player)))
      .subscribe((players: IPlayer[]) => (this.playersSharedCollection = players));
  }
}
