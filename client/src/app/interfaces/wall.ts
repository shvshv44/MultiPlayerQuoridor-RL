import {Direction} from '../enums/direction';
import {Position} from './position';

export interface Wall {
  position: Position;
  wallDirection: Direction;
}
