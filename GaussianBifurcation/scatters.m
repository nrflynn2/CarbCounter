function scatters( args )
%Dan Bonistalli
%better scatter

% one input, scatters second row over first, or will scatter three
% dimensions.

% scatter(matrix(x,y,z))

[www] = size(args);
if www(2) == 2
    if length(args) == 2
        scatter(args(:,1),args(:,2),500);
    else
        scatter(args(:,1),args(:,2));
    end
else
    if www(2) == 3
        scatter3(args(:,1),args(:,2),args(:,3));
    end
end

end

