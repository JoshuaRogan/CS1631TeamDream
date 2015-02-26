function accuracy = TON_I(par_name,par_imgPath,par_rstPath,par_P)
%���׽��� third-order neighbors method 0.8133 
% TON_I('yale','E:\SIS\workspace\FaceRecognition\Data\Face\Yale\','E:\SIS\workspace\FaceRecognition\Data\Result\TON\',0.9)
% FaceRec.m
%clear;clc;
%par_name='yale';
%par_imgPath='E:\SIS\workspace\FaceRecognition\Data\yalefaces\';
%par_rstPath='E:\SIS\workspace\FaceRecognition\Data\Result\TON\';
%par_P=0.9;ѡ��90%������

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
%��ȡͼ���С
a=imread(strcat(par_imgPath,'001-01.bmp'));
[M,N] = size(a);

% calc xmean,sigma and its eigen decomposition
allsamples=[];%����ѵ��ͼ��
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
        % imshow(a);
        b=a(1:M*N); % b ����ʸ�� 1��P������P��M*N����ȡ˳�������к��У������ϵ��£�������
        b=double(b);
        allsamples=[allsamples; b]; % allsamples ��һ��M * N ����allsamples ��ÿһ�����ݴ���һ��ͼƬ������M��200

    end
end
samplemean=mean(allsamples); % ƽ��ͼƬ��1 �� N

for i=1:sampleimgs 
    xmean(i,:)=allsamples(i,:)-samplemean; % xmean ��һ��M �� N ����xmeanÿһ�б���������ǡ�ÿ��ͼƬ����-ƽ��ͼƬ��
end;
% ��ȡ����ֵ����������
sigma=xmean*xmean'; % M * M �׾���
[v d]=eig(sigma);
d1=diag(d);
% ������ֵ��С�Խ�������
dsort = flipud(d1);
vsort = fliplr(v);
%����ѡ��90%������
dsum = sum(dsort);
dsum_extract = 0;
p = 0;
while( dsum_extract/dsum < par_P)
    p = p + 1;
    dsum_extract = sum(dsort(1:p));
end

i=1;
% (ѵ���׶�)�����������γɵ�����ϵ
base = xmean' * vsort(:,1:p) * diag(dsort(1:p).^(-1/2));
% base ��N��p �׾��󣬳���dsort(i)^(1/2)�Ƕ�����ͼ��ı�׼����ʹ�䷽��Ϊ1��
% ���������PCA ������ʶ���㷨�о���p31
% xmean' * vsort(:,i)��С���������������������������ת���Ĺ���
%while (i<=p && dsort(i)>0)
% base(:,i) = dsort(i)^(-1/2) * xmean' * vsort(:,i); % base ��N��p �׾��󣬳���dsort(i)^(1/2)�Ƕ�����ͼ��ı�׼����ʹ�䷽��Ϊ1��

% ���������PCA ������ʶ���㷨�о���p31
% i = i + 1; % xmean' * vsort(:,i)��С���������������������������ת���Ĺ���

%end
% ��������add by gongxun ��ѵ������������ϵ�Ͻ���ͶӰ,�õ�һ�� M*p �׾���allcoor
allcoor = allsamples * base; % allcoor ������ÿ��ѵ������ͼƬ��M*p �ӿռ��е�һ���㣬�����ӿռ��е����ϵ����

accu = 0; % ���������ʶ������о���������Щ���ϵ��������ʶ��
% ���Թ���
% variable for test result
recogintion=zeros(testimgs, 3);
for i=1:totalPeople
    if i<10
        s1=['00',int2str(i)];
    elseif i<100
        s1=['0', int2str(i)];
    else s1=[int2str(i)];
    end
    for j=grpSampleCount+1:grpSampleCount+grpTestCount %�������ͼ��
        if j<10
            s2=['0', int2str(j)];
        else s2=[int2str(j)];
        end
        a=imread(strcat(par_imgPath,s1,'-',s2,'.bmp'));
        b=a(1:M*N);
        b=double(b);
        tcoor= b * base; %�������꣬��1��p �׾���
        for k=1:sampleimgs
            mdist(k)=norm(tcoor-allcoor(k,:));
        end;
        %���׽���
        [dist,index2]=sort(mdist);
        class1=floor( (index2(1)-1)/grpSampleCount )+1;
        class2=floor((index2(2)-1)/grpSampleCount)+1;
        class3=floor((index2(3)-1)/grpSampleCount)+1;
        if class1~=class2 && class2~=class3
            class=class1;
        elseif class1==class2
            class=class1;
        elseif class2==class3
            class=class2;
        end;
        if class==i
            accu=accu+1;
        end;
        recogintion((i-1)*grpTestCount+j-grpSampleCount,1)=i;
        recogintion((i-1)*grpTestCount+j-grpSampleCount,2)=j;
        recogintion((i-1)*grpTestCount+j-grpSampleCount,3)=(class==i);

    end;
end;

% write the test result to txt
dlmwrite([par_rstPath,'TON_', par_name,  '_P',num2str(par_P), '.txt'], recogintion);    

accuracy=accu/testimgs; %���ʶ����

