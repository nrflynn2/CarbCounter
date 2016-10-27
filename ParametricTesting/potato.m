function carbs = potato(a, b, c);
%UNTITLED Summary of this function goes here

prompt = 'What is the ellipsoid height in centimeters? ';
a = input(prompt);

prompt = 'What is the ellipsoid length in centimeters? ';
b = input(prompt);

prompt = 'What is the ellipsoid width in centimeters? ';
c = input(prompt);

ellipsoid_volume =(4/3)*pi*a*b*c;

density = 0.641; %in g/cm^3
carb_density = 0.1713; %in carbs/g

est_mass = density*ellipsoid_volume;

carbs = est_mass*carb_density;

prompt = 'What is the mass in grams? ';
mass = input(prompt);

percent_error = abs((mass*carb_density - carbs)/(mass*carb_density)*100)

display carbs
        
end

