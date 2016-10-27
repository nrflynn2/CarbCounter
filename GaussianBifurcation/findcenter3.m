function [diam center XYMAT gmat] = findcenter3(XYMAT,q)

% Dan Bonistalli
% Finds an estimation of inner circle diameter, finds the location of the
% center, returns a matrix of points not including the center (XYMAT), and 
%a matrix of points that is only the center (gmat)

Xloc = median(XYMAT(:,1)); % Estimates the median, should be within the card
Yloc = median(XYMAT(:,2));

dmat(:,1) = XYMAT(:,1) - Xloc;
dmat(:,2) = XYMAT(:,2) - Yloc;
dmat1(:,1) = sqrt(dmat(:,1).^2 + dmat(:,2).^2); % matrix of distances of points
                                                % to the median
[sortedValues,~] = sort(dmat1(:),'ascend');

%fitcircle loops around the points, for each point starting with the one
%closest to the median, decides whether or not it fits the criteria of
%being a part of the circle.  It does fit this part if from this point,
%there is a lack of certain values for the distance of points to it.  Hard
%to explain, just go with it, it works really well actually, I was very
%surprised, things never work this well in MATLAB.
idd = 1; 
while fitcircle(dmat((find(sortedValues(idd) == dmat1)),1)+Xloc,...
        dmat((find(sortedValues(idd) == dmat1)),2)+Yloc,XYMAT,q) == 0
    idd = idd + 1;
end

[~, diam center] = fitcircle(dmat((find(sortedValues(idd) == dmat1)),1)+Xloc,...
    dmat((find(sortedValues(idd) == dmat1)),2)+Yloc,XYMAT,q);

XYMAT1(:,1) = sqrt((XYMAT(:,1)-center(1)).^2 + (XYMAT(:,2)-center(2)).^2);
[sortedValues3,~] = sort(XYMAT1(:),'ascend');

delmat = sortedValues3(sortedValues3 < diam/2+10);

for a = 1:length(delmat)
    gmat = find(XYMAT1(:,1) == delmat(a));
    for g = 1:length(gmat)
        XYMAT1(gmat(g)) = 99999;
    end
end

XYMAT(XYMAT1(:,1) == 99999,:) = [];
scatter(XYMAT(:,1),XYMAT(:,2));
end


