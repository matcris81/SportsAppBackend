import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { PlayerDetailComponent } from './player-detail.component';

describe('Player Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PlayerDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: PlayerDetailComponent,
              resolve: { player: () => of({ id: 'ABC' }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(PlayerDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load player on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', PlayerDetailComponent);

      // THEN
      expect(instance.player).toEqual(expect.objectContaining({ id: 'ABC' }));
    });
  });
});
