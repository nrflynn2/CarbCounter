function [RE LE BE ORIGIN UE center1] = findcorners2(BE,RE,UE,LE,center,XYMAT,lengthcard,widthcard,center1)
%Dan Bonistalli

% Old code
% Uses the corner values to project Z values to each corner.

LV = [RE(1)-BE(1) RE(2)-BE(2) 0];
SV = [LE(1)-BE(1) LE(2)-BE(2) 0];

C = -dot(LV,SV)^2;
B = (widthcard/lengthcard)^-2*(dot(LV,LV))-dot(SV,SV);
A = (widthcard/lengthcard)^-2;

%  A(zl)^2 + B(zl) + C = 0

ZL2 = (-B + sqrt(B^2 - 4*A*C))/(2*A);
ZL = sqrt(ZL2);
ZS = -dot(LV,SV)/ZL;

%Edit extreme measures

BEo = BE;
REo = RE;
LEo = LE;
UEo = UE;

%BE = BEo;
%RE = REo;
%LE = LEo;
%UE = UEo;

%hold off
%scatters(BE)
%hold on
%scatters(BE)
%scatters(LE)
%scatters(UE)
%scatters(RE)

BE = [BE 0];
ORIGIN = BE;
LE = [LE -ZS];
RE = [RE -ZL];
LE = LE - BE;
RE = RE - BE;
UE = [UE -ZS-ZL];
UE = UE-BE;
BE = [0 0 0];

pval = sqrt(dot(RE,RE))/lengthcard; %pixels per millimeter
% Pval should be close to the pVal we calculated in the center, and it is!

%th = atan(LV/(pval*450));
%th2 = pi/2-atan(ZL/sqrt(dot(LV,LV)));
%xval = sin(th2-th)/((ZL/pval)*sin(th));
%xval = xval^-1;
%scaler = (xval/sqrt(dot(RE,RE)))+1;

%pval2 = norm(RE.*(scaler))/lengthcard; %pixels per millimeter

BE = BE/pval;
LE = LE/pval;
RE = RE/pval;
UE = RE+LE;

%NEWMAT(:,1) = XYMAT(:,1)-ORIGIN(1);
%NEWMAT(:,2) = XYMAT(:,2)-ORIGIN(2);
%NEWMAT= (NEWMAT)./(pval);

[A] = [LE(1) LE(2);RE(1) RE(2)]\[-LE(3);-RE(3)];
A = -A;

center1 = center1-ORIGIN(1:2);
center1(1,3) = center1(1).*A(1) + center1(2).*A(2);
center1 = center1/pval;

%NEWMAT(:,3) = NEWMAT(:,1).*A(1)+NEWMAT(:,2).*A(2);

%hold off

%pval is true pixel/mm ratio

%scatter3(NEWMAT(:,1),-NEWMAT(:,3),NEWMAT(:,2))

x = [LE(1) UE(1) RE(1) BE(1) center1(1) LE(1)];
y = [LE(2) UE(2) RE(2) BE(2) center1(2) LE(2)];
z = [LE(3) UE(3) RE(3) BE(3) center1(3) LE(3)];
plot3(x,y,z);
end

