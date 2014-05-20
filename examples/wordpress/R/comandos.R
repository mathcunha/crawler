library(lattice)
resultado = read.csv("resultado.csv", header = TRUE)
xyplot(resultado$provider_id ~ resultado$workload)
xyplot(resultado$median ~ resultado$workload | resultado$provider_id * resultado$request)

library(ggplot2) 
qplot(resultado$workload, resultado$median, data = resultado, color = resultado$provider_id)
find <- subset(resultado, request == "find posts")
edit <- subset(resultado, request == "edit post")
view <- subset(resultado, request == "view post")
novo <- subset(resultado, request == "new post")


qplot(novo$workload, novo$mean, data = novo, color = novo$provider_id, main = "New Post")
qplot(edit$workload, edit$mean, data = edit, color = edit$provider_id, main = "Edit Post")
qplot(view$workload, view$mean, data = view, color = view$provider_id, main = "View Post")
qplot(find$workload, find$mean, data = find, color = find$provider_id, main = "Find Posts")


summary(subset(results, results$workload == 1000)$response_time, digits = 8)

aggregate(subset(results, results$provider_id == "4_c3_xlarge")$response_time, list(Workload = subset(results, results$provider_id == "4_c3_xlarge")$workload), mean)

aggregate(results$response_time ~ results$workload + results$provider_id, FUN=mean)
aggregate(results$response_time ~ results$workload + results$provider_id, FUN=min)
aggregate(results$response_time ~ results$workload + results$provider_id, FUN=max)
aggregate(results$response_time ~ results$workload + results$provider_id, FUN=function(x){c(Mean=mean(x), Median=median(x), Min=min(x), Max=max(x)))})

valores <- aggregate(results$response_time ~ results$workload + results$provider_id, FUN=function(x){c(Mean=mean(x), Median=median(x), Max=max(x), Min=min(x))})

install.packages("doBy")


library(doBy)
library(ggplot2)

results = read.csv("result.csv", header = TRUE)

boxplot(subset(results, workload == 100)$response_time~subset(results, workload == 100)$provider_id ,data=subset(results, workload == 100), main="Response Time - Workload 100", xlab="Provider ID", ylab="Response Time", las=2)
boxplot(subset(results, workload == 200)$response_time~subset(results, workload == 200)$provider_id ,data=subset(results, workload == 200), main="Response Time - Workload 200", xlab="Provider ID", ylab="Response Time", las=2)
boxplot(subset(results, workload == 300)$response_time~subset(results, workload == 300)$provider_id ,data=subset(results, workload == 300), main="Response Time - Workload 300", xlab="Provider ID", ylab="Response Time", las=2)
boxplot(subset(results, workload == 400)$response_time~subset(results, workload == 400)$provider_id ,data=subset(results, workload == 400), main="Response Time - Workload 400", xlab="Provider ID", ylab="Response Time", las=2)
boxplot(subset(results, workload == 500)$response_time~subset(results, workload == 500)$provider_id ,data=subset(results, workload == 500), main="Response Time - Workload 500", xlab="Provider ID", ylab="Response Time", las=2)
boxplot(subset(results, workload == 600)$response_time~subset(results, workload == 600)$provider_id ,data=subset(results, workload == 600), main="Response Time - Workload 600", xlab="Provider ID", ylab="Response Time", las=2)
boxplot(subset(results, workload == 700)$response_time~subset(results, workload == 700)$provider_id ,data=subset(results, workload == 700), main="Response Time - Workload 700", xlab="Provider ID", ylab="Response Time", las=2)
boxplot(subset(results, workload == 800)$response_time~subset(results, workload == 800)$provider_id ,data=subset(results, workload == 800), main="Response Time - Workload 800", xlab="Provider ID", ylab="Response Time", las=2)
boxplot(subset(results, workload == 900)$response_time~subset(results, workload == 900)$provider_id ,data=subset(results, workload == 900), main="Response Time - Workload 900", xlab="Provider ID", ylab="Response Time", las=2)
boxplot(subset(results, workload == 1000)$response_time~subset(results, workload == 1000)$provider_id ,data=subset(results, workload == 1000), main="Response Time - Workload 1000", xlab="Provider ID", ylab="Response Time", las=2)

valores2 <- summaryBy(results$response_time ~ results$provider_id + results$workload, results, FUN = c(mean, median, max, min, sd))

write.table(valores2, "summarized.csv", sep=",")

find = read.csv("summarized.csv", header = TRUE)


qplot(find$workload, find$mean, data = find, color = find$provider_id, main = "Sessao")
