import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import SharedModule from 'app/shared/shared.module';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';
import { IVenue } from '../venue.model';
import { VenueService } from '../service/venue.service';

@Component({
  standalone: true,
  templateUrl: './venue-delete-dialog.component.html',
  imports: [SharedModule, FormsModule],
})
export class VenueDeleteDialogComponent {
  venue?: IVenue;

  constructor(
    protected venueService: VenueService,
    protected activeModal: NgbActiveModal,
  ) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.venueService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
