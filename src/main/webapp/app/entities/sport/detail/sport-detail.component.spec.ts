import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { SportDetailComponent } from './sport-detail.component';

describe('Sport Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SportDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: SportDetailComponent,
              resolve: { sport: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(SportDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load sport on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', SportDetailComponent);

      // THEN
      expect(instance.sport).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
