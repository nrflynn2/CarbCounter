function [ XYMAT ] = DetermineCoor( GAUSSIAN, iscreditcard, q)

% Dan Bonistalli
% Determines coordinates based upon the gaussian edge detection.  XYMAT is
% a n by 2 matrix, where XYMAT(:,1) is x values, and XYMAT(:,2) is y
% values.  XYMAT consists of the top 'n' most clear data available.  Here,
% n is set to 500, but can be edited.

if iscreditcard == 1
    n = q; %100000, but in the future, edit to have a 'cutoff frequency'
                % based on residual analysis.
                % note 100000 works best for credit card under noise
else
    n = 500; %originally set to 500 for ellipse
end

[sortedValues,~] = sort(GAUSSIAN(:),'descend');
maxIndex = sortedValues(1:n);  % Get a linear index of the n largest values
cutoff = min(maxIndex);        % cutoff is the 'meaningful activation level'

GAUSSIAN( GAUSSIAN > cutoff) = 1; % sets cutoff
GAUSSIAN( GAUSSIAN <= cutoff) = 0;

multmat = zeros(size(GAUSSIAN));
[xx yy] = size(GAUSSIAN);
multmat(5:xx-5,5:yy-5) = 1; % because the weiner2 leaves a border

GAUSSIAN = multmat.*GAUSSIAN;

[maxy ~] = size(GAUSSIAN);

[x,y] = find(GAUSSIAN == 1);
XYMAT = [y maxy-x];

scatter(XYMAT(:,1),XYMAT(:,2));
hold on
end

