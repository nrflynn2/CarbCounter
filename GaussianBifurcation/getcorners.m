function [corner1 corner2 corner3 corner4] = getcorners(XYMAT, center)

% Dan Bonistalli
% Sorts the point matrix into coordinates of angle,distance based on its
% distance from center.  Finds the minimum value for each of 200 ranges of
% angles.  Then, uses the change in slope at the corners to find the angle
% at which this appears, in order to find 4 corners.

%% Sort Matrix
anglevec = atan2((XYMAT(:,2)-center(2)),(XYMAT(:,1)-center(1)));
distancevec = sqrt((XYMAT(:,1)-center(1)).^2 + (XYMAT(:,2)-center(2)).^2);
realvec = sortrows([anglevec distancevec]);

spac = (max(anglevec) - min(anglevec))/200;
dvec2 = [];
evec2 = [];

for i = linspace(min(anglevec),max(anglevec)-spac,200)+spac/2
    gvec = find(realvec(:,1)>= i-spac & realvec(:,1) <= i+spac);
    if isempty(gvec) == 0
        sortedvec = sort(realvec(gvec,2));
        if length(sortedvec) >= 1 && length(sortedvec) <=3
            dvec2 = [];
            evec2 = [];
        else if length(sortedvec) >= 4 && length(sortedvec) <=6
            dvec2 = [dvec2;sortedvec(1)];
            evec2 = [evec2;i];
            else
            dvec2 = [dvec2;sortedvec(1)];
            evec2 = [evec2;i];
            end
        end
    end
end


scatter(evec2,dvec2)
pause

%% Get Corners

a = diff(diff(dvec2));
p = sort(a);

g = 4;
while g > 0
    if g == 4
        corner1 = [evec2(find(a == p(1))+1) dvec2(find(a == p(1))+1)];
        i = 2;
        g = g-1;
    end
    if g == 3
        if abs(evec2(find(a == p(i)) + 1) - corner1(1)) <= 0.1
            i = i+1;
        else
            corner2 = [evec2(find(a == p(i))+1) dvec2(find(a == p(i))+1)];
            i = i+1;
            g = g-1;
        end
    end
    if g == 2
        if abs(evec2(find(a == p(i)) + 1) - corner1(1)) <= 0.1 ...
                || abs(evec2(find(a == p(i)) + 1) - corner2(1)) <= 0.1
            i = i + 1;
        else
            corner3 = [evec2(find(a == p(i))+1) dvec2(find(a == p(i))+1)];
            i = i+1;
            g = g-1;
        end
    end
    if g == 1
        if abs(evec2(find(a == p(i)) + 1) - corner1(1)) <= 0.1 ...
                || abs(evec2(find(a == p(i)) + 1) - corner2(1)) <= 0.1 ...
                || abs(evec2(find(a == p(i)) + 1) - corner3(1)) <= 0.1
            i = i + 1;
        else
            corner4 = [evec2(find(a == p(i))+1) dvec2(find(a == p(i))+1)];
            i = i+1;
            g = g-1;
        end
    end
end