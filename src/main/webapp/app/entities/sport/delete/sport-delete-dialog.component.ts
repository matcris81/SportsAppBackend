import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { ISport } from '../sport.model';
import { SportService } from '../service/sport.service';

@Component({
  standalone: true,
  templateUrl: './sport-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class SportDeleteDialogComponent {
  sport?: ISport;

  constructor(
    protected sportService: SportService,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.sportService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
