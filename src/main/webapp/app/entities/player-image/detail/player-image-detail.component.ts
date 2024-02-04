import { Component, Input } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';

import SharedModule from 'app/shared/shared.module';
import { DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe } from 'app/shared/date';
import { DataUtils } from 'app/core/util/data-util.service';
import { IPlayerImage } from '../player-image.model';

@Component({
  standalone: true,
  selector: 'jhi-player-image-detail',
  templateUrl: './player-image-detail.component.html',
  imports: [SharedModule, RouterModule, DurationPipe, FormatMediumDatetimePipe, FormatMediumDatePipe],
})
export class PlayerImageDetailComponent {
  @Input() playerImage: IPlayerImage | null = null;

  constructor(
    protected dataUtils: DataUtils,
    protected activatedRoute: ActivatedRoute,
  ) {}

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  previousState(): void {
    window.history.back();
  }
}
