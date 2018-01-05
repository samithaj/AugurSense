import {Component, Input, OnInit} from '@angular/core';
import {Zone} from "../../../resources/zone";
import {GlobalMap} from "../../../resources/global-map";
import {ConfigService} from "../../../services/config.service";
import {Message} from "../../../lib/message";

@Component({
  selector: 'app-zone-ui',
  templateUrl: './zone-ui.component.html',
  styleUrls: ['./zone-ui.component.css']
})
export class ZoneUiComponent implements OnInit {

  private _message: Message;
  private _zones: Zone[] = [];
  private _globalMap: GlobalMap;
  private _modalId: string;
  zone: Zone;

  constructor(private configService: ConfigService) {
    this.zone = new Zone(0, null, [], [], 0);
  }

  ngOnInit() {
  }

  public addZone() {
    if (this.zone.zoneName == null || this.zone.zoneName.length == 0) {
      this.message = new Message("Please add a proper zone name", Message.ERROR);
      return;
    } else if (this.zone.xCoordinates.length < 3) {
      this.message = new Message("A zone should be a polygon with at least 4 points", Message.ERROR);
      return;
    }

    this.configService.addZone(this.zone).then(zone => {
      this.message = new Message("Zone added successfully", Message.SUCCESS);
      this.zones.push(zone);
      this.clear();
    }).catch(reason => {
      this.message = new Message("Zone couldn't be added", Message.ERROR);
    });
  }

  public clicked(event: MouseEvent) {
    this.zone.xCoordinates.push(event.offsetX);
    this.zone.xCoordinates = this.zone.xCoordinates;
    this.zone.yCoordinates.push(event.offsetY);
    this.zone.yCoordinates = this.zone.yCoordinates;
  }

  public clear() {
    this.zone.zoneName = null;
    this.zone.zoneLimit = 0;
    this.zone.xCoordinates = [];
    this.zone.yCoordinates = [];
  }

  get zones(): Zone[] {
    return this._zones;
  }

  @Input() set zones(value: Zone[]) {
    this._zones = value;
  }

  get globalMap(): GlobalMap {
    return this._globalMap;
  }

  @Input() set globalMap(value: GlobalMap) {
    this._globalMap = value;
  }

  get modalId(): string {
    return this._modalId;
  }

  @Input() set modalId(value: string) {
    this._modalId = value;
  }

  get message(): Message {
    return this._message;
  }

  set message(value: Message) {
    this._message = value;
  }
}
