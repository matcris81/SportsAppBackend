import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { VenueDetailComponent } from './venue-detail.component';

describe('Venue Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VenueDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: VenueDetailComponent,
              resolve: { venue: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(VenueDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load venue on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', VenueDetailComponent);

      // THEN
      expect(instance.venue).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
