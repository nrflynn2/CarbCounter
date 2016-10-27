
function [degperpix distance maxdist halfpoint] = FindDistance(xmaximum,ymaximum,...
    center1,appxangle,truediam,truediam_mm)

% Dan Bonistalli

% Find the distance from a 'center point' and the camera

halfpoint = [xmaximum/2 ymaximum/2];

truecenter = center1-halfpoint;
distcent = norm(truecenter);

k = xmaximum/truecenter(1);
kk = ymaximum/truecenter(2);

k = min([k kk])/2;

%maxdist = k*distcent;
maxdist = norm(halfpoint);

ourangle = appxangle*(truediam/maxdist);

dist_mm = truediam_mm/ourangle;

ourangle2 = appxangle*(distcent/maxdist);
distance = dist_mm*cos(ourangle2);

degperpix = appxangle/maxdist;
end



