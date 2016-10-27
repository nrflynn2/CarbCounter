function [ROT] = MatrixBuilder(RE,LE,BE,UE,center1)
% Dan Bonistalli
% Finds A rotational matrix to convert Frame of Reference.

A1 = RE(1);
A2 = RE(2);
A3 = RE(3);
Atot = norm(RE);

B1 = LE(1);
B2 = LE(2);
B3 = LE(3);
Btot = norm(LE);

C1 = BE(1);
C2 = BE(2);
C3 = BE(3);
Ctot = norm(BE);

theta = -atan(-A3/A1);
maty = [cos(theta) 0 sin(theta);0 1 0;-sin(theta) 0 cos(theta)];

theta2 = -atan(A2/sqrt(A1^2+A3^2));
matx = [cos(theta2) -sin(theta2) 0;sin(theta2) cos(theta2) 0; 0 0 1];

k = matx*maty*LE';
theta3 = -atan(k(2)/-k(3));
matz = [1 0 0;0 cos(theta3) -sin(theta3); 0 sin(theta3) cos(theta3)];

Rotation = matz*matx*maty;
 
ROT = Rotation;
 
RErot = ROT*RE';
LErot = ROT*LE';
UErot = ROT*UE';
centerrot = ROT*center1';
BErot = ROT*BE';
 
hold on
x = [LErot(1) UErot(1) RErot(1) BErot(1) centerrot(1) LErot(1)];
z = [LErot(2) UErot(2) RErot(2) BErot(2) centerrot(2) LErot(2)];
y = -[LErot(3) UErot(3) RErot(3) BErot(3) centerrot(3) LErot(3)];
plot3(x,y,z);
end
