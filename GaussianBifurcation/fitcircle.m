function [b diam center] = fitcircle(xc,yc,XYMAT,q)

%Dan Bonistalli

% returns b=1 if circle is found
% returns b=0 if circle is not found
% alse calculates the diameter and center of the circle.

xc = xc(1);
yc = yc(1);

trymat(:,1) = XYMAT(:,1) - xc;
trymat(:,2) = XYMAT(:,2) - yc;
trymat1(:,1) = sqrt(trymat(:,1).^2 + trymat(:,2).^2);

[sortedValues1,~] = sort(trymat1(:),'ascend');

for r = 1:length(sortedValues1)-1
    sortedValues2(r,1) = sortedValues1(r+1,1) - sortedValues1(r,1); %#ok<AGROW>
end

[x y] = max(sortedValues2(1:0.5*q)); % was 90000

if x > 10
    b = 1;
else
    b = 0;
end

fx = trymat((find(sortedValues1(y) == trymat1)),1)+xc;
fy = trymat((find(sortedValues1(y) == trymat1)),2)+yc;

diam = sqrt((xc-fx).^2 + (yc-fy).^2);
center = [xc-0.5*(xc-fx) yc-0.5*(yc-fy)];
if diam < 40
    b = 0;
end
end

