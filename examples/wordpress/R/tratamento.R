setwd("M:/users/matheus/mongo/results")
#sed -i 's;large;l;g' ./results/results_c32xlarge.csv
#sed -i 's;medium;m;g' ./results/results_c32xlarge.csv
results = read.csv("results_all.csv", header = TRUE, stringsAsFactors = FALSE)

str(results)

my_quantile <- function(x){quantile(x, c(.90))}
library(doBy)
valores_rt <- summaryBy(results$response_time ~ results$provider_id + results$workload, results, FUN = c(mean, median, max, min, sd, my_quantile))
valores_count <- summaryBy(results$count ~ results$provider_id + results$workload, results, FUN = c(sum))
valores_count_ok <- summaryBy(results$count_ok ~ results$provider_id + results$workload, results, FUN = c(sum))
valores_count_ko <- summaryBy(results$count_ko ~ results$provider_id + results$workload, results, FUN = c(sum))
valores_merged <- merge(valores_rt, valores_count, by = c("provider_id","workload"))
valores_merged <- merge(valores_merged, valores_count_ok, by = c("provider_id","workload"))
valores_merged <- merge(valores_merged, valores_count_ko, by = c("provider_id","workload"))
write.table(valores_merged, "summarized.csv", sep=",")


results <- read.csv("summarized.csv", header = TRUE, stringsAsFactors = FALSE)
#"provider_id","workload","results$response_time.mean","results$response_time.median","results$response_time.max","results$response_time.min","results$response_time.sd","percentile","results$count.sum","results$count_ok.sum","results$count_ko.sum"
str(results)

results$configuration <- results$provider_id
results$instances <- sapply(results$provider_id, function(x) {
  unlist(strsplit(x,"_"))[1]
  })
results$provider_id <- sapply(results$provider_id, function(x) {
  provider <- unlist(strsplit(x,"_"))
  paste(provider[2],provider[3],sep="_")
})

results$instance_price[results$provider_id == "m3_m"] <- 0.07
results$instance_price[results$provider_id == "c3_l"] <- 0.105
results$instance_price[results$provider_id == "m3_l"] <- 0.14
results$instance_price[results$provider_id == "c3_xl"] <- 0.21
results$instance_price[results$provider_id == "m3_xl"] <- 0.28
results$instance_price[results$provider_id == "c3_2xl"] <- 0.42
results$instance_price[results$provider_id == "m3_2xl"] <- 0.56

results$instances <- as.numeric(results$instances)

results$total_price <- mapply(function(x,y){x * y}, results$instance_price, results$instances)
results$cost_user <- mapply(function(x,y){x / y}, results$instance_price, results$workload)
results$cost_performance <- mapply(function(x,y){x / y}, results$instance_price, results$percentile)

write.table(results, "summarized_price.csv", sep=",")
#"provider_id","workload","mean","median","max","min","sd","percentile","count","count_ok","count_ko","configuration","instances","instance_price","total_price","cost_user","cost_performance"


performance = read.csv("performance.csv", header = TRUE, stringsAsFactors = FALSE)
str(performance)
#sed -i 's;large;l;g' ./results/performance.csv
#sed -i 's;medium;m;g' ./results/performance.csv

performance$configuration <- performance$provider_id
performance$instances <- sapply(performance$provider_id, function(x) {
  unlist(strsplit(x,"_"))[1]
})
performance$provider_id <- sapply(performance$provider_id, function(x) {
  provider <- unlist(strsplit(x,"_"))
  paste(provider[2],provider[3],sep="_")
})
write.table(performance, "performance_all.csv", sep=",")
