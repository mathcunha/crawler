library(lattice)
resultado = read.csv("resultado.csv", header = TRUE)
xyplot(resultado$provider_id ~ resultado$workload)
xyplot(resultado$median ~ resultado$workload | resultado$provider_id * resultado$request)

library(ggplot2) 
library(RColorBrewer)
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

pdf()

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

dev.off()

results = read.csv("results_all.csv", header = TRUE)
library(doBy)
my_quantile <- function(x){quantile(x, c(.90))}
valores_rt <- summaryBy(results$response_time ~ results$provider_id + results$workload, results, FUN = c(mean, median, max, min, sd, my_quantile))
valores_count <- summaryBy(results$count ~ results$provider_id + results$workload, results, FUN = c(sum))
valores_count_ok <- summaryBy(results$count_ok ~ results$provider_id + results$workload, results, FUN = c(sum))
valores_count_ko <- summaryBy(results$count_ko ~ results$provider_id + results$workload, results, FUN = c(sum))

valores_merged <- merge(valores_rt, valores_count, by = c("provider_id","workload"))
valores_merged <- merge(valores_merged, valores_count_ok, by = c("provider_id","workload"))
valores_merged <- merge(valores_merged, valores_count_ko, by = c("provider_id","workload"))

write.table(valores_merged, "summarized_all_ko.csv", sep=",")

find = read.csv("summarized_all_ko.csv", header = TRUE)
library(ggplot2) 
library(RColorBrewer)
pdf()
qplot(workload, percentile, data = find, color = provider_id, main = "Sessao")
qplot(workload, percentile, data = find, color = provider_id, main = "Sessao")+ geom_line()

ggplot(data=subset(valores2, workload == 100), aes(x=instances, y=percentile, fill=provider_id)) + geom_bar(stat="identity", position=position_dodge(), colour="black") + ggtitle("90th Percentil Response Time - Workload 100")
ggplot(data=subset(valores2, workload == 200), aes(x=instances, y=percentile, fill=provider_id)) + geom_bar(stat="identity", position=position_dodge(), colour="black") + ggtitle("90th Percentil Response Time - Workload 200")
ggplot(data=subset(valores2, workload == 300), aes(x=instances, y=percentile, fill=provider_id)) + geom_bar(stat="identity", position=position_dodge(), colour="black") + ggtitle("90th Percentil Response Time - Workload 300")
ggplot(data=subset(valores2, workload == 400), aes(x=instances, y=percentile, fill=provider_id)) + geom_bar(stat="identity", position=position_dodge(), colour="black") + ggtitle("90th Percentil Response Time - Workload 400")
ggplot(data=subset(valores2, workload == 500), aes(x=instances, y=percentile, fill=provider_id)) + geom_bar(stat="identity", position=position_dodge(), colour="black") + ggtitle("90th Percentil Response Time - Workload 500")
ggplot(data=subset(valores2, workload == 600), aes(x=instances, y=percentile, fill=provider_id)) + geom_bar(stat="identity", position=position_dodge(), colour="black") + ggtitle("90th Percentil Response Time - Workload 600")
ggplot(data=subset(valores2, workload == 700), aes(x=instances, y=percentile, fill=provider_id)) + geom_bar(stat="identity", position=position_dodge(), colour="black") + ggtitle("90th Percentil Response Time - Workload 700")
ggplot(data=subset(valores2, workload == 800), aes(x=instances, y=percentile, fill=provider_id)) + geom_bar(stat="identity", position=position_dodge(), colour="black") + ggtitle("90th Percentil Response Time - Workload 800")
ggplot(data=subset(valores2, workload == 900), aes(x=instances, y=percentile, fill=provider_id)) + geom_bar(stat="identity", position=position_dodge(), colour="black") + ggtitle("90th Percentil Response Time - Workload 900")
ggplot(data=subset(valores2, workload == 1000), aes(x=instances, y=percentile, fill=provider_id)) + geom_bar(stat="identity", position=position_dodge(), colour="black") + ggtitle("90th Percentil Response Time - Workload 1000")
ggplot(data=subset(valores2, percentile < 22000), aes(x=provider_id, y=instances, fill=workload)) + geom_bar(stat="identity", position=position_dodge(), colour="black") + ggtitle("90th Percentil Response Time < 22s") + scale_fill_gradientn(colours=brewer.pal(9,"Spectral"))
ggplot(data=subset(valores2, percentile < 25000), aes(x=provider_id, y=instances, fill=workload)) + geom_bar(stat="identity", position=position_dodge(), colour="black") + ggtitle("90th Percentil Response Time < 25s") + scale_fill_gradientn(colours=brewer.pal(9,"Spectral"))
dev.off


library(lattice)
valores2 = read.csv("summarized_all_ko.csv", header = TRUE)
head(valores2)
xyplot(percentile ~ workload | instances * provider_id, data = below_25)

xyplot(percentile ~ workload | instances * provider_id, data = subset(valores2, percentile < 30000))

barplot(percentile ~ workload | instances * provider_id, data = subset(valores2, percentile < 30000))
wordpress = read.csv("summarized_all_ko_price.csv", header = TRUE)

cost.cut <- equal.count(wordpress$cost_user, 6)
cost.cut
?equal.count
xyplot(cost_user ~ workload | configuration, data = wordpress)
xyplot(percentile 



library(lattice)
valores2 = read.csv("summarized_all_ko_price.csv", header = TRUE)
str(valores2)

valores2$instances <- factor(valores2$instances, order=TRUE)
valores2$workload <- factor(valores2$workload, order=TRUE)
valores2$instance_price <- factor(valores2$instance_price, order=TRUE)
valores2$price_provider_id <- mapply(function(x,y){paste(x,y,sep="_")}, valores2$instance_price, valores2$provider_id)
valores2$price_provider_id <- factor(valores2$price_provider_id, order=TRUE)
ggplot(valores2, aes(x=instances, y=provider_id, size=instance_price, colour=instance_price)) + layer(geom = "point") + facet_grid(. ~ workload)
str(valores2)
valores2$provider_id <- factor(valores2$provider_id, levels = c("m3_m", "c3_l", "m3_l","c3_xl", "m3_xl", "c3_2xl", "m3_2xl"))
ggplot(valores2, aes(x=instances, y=provider_id, size=instance_price, colour=instance_price)) + layer(geom = "point") + facet_grid(. ~ workload)
ggplot(subset(valores2, percentile < 30000), aes(x=instances, y=provider_id, size=fc_cost_user, colour=instance_price)) + layer(geom = "point") + facet_grid(. ~ workload)
ggplot(subset(valores2, percentile < 30000), aes(x=instances, y=provider_id, size=fc_cost_user, colour=instance_price)) + layer(geom = "point") + facet_grid(. ~ workload) + guides(size=FALSE)
ggplot(subset(valores2, percentile < 30000), aes(x=instances, y=provider_id, size=fc_cost_user, colour=instance_price)) + layer(geom = "point") + facet_grid(. ~ workload) + guides(size=FALSE) + ggtitle("90th Response Time < 30 | workload + provider_id + User Cost")

valores2$fc_cost_user <- factor(valores2$cost_user, order=TRUE)

qplot(cost_user, workload, data = valores2, colour = factor(configuration))
qplot(cost_user, data = valores2, facets = ~ workload , colour = factor(configuration))
qplot(cost_user, data = valores2, facets = . ~ workload , colour = factor(configuration))

cost.cut <- equal.count(wordpress$cost_user, 6)

