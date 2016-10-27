function [JZ] = weiner(FILENAME, ~, ~)

% Dan Bonistalli
% Opens FILENAME, performs a weiner smoothing with a 50x50 kernel
% Performs Gaussian subtraction
% Performs Sussex convolution
% Finally, another weiner smoothing of Gaussian noise.

% Each image is normalized to 0-1 after each convolution.

im = imread(FILENAME);

%% weiner2
[J1,~] = wiener2(im(:,:,1),[50 50]);
[J2,~] = wiener2(im(:,:,2),[50 50]);
[J3,~] = wiener2(im(:,:,3),[50 50]);

[x,y,~] = size(J1);
J = zeros(x,y,3);

J(:,:,1) = J1;
J(:,:,2) = J2;
J(:,:,3) = J3;

J = J/max(max(max(J)));
[x,y,~] = size(J);
J = J(50:x-50,50:y-50,1:3); %shortens the image by 100 total

%% Gaussian Subtraction
im = J;

subOpt = 2;
G = fspecial('gaussian', [5 5], 2);
Ig = imfilter(im, G, 'same');
if(subOpt == 1)
    outImage = im - Ig;
end
if(subOpt == 2)
    outImage = Ig - im;
end
if(subOpt == 3)
    outImage = (Ig-im) + (im-Ig);
end

GAUSSIAN = outImage;
GAUSSIAN = (1/max(max(max(GAUSSIAN))))*GAUSSIAN;

%% Sussex Convolution

g = fspecial('log', 50, 5);
im = GAUSSIAN;

for i = 1
    newim = convolve2(im(:,:,i), g, 'valid');
end
inImage = (1/max(max(newim)))*newim;

%% Another weiner, this time 30x30 kernel

[JZ,~] = wiener2(inImage,[30 30]);
JZ = (1/max(max(JZ))).*JZ;

imshow(JZ);
end




