function carbs = bread(a, b, c);
%UNTITLED Summary of this function goes here

prompt = 'What is the rectangular prism height in centimeters? ';
a = input(prompt);

prompt = 'What is the rectangular prism length in centimeters? ';
b = input(prompt);

prompt = 'What is the rectangular prism width in centimeters? ';
c = input(prompt);

rectangular_prism_volume = a*b*c;

density = 0.19; %in g/cm^3
carb_density = 0.49; %in carbs/g

est_mass = density*rectangular_prism_volume;

carbs = est_mass*carb_density;

prompt = 'What is the mass in grams? ';
mass = input(prompt);

percent_error = abs((mass*carb_density - carbs)/(mass*carb_density)*100)

display carbs
        
end