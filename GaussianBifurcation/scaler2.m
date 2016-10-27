% Scaler
% The function scaler1 was being weird so we are just running a script
% directly.

Input = Input-halfpoint;

X = Input(1);
Y = Input(2);

dist2 = sqrt((X)^2+(Y)^2);

theta = dist2*appxangle/maxdist;

scaler = tan(theta)/theta;

newx = X*scaler(1);
newy = Y*scaler(1);

output = [newx,newy];
