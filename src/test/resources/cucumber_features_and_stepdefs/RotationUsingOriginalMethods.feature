Feature: FloatColoredPVector Rotations using original methods
  Background:
    Given a tolerance of 0.00001

   Scenario Outline: Test rotations using original methods
    Given a 3d point <intX>, <intY>, <intZ>
    When I rotate it by <rotateAngle> degrees around the <axis> axis
    Then the new coordinates should be <newX>, <newY>, <newZ>

    Examples:
      |intX |intY |intZ |rotateAngle |axis |newX |newY |newZ |
      |10   |10   |10   |0           |x    |10   |10   |10   |
      |10   |10   |10   |90          |x    |10   |-10  |10   |
      |10   |10   |10   |180         |x    |10   |-10  |-10  |
      |10   |10   |10   |270         |x    |10   |10   |-10  |
      |10   |10   |10   |360         |x    |10   |10   |10   |
      |10   |0    |0    |90          |x    |10   |0    |0    |
      |0    |10   |0    |90          |x    |0    |0    |10   |
      |0    |0    |10   |90          |x    |0    |-10  |0    |
      |0    |0    |0    |90          |x    |0    |0    |0    |
      |10   |10   |10   |0           |y    |10   |10   |10   |
      |10   |10   |10   |90          |y    |10   |10   |-10  |
      |10   |10   |10   |180         |y    |-10  |10   |-10  |
      |10   |10   |10   |270         |y    |-10  |10   |10   |
      |10   |10   |10   |360         |y    |10   |10   |10   |
      |0    |10   |10   |90          |y    |10   |10   |0    |
      |10   |0    |10   |90          |y    |10   |0    |-10  |
      |10   |10   |0    |90          |y    |0    |10   |-10  |
      |0    |0    |0    |90          |y    |0    |0    |0    |
      |10   |10   |10   |0           |z    |10   |10   |10   |
      |10   |10   |10   |90          |z    |-10  |10   |10   |
      |10   |10   |10   |180         |z    |-10  |-10  |10   |
      |10   |10   |10   |270         |z    |10   |-10  |10   |
      |10   |10   |10   |360         |z    |10   |10   |10   |
      |10   |0    |0    |90          |z    |0    |10   |0    |
      |0    |10   |0    |90          |z    |-10  |0    |0    |
      |0    |0    |10   |90          |z    |0    |0    |10   |
      |0    |0    |0    |90          |z    |0    |0    |0    |
