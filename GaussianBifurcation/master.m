%% Edge Detect and Measure
%% Input Image Below, under 'weiner'
% Uses known credit card dimensions and camera specs to:
%-find distance from the card
%-find the 3d orientation of the card
%-find a rotational matrix to convert any other point to an x-y-z
% field based upon the card's orientation
%-contains a function 'scaler3' which takes x,y coordinates of original
%photo and scales them appropriately to represent true xy coordinates,
%which have an original error due to camera field.

%% constants to fill
q = 20000;
%100000, but in the future, edit to have a 'cutoff frequency'
% based on residual analysis.
% note 100000 works best for credit card under noise, this is for non-cards

iscreditcard = 1;
% yes it is a credit card

numkern = 5;
subopt = 2;

lengthcard = 30;
widthcard = 65;
truediam_mm = 4;
xcenter = 9.5;
ycenter = 15;

%android specs
resolution = [3264 1952];
appxangle = atan2(1.25,13/6);

hold off
%% weiner performs a smoothed edge detection, Gaussian noise cancellation
% with a fifth degree weiner filter.

figure(1);
[GAUSSIAN] = weiner('2012-12-11 18.09.47.jpg',numkern,subopt);
[ymaximum xmaximum] = size(GAUSSIAN);
pause

%% Transmits the coordinates of a certain cutoff q into a scatterplot.

figure(2);
[XYMAT1] = DetermineCoor(GAUSSIAN,iscreditcard,q);
pause

%% findcenter3 bypasses noise to find the central circle of the card-object.
% surprisingly very powerful.

figure(3);
[diam center XYMAT gmat] = findcenter3(XYMAT1,q);

% cuts out the center dot, returns a single point representing the center.
hold off
scatter(center(1),center(2),500);
hold on
scatter(XYMAT(:,1),XYMAT(:,2));
pause

%% Getcorners runs an iteration which first plots the card as distance from
% center as a function of angle from center, and then finds the four
% local maxima.

figure(4);
hold off
[corner1 corner2 corner3 corner4] = getcorners(XYMAT, center);

hold on
scatter(corner1(1),corner1(2),500);
scatter(corner2(1),corner2(2),500);
scatter(corner3(1),corner3(2),500);
scatter(corner4(1),corner4(2),500);
pause

%% findcenterval will find interesting and useful data using the data gathered
% from the center circle.  the diameter of this is very useful.

[pVal angleIncident center1 truediam] = findcenterval(XYMAT1,XYMAT,truediam_mm);


%% Analyze corners will return [angle distance] coordinates of the corners 
% into [x y] and then figure out which ones are farthest left, farthest
% right, etc.  Right End = RE; Upper End = UE, etc.

figure(6);
[UE BE RE LE islongbottom] = analyzecorners(center,corner1,corner2,corner3,corner4, XYMAT);


%%  FindDistance uses center data and phone specs to find the distance from 
% the phone to the corners of the card

[degperpix distance maxdist halfpoint] = FindDistance(xmaximum,ymaximum,...
    center1,appxangle,truediam,truediam_mm);

%% Use scaler3, which uses euclidean geometry to scale the corner values to
% more accurate ones, undisturbed by the distance to the center of the
% image, and camera angles.

% edit the RE,BE,LE,UE values

[ RE ] = scaler3(RE,halfpoint,appxangle,maxdist);
[ BE ] = scaler3(BE,halfpoint,appxangle,maxdist);
[ LE ] = scaler3(LE,halfpoint,appxangle,maxdist);
[ UE ] = scaler3(UE,halfpoint,appxangle,maxdist);
[ center1 ] = scaler3(center1,halfpoint,appxangle,maxdist);


%% findcorners2 uses the corner values already obtained (is a bit of a
% misnomer of a function) and projects them into 3d space using euclidean 
% geometry, especially the known fact that we know both true side lengths
% and true angles, and we also know projected side lengths and side angles,
% and with this we can assign a Z value to each corner.

figure(7);
hold off
[RE LE BE ORIGIN UE center1] = findcorners2(BE,RE,UE,LE,center,XYMAT,lengthcard,widthcard,center1);
pause
%hold off


%% Calculates and applies a Rotational Matrix to the four corner points.
% can apply ROT to any other points in the image to move it into card frame
% of reference.

[ROT] = MatrixBuilder(RE,LE,BE,UE,center1);
pause

















