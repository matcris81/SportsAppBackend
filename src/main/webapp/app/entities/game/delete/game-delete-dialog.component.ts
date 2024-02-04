import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IGame } from '../game.model';
import { GameService } from '../service/game.service';

@Component({
  standalone: true,
  templateUrl: './game-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class GameDeleteDialogComponent {
  game?: IGame;

  constructor(
    protected gameService: GameService,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.gameService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
