import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {BrowserModule} from '@angular/platform-browser';
import {CalendarModule, ChartModule, CheckboxModule, RadioButton, SliderModule} from 'primeng/primeng';

import {AppComponent} from "./app.component";
import {AppRoutingModule} from "./app-routing.module";
import {DashboardComponent} from "./dashboard/dashboard.component";
import {AnalyticsService} from "./services/analytics.service";
import {HeatmapComponent} from "./heatmap/heatmap.component";
import {PeopleCountComponent} from './people-count/people-count.component';
import {PointMappingComponent} from './settings/point-mapping/point-mapping.component';
import {ConfigService} from "./services/config.service";
import {ZonesComponent} from './zones/zones.component';
import {ZoneInfoComponent} from './zone-info/zone-info.component';
import {RealtimeMapComponent} from './realtime-map/realtime-map.component';
import {RealtimeInfoComponent} from './realtime-info/realtime-info.component';
import {TimeBoundMapComponent} from './time-bound-map/time-bound-map.component';
import {PersonStopPointsComponent} from "./person-stop-points/person-stop-points.component";
import {ReIdComponent} from './re-id/re-id.component';
import {TimelineComponent} from './timeline/timeline.component';
import {TimeVelocityDistributionComponent} from './time-velocity-distribution/time-velocity-distribution.component';
import {DirectionRingComponent} from './direction-ring/direction-ring.component';
import {MovementDirectionComponent} from './movement-direction/movement-direction.component';
import {MatButtonModule, MatCheckboxModule} from '@angular/material';

@NgModule({
  declarations: [
    AppComponent,
    DashboardComponent,
    HeatmapComponent,
    PeopleCountComponent,
    PointMappingComponent,
    ZonesComponent,
    ZoneInfoComponent,
    RealtimeMapComponent,
    RealtimeInfoComponent,
    PersonStopPointsComponent,
    TimeBoundMapComponent,
    ReIdComponent,
    TimelineComponent,
    TimeVelocityDistributionComponent,
    DirectionRingComponent,
    MovementDirectionComponent,
    RadioButton
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    FormsModule,
    AppRoutingModule,
    HttpClientModule,
    CalendarModule,
    CheckboxModule,
    ChartModule,
    SliderModule,
    MatButtonModule,
    MatCheckboxModule
  ],
  providers: [AnalyticsService, ConfigService],
  bootstrap: [AppComponent]
})

export class AppModule {
}
