import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IPlayer } from '../player.model';
import { PlayerService } from '../service/player.service';

@Component({
  standalone: true,
  templateUrl: './player-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class PlayerDeleteDialogComponent {
  player?: IPlayer;

  constructor(
    protected playerService: PlayerService,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: string): void {
    this.playerService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
