import {Injectable} from '@angular/core';

const config = require('../../../assets/config.json');


@Injectable({
  providedIn: 'root'
})
export class ConfigService {
  private readonly config: any;

  constructor() {
    this.config = config;
  }

  getConfig(): any {
    return this.config;
  }
}
