from enum import Enum


# Using enum class create enumerations
class MovementDirection(Enum):
    Up = 'Up'
    Down = 'Down'
    Left = 'Left'
    Right = 'Right'


class MovePawnAction:
    def __init__(self, direction):
        self.direction = direction
