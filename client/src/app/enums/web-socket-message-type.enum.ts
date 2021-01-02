export enum WebSocketMessageType {
  RoomStateRequest = 'RoomStateRequest',
  RoomStateResponse = 'RoomStateResponse',
  StartGameMessage = 'StartGameMessage',
  NewTurnEvent = 'NewTurnEvent',
  GameOverEvent = 'GameOverEvent',
  EndTurnEvent = 'EndTurnEvent'
}
