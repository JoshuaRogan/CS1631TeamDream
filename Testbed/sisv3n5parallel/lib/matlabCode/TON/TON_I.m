function accuracy = TON_I(par_name,par_imgPath,par_rstPath,par_P)
%三阶近邻 third-order neighbors method 0.8133 
% TON_I('yale','E:\SIS\workspace\FaceRecognition\Data\Face\Yale\','E:\SIS\workspace\FaceRecognition\Data\Result\TON\',0.9)
% FaceRec.m
%clear;clc;
%par_name='yale';
%par_imgPath='E:\SIS\workspace\FaceRecognition\Data\yalefaces\';
%par_rstPath='E:\SIS\workspace\FaceRecognition\Data\Result\TON\';
%par_P=0.9;选择90%的能量

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

% calc xmean,sigma and its eigen decomposition
allsamples=[];%所有训练图像
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
        b=a(1:M*N); % b 是行矢量 1×P，其中P＝M*N，提取顺序是先列后行，即从上到下，从左到右
        b=double(b);
        allsamples=[allsamples; b]; % allsamples 是一个M * N 矩阵，allsamples 中每一行数据代表一张图片，其中M＝200

    end
end
samplemean=mean(allsamples); % 平均图片，1 × N

for i=1:sampleimgs 
    xmean(i,:)=allsamples(i,:)-samplemean; % xmean 是一个M × N 矩阵，xmean每一行保存的数据是“每个图片数据-平均图片”
end;
% 获取特征值及特征向量
sigma=xmean*xmean'; % M * M 阶矩阵
[v d]=eig(sigma);
d1=diag(d);
% 按特征值大小以降序排列
dsort = flipud(d1);
vsort = fliplr(v);
%以下选择90%的能量
dsum = sum(dsort);
dsum_extract = 0;
p = 0;
while( dsum_extract/dsum < par_P)
    p = p + 1;
    dsum_extract = sum(dsort(1:p));
end

i=1;
% (训练阶段)计算特征脸形成的坐标系
base = xmean' * vsort(:,1:p) * diag(dsort(1:p).^(-1/2));
% base 是N×p 阶矩阵，除以dsort(i)^(1/2)是对人脸图像的标准化（使其方差为1）
% 详见《基于PCA 的人脸识别算法研究》p31
% xmean' * vsort(:,i)是小矩阵的特征向量向大矩阵特征向量转换的过程
%while (i<=p && dsort(i)>0)
% base(:,i) = dsort(i)^(-1/2) * xmean' * vsort(:,i); % base 是N×p 阶矩阵，除以dsort(i)^(1/2)是对人脸图像的标准化（使其方差为1）

% 详见《基于PCA 的人脸识别算法研究》p31
% i = i + 1; % xmean' * vsort(:,i)是小矩阵的特征向量向大矩阵特征向量转换的过程

%end
% 以下两行add by gongxun 将训练样本对坐标系上进行投影,得到一个 M*p 阶矩阵allcoor
allcoor = allsamples * base; % allcoor 里面是每张训练人脸图片在M*p 子空间中的一个点，即在子空间中的组合系数，

accu = 0; % 下面的人脸识别过程中就是利用这些组合系数来进行识别
% 测试过程
% variable for test result
recogintion=zeros(testimgs, 3);
for i=1:totalPeople
    if i<10
        s1=['00',int2str(i)];
    elseif i<100
        s1=['0', int2str(i)];
    else s1=[int2str(i)];
    end
    for j=grpSampleCount+1:grpSampleCount+grpTestCount %读入测试图像
        if j<10
            s2=['0', int2str(j)];
        else s2=[int2str(j)];
        end
        a=imread(strcat(par_imgPath,s1,'-',s2,'.bmp'));
        b=a(1:M*N);
        b=double(b);
        tcoor= b * base; %计算坐标，是1×p 阶矩阵
        for k=1:sampleimgs
            mdist(k)=norm(tcoor-allcoor(k,:));
        end;
        %三阶近邻
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

accuracy=accu/testimgs; %输出识别率

