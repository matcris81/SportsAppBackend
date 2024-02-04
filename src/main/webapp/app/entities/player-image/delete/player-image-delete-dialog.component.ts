import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IPlayerImage } from '../player-image.model';
import { PlayerImageService } from '../service/player-image.service';

@Component({
  standalone: true,
  templateUrl: './player-image-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class PlayerImageDeleteDialogComponent {
  playerImage?: IPlayerImage;

  constructor(
    protected playerImageService: PlayerImageService,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.playerImageService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
