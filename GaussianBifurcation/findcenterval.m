function [pVal angleIncident center1 truediam] = findcenterval(XYMAT1,XYMAT,truediam_mm)

% Dan Bonistalli
% Uses the matrix of values comprising the center of the card (little dot).
% This matrix can give us a great deal of information such as the true
% diameter of the center, the mm per pixels at this point (changes with
% distance to object from camera) and the angle of incident that the camera
% has.  With distance, this angle can give us things like the camera's
% height too, but that is not required.

%% find the center matrix
% by comparing XYMAT1 (without center) and XYMAT (with center)
b = 1;
g = 1;
d = 1;
while b <= length(XYMAT1)
    if XYMAT1(b,1) == XYMAT(g,1) && XYMAT1(b,2) == XYMAT(g,2)
        b = b+1;
        g = g+1;
    else
        cenmat(d,1) = XYMAT1(b,1);
        cenmat(d,2) = XYMAT1(b,2);
        d = d+1;
        b = b+1;
    end
end

%% Find the trueDiameter
for ie = 1:length(cenmat)
    for iee = 1:length(cenmat)
        suckmat(iee) = sqrt((cenmat(ie,1)-cenmat(iee,1)).^2 + (cenmat(ie,2)-cenmat(iee,2)).^2);
    end
   suckmat2(ie) = max(suckmat);
end
truediam = max(suckmat2);

%% Find Center value
longit = find(suckmat2 == truediam);
closer = cenmat(longit(1),:);
farther = cenmat(longit(2),:);

angle = pi/2 + atan2(farther(2)-closer(2),farther(1) - closer(1));
center1 = [mean([closer(1) farther(1)]) mean([closer(2) farther(2)])];

%% Find the angle of incident by finding the minor axis length
anglevec1 = atan2((cenmat(:,2)-center1(2)),(cenmat(:,1)-center1(1)));
distancevec1 = sqrt((cenmat(:,1)-center1(1)).^2 + (cenmat(:,2)-center1(2)).^2);

f= anglevec1;
val = angle; %value to find
tmp = abs(f-val);
[idx idx] = min(tmp); %index of closest value
closest1 = f(idx);

f= anglevec1;
if angle > 0
    val = angle-pi;
else
val = angle+pi; %value to find
end
tmp = abs(f-val);
[idx idx] = min(tmp); %index of closest value
closest2 = f(idx);

for z = -20:20
    shortdiam(z+21) = max([distancevec1(find(anglevec1 == closest1)+z) distancevec1(find(anglevec1 == closest1)+z)]);
end

shortdiam = 2*max(shortdiam);

angleIncident = asin(shortdiam/truediam);
pVal = truediam_mm/truediam;
end