clear all
close all
more off
format short e 
rand('twister',sum(100*clock))

%% begin msli tex 
%% loglets for web 2.0
%% \[
%% p(t) = \frac{\kappa}{1 + \exp\left(\frac{\log_e(81)}{-\alpha} (t - \beta)\right)}
%% \]

%% \[
%% p(t) = \frac{k}{1 + \exp\left(\frac{\log_e(81)}{-a} (t - b)\right)}
%% \]

%% note that alpha = 0 could be numerical issue 

loglet = @(t,a,k,b)( (k ./ (1 + exp( -1.0 .*(log(81)./a) .* (t - b)))))

randnum = @(low,high) ( (high - low).* rand() + low)

%% REMEMBER to set number loglets 


%% sunflower data
%load sun.txt
%x = sun(:,1);
%y = sun(:,2);
%ag(1) = 50.10;
%kg(1) = 261.04;
%bg(1) = 34.27;


load bilog.txt
x = bilog(:,1);
y = bilog(:,2);
ag(1) = 20;
kg(1) = 100;
bg(1) = 1940;
ag(2) = 25;
kg(2) = 150;
bg(2) = 1970;


% lotka papers 
%load lotka.txt
%x = lotka(:,1);
%y = lotka(:,2);
%ag(1) = 32.91;
%kg(1) = 98.54;
%bg(1) = 1928.02;


%% Bi-logistic example 
% %% nuclear tests
%load boom.txt
%x = boom(:,1);
%y = boom(:,2);
%ag(1) = 17.29;
%kg(1) = 697.70;
%bg(1) = 1963.36;
%ag(2) = 19.21;
%kg(2) = 276.85;
%bg(2) = 1982.79;


%%% the auto algorithm fails on this 
%%% two logistics are too different 
% load univ.txt
% x = univ(:,1);
% y = univ(:,2);
% ag(1) = 103.42;
% kg(1) = 498.85;
% bg(1) = 1884.27;
% ag(2) = 16.44;
% kg(2) = 63.01;
% bg(2) = 1963.08;
% a_low(1) = 50; a_high(1) = 150;
% k_low(1) = 250; k_high(1) = 1000;
% b_low(1) = 1850; b_high(1) = 1950;
% a_low(2) = 10; a_high(2) = 20;
% k_low(2) = 40; k_high(2) = 100;
% b_low(2) = 1950; b_high(2) = 2000;


%% US Populationn from Statistical Abstract of US 
%load USpopStatAB08scaled.txt
%x =  USpopStatAB08scaled(:,1);
%y =  USpopStatAB08scaled(:,2);
%ag(1) = 210.43 ;
%kg(1) = 480.41289600;
%bg(1) = 1984.26;


%% rembrandt
%%load rembrandt.txt
%%x = rembrandt(:,1);
%%y = rembrandt(:,2);
%%ag(1) = 9.5; %% these are wrong, logletlg.c seg faults...
%%kg(1) = 169.03;
%%bg(1) = 1632.6;;
%%ag(2) = 21.7;
%%kg(2) = 162.2;
%%bg(2) = 1647;


%% elements
% load elements.txt
% elements(:,2) = elements(:,2) - 14; %% known before science
% x = elements(:,1);
% y = elements(:,2);
% ag(1) = 68.32;
% kg(1) = 47.05;
% bg(1) = 1803.7;
% ag(2) = 38.72;
% kg(2) = 25.2;
% bg(2) = 1889.74;
% ag(3) = 34.02;
% kg(3) = 19.92;
% bg(3) = 1949.12;

number_loglets = 2

yg = zeros(max(size(x)),1);
for i=1:number_loglets,
  yg = yg + loglet(x,ag(i),kg(i),bg(i));
end
  
energyloglet_g = sum( (y - yg).^2)

xstart = x(1);
xstop = x(end);
ystart = y(1);
ystop = y(end);


%% this needs works 
for i=1:number_loglets,
 
  a_low(i) =  ((xstop - xstart) / (8 * number_loglets));
  a_high(i) = ((xstop - xstart) * (1 / number_loglets));
  
  k_low(i) = (ystop - ystart) / (number_loglets * 2);
  k_high(i) =(ystop - ystart) * (4 / number_loglets); 

  b_low(i) =   xstart + (((xstop - xstart)/number_loglets) * (i-1));
  b_high(i) =  xstart + (((xstop - xstart)/number_loglets) * (i));


end


energybest = 1e23; %% high value

MCiter = 50e3 * number_loglets; 
contraction_param = 25 ;

energymatrix = zeros(MCiter,2);
a_matrix = zeros(MCiter,number_loglets);
k_matrix = zeros(MCiter,number_loglets);
b_matrix = zeros(MCiter,number_loglets);


%% the first sampling pass (no contraction) 

for iter=1:MCiter,
  for i=1:number_loglets,
    a_try(i) = randnum(a_low(i),a_high(i));
    k_try(i) = randnum(k_low(i),k_high(i));
    b_try(i) = randnum(b_low(i),b_high(i));
  end

  y_try = zeros(max(size(x)),1);
  for i=1:number_loglets,
    y_try = y_try + loglet(x,a_try(i),k_try(i),b_try(i));
  end
  
  energy = sum( (y_try - y).^2);

  energymatrix(iter,:) = [energy,iter];
  a_matrix(iter,:) = a_try;
  k_matrix(iter,:) = k_try;
  b_matrix(iter,:) = b_try;
  
end

disp('starting the contractsion')

for contractiter=1:number_loglets,

  energymatrixsorted = sortrows(energymatrix,1);

  for i=1:number_loglets,

    a_low_contracted(i) = min(a_matrix(energymatrixsorted(1:contraction_param,2),i));
    k_low_contracted(i) = min(k_matrix(energymatrixsorted(1:contraction_param,2),i));
    b_low_contracted(i) = min(b_matrix(energymatrixsorted(1:contraction_param,2),i));

    a_high_contracted(i) = max(a_matrix(energymatrixsorted(1:contraction_param,2),i));
    k_high_contracted(i) = max(k_matrix(energymatrixsorted(1:contraction_param,2),i));
    b_high_contracted(i) = max(b_matrix(energymatrixsorted(1:contraction_param,2),i));
  
  end


  for iter=1:MCiter,

    for i=1:number_loglets,
      a_try(i) = randnum(a_low_contracted(i),a_high_contracted(i));
      k_try(i) = randnum(k_low_contracted(i),k_high_contracted(i));
      b_try(i) = randnum(b_low_contracted(i),b_high_contracted(i));
    end

    y_try = zeros(max(size(x)),1);
    for i=1:number_loglets,
      y_try = y_try + loglet(x,a_try(i),k_try(i),b_try(i));
    end
  
    energy = sum( (y_try - y).^2);
  
    energymatrix(iter,:) = [energy,iter];
    a_matrix(iter,:) = a_try;
    k_matrix(iter,:) = k_try;
    b_matrix(iter,:) = b_try;
    
    if energy < energybest,
      for i=1:number_loglets,
        a_best(i) = a_try(i);
        k_best(i) = k_try(i);
        b_best(i) = b_try(i);
        energybest = energy;
      end
      
      disp('contraction')
      contractiter
      disp(iter)
      disp(energybest)
    end
  
  end

end



tstart = min(b_best) - 1.2 * max(a_best);
tstop = max(b_best) + 1.2 * max(a_best);
t = linspace(tstart,tstop,256);
t = t';
ymc = zeros(max(size(t)),1);
yg = zeros(max(size(t)),1);
for i=1:number_loglets,
  ymc = ymc + loglet(t,a_best(i),k_best(i),b_best(i));
  yg = yg + loglet(t,ag(i),kg(i),bg(i));
end


disp('loglet1 energy')
disp(energyloglet_g)

hold off
plot(t,yg,'-k')
hold
plot(x,y,'ok')
plot(t,ymc,'-r')

clc

energyloglet_g

ag

kg

bg

energybest

a_best

k_best

b_best 




%% end msli tex

