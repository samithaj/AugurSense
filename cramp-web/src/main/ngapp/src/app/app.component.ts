/*
 * Copyright 2017 Eduze
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the "Software"), to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and
 * to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions
 * of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 * CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */
import {Component} from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})

export class AppComponent {

  options: any = [
    {
      route: "/realtime",
      name: "Real Time",
      icon: "hourglass_empty"
    },
    {
      route: "/time-bound-map",
      name: "Time Shift",
      icon: "timeline"
    },
    {
      route: "/zones",
      name: "Zones",
      icon: "picture_in_picture"
    },
    {
      route: "/heatmap",
      name: "Heat Map",
      icon: "satellite"
    },
    {
      route: "/statistics",
      name: "Statistics",
      icon: "trending_up"
    },
    {
      route: "/stoppoints",
      name: "Stop Points",
      icon: "people"
    },
    {
      route: "/movement-direction",
      name: "Movement Directions",
      icon: "directions_walk"
    }
  ];

  constructor() {
  }
}