function [UE BE RE LE islongbottom] = analyzecorners(center,corner1,corner2,corner3,corner4,XYMAT)

% Dan Bonistalli
% Convert corners into cartesian coordinates, and also find which ones are
% which.

%% Cartesian
cornerone = [center(1)+corner1(2)*cos(corner1(1)) center(2) + corner1(2)*sin(corner1(1))];
cornertwo = [center(1)+corner2(2)*cos(corner2(1)) center(2) + corner2(2)*sin(corner2(1))];
cornerthree = [center(1)+corner3(2)*cos(corner3(1)) center(2) + corner3(2)*sin(corner3(1))];
cornerfour = [center(1)+corner4(2)*cos(corner4(1)) center(2) + corner4(2)*sin(corner4(1))];

hold off
scatters(XYMAT);
hold on
scatters(center);
scatters(cornerone);
scatters(cornertwo);
scatters(cornerthree);
scatters(cornerfour);
pause

%% Which are Which
z = max([cornerone(2) cornertwo(2) cornerthree(2) cornerfour(2)]);
if cornerone(2) == z
    UE = cornerone;
else if cornertwo(2) == z
        UE = cornertwo;
    else if cornerthree(2) == z
            UE = cornerthree;
            else if cornerfour(2) == z
                UE = cornerfour;
                end
        end
    end
end
%%
z = min([cornerone(2) cornertwo(2) cornerthree(2) cornerfour(2)]);
if cornerone(2) == z
    BE = cornerone;
else if cornertwo(2) == z
        BE = cornertwo;
    else if cornerthree(2) == z
            BE = cornerthree;
            else if cornerfour(2) == z
                BE = cornerfour;
                end
        end
    end
end
%%
z = max([cornerone(1) cornertwo(1) cornerthree(1) cornerfour(1)]);
if cornerone(1) == z
    RE = cornerone;
else if cornertwo(1) == z
        RE = cornertwo;
    else if cornerthree(1) == z
            RE = cornerthree;
            else if cornerfour(1) == z
                RE = cornerfour;
                end
        end
    end
end
%%
z = min([cornerone(1) cornertwo(1) cornerthree(1) cornerfour(1)]);
if cornerone(1) == z
    LE = cornerone;
else if cornertwo(1) == z
        LE = cornertwo;
    else if cornerthree(1) == z
            LE = cornerthree;
            else if cornerfour(1) == z
                LE = cornerfour;
                end
        end
    end
end
%%
%now time to figure out which ones are important

lefte = [mean([BE(1) LE(1)]) mean([BE(2) LE(2)])];
righte = [mean([UE(1) RE(1)]) mean([UE(2) RE(2)])];

halfval = 0.4*sqrt((lefte(1)-righte(1))^2 + (lefte(2)-righte(2))^2);
centerval = sqrt((center(1)-lefte(1))^2 + (center(2)-lefte(2))^2);

% next time just use norm function

%% find out if the long end is the bottom edge
if centerval < halfval
    islongbottom = 1;
else
    islongbottom = 0;
end

end










