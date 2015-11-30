function printData(x,y)
figure
test = xlsread('survival1.xlsx', 'D1:D3000');
x=[1:2400];
y=test(:,1);

for i=[1:2400]
   y(i)=sum(y(i:i+500))/500;
end


plot(x(1:2400),y(1:2400));
title('AccumlativeReward Per Battle Vs. Number of Battles ExplorationRate=0.01 ');
%scatter3(x,y,z,'filled')
xlabel('Number of battles'); % x-axis label
ylabel('Averaged AcummulativeReward');% y-axis label

end
