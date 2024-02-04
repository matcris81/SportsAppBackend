import { Routes } from '@angular/router';

const routes: Routes = [
  {
    path: 'player',
    data: { pageTitle: 'footyFixApp.player.home.title' },
    loadChildren: () => import('./player/player.routes'),
  },
  {
    path: 'sport',
    data: { pageTitle: 'footyFixApp.sport.home.title' },
    loadChildren: () => import('./sport/sport.routes'),
  },
  {
    path: 'venue',
    data: { pageTitle: 'footyFixApp.venue.home.title' },
    loadChildren: () => import('./venue/venue.routes'),
  },
  {
    path: 'game',
    data: { pageTitle: 'footyFixApp.game.home.title' },
    loadChildren: () => import('./game/game.routes'),
  },
  {
    path: 'notification',
    data: { pageTitle: 'footyFixApp.notification.home.title' },
    loadChildren: () => import('./notification/notification.routes'),
  },
  {
    path: 'payment',
    data: { pageTitle: 'footyFixApp.payment.home.title' },
    loadChildren: () => import('./payment/payment.routes'),
  },
  {
    path: 'player-image',
    data: { pageTitle: 'footyFixApp.playerImage.home.title' },
    loadChildren: () => import('./player-image/player-image.routes'),
  },
  /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
];

export default routes;
