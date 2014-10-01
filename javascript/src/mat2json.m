clear all
close all
format short e

%%load sun.txt
%%data.x = sun(:,1);
%%data.y = sun(:,2);

%load elements.txt
%%% subtract known elements 
%elements(:,2) = elements(:,2) - 14;
%data.x = elements(:,1);
%data.y = elements(:,2);

%load USpopStatAB08scaled.txt
%data.x = USpopStatAB08scaled(:,1);
%data.y = USpopStatAB08scaled(:,2);

%load boom.txt
%data.x = boom(:,1);
%data.y = boom(:,2);

%load rembrandt.txt
%data.x = rembrandt(:,1);
%data.y = rembrandt(:,2);

%load van_gogh.txt;
%data.x = van_gogh(:,1);
%data.y = van_gogh(:,2);

%load univ.txt
%data.x = univ(:,1);
%data.y = univ(:,2);

load downexample.txt
data.x = downexample(:,1);
data.y = downexample(:,2);

fid = fopen('downward.json','w');

%% DONT CHANGE THIS 
dataname = 'data' 

numdata = max(size(fieldnames(eval(dataname))))

fdata = char(fieldnames(eval(dataname)));

fprintf(fid,'{ "%s" : {\n ',dataname)
for dataiter=1:numdata,
  fprintf(fid,' "%s" : [ \n',fdata(dataiter));
  stmp = sprintf('%s.%s',dataname,fdata(dataiter));
  for i=1:(max(length(eval(stmp))))-1,
    stmp = sprintf('%s.%s(%i)',dataname,fdata(dataiter),i);
    fprintf(fid,'%1.10e , \n',eval(stmp));
  end
  stmp2 =  sprintf('%s.%s',dataname,fdata(dataiter));;
  stmp = sprintf('%s.%s(%i)',dataname,fdata(dataiter),(max(length(eval(stmp2)))));
  fprintf(fid,'%1.10e  \n',eval(stmp));

  if dataiter == numdata ,
    fprintf(fid,'] \n');
  else 
    fprintf(fid,'], \n');
  end
  
end

fprintf(fid,'} } \n');




