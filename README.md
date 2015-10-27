# 3DImager
3D Imaging and Correlation Software

Software records a series of three dimensional point-clouds from the XBox Kinect 3D sensor.
It displays in 3D using Processing.org and allows them to be rotated in space using arrow keys.
Pressing 1 starts correlation process whereby successive images are tested against each other.
The second image is rotated and translated in 3 directions.
Unit tests focus on the array multiplication maths.  A custom array maths class has been written. 
It has been cut down to the core methods required for translation and rotation and hard-coded for
the number of dimensions used in the program. Performance has been increased by reducing functionality.

The objective is to recreate a full three-dimensional object and print to a 3D printer.
