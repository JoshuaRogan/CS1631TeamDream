function accuracy = PCA_I(par_name,par_imgPath,par_rstPath,par_P)
%PCA_I('yale','E:\SIS\workspace\FaceRecognition\Data\Face\yale\','E:\SIS\workspace\FaceRecognition\Data\Result\PCA\',0.2)
%par_name='orl';
%par_imgPath='E:\SIS\workspace\FaceRecognition\Data\yalefaces\';
%par_rstPath='E:\SIS\workspace\FaceRecognition\Data\Result\PCA\';
%par_P=0.2;特征向量占图像尺寸的比率

% 2DPCA
clear all_text;
%clc;
%Initial variable
filelist=dir([par_imgPath,'*-01.bmp']);
totalPeople=size(filelist,1);

filelist=dir(strcat(par_imgPath,'001-*.bmp'));
grpImgCount=size(filelist,1);

if(exist(par_rstPath,'dir')==0)
    mkdir(par_rstPath);
end

grpSampleCount=floor(grpImgCount*0.5);
grpTestCount=grpImgCount-grpSampleCount;
sampleimgs=totalPeople*grpSampleCount;
testimgs=totalPeople*grpTestCount;
%获取图像大小
a=imread(strcat(par_imgPath,'001-01.bmp'));
[M,N] = size(a);  

% training
allsamples1=[];
for i=1:totalPeople
    if i<10
        s1=['00',int2str(i)];
    elseif i<100
        s1=['0', int2str(i)];
    else s1=[int2str(i)];
    end
    for j=1:grpSampleCount
        if j<10
            s2=['0', int2str(j)];
        else s2=[int2str(j)];
        end
        a=imread(strcat(par_imgPath,s1,'-',s2,'.bmp'));
        b=a(1:M*N);
        b=double(b);
        allsamples1=[allsamples1;b];
    end    
end
% 
samplesmean=mean(allsamples1);
allsamples=allsamples1-repmat(samplesmean,sampleimgs,1);

Gt=0;
for i=1:sampleimgs
    a=reshape(allsamples(i,:),M,N);
    Gt=Gt+a'*a;
end
Gt=Gt/sampleimgs;

[V D]=eig(Gt);
[PD index2]=sort(-diag(D));
PV=V(:,index2);
%dim=5;%可能是特征向量个数

if M>N MaxDim=N; else MaxDim=M; end;
dim=floor(MaxDim*par_P);
if dim<1 dim=1; elseif dim>MaxDim dim=MaxDim; end;

PV=PV(:,1:dim);
%weight=sum(abs(PD(1:dim)))/sum(diag(D))

Y=zeros(sampleimgs,M,dim);
for i=1:sampleimgs
    a=reshape(allsamples1(i,:),M,N);
    Y(i,:,:)=a*PV;
end

%testing
accu=0;

% variable for test result
recogintion=zeros(testimgs, 3);
for i=1:totalPeople
    if i<10
        s1=['00',int2str(i)];
    elseif i<100
        s1=['0', int2str(i)];
    else s1=[int2str(i)];
    end
    for j=grpSampleCount+1:grpSampleCount+grpTestCount
        if j<10
            s2=['0', int2str(j)];
        else s2=[int2str(j)];
        end
        a=imread(strcat(par_imgPath,s1,'-',s2,'.bmp'));
        a=double(a);
%         a=a-reshape(samplesmean,M,N);
        Y1=a*PV;
        mdist=zeros(1,sampleimgs);
        for m=1:sampleimgs
            for n=1:dim
                mdist(m)=mdist(m)+norm(((Y(m,:,n))'-Y1(:,n)));
            end
        end
        [dist ,index4]=sort(mdist);
        class=ceil(index4(1)/grpSampleCount);
        if class==i;
            accu=accu+1;
        end
        recogintion((i-1)*grpTestCount+j-grpSampleCount,1)=i;
        recogintion((i-1)*grpTestCount+j-grpSampleCount,2)=j;
        recogintion((i-1)*grpTestCount+j-grpSampleCount,3)=(class==i);

    end
end
% write the test result to txt
dlmwrite([par_rstPath,'PCA_', par_name,  '_P',num2str(par_P), '.txt'], recogintion);    

accuracy=accu/testimgs;
        
